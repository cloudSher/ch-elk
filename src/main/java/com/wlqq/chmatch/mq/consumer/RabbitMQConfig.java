package com.wlqq.chmatch.mq.consumer;

/**
 * Created by wei.zhao on 2017/9/26.
 */

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
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
    private String address;

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
        factory.setAddresses(address);
        factory.setVirtualHost(virtualHost);
        return factory;
    }


    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange("",true,false);
    }


    @Bean
    public Queue queue(){
        return new Queue("",true);
    }

    @Bean
    public Binding exchangeBind(){
        return BindingBuilder.bind(queue()).to(exchange());
    }


}
