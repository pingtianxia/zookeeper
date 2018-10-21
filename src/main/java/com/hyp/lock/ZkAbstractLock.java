package com.hyp.lock;

import org.I0Itec.zkclient.ZkClient;

/**
 * @作者 霍云平
 * @包名 com.hyp.lock
 * @日期 2018/10/20 9:46
 * @描述 10
 */
public abstract class ZkAbstractLock implements Lock
{
//    protected static String connectStr = "192.168.80.134:2181,192.168.80.135:2181,192.168.80.136:2181";
    protected static String connectStr = "192.168.80.200:2181";
    protected static String node = "/lock";
    protected ZkClient client = new ZkClient(connectStr);


    protected abstract boolean tryLock();
    protected abstract void waitForLock();

    /**
     * 获取锁
     * 在高并发下这个只有一个线程获取锁成功
     * 其他线程获取不到锁就等待
     *
     * 1、线程先去试着获取锁
     * 2、如果获取锁失败，等待，等待完之后再去获取锁
     */
    public void lock()
    {
        if (tryLock()){
            System.out.println(Thread.currentThread().getName()+"------》获取锁成功");
        }else{
            waitForLock();
            lock();
        }
    }
    /**
     * 释放锁
     */
    public void unLock()
    {
        client.close();
    }
}
