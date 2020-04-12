package com.github.yyeddy.sdj.service.impl;

import com.github.yyeddy.sdj.db.DelayTask;
import com.github.yyeddy.sdj.db.mongo.MongoRepository;
import com.github.yyeddy.sdj.dto.DelayTaskDTO;
import com.github.yyeddy.sdj.service.InnerDelayTaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service("delayTaskService")
public class DelayTaskServiceImpl implements InnerDelayTaskService {

    @Autowired
    private MongoRepository mongoRepository;

    @Override
    public Boolean addTask(DelayTaskDTO delayTaskDTO) {
        if(StringUtils.isEmpty(delayTaskDTO.getBusinessKey())){
            throw new NullPointerException("业务标识不能为空");
        }
        if(StringUtils.isEmpty(delayTaskDTO.getTaskId())){
            throw new NullPointerException("任务ID不能为空");
        }
        if(delayTaskDTO.getRunTimestamp() == null){
            throw new NullPointerException("任务的执行时间不能为空");
        }
        //后续使用bean复制框架 Dozer
        DelayTask delayTask = new DelayTask();
        delayTask.setBusinessKey(delayTaskDTO.getBusinessKey());
        delayTask.setTaskId(delayTaskDTO.getTaskId());
        delayTask.setRunTimestamp(delayTaskDTO.getRunTimestamp());
        delayTask.setData(delayTaskDTO.getData());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        LocalDateTime createDateTime = LocalDateTime.now();
        delayTask.setTaskState(1);//等待中
        delayTask.setCreateDateTime(createDateTime.format(formatter));
        long createTimestamp = createDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        delayTask.setCreateTimestamp(createTimestamp);
        LocalDateTime runDateTime = Instant.ofEpochMilli(delayTask.getRunTimestamp()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        delayTask.setRunDateTime(runDateTime.format(formatter));
        return mongoRepository.addDelayTask(delayTask);
    }
}
