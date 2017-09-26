package com.wlqq.chmatch.mq.consumer;

/**
 * Created by wei.zhao on 2017/9/26.
 */

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.rabbitmq")
@Component
public class RabbitMQConfig {

    private String username;
    private String password;
    private String virtualHost;
    private String host;
    private Integer port;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }


    @Bean
    public CachingConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        factory.setVirtualHost(virtualHost);
        return factory;
    }






}
