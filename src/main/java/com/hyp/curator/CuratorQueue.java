package com.hyp.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;

import java.util.concurrent.TimeUnit;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/28 10:40
 * @描述 10
 */
public class CuratorQueue {
    private static final String path = "/curator_Queue";
    public static void main(String[] args) {
        try {
            CuratorFramework clientA = CuratorUtil.getInnerClass();
            clientA.start();
            CuratorFramework clientB = CuratorUtil.getInnerClass();
            clientB.create();

            DistributedQueue<String> queueA;
            QueueBuilder<String> consumerA = QueueBuilder.builder(clientA, createQueueConsumer("A"), createQueueSerializer(), path);
            queueA = consumerA.buildQueue();
            queueA.start();
            
            DistributedQueue<String> queueB;
            QueueBuilder<String> consumerB = QueueBuilder.builder(clientA, createQueueConsumer("B"), createQueueSerializer(), path);
            queueB = consumerB.buildQueue();
            queueB.start();

            for (int i = 0; i < 100; i++) {
                queueA.put("平天下-A-"+i);
                TimeUnit.SECONDS.sleep(1);
                queueB.put("平天下-B-"+i);
            }
            TimeUnit.SECONDS.sleep(10);
            queueA.close();
            queueB.close();
            clientA.create();
            clientB.close();
            System.out.println("完成消费----------》");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static QueueSerializer<String> createQueueSerializer(){
        QueueSerializer<String> queueSerializer = new QueueSerializer<String>() {
            public byte[] serialize(String s) {
                return s.getBytes();
            }

            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };
        return queueSerializer;
    }



    public static QueueConsumer<String> createQueueConsumer(final String name){
        QueueConsumer<String> queueConsumer = new QueueConsumer<String>() {
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println("连接状态改变-----》"+newState.name());
            }

            public void consumeMessage(String s) throws Exception {
                System.out.println("消费[【"+name+"】队列的消息---------》"+s);
            }
        };
        return queueConsumer;
    }
}
