package com.zuzuche.rpc.registry;

import com.zuzuche.rpc.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Slf4j
public class ServerRegister {

    private CountDownLatch countDown = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    public ServerRegister(String address) {
        try {
            this.zooKeeper = connectServer(address);
        } catch (IOException e) {
            log.error("连接zookeeper失败:{}", e.getMessage(), e);
        }
        if (zooKeeper != null) {
            addRootNode(zooKeeper);
        }
    }

    public void registry(String data) {
        createData(zooKeeper, data);
    }

    private ZooKeeper connectServer(String address) throws IOException {
        ZooKeeper zk = null;
        zk = new ZooKeeper(address, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDown.countDown();
                }
            }
        });
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }

    public void addRootNode(ZooKeeper zk) {
        try {
            Stat s = zk.exists(Constant.REGISTR, false);
            if (s == null) {
                zk.create(Constant.REGISTR, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createData(ZooKeeper zk, String data) {
        byte[] bytes = data.getBytes();
        String path = null;
        try {
            path = zk.create(Constant.DATA, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("create zookeeper node ({} => {})", path, data);
    }
}
