package com.hyp.curator;
import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @作者 霍云平
 * @包名 com.hyp.curator
 * @日期 2018/10/29 20:26
 * @描述 10
 * 这个leaderSelector是轮训选举master节点，也就是说每一个客户端就可以获取到master节点
 * 你可以在takeLeadership进行任务的分配等等，并且不要返回，
 * 如果你想要要此实例一直是leader的话可以加一个死循环。
 * 在这里我们使用AtomicInteger来记录此client获得领导权的次数， 它是”fair”， 每个client有平等的机会获得领导权。
 * 当你不再使用LeaderSelector实例时，应该调用它的close方法。
 */
public class LeaderSelectorTest {
    private static final Integer count = 10;
    private static String PATH = "/curator/leader";
    public static void main(String[] args) {
        List<LeaderSelectorAdapter> leaderSelectorAdapters = Lists.newArrayList();
        List<CuratorFramework> curatorFrameworks = Lists.newArrayList();
        try {
            for (int i = 0; i <count ; i++) {
                CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.80.200:2181").connectionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
                LeaderSelectorAdapter selectorAdapter = new LeaderSelectorAdapter(client, "client name #" + i, PATH);
                leaderSelectorAdapters.add(selectorAdapter);
                curatorFrameworks.add(client);
                client.start();
                selectorAdapter.start();
            }
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("释放资源！！！");
            for (LeaderSelectorAdapter leaderSelectorAdapter : leaderSelectorAdapters) {
                CloseableUtils.closeQuietly((Closeable) leaderSelectorAdapter);
            }
            for (CuratorFramework client : curatorFrameworks) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
