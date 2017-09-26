package com.wlqq.chmatch.mq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by wei.zhao on 2017/9/26.
 */
@Component
public class RabbitMQConsumer {


    @RabbitListener(queues = "")
    private void processMessage(){

    }
}
