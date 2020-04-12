package com.github.yyeddy.sdj.db.mongo;

import com.alibaba.fastjson.JSON;
import com.github.yyeddy.sdj.SdjApplication;
import com.github.yyeddy.sdj.db.DelayTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SdjApplication.class)
public class MongoRepositoryTest {

    @Autowired
    private MongoRepository mongoRepository;

    @Test
    public void testGetReadyExecuteData(){
        try {
            List<DelayTask> dataList = mongoRepository.getReadyExecuteData();
            System.out.println("待运行的数据是："+ JSON.toJSONString(dataList));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
