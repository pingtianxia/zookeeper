package com.hyp.queue;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

/**
 * @作者 霍云平
 * @包名 com.hyp.queue
 * @日期 2018/10/26 23:11
 * @描述 10
 */
public class FIFOTest {
    private volatile boolean stop = true;

    public static void main(String[] args) {

        new FIFOTest().start();
    }

    private void start(){
        ZkClient client = new ZkClient("192.168.80.200:2181");

        FIFOQueue queue = new FIFOQueue(client,"/queue");

        int count = 10;

        new Thread(new provider(queue)).start();
        new Thread(new Consumer(queue)).start();

        for (int i = 0; i < 100; i++) {
            if (i==90)
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            TimeUnit.MICROSECONDS.sleep(3);
                            stop = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
        }
    }
    class Consumer implements Runnable{

        private FIFOQueue queue;

        public Consumer(FIFOQueue queue) {
            this.queue = queue;
        }
        public void run() {
            while (stop){
                try {
                    TimeUnit.MICROSECONDS.sleep(100);
                    Object poll = queue.poll();
                    System.out.println(poll);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class provider implements Runnable{
        private FIFOQueue queue;

        public provider(FIFOQueue queue) {
            this.queue = queue;
        }

        public void run() {
            while (stop){
                try {
                    queue.push(Thread.currentThread().getName());
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
