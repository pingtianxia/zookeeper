package com.hyp.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/29 20:01
 * @描述 10
 */
public class LeaderSelectorAdapter extends LeaderSelectorListenerAdapter implements Cloneable {

    private static LeaderSelector leaderSelector;
    private String name;
    // 对个客户端在选举的时候 统计一下有多少个客户端选举leader成功
    private final AtomicInteger leaderCount = new AtomicInteger();

    public LeaderSelectorAdapter(CuratorFramework client,String name,String path) {
        this.name = name;
        this.leaderSelector = new LeaderSelector(client,path,this);
        /**
         * 由于网络原因，客户端与服务端之间断开连接，会释放这把锁,删除临时节点（无法保证网络一直是畅通的）
         *  autoRequeue 如果出现这种异常他还会重新的选举
         * 如果客户端释放了锁，还是会重新选举的
         */
        leaderSelector.autoRequeue();
    }

    // 如果获取到master节点，那么就会执行这个回掉方法
    public void takeLeadership(CuratorFramework client) throws Exception {
        // 这个方法做一些自己的业务逻辑，获取到master的节点之后才有权限去做
        System.out.println(name + "这个客户端master选举成功！现在是leader。");
        System.out.println(name + "现在是leader"+leaderCount.getAndIncrement());

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println(name + "模拟线程中断");
            Thread.currentThread().interrupt();
        }finally {
            System.out.println(name + "==========================");
        }
    }

    // 这个方法就是客户端开始选举master
    public static void start(){
        leaderSelector.start();
    }
    // 释放资源
    public static void close(){
        leaderSelector.close();
    }
}
