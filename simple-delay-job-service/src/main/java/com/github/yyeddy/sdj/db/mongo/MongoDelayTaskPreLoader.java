package com.github.yyeddy.sdj.db.mongo;

import com.alibaba.fastjson.JSON;
import com.github.yyeddy.sdj.configuration.DelayJobConfig;
import com.github.yyeddy.sdj.db.AbstractDelayTaskPreLoader;
import com.github.yyeddy.sdj.db.DelayTask;
import com.github.yyeddy.sdj.dto.DelayTaskDTO;
import com.github.yyeddy.sdj.wheel.Timer;
import com.github.yyeddy.sdj.wheel.TimerTask;
import com.github.yyeddy.sdj.wheel.TimerTaskCallBack;
import com.github.yyeddy.sdj.wheel.TimerTaskRunnable;
import com.github.yyeddy.sdj.wheel.kafka.KafkaTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MongoDelayTaskPreLoader extends AbstractDelayTaskPreLoader {

    private final Logger logger = LoggerFactory.getLogger(MongoDelayTaskPreLoader.class);

    @Autowired
    private DelayJobConfig delayJobConfig;

    @Autowired
    private MongoRepository mongoRepository;

    @Override
    protected DelayJobConfig getDelayJobConfig() {
        return this.delayJobConfig;
    }

    @Override
    protected Runnable getRunnable() {
        return new ScheduleTask();
    }


    public static void main(String[] args) {
        System.out.println(TimeUnit.SECONDS.toMillis(2));
    }
    class ScheduleTask implements Runnable{

        @Override
        public void run() {
            try {
                //计算当前时间处于哪个数据表中
                Timer timer = new KafkaTimer(delayJobConfig.getTick(),delayJobConfig.getWheel(), delayJobConfig.getTick() * delayJobConfig.getWheel() + 600);
                List<DelayTask> taskList = mongoRepository.getReadyExecuteData();
                for (DelayTask delayTask : taskList){
                    DelayTaskDTO delayTaskDTO = new DelayTaskDTO();
                    delayTaskDTO.setBusinessKey(delayTask.getBusinessKey());
                    delayTaskDTO.setTaskId(delayTask.getTaskId());
                    delayTaskDTO.setData(delayTask.getData());
                    delayTaskDTO.setRunTimestamp(delayTask.getRunTimestamp());

                    TimerTaskCallBack timerTaskCallBack = new TimerTaskCallBack() {
                        @Override
                        public void process(DelayTaskDTO delayTaskDTO) {
                            try {
                                logger.info("延迟任务到期回调:{}",JSON.toJSONString(delayTaskDTO));
                            }catch (Exception e){
                                logger.error("延迟任务到期回调异常",e);
                            }
                        }
                    };
                    //计算到期的毫秒时间
                    long nowTimestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    TimerTaskRunnable taskRunnable = new TimerTaskRunnable(delayTaskDTO, timerTaskCallBack);
                    long delay = delayTask.getRunTimestamp() - nowTimestamp;
                    //添加任务
                    TimerTask timerTask = new TimerTask(delay,taskRunnable);
                    timer.addTask(timerTask);
                    logger.info("延迟任务添加完毕 数据ID是:{} 执行时间是:{} 剩余毫秒时间:{}",delayTask.getId(),delayTask.getRunDateTime(),delay);
                }
            }catch (Exception e){
                logger.error("定时周期线程执行异常",e);
            }
        }
    }
}
