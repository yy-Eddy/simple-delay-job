package com.github.yyeddy.sdj.wheel.kafka;

import com.github.yyeddy.sdj.wheel.TimerTaskRunnable;

/**
 * @ClassName: TimerTask.java
 * @version: v1.0.0
 * @Description: 任务
 * @author: yangyu
 * @date: 2020年3月30日 下午4:51:30
 */
public class KafkaTimerTask {
	/**
	 * 延迟时间
	 */
	private long delay;

	/**
	 * 任务
	 */
	private TimerTaskRunnable timerTaskRunnable;

	/**
	 * 时间槽
	 */
	protected TimerTaskList timerTaskList;

	/**
	 * 下一个节点
	 */
	protected KafkaTimerTask next;

	/**
	 * 上一个节点
	 */
	protected KafkaTimerTask pre;

	/**
	 * @param delay 延迟多少时间(毫秒)
	 * @param taskRunnable 任务到期时执行的线程
	 */
	public KafkaTimerTask(long delay, TimerTaskRunnable taskRunnable) {
		this.delay = System.currentTimeMillis() + delay;
		this.timerTaskRunnable = taskRunnable;
		this.timerTaskList = null;
		this.next = null;
		this.pre = null;
	}

	public TimerTaskRunnable getTimerTaskRunnable() {
		return timerTaskRunnable;
	}

	public long getDelayMs() {
		return delay;
	}

}
