package com.bigdata.java.queue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class TestBlockingQueueConsumer implements Runnable {

    private BlockingQueue<String> queue;

    public TestBlockingQueueConsumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    Random random = new Random();


    public void run() {

        try {
            Thread.sleep(random.nextInt(10));

            System.out.println(Thread.currentThread().getName() + " try get a message");
            String task = queue.take();  // 阻塞方法. 如果队列为空 会一直等待
            int msgNum = queue.remainingCapacity();
            System.out.println(Thread.currentThread().getName() + " get a message " + task);
            System.out.println("剩余消息个数: "+ msgNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
