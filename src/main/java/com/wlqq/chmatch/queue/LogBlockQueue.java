package com.wlqq.chmatch.queue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by wei.zhao on 2017/9/22.
 */
public class LogBlockQueue {

    private static final int QUEUE_MAX = 10000;

    private static ArrayBlockingQueue queue = new ArrayBlockingQueue(QUEUE_MAX);


    public static void put(Object obj) throws InterruptedException {
        queue.put(obj);
    }

    public static Object poll(){
        return queue.poll();
    }

}
