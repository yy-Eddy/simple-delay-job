package com.github.yyeddy.sdj.db;

import com.github.yyeddy.sdj.configuration.DelayJobConfig;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDelayTaskRepository implements DelayTaskRepository{

    protected abstract DelayJobConfig getDelayJobConfig();

    protected abstract Set<String> getNearTableNames();

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    protected String getReadyExecuteDataTableName(){
        DelayJobConfig delayJobConfig = getDelayJobConfig();
        Set<String> tableNames = getNearTableNames();
        LocalDate nowDate = LocalDate.now();
        String tableName = null;
        for(String tn : tableNames){
            //TODO 原本是要做 随意指定分区的时间分表的 目前还没想好 先保留部分代码
            String tableNameDateString = parseTableNameSuffix(tn, delayJobConfig.getTableNamePrefix());
            LocalDate localDate = LocalDate.parse(tableNameDateString, dateTimeFormatter);
            if(localDate.isEqual(nowDate)){
                tableName = tn;
                break;
            }
        }
        if(StringUtils.isEmpty(tableName)){
            //还没建立过表
            tableName = initTableName(delayJobConfig.getTableNamePrefix());
        }else{
            tableName = calculateNextTableName(tableName,delayJobConfig.getTableNamePrefix(),delayJobConfig.getTableShardingTime());
        }
        return tableName;
    }

    public long getBetweenEndTimestamp(){
        DelayJobConfig delayJobConfig = getDelayJobConfig();
        long nanos = TimeUnit.MILLISECONDS.toNanos(delayJobConfig.getTick() * delayJobConfig.getWheel());
        LocalDateTime localDateTime = LocalDateTime.now().plusNanos(nanos);
        return getTimestamp(localDateTime);
    }

    protected static long getTimestamp(LocalDateTime localDateTime){
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    protected static String initTableName(String tableNamePrefix){
        String date = LocalDate.now().format(dateTimeFormatter);
        return StringUtils.join(tableNamePrefix,"_",date);
    }

    protected static String calculateNextTableName(String currentTableName,String tableNamePrefix,Integer tableShardingTime){
        String currentTableNameSuffix = parseTableNameSuffix(currentTableName, tableNamePrefix);
        LocalDate currentTableNameDate = LocalDate.parse(currentTableNameSuffix, dateTimeFormatter);
        LocalDate nextDate = currentTableNameDate.plusDays(tableShardingTime);
        LocalDate nowDate = LocalDate.now();
        boolean isAfter = nextDate.isAfter(nowDate);
        boolean isToday = nextDate.isEqual(nowDate);
        if(isAfter || isToday){
            //还在当前分片周期内
            return currentTableName;
        }else{
            //下一个周期
           return initTableName(tableNamePrefix);
        }
    }

    private static String parseTableNameSuffix(String tableName,String tableNamePrefix){
        String[] data = StringUtils.split(tableName, tableNamePrefix);
        return data[0];
    }

    public static void main(String[] args) {
        long nanos = TimeUnit.MILLISECONDS.toNanos(10 * 2);
        LocalDateTime localDateTime = LocalDateTime.now().plusNanos(nanos);
        long times =  localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println(times);
    }
}
