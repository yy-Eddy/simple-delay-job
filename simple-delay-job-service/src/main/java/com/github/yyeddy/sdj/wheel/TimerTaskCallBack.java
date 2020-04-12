package com.github.yyeddy.sdj.wheel;

import com.github.yyeddy.sdj.dto.DelayTaskDTO;

/**
 * @ClassName: TimerTaskCallBack.java
 * @version: v1.0.0
 * @Description:任务回调
 * @author: yangyu
 * @date: 2020年3月31日 下午4:23:29
 */
public interface TimerTaskCallBack {

	public void process(DelayTaskDTO delayTaskDTO);
}
