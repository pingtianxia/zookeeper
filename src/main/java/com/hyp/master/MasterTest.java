package com.hyp.master;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @作者 霍云平
 * @包名 com.hyp.master
 * @日期 2018/10/27 9:01
 * @描述 10
 */
public class MasterTest {

    public static void main(String[] args) {
        List<MasterSelector> list = new ArrayList<MasterSelector>();

        try {

            for (int i = 0; i < 10; i++) {
                ZkClient zkClient = new ZkClient("192.168.80.200:2181",5000,5000,new SerializableSerializer());
                UserCenter userCenter = new UserCenter();
                userCenter.setM_id(i);
                userCenter.setM_name("客户端"+i);

                MasterSelector selector = new MasterSelector(zkClient, userCenter);
                list.add(selector);
                selector.start();
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            for (MasterSelector masterSelector : list) {
                masterSelector.start();
            }
        }

    }
}
