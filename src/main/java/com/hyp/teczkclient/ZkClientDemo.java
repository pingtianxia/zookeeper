package com.hyp.teczkclient;

import org.I0Itec.zkclient.ZkClient;

/**
 * @author hyp
 * @date 2018/10/4 11:17
 */
public class ZkClientDemo
{
//    private static String connectStr = "192.168.80.134:2181,192.168.80.135:2181,192.168.80.136:2181";
    private static String connectStr = "192.168.80.200:2181";


    public static void main(String[] args)
    {
        ZkClient zkClient = new ZkClient(connectStr, 5000);
//        zkClient.createPersistent("/zkClient","zkClient");
        zkClient.delete("/zkClient");
    }
}
