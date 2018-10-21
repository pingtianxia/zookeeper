package com.hyp.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.awt.datatransfer.Clipboard;
import java.util.concurrent.CountDownLatch;

/**
 * @作者 霍云平
 * @包名 com.hyp.lock
 * @日期 2018/10/20 10:01
 * @描述 10
 * 同一时间只有一个线程获取到锁（很多线程只有一个线程获取成功）
 * 其他线程都要等待并注册一个事件（等待了还要注册事件有必要吗） waitForLock()方法
 * 对客户端来说这是一个很大的资源浪费，对服务端来说也是很大的性能开销
 * 优化方案 : ZkImproveLockImpl
 */
public class ZkLockImpl extends ZkAbstractLock
{
    private CountDownLatch cdl = null;

    /**
     * 这里就是创建znode节点 如果获取锁成功就返回true
     * @return
     */
    protected boolean tryLock()
    {
        try
        {
            client.createEphemeral(node);
            return true;
        } catch (ZkNodeExistsException e)
        {
            return false;
        }
    }

    /**
     * waitForLock方法要监控前面的线程创建的 /lock 节点
     * 要知道 /lock 节点有没有被删除
     */
    protected void waitForLock()
    {

        IZkDataListener iZkDataListener = new IZkDataListener()
        {
            public void handleDataChange(String s, Object o) throws Exception
            {

            }

            public void handleDataDeleted(String s) throws Exception
            {
                if (cdl != null)
                {
                    cdl.countDown();
                }
            }
        };
        //这里注册node节点事件
        client.subscribeDataChanges(node,iZkDataListener);
        if (client.exists(node)){
            try
            {
                cdl = new CountDownLatch(1);
                //这里是要等待，只有当前面的线程释放锁，也就是前面的线程的zookeeper的回话失效
                cdl.await();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        client.unsubscribeDataChanges(node,iZkDataListener);
    }
}