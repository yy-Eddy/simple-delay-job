package com.github.yyeddy.sdj.wheel;

public interface Timer {

    /**
     * 添加任务
     * @param timerTask
     * @return
     */
    public Boolean addTask(TimerTask timerTask);
}
