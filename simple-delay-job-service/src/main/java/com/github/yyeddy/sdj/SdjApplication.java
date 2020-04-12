package com.github.yyeddy.sdj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@ComponentScan(basePackages = {"com.github.yyeddy.sdj","com.github.yyeddy.scb"})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class )
public class SdjApplication {

    public static void main(String[] args) {
        SpringApplication.run(SdjApplication.class, args);
    }

}
