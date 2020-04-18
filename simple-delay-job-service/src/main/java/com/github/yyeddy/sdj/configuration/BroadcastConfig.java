package com.github.yyeddy.sdj.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class BroadcastConfig {

    /*@Bean(initMethod = "start",destroyMethod = "stop")
    public ClusterBroadcast jedisBroadcast(){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, "127.0.0.1", 6379, 1000, "");
        ClusterBroadcast broadcast = new JedisBroadcast(pool);
        return broadcast;
    }*/
}
