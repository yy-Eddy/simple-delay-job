package com.github.yyeddy.sdj.wheel.kafka;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @ClassName: TimerTaskList.java
 * @version: v1.0.0
 * @Description: 时间槽
 * @author: yangyu
 * @date: 2020年3月30日 下午4:52:18
 */
public class TimerTaskList implements Delayed {
	/**
	 * 过期时间
	 */
	private AtomicLong expiration = new AtomicLong(-1L);

	/**
	 * 根节点
	 */
	private KafkaTimerTask root = new KafkaTimerTask(-1L, null);

	{
		root.pre = root;
		root.next = root;
	}

	/**
	 * 设置过期时间
	 */
	public boolean setExpiration(long expire) {
		return expiration.getAndSet(expire) != expire;
	}

	/**
	 * 获取过期时间
	 */
	public long getExpiration() {
		return expiration.get();
	}

	/**
	 * 新增任务
	 */
	public void addTask(KafkaTimerTask task) {
		synchronized (this) {
			if (task.timerTaskList == null) {
				task.timerTaskList = this;
				KafkaTimerTask tail = root.pre;
				task.next = root;
				task.pre = tail;
				tail.next = task;
				root.pre = task;
			}
		}
	}

	/**
	 * 移除任务
	 */
	public void removeTask(KafkaTimerTask timerTask) {
		synchronized (this) {
			if (timerTask.timerTaskList.equals(this)) {
				timerTask.next.pre = timerTask.pre;
				timerTask.pre.next = timerTask.next;
				timerTask.timerTaskList = null;
				timerTask.next = null;
				timerTask.pre = null;
			}
		}
	}

	/**
	 * 重新分配
	 */
	public synchronized void flush(Consumer<KafkaTimerTask> flush) {
		KafkaTimerTask timerTask = root.next;
		while (!timerTask.equals(root)) {
			this.removeTask(timerTask);
			flush.accept(timerTask);
			timerTask = root.next;
		}
		expiration.set(-1L);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long delay = Math.max(0, unit.convert(expiration.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
		return delay;
	}

	@Override
	public int compareTo(Delayed o) {
		if (o instanceof TimerTaskList) {
			return Long.compare(expiration.get(), ((TimerTaskList) o).expiration.get());
		}
		return 0;
	}

	@Override
	public String toString() {
		return "TimerTaskList{" +
				"expiration=" + expiration +
				'}';
	}
}
