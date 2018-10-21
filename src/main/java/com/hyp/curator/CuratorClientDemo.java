package com.hyp.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/16 22:28
 * @描述 10
 */
public class CuratorClientDemo
{
//    private static String connectStr = "192.168.80.134:2181,192.168.80.135:2181,192.168.80.136:2181";
    private static String connectStr = "39.106.147.223:2181";

    public static void main(String[] args)
    {
        CuratorFramework newClient = CuratorFrameworkFactory.newClient(connectStr, 5000, 5000, new ExponentialBackoffRetry(1000, 3));
        newClient.start();

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(connectStr).connectionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        client.start();
    }


}
