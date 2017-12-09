package com.foxx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 作者：wangsy
 * 日期：2016/7/6 15:31
 * 描述：If you need to run some specific code once the SpringApplication has started, you can implement
 * the ApplicationRunner or CommandLineRunner interfaces. Both interfaces work in the same
 * way and offer a single run method which will be called just before SpringApplication.run(…)
 * completes.
 */
@Component
@Order(1)
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MyCommandLineRunner1 implements CommandLineRunner {

    @Value("${name}")
    private String name;

    private String host;
    private int port;
    private String database;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("after springApplication.run() completes 1111111111111111");
        System.out.println("@Value注入属性值， name: " + name);
        System.out.println("@ConfigurationProperties注入属性值， host: " + host + ", port: " + port + ", database: " + database);
    }

    public String getHost() {
        return host;
    }

        public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
