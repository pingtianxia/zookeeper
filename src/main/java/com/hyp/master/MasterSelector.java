package com.hyp.master;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;

import javax.sound.midi.Track;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @作者 霍云平
 * @包名 com.hyp.master
 * @日期 2018/10/27 8:14
 * @描述 10
 */
public class MasterSelector {

    private ZkClient zkClient;

    private final static String Master_Path = "/master";
    // 机器
    private UserCenter server;
    // 主机
    private UserCenter master;

    // 表示主机是否在运行
    private boolean isRunning = false;

    private IZkDataListener dataListener;

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public MasterSelector(ZkClient zkClient, UserCenter server) {
        this.zkClient = zkClient;
        this.server = server;
        this.dataListener = new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {

            }

            public void handleDataDeleted(String s) throws Exception {
                // 如果master节点删除了，就代表master节点挂掉了
                chooseMaster();
            }
        };

        // 去竞争主机

    }

    /**
     * master选举的方法
     */
    private void chooseMaster(){

        if (!isRunning){
            System.out.println("当前服务器没有启动");
            return;
        }
        try {
            // 如果当前客户端能够成功的创建节点，那么这个客户端肯定是一个master主机
            zkClient.createEphemeral(Master_Path,server);
            master = server;
            System.out.println(master + "------》成功当选！");

            // 在这里模拟过一段时间master挂掉了  推迟5秒执行 new Runnable() 这个线程
            scheduledExecutorService.schedule(new Runnable() {
                public void run() {
                    // 释放锁
                    releaseMaster();
                }
            },5,TimeUnit.SECONDS);

        } catch (ZkNoNodeException e) {
            UserCenter userCenter = zkClient.readData(Master_Path, true);
            if (userCenter == null){
                System.out.println("再触发选举");
                chooseMaster();
            }else {
                master = userCenter;
            }
        }catch (ZkNodeExistsException e){
            zkClient.delete(Master_Path);
        }
    }

    /**
     * 这个节点就是master选举的方法一旦创建client就去选举
     */
    public void start(){
        if(!isRunning){
            isRunning = true;
            zkClient.subscribeDataChanges(Master_Path,dataListener);
            chooseMaster();
        }
    }

    public void stop(){
        if (isRunning){
            isRunning = false;
            scheduledExecutorService.shutdown();
            zkClient.unsubscribeDataChanges(Master_Path,dataListener);
            releaseMaster();
        }
    }

    // 模拟master节点挂掉，并不是所有client都能删除节点，只有master才能删除
    private void releaseMaster(){
        // 判断当前client 是否是master
        if (checkIsMaster()){
            zkClient.delete(Master_Path);
        }
    }

    private boolean checkIsMaster(){
        UserCenter userCenter = zkClient.readData(Master_Path);
        if (userCenter.getM_name().equals(server.getM_name())){
            master = userCenter;
            return true;
        }
        return false;
    }
}
