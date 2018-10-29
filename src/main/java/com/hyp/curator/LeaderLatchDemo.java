package com.hyp.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/29 20:57
 * @描述 10
 * 一旦选举出Leader，除非有客户端挂掉重新触发选举，否则不会交出领导权。
 */
public class LeaderLatchDemo {
    protected static String PATH = "/francis/leader";

    private static final int CLIENT_QTY = 10;

    public static void main(String[] args) {
        List<CuratorFramework> clients = new ArrayList<CuratorFramework>();
        List<LeaderLatch> examples = new ArrayList<LeaderLatch>();

        try {
            for (int i = 0; i <CLIENT_QTY ; i++) {
                CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.80.200:2181",new ExponentialBackoffRetry(20000, 3));
                clients.add(client);
                LeaderLatch latch = new LeaderLatch(client, PATH, "Client #"  + i);
                latch.addListener(new LeaderLatchListener() {
                    public void isLeader() {
                        System.out.println("I am Leader");
                    }

                    public void notLeader() {
                        System.out.println("I am not Leader");
                    }
                });
                examples.add(latch);
                client.start();
                latch.start();
            }
            Thread.sleep(10000);
            LeaderLatch currentLeader = null;
            for (LeaderLatch latch : examples) {
                if (latch.hasLeadership()) {
                    currentLeader = latch;
                }
            }
            System.out.println("current leader is " + currentLeader.getId());
            System.out.println("release the leader " + currentLeader.getId());
            //释放领导权
            currentLeader.close();
            Thread.sleep(5000);
            for (LeaderLatch latch : examples) {
                if (latch.hasLeadership()) {
                    currentLeader = latch;
                }
            }
            System.out.println("current leader is " + currentLeader.getId());
            System.out.println("release the leader " + currentLeader.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
         /*finally {
            for (LeaderLatch latch : examples) {
                if (null != latch.getState())
                    CloseableUtils.closeQuietly(latch);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }*/
    }
}
