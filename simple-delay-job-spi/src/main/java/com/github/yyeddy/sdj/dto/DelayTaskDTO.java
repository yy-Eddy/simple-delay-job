package com.github.yyeddy.sdj.dto;

import java.io.Serializable;

public class DelayTaskDTO implements Serializable {

    /**
     * 业务标示
     */
    private String businessKey;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 业务方 自有的数据,用于回调时返回过去
     */
    private String data;

    /**
     * 运行时间
     */
    private Long runTimestamp;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getRunTimestamp() {
        return runTimestamp;
    }

    public void setRunTimestamp(Long runTimestamp) {
        this.runTimestamp = runTimestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
