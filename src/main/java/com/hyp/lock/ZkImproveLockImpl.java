package com.hyp.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @作者 霍云平
 * @包名 com.hyp.lock
 * @日期 2018/10/21 11:27
 * @描述 10
 */
public class ZkImproveLockImpl extends ZkAbstractLock
{
    private Logger log = LoggerFactory.getLogger(ZkImproveLockImpl.class);
    //当前节点
    private String currentPath;
    //之前节点
    private String beforePath;
    private CountDownLatch cdl;

    public ZkImproveLockImpl()
    {
        if(!this.client.exists(node) ){
            this.client.createPersistent(node,"有序节点");

        }
    }

    protected boolean tryLock()
    {
        /**
         * 当前节点currentPath 没有初始化，没有赋值
         * 代表当前节点没有创建
         */
        if (currentPath ==null || currentPath.length() <=0 ){
            // 节点创建在node 节点下面 "/"
            currentPath = this.client.createEphemeralSequential(node+"/","临时有序节点");
            log.info("给当前节点currentPath赋值 -----》"+currentPath);
        }
        // 获取node下面的所有子节点
        List<String> childrens = this.client.getChildren(node);
        log.info("排序前node下面的所有子节点----【》"+childrens+"】node下面的所有子节点数量【"+childrens.size()+"】");
        // 给childrens排序 从小到大排序
        Collections.sort(childrens);
        log.info("排序后node下面的所有子节点----【》"+childrens+"】node下面的所有子节点数量【"+childrens.size()+"】");
        //这个就是判断当前用户创建的临时节点的名称是否跟childrens里面最新的那个相等，如果相等就代表可以获得锁
        if(currentPath.equals(node+"/"+childrens.get(0))){
            log.info("currentPath节点的值-----》【"+currentPath+"】 node+\"/\"+childrens.get(0)节点的值-----》【"+node+"/"+childrens.get(0)+"】");
            return true;
        }else {
            /**
             * 如果不能获取到这把锁，那么必须要获取到前面那个节点，要注册对前面那个节点的事件监控
             * 获取到前面节点的位置
             */
            int i = Collections.binarySearch(childrens,currentPath.substring(6));
            System.out.println(i);
            beforePath = node + "/" + childrens.get(i - 1);
        }
        return false;
    }

    protected void waitForLock()
    {
        IZkDataListener iZkDataListener = new IZkDataListener()
        {

            public void handleDataChange(String s, Object o) throws Exception
            {

            }

            public void handleDataDeleted(String s) throws Exception
            {
                if (cdl != null){
                    cdl.countDown();
                }
            }
        };
        /**
         * 注册事件监控beforePath节点
         */
        client.subscribeDataChanges(beforePath,iZkDataListener);
        if(client.exists(beforePath)){
            cdl = new CountDownLatch(1);
            try
            {
                cdl.await();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        client.unsubscribeDataChanges(beforePath,iZkDataListener);
    }
}
