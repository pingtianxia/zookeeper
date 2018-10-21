package com.hyp.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @作者 霍云平
 * @包名 com.hyp.lock
 * @日期 2018/10/20 8:48
 * @描述 10
 */
public class OrderService implements Runnable
{
    Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static Integer count = 60;

    private static CountDownLatch cdl = new CountDownLatch(count);

    private OrderNumFactory factory = new OrderNumFactory();

//    private Lock lock = new ZkLockImpl();
    private Lock lock = new ZkImproveLockImpl();
    public void run()
    {
        try
        {
            cdl.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        createOrderNum();
    }

    public void createOrderNum(){
        lock.lock();
        String orderNum = factory.createOrderNum();
        System.out.println(Thread.currentThread().getName()+"创建了订单号-----》【"+orderNum+"】");
        lock.unLock();
    }

    public static void main(String[] args)
    {
        for (int i = 0; i < count; i++)
        {
            new Thread(new OrderService()).start();
            cdl.countDown();
        }
    }

}
