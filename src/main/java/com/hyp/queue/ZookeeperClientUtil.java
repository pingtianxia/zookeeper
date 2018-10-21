package com.hyp.queue;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @作者 霍云平
 * @包名 com.hyp.queue
 * @日期 2018/10/21 14:47
 * @描述 10
 * zookeeper客户端工具类
 */
public class ZookeeperClientUtil
{
    private static String connetStr = "192.168.80.200:2181";

    public static ZooKeeper getClient(ZkQueue zkQueue){
        CountDownLatch cdl = new CountDownLatch(1);
        try
        {
            ZooKeeper client = new ZooKeeper(connetStr,5000,new WatcherDemo(cdl,zkQueue));
            cdl.await();
            // true 注册一个Watcher事件
            client.exists("/start",true);
            return client;
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (KeeperException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
