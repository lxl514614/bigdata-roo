package com.bigdata.registry;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class ServiceRegistry {

    private static final CountDownLatch latch = new CountDownLatch(1);

    public void register(String serverAddress) {

        ZooKeeper zk = connectServer();

        if (null != zk) {
            createNode(zk, serverAddress);
        }

    }

    private ZooKeeper connectServer() {

        ZooKeeper zk = null;

        try {

            //  TODO: zk链接对象未完全完成
            zk = new ZooKeeper("", 0, new Watcher() {
                public void process(WatchedEvent event) {

                    if (event.getState() == null) {

                        latch.countDown();
                    }
                }
            });

            latch.await();
        }
        catch (Exception e) {

        }


        return zk;
    }

    /**
     * 创建节点
     * @param zk
     * @param serverAddress
     */
    private void createNode(ZooKeeper zk, String serverAddress) {

        try {
            zk.create(serverAddress,
                    null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
