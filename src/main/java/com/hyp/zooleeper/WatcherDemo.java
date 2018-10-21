package com.hyp.zooleeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author hyp
 * @date 2018/10/4 9:12
 */
public class WatcherDemo implements Watcher
{
    private static CountDownLatch cdl;

    public WatcherDemo(CountDownLatch cdl){
        super();
        this.cdl = cdl;
    }

    public void process(WatchedEvent event)
    {
        if (Event.KeeperState.SyncConnected ==event.getState()){
            if(Event.EventType.None == event.getType() && event.getPath() ==null){
                System.out.println("Zookeeper 会话创建成功");
                cdl.countDown();
            }else if (Event.EventType.NodeCreated == event.getType()){
                System.out.println("触发了创建节点事件。event.getPath() ==" + event.getPath());
            }else if(Event.EventType.NodeDataChanged == event.getType()){
                System.out.println("触发了修改节点事件。event.getPath() ==" + event.getPath());
            }else if (Event.EventType.NodeDeleted == event.getType()){
                System.out.println("触发了删除节点事件。event.getPath() ==" + event.getPath());
            }else if (Event.EventType.NodeChildrenChanged == event.getType()){
                System.out.println("触发了修改子节点事件。event.getPath() ==" + event.getPath());
            }
        }
    }
}
