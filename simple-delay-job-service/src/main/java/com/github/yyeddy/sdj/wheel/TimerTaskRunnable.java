package com.github.yyeddy.sdj.wheel;

import com.github.yyeddy.sdj.dto.DelayTaskDTO;

/**
 * @ClassName: TimerTaskRunnable.java
 * @version: v1.0.0
 * @Description:任务线程执行
 * @author: yangyu
 * @date: 2020年3月31日 下午4:23:45
 */
public class TimerTaskRunnable implements Runnable{

	/**
	 * 任务数据
	 */
	private DelayTaskDTO delayTaskDTO;
	
	/**
	 * 任务执行的回调
	 */
	private TimerTaskCallBack timerTaskCallBack;

	public TimerTaskRunnable(DelayTaskDTO delayTaskDTO, TimerTaskCallBack timerTaskCallBack) {
		super();
		this.delayTaskDTO = delayTaskDTO;
		this.timerTaskCallBack = timerTaskCallBack;
	}

	@Override
	public void run() {
		this.timerTaskCallBack.process(this.delayTaskDTO);
	}

	public DelayTaskDTO getData() {
		return delayTaskDTO;
	}

	public TimerTaskCallBack getTimerTaskCallBack() {
		return timerTaskCallBack;
	}
}
