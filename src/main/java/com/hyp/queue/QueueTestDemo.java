package com.hyp.queue;

/**
 * @作者 霍云平
 * @包名 com.hyp.queue
 * @日期 2018/10/21 15:30
 * @描述 10
 */
public class QueueTestDemo
{
    public static void main(String[] args)
    {
        final Integer limit = 10;

        for (int i = 0; i < limit; i++)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    ZkQueue zkQueue = new ZkQueue(limit);
                    zkQueue.push("people");
                }
            }).start();
        }
    }
}
