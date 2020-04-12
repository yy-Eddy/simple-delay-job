package com.github.yyeddy.sdj.db;

import java.util.List;

/**
 * 数据源操作
 */
public interface DelayTaskRepository {

    /**
     * 获取准备执行的任务数据
     * @return
     */
    public List<DelayTask> getReadyExecuteData();
}
