package com.zuzuche.rpc.client.discover;

import com.zuzuche.rpc.client.ConnectManager;
import com.zuzuche.rpc.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhouj
 * @since 2020-08-03
 */
@Component
@Slf4j
public class ServiceDiscover implements InitializingBean {

    @Autowired
    private DiscoverProperties discoverProperties;

    private CountDownLatch countDown = new CountDownLatch(1);

    public volatile List<String> nodeList = new ArrayList<String>();

    @Override
    public void afterPropertiesSet() throws Exception {
        ZooKeeper zooKeeper = connect();
        if (zooKeeper != null) {
            watchNodes(zooKeeper);
        }
    }

    public ZooKeeper connect() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(discoverProperties.getAddress(), 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        countDown.countDown();
                    }
                }
            });
            countDown.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }

    public void watchNodes(final ZooKeeper zooKeeper) {
        try {
            List<String> nodes = zooKeeper.getChildren(Constant.REGISTR, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)) {
                        log.info("zookeeper节点变更,触发更新handler");
                        watchNodes(zooKeeper);
                    }
                }
            });
            for (String node : nodes) {
                byte[] data = zooKeeper.getData(Constant.REGISTR + "/" + node, false, null);
                String address = new String(data);
                log.info("address:{}", address);
                nodeList.add(address);
            }
            ConnectManager.getInstance().updateServices(nodeList);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
