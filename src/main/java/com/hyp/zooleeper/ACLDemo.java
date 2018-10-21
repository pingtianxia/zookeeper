package com.hyp.zooleeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author hyp
 * @date 2018/10/4 10:37
 */
public class ACLDemo
{
    private static String connectStr = "192.168.80.134:2181,192.168.80.135:2181,192.168.80.136:2181";
    private static CountDownLatch cdl = new CountDownLatch(1);
    public static void main(String[] args) throws Exception
    {
        ZooKeeper client = new ZooKeeper(connectStr, 5000, new WatcherDemo(cdl));
        cdl.await();

        ACL acl1 = new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest("java:123")));
//        ACL acl2 = new ACL(ZooDefs.Perms.WRITE, new Id("ip", "192.168.80.134"));
        List<ACL> aclList = new ArrayList<ACL>();
        aclList.add(acl1);
//        aclList.add(acl2);
        String s = client.create("/huoACLNode", "huoACL".getBytes(), aclList, CreateMode.PERSISTENT);
        System.out.println(s);

        // 下面的客户端就需要授权
        ZooKeeper client1 = new ZooKeeper(connectStr, 5000, new WatcherDemo(cdl));
        client1.addAuthInfo("digest", "java:123".getBytes());

        Stat stat = client1.setData("/huoACLNode", "songxiangyunACL".getBytes(), -1);
        System.out.println(stat);

    }
}
