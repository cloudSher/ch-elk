//package com.wlqq.chmatch.log.consumer;
//
//import com.alibaba.fastjson.JSON;
//import com.wlqq.chmatch.constants.ELKUpdateConstants;
//import com.wlqq.chmatch.queue.LogBlockQueue;
//import com.wlqq.chmatch.vo.ULogData;
//import kafka.consumer.Consumer;
//import kafka.consumer.ConsumerConfig;
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.Charset;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by wei.zhao on 2017/9/21.
// */
//@Component
//public class KafkaLogConsumer{
//
//    private ConsumerConnector consumerConnector;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaLogConsumer.class);
//
//    private static final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
//
//    private String topic = "ms2_click_history";
//
//    private String zkIp = "10.2.1.1:21840";
//
//    private String group = "logstash-update";
//
//    private int threadNum = 2;
//
//    private int intervalTime = 60;
//
//    private String logIdFilterFlag = "";
//
//    private String logServiceFlag = "";
//
//    public KafkaLogConsumer(){
//        consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig(zkIp,group));
//
//    }
//
//    public static void main(String args[]){
//        KafkaLogConsumer consumer = new KafkaLogConsumer();
//        consumer.start();
//    }
//
//    /**
//     *
//     */
//    public void start() {
//        Map<String,Integer> topicMap = new HashMap<>();
//        topicMap.put(topic,threadNum);
//        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumerConnector.createMessageStreams(topicMap);
//
//        List<KafkaStream<byte[], byte[]>> kafkaStreams = messageStreams.get(topic);
//
//        ExecutorService service = Executors.newFixedThreadPool(threadNum);
//
//        for (KafkaStream stream : kafkaStreams){
//            service.submit(new ConsumerTask(stream));
//        }
//
//        schedule.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },intervalTime,intervalTime, TimeUnit.SECONDS);
//
//    }
//
//    private ConsumerConfig createConsumerConfig(String zkIp,String groupId){
//        Properties prop = new Properties();
//        prop.put("zookeeper.connect",zkIp);
//        prop.put("group.id",groupId);
//        prop.put("zookeeper.session.timeout.ms", "400");
//        prop.put("zookeeper.sync.time.ms", "200");
//        prop.put("auto.commit.interval.ms", "1000");
//        return new ConsumerConfig(prop);
//    }
//
//    private String filter(String msg){
//        if(msg == null || msg.isEmpty()){
//            return null;
//        }
//        ULogData data= JSON.parseObject(msg, ULogData.class);
//        if(data != null){
//            if(!data.getId().contains(logIdFilterFlag)){
//                return null;
//            }
//            String service = data.getService();
//            if(!service.contains(logServiceFlag)){
//                return null;
//            }
//            Map<String, Object> tags = data.getTags();
//            if(tags == null || tags.size() == 0){
//                return null;
//            }
//            Object domainId = tags.get("domainId");
//            Object userId = tags.get("userId");
//            Object originalMsgId = tags.get("originalMsgId");
//            long ts = data.getTs();
//            String result = String.format(ELKUpdateConstants.ELK_UPDATE_KEY,service,domainId,userId,originalMsgId,ts);
//            return result;
//        }
//
//        return null;
//
//    }
//
//
//
//    class ConsumerTask implements Runnable{
//
//        private KafkaStream stream;
//
//        ConsumerTask(KafkaStream stream){
//            this.stream = stream;
//        }
//
//        @Override
//        public void run() {
//            ConsumerIterator<byte[], byte[]> iterator = this.stream.iterator();
//
//            while (iterator.hasNext()){
//                try {
//                    String msg = new String(iterator.next().message(), Charset.forName("UTF-8"));
//                    LOGGER.info(" -- kafka stream, msg:{}",msg);
//                    String filter = filter(msg);
//                    if(filter == null || filter.isEmpty()){
//                        continue;
//                    }
//                    LogBlockQueue.put(filter);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
//    }
//
//
//
//
//    class ScheduledTask implements Runnable{
//
//        @Override
//        public void run() {
//
//        }
//    }
//
//}
