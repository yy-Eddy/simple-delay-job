package com.github.yyeddy.sdj.db;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class DelayTask implements Serializable {

    private String id;

    /**
     * 业务标示
     */
    private String businessKey;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态
     */
    private Integer taskState;

    /**
     * 创建时间
     */
    private Long createTimestamp;

    /**
     * 创建时间-用于查询方便
     */
    private String createDateTime;

    /**
     * 运行时间
     */
    private Long runTimestamp;

    /**
     * 运行时间-用于查看方便
     */
    private String runDateTime;

    /**
     *
     */
    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getTaskState() {
        return taskState;
    }

    public void setTaskState(Integer taskState) {
        this.taskState = taskState;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Long getRunTimestamp() {
        return runTimestamp;
    }

    public void setRunTimestamp(Long runTimestamp) {
        this.runTimestamp = runTimestamp;
    }

    public String getRunDateTime() {
        return runDateTime;
    }

    public void setRunDateTime(String runDateTime) {
        this.runDateTime = runDateTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
