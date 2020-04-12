package com.github.yyeddy.sdj.wheel;

/**
 * @ClassName: TimerTask.java
 * @version: v1.0.0
 * @Description: 任务
 * @author: yangyu
 * @date: 2020年3月30日 下午4:51:30
 */
public class TimerTask {
	/**
	 * 延迟时间
	 */
	private long delay;

	/**
	 * 任务
	 */
	private TimerTaskRunnable timerTaskRunnable;

	/**
	 * @param delay 延迟多少时间(毫秒)
	 * @param taskRunnable 任务到期时执行的线程
	 */
	public TimerTask(long delay, TimerTaskRunnable taskRunnable) {
		this.delay = delay;
		this.timerTaskRunnable = taskRunnable;
	}

	public TimerTaskRunnable getTimerTaskRunnable() {
		return timerTaskRunnable;
	}

	public long getDelayMs() {
		return delay;
	}

}
