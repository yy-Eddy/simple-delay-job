package com.github.yyeddy.sdj.service;

import com.github.yyeddy.sdj.dto.DelayTaskDTO;

/**
 * 延迟任务接口
 */
public interface DelayTaskService {

    public Boolean addTask(DelayTaskDTO delayTaskDTO);

}
