package com.hyp.queue;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @作者 霍云平
 * @包名 com.hyp.queue
 * @日期 2018/10/21 14:53
 * @描述 10
 * 这个是分布式的同步队列
 * 当队列里面的元素满了以后做一些事情
 * 这个模拟的是一个同步队列
 */
public class ZkQueue
{
    private Logger log = LoggerFactory.getLogger(ZkQueue.class);
    private static String path = "/queue";

    //这个节点代表我们要出发了..  这个节点是用来，队列满的时候就创建出这个队列
    private static String start = "/start";
    // 线程数，比如说：满50个人开始干某事
    private Integer limit;

    private ZooKeeper client = ZookeeperClientUtil.getClient(this);
    public ZkQueue(Integer limit)
    {
        this.limit = limit;
        init();
    }

    /**
     * 这个方法用来通知做事情
     */
    public void doSomething() {
        System.out.println(Thread.currentThread().getName() +
                "--> 队列满了，人员到齐了，我们该出发了-----》GO ！");
    }

    private void init(){
        try
        {
            if(client.exists(path,false) == null){
                client.create(path,"queue节点".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (KeeperException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 往队列里面加元素
     */
    public void push(String data){
        /**
         * 队列还没有满
         */
        if (limit > getSize()){
            try
            {
                client.create(path+"/",data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
                //再次判断队列是否满了
                if(limit > getSize()){
                    if (client.exists(start, false) != null) {
                        client.delete(start, -1);
                    }
                } else {
                    //如果队列满了,创建start节点
                    Stat stat = client.exists(start, false);
                    System.out.println(stat);
                    client.create(start, data.getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                }
            } catch (KeeperException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }else {
            //如果队列满了,创建start节点
            try {
                client.exists(start, false);
                client.create(start, data.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


//    public void push(String data) {
//        try {
//            String currentPath = client.create(path + "/", data.getBytes(),
//                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
//                    CreateMode.EPHEMERAL_SEQUENTIAL);
//            //队列还没有满
//            int size = getSize();
//            System.out.println("queue size: " + size);
//            //队列已满
//            if (limit <= size) {
//                List<String> childens = client.getChildren(path, false);
//                Collections.sort(childens);
//                int i = Collections.binarySearch(childens, currentPath.substring(path.length() + 1));
//                if (i == (limit - 1)) {
//                    System.out.println("队列已满~~~~");
//                    client.create(start, data.getBytes(),
//                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
//                            CreateMode.PERSISTENT);
//                }
//            }
//        } catch (KeeperException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 获取path下面的的子节点个数
     * @return
     */
    public Integer getSize(){
        try
        {
            int size = client.getChildren(path, false).size();
            System.out.println("getSize方法中的子节点的个数size-----》"+size);
            return size;
        } catch (KeeperException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
