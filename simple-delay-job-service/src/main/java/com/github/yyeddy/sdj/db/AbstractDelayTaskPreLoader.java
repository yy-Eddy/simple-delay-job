package com.github.yyeddy.sdj.db;

import com.github.yyeddy.sdj.configuration.DelayJobConfig;
import com.github.yyeddy.sdj.db.mongo.MongoDelayTaskPreLoader;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDelayTaskPreLoader implements DelayTaskPreLoader, InitializingBean, DisposableBean {

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public synchronized void destroy() throws Exception {
        scheduledExecutorService.shutdown();
    }

    @Override
    public synchronized void afterPropertiesSet() throws Exception {
        //创建一个固定执行周期的线程
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        DelayJobConfig delayJobConfig = getDelayJobConfig();
        Runnable runnable = getRunnable();
        this.scheduledExecutorService.scheduleAtFixedRate(runnable,1,delayJobConfig.getTick() * delayJobConfig.getWheel(), TimeUnit.MILLISECONDS);
    }

    protected abstract DelayJobConfig getDelayJobConfig();

    protected abstract Runnable getRunnable();

}