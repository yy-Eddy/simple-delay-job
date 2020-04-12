package com.github.yyeddy.sdj.service;

import com.github.yyeddy.sdj.SdjApplication;
import com.github.yyeddy.sdj.dto.DelayTaskDTO;
import com.github.yyeddy.sdj.service.DelayTaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SdjApplication.class)
public class DelayTaskServiceTest {

    @Autowired
    private DelayTaskService delayTaskService;


    @Test
    public void testAddTask(){
        try {
            DelayTaskDTO delayTaskDTO = new DelayTaskDTO();
            delayTaskDTO.setBusinessKey("Eddy");
            int randomId = ThreadLocalRandom.current().nextInt(10000, 10000000);
            delayTaskDTO.setTaskId(String.valueOf(randomId));
            delayTaskDTO.setData("你好 helloworld !!!");
            LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(2);
            long runTimestamp = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            delayTaskDTO.setRunTimestamp(runTimestamp);
            Boolean addRes = delayTaskService.addTask(delayTaskDTO);
            System.out.println("任务添加结果:" + addRes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
