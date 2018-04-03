package com.bigdata.zookeeper.distributed;

import java.util.ArrayList;
import java.util.List;

/**
 * DistributeCache
 *
 * @author Lee
 * @date 2018/4/2
 * description:
 * Created by Lee on 2018/4/2.
 */
public class DistributeCache {

    private static List<String> msgCache = new ArrayList<>();

    static class MsgConsumer extends Thread {

        @Override
        public void run() {

            while (null != msgCache && msgCache.size() > 0) {

                String lock = ZookeeperLock.getInstance().getLock();
                if (null == msgCache || msgCache.size() <= 0) {
                    return;
                }

                String msg = msgCache.get(0);
                System.out.println(Thread.currentThread().getName() + "consume msg: " + msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                msgCache.remove(msg);
                ZookeeperLock.getInstance().releaseLock(lock);
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            msgCache.add("msg"+i);
        }
        MsgConsumer consumer1 = new MsgConsumer();
        MsgConsumer consumer2 = new MsgConsumer();

        consumer1.start();
        consumer2.start();
    }
}

