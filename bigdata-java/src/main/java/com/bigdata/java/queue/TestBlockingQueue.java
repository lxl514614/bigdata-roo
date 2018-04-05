package com.bigdata.java.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TestBlockingQueue {

    public static void main(String[] args) {
        BlockingQueue<String> queue = new LinkedBlockingDeque<String>(5);

        TestBlockingQueueConsumer consumer = new TestBlockingQueueConsumer(queue);
        TestBlockingQueueProducer producer = new TestBlockingQueueProducer(queue);

        for (int i = 0; i < 3; i++ ) {
            new Thread(producer, "producer" + (i+1)).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(consumer, "consumer" + (i+1)).start();
        }

//        new Thread(producer, "producer" + 5).start();
    }
}
