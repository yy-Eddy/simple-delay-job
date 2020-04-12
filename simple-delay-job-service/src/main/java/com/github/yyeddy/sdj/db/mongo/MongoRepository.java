package com.github.yyeddy.sdj.db.mongo;

import com.github.yyeddy.sdj.configuration.DelayJobConfig;
import com.github.yyeddy.sdj.db.AbstractDelayTaskRepository;
import com.github.yyeddy.sdj.db.DelayTask;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class MongoRepository extends AbstractDelayTaskRepository {


    @Autowired
    private DelayJobConfig delayJobConfig;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<DelayTask> getReadyExecuteData() {
        //获取本次数据所处的表名
        long betweenEndTimestamp = getBetweenEndTimestamp();
        String readyExecuteDataTableName = getReadyExecuteDataTableName();
        MongoCollection<Document> collection = mongoTemplate.getCollection(readyExecuteDataTableName);
        //获取当前范围内的数据
        BasicDBObject runTimestamp = new BasicDBObject();
        runTimestamp.put("$gt",getTimestamp(LocalDateTime.now()));
        runTimestamp.put("$lte",betweenEndTimestamp);
        BasicDBObject filter = new BasicDBObject("runTimestamp",runTimestamp);
        FindIterable<Document> findIterable = collection.find(filter);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        List<DelayTask> resList = new ArrayList<>(200);
        //先这样写 后续改为对象形式
        while (mongoCursor.hasNext()){
            Document doc = mongoCursor.next();
            DelayTask delayTask = new DelayTask();
            delayTask.setId(doc.getObjectId("_id").toString());
            delayTask.setBusinessKey(doc.getString("businessKey"));
            delayTask.setTaskId(doc.getString("taskId"));
            delayTask.setTaskState(doc.getInteger("taskState"));
            delayTask.setCreateTimestamp(doc.getLong("createTimestamp"));
            delayTask.setCreateDateTime(doc.getString("createDateTime"));
            delayTask.setRunTimestamp(doc.getLong("runTimestamp"));
            delayTask.setRunDateTime(doc.getString("runDateTime"));
            resList.add(delayTask);
        }
        return resList;
    }

    @Override
    protected DelayJobConfig getDelayJobConfig() {
        return this.delayJobConfig;
    }

    @Override
    protected Set<String> getNearTableNames() {
        //这种获取方式 可能会导致数据过大,后续要优化
        Set<String> collectionNames = this.mongoTemplate.getCollectionNames();
        return collectionNames;
    }

    public Boolean addDelayTask(DelayTask delayTask) {
        String readyExecuteDataTableName = getReadyExecuteDataTableName();
        mongoTemplate.insert(delayTask,readyExecuteDataTableName);
        return true;
    }
}
