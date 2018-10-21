package com.hyp.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/19 22:37
 * @描述 10
 */
public class CuratorDemo
{
    private static final String zk_Node = "/curator";
    public static void main(String[] args)
    {
        /**
         * 通过客户端创建的临时节点  client.close(); 关闭后临时节点就会被删除
         */
        CuratorFramework client = CuratorUtil.getInnerClass();
        client.start();

//        createSync(client,zk_Node,"createSync");
//        createSyncExcutor(client,"/createsyncExcutor","createsyncExcutorData");
//        transation(client,zk_Node);
    }

    public static void transation(CuratorFramework client,String node){
        try
        {
            Collection<CuratorTransactionResult> transactionResults = client.inTransaction().create().withMode(CreateMode.EPHEMERAL).forPath(node, "NodeData".getBytes()).and()
                    .setData().forPath(node, "updataNodeData".getBytes()).and().commit();
            for (CuratorTransactionResult transactionResult : transactionResults)
            {
                System.out.println(transactionResult.getForPath()+"===>"+transactionResult.getResultPath()+"----->"+transactionResult.getResultStat());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (client != null)
                client.close();
        }
    }

    public static void createSyncExcutor(CuratorFramework client,String node,String data){
        ExecutorService pools = Executors.newFixedThreadPool(2);
        try
        {
            String forPath = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).inBackground(new BackgroundCallback()
            {
                public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception
                {
                    System.out.println("节点创建成功进入回掉函数----》" + curatorEvent.getName());
                }
            }, pools).forPath(node, data.getBytes());
            System.out.println("创建成功-------->"+forPath);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
//            if (client != null)
//                client.close();
        }
    }

    /**
     * inBackground这个方法就是异步运行的意思
     * @param client
     * @param node
     * @param data
     */
    public static void createSync(CuratorFramework client,String node,String data){
        try
        {
            String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).inBackground(new BackgroundCallback()
            {
                //只有当接到创建成功才会回调
                public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception
                {
                    System.out.println("节点创建成功进入回掉函数----》"+curatorEvent.getName());
                }
            }).forPath(node, data.getBytes());
        } catch (Exception e)
        {
            e.printStackTrace();
        }finally
        {
            if (client != null)
                client.close();
        }
    }



    public static void delete(CuratorFramework client,String node){
        try
        {
            Void path = client.delete().deletingChildrenIfNeeded().withVersion(-1).forPath(node);
            System.out.println("删除数据成功-------》"+path);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (client != null)
                client.close();
        }

    }

    public static void update(CuratorFramework client,String node,String data){
        try
        {
            Stat stat = client.setData().withVersion(-1).forPath(node, data.getBytes());
            System.out.println("修改数据成功--------》"+stat);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (client != null)
                client.close();
        }
    }

    public static void query(CuratorFramework client,String node){
        try
        {
            byte[] bytes = client.getData().storingStatIn(new Stat()).forPath(node);
            System.out.println("获取数据成功------------->"+new String(bytes));
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (client != null)
                client.close();
        }
    }

    public static void creta(CuratorFramework client,String node,String data){
        try
        {
            String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(node, data.getBytes());
            System.out.println("创建节点成功-----》"+path);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (client != null){
                client.close();
            }
        }
    }
}
