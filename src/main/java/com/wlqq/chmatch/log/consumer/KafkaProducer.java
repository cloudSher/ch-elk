//package com.wlqq.chmatch.log.consumer;
//
//import kafka.javaapi.producer.Producer;
//import kafka.producer.KeyedMessage;
//import kafka.producer.ProducerConfig;
//
//import java.util.Properties;
//
///**
// * Created by wei.zhao on 2017/9/21.
// */
//public class KafkaProducer {
//
//    public static void main(String args[]){
//        Properties props = new Properties();
//        props.setProperty("metadata.broker.list","localhost:9092");
//        props.setProperty("serializer.class","kafka.serializer.StringEncoder");
//        props.put("request.required.acks","1");
//        ProducerConfig config = new ProducerConfig(props);
//        //创建生产这对象
//        Producer<String, String> producer = new Producer<String, String>(config);
//        //生成消息
//        KeyedMessage<String, String> data = new KeyedMessage<String, String>("test","test-kafka");
//        try {
//            int i =1;
//            while(i < 100){
//                //发送消息
//                producer.send(data);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        producer.close();
//    }
//
//
//}
