package com.hyp.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/19 22:28
 * @描述 Curator获取zookeeper客户端工具类
 * 使用单利模式
 *
 */
public class CuratorUtil
{
    private static String connectStr = "192.168.80.134:2181,192.168.80.135:2181,192.168.80.136:2181";

    private static class innerClass{
        private static final CuratorFramework client = CuratorFrameworkFactory.builder().connectString(connectStr).connectionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
    }

    public CuratorUtil(){}

    public static CuratorFramework getInnerClass(){
        return innerClass.client;
    }
}
