package com.github.yyeddy.sdj.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sdj")
public class DelayJobConfig {

    /**
     * 数据的表名前缀
     * 默认:job_delay
     */
    @Value("${sdj.data.table.name.prefix:job_delay}")
    private String tableNamePrefix;

    /**
     * 每个表分片的时间周期(小时)
     * 默认1个小时
     * 注意:表的分片周期一定是 tick * wheel 的倍数
     */
    //@Value("${sdj.data.table.sharding.time:1}")
    private Integer tableShardingTime = 1;

    /**
     * 每个刻度的时间(毫秒)
     * 默认:1分钟
     */
    @Value("${sdj.time.tick:60000}")
    private Long tick;

    /**
     * 总共有多少个刻度
     * 默认:60个
     */
    @Value("${sdj.time.wheel:60}")
    private Integer wheel;



    public String getTableNamePrefix() {
        return tableNamePrefix;
    }

    public Integer getTableShardingTime() {
        return tableShardingTime;
    }

    public Long getTick() {
        return tick;
    }

    public Integer getWheel() {
        return wheel;
    }

}
