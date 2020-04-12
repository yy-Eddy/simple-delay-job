package com.github.yyeddy.sdj.wheel.kafka;

import com.github.yyeddy.sdj.wheel.Timer;
import com.github.yyeddy.sdj.wheel.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName: Timer.java
 * @version: v1.0.0
 * @Description:定时器
 * @author: yangyu
 * @date: 2020年3月30日 下午4:50:53
 */
public class KafkaTimer implements Timer {
	
	private final Logger logger = LoggerFactory.getLogger(KafkaTimer.class);
	
	/**
	 * 一个Timer只有一个delayQueue
	 */
	private final DelayQueue<TimerTaskList> delayQueue;

	/**
	 * 底层时间轮
	 */
	private final TimeWheel timeWheel;

	/**
	 * 过期任务执行线程
	 */
	private final ExecutorService workerThreadPool;

	/**
	 * 轮询delayQueue获取过期任务线程
	 */
	private final ExecutorService bossThreadPool;
	
	/**
	 * 关闭线程,后续优化使用 等待-通知 机制
	 */
	private final AtomicBoolean threadShutdown;
	
	/**
	 * 是否允许添加任务
	 */
	private final AtomicBoolean addTaskBoolean;
	
	/**
	 * 冗余查询次数
	 */
	private static final int REDUNDANCY_TIMES = 10000;
	
	/**
	 * @param tick 一个刻度的时间(毫秒)
	 * @param wheelSize 总共有多少个刻度
	 * @param stop 多久后停止时间(毫秒)
	 */
	public KafkaTimer(final long tick, final int wheelSize,final long stop) {
		if(tick <= 10) {
			throw new IllegalArgumentException("刻度时间必须大于10,过小可能会导致时间不精准");
		}
		if(wheelSize <= 0) {
			throw new IllegalArgumentException("刻度轮必须大于0");
		}
		if(stop < tick * wheelSize) {
			throw new IllegalArgumentException("结束时间必须大于该次的刻度周期");
		}
		this.delayQueue = new DelayQueue<>();
		this.workerThreadPool = getWorkerThreadPool(tick * wheelSize + stop);
		this.bossThreadPool = Executors.newFixedThreadPool(1);//因为只需要一个线程进行时间轮
		this.timeWheel = new TimeWheel(tick, wheelSize, System.currentTimeMillis(), delayQueue);
		threadShutdown = new AtomicBoolean(false);
		addTaskBoolean = new AtomicBoolean(true);//允许添加任务
		long timeout = tick;
		if(tick > 60){
			//时间轮调度过大 需要对poll的调度时间更改
			timeout  = 60;
		}
		final long realTimeout = timeout;
		startShutdownThread();
		final long stopTime = System.currentTimeMillis() + stop;
		//推进时间轮线程
		this.bossThreadPool.execute(() -> {
			long supTime = stopTime - System.currentTimeMillis();
			logger.info("分层时间轮-当前调度开始.结束时间是:{} 剩余执行时间是:{}",stopTime,supTime);
			while (supTime >= 0) {//此处用时间判断 是因为可能在时间结束之前还会有任务进来
				this.advanceClock(realTimeout);
				supTime = stopTime - System.currentTimeMillis();
			}
			addTaskBoolean.set(false);//关闭线程前 不允许添加任务
			int i = 0;
			while(!this.delayQueue.isEmpty() && i < REDUNDANCY_TIMES) {
				//如果时间到期了 但是队列里面还有数据,那么再执行一定的次数
				this.advanceClock(realTimeout);
				i++;
			}
			logger.info("分层时间轮-当前调度完成.冗余运行次数是:{}",i);
			threadShutdown.set(true);//线程执行完毕
		});
	}

	//获取工作线程池配置
	private ExecutorService getWorkerThreadPool(long keepAliveTime) {
		int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
		BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(2000, true);//以空间换时间 悲观策略
		return new ThreadPoolExecutor(1, maximumPoolSize,keepAliveTime,TimeUnit.MILLISECONDS,blockingQueue,
					Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
	}
	
	//开启关闭线程
	private void startShutdownThread() {
		//使用Executors创建的是守护线程,后续优化, 就可以不需要单独线程监控了
		new Thread(() -> {
			try {
				for(;;) {
					if(threadShutdown.get()) {
						Thread.sleep(10l);
						logger.info("分层时间轮-当前调度完成.关闭线程池开始");
						this.bossThreadPool.shutdown();
						this.workerThreadPool.shutdown();
						logger.info("分层时间轮-当前调度完成.关闭线程池结束");
						break;
					}
				}
			} catch (Exception e) {
				logger.error("分层时间轮-当前调度完成.关闭线程池异常",e);
			}
		}).start();
	}
	
	/**
	 * @Description: 添加任务
	 * @param timerTask
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: yangyu
	 * @date: 2020年3月31日 下午3:11:27
	 */
	@Override
	public Boolean addTask(TimerTask timerTask) {
		KafkaTimerTask kafkaTimerTask = new KafkaTimerTask(timerTask.getDelayMs(),timerTask.getTimerTaskRunnable());
		addTaskInner(kafkaTimerTask);
		return true;
	}

	private void addTaskInner(KafkaTimerTask timerTask){
		if(!addTaskBoolean.get()) {
			throw new RejectedExecutionException("不允许添加任务 " + timerTask.toString());
		}
		if(timerTask.getTimerTaskRunnable() == null){
			throw new NullPointerException("任务到期执行的线程不能为空");
		}
		//添加失败任务直接执行
		if (!timeWheel.addTask(timerTask)) {
			workerThreadPool.execute(timerTask.getTimerTaskRunnable());
		}
	}

	/**
	 * 获取过期任务
	 * @throws Exception 
	 */
	private void advanceClock(long timeout){
		try {
			TimerTaskList timerTaskList = delayQueue.poll(timeout, TimeUnit.MILLISECONDS);
			if (timerTaskList != null) {
				// 推进时间
				timeWheel.advanceClock(timerTaskList.getExpiration());
				// 执行过期任务（包含降级操作）
				timerTaskList.flush(this::addTaskInner);
			}
		} catch (Exception e) {
			//处理异常数据 
			e.printStackTrace();
		}
	}

}
