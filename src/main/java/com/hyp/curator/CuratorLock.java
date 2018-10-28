package com.hyp.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/28 10:13
 * @描述 10
 */
public class CuratorLock {
    public static void main(String[] args) {
        CuratorFramework client = CuratorUtil.getInnerClass();
        client.start();

        InterProcessMutex interProcessMutex = new InterProcessMutex(client, "/curator/lock");

        try {
            //获取锁
            interProcessMutex.acquire();
            System.out.println("获取锁成功，业务操作");
            TimeUnit.SECONDS.sleep(3);
            //释放锁
            interProcessMutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
