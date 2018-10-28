package com.hyp.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;

import javax.xml.bind.SchemaOutputResolver;
import java.util.concurrent.TimeUnit;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/27 20:12
 * @描述 10
 * 模拟事件
 */
public class CuratorEventDemo {

    public static void main(String[] args) {
        CuratorFramework curatorFramework = CuratorUtil.getInnerClass();
        String path = "/curator_Event";

        pathChildrenCacheTestAdd(curatorFramework,path,"/pathChildrenCache");
    }

    private static void pathChildrenCacheTestAdd(CuratorFramework curatorFramework,String path,String data){
        try {
            curatorFramework.start();
            pathChildrenCache(curatorFramework,path);
            CuratorDemo.creta(curatorFramework,path,data);
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path+"/child1");
            // 这里必须要睡一下，回掉打印不出来
            TimeUnit.SECONDS.sleep(5);
            /**
             * 修改之后立即删除
             * 中间需要休眠一下  TimeUnit.SECONDS.sleep(5);
             * 不然不会触发删除操作的回掉
             */
            curatorFramework.setData().withVersion(-1).forPath(path+"/child1","XXXX".getBytes());
            TimeUnit.SECONDS.sleep(5);
            curatorFramework.delete().forPath(path+"/child1");
            TimeUnit.SECONDS.sleep(5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nodeCacheTest(CuratorFramework curatorFramework,String path){
        try {
            curatorFramework.start();
            CuratorDemo.creta(curatorFramework,path,"平天下");
            nodeCache(curatorFramework,path);
            CuratorDemo.update(curatorFramework,path,"王者荣耀");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (curatorFramework != null)
                curatorFramework.close();
        }
    }

    private static void nodeCache(CuratorFramework client,String path){
        try {
            /**
             * 第一个参数客户端 第二个参数路径path 第三个参数是否压缩
             * 对一个节点的事件监控（对path节点注册事件）
             * 是一个事件模板
             * NodeCache（把修改后的数据进行缓存）
             */
            final NodeCache nodeCache = new NodeCache(client, path, false);
            nodeCache.start();
            //这个就是类似事件注册
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    System.out.println("监控节点变化，变化后的结果为："+new String(nodeCache.getCurrentData().getData()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对路径下面的子节点进行监控
     * @param client
     * @param path
     */
    private static void pathChildrenCache(CuratorFramework client,String path){
        try {
            PathChildrenCache childrenCache = new PathChildrenCache(client, path, false);
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    System.out.println("节点下面的子节点有变更");
                    switch (event.getType()){
                        case CHILD_ADDED:
                            System.out.println("添加了子节点");
                            break;
                        case CHILD_REMOVED:
                            System.out.println("删除了子节点");
                            break;
                        case CHILD_UPDATED:
                            System.out.println("修改了子节点");
                            break;
                        case INITIALIZED:
                            System.out.println("INITIALIZED 是干嘛的--1--》");
                            break;
                        case CONNECTION_LOST:
                            System.out.println("CONNECTION_LOST 是干嘛的---2---》");
                            break;
                        case CONNECTION_SUSPENDED:
                            System.out.println("CONNECTION_SUSPENDED 是干嘛的---3--》");
                            break;
                        case CONNECTION_RECONNECTED:
                            System.out.println("CONNECTION_RECONNECTED 是干嘛的--4----》");
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
