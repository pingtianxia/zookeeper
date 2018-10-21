package com.hyp.zooleeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import sun.misc.Cleaner;

import java.util.concurrent.CountDownLatch;

/**
 * @author hyp
 * @date 2018/10/3 15:17
 */
public class ZookeeperDemo
{
    private static String connectStr = "192.168.80.134:2181,192.168.80.135:2181,192.168.80.136:2181";
    private static CountDownLatch cdl = new CountDownLatch(1);
    public static void main(String[] args) throws Exception
    {

        final ZooKeeper client = new ZooKeeper(connectStr, 5000, new WatcherDemo(cdl));
        cdl.await();
        System.out.println(client.getState());

        createNode("/huoNode1","huoNode",client);
        changeNode("/huoNode1","songxiangyun",client);
        deleteNode("/huoNode1",client);
    }

    public static void deleteNode(String path,ZooKeeper client) throws Exception{
        client.exists(path,true);
        client.delete(path,-1);
    }

    public static void changeNode(String path,String data,ZooKeeper client) throws  Exception{
//        client.exists(path,true);
        Stat stat = new Stat();
        byte[] data1 = client.getData(path, true, stat);
        System.out.println(stat);
        System.out.println(data1);
        // version 版本号 -1 代表这什么版本都要修改
        client.setData(path,data.getBytes(),-1);
    }

    public static void createNode(String path,String data,ZooKeeper client) throws  Exception{
        // 注册事件  如果是true就代表着 path 注册一个WatcherDemo 事件
        client.exists(path,true);
        // client.create 方法传传入方法说明：节点 数据 权限验证 创建节点的类型
        client.create(path,data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
    }
}
