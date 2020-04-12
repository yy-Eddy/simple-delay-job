package com.github.yyeddy.sdj.db.mongo.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PreDestroy;


@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,MongoDataAutoConfiguration.class})
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDbFactory")
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoConfig {

    private final MongoClientOptions options;

    private final MongoClientFactory factory;

    private MongoProperties properties;

    private MongoClient mongo;

    private MongoTemplate mongoTemplate;

    public MongoConfig(MongoProperties properties, ObjectProvider<MongoClientOptions> options,
                                  Environment environment) {
        this.properties = properties;
        this.options = options.getIfAvailable();
        this.factory = new MongoClientFactory(properties, environment);
    }

    @PreDestroy
    public void close() {
        if (this.mongo != null) {
            this.mongo.close();
        }
    }

    @Bean
    @ConditionalOnMissingBean(type = { "com.mongodb.MongoClient", "com.mongodb.client.MongoClient" })
    public MongoClient mongo() {
        this.mongo = this.factory.createMongoClient(this.options);
        return this.mongo;
    }

    @Bean
    @ConditionalOnMissingBean(MongoTemplate.class)
    @DependsOn({"mongo"})
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(this.mongo,properties.getMongoClientDatabase());
        mongoTemplate.setWriteResultChecking( WriteResultChecking.EXCEPTION );
        this.mongoTemplate = mongoTemplate;
        return mongoTemplate;
    }
}
