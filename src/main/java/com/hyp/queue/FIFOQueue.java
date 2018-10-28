package com.hyp.queue;
import org.I0Itec.zkclient.ExceptionUtil;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @作者 霍云平
 * @包名 com.hyp.queue
 * @日期 2018/10/26 21:15
 * @描述 10
 */
public class FIFOQueue<T> {
    protected  final ZkClient zkClient;

    protected final String root;

    protected static final String node_name = "n_";

    public FIFOQueue(ZkClient zkClient, String root) {
        this.zkClient = zkClient;
        this.root = root;
    }

    public int size(){
        return zkClient.getChildren(root).size();
    }
    public boolean isEmpty(){
        return zkClient.getChildren(root).size() ==0;
    }

    public boolean push(T data){
        // /queue/n_00000001
        try {
            String nodePath = root.concat("/").concat(node_name);
            String sequential = zkClient.createEphemeralSequential(nodePath, data);
            System.out.println(sequential);
        } catch (ZkNoNodeException e) {
            zkClient.createPersistent(root);
            push(data);
        } catch (Exception e){
            throw ExceptionUtil.convertToRuntimeException(e);
        }
        return true;
    }

    //
    public T poll(){
        // 拿到所有节点进行排序
        List<String> childrens = zkClient.getChildren(root);
        if (childrens.size() == 0)
            return null;

        Collections.sort(childrens, new Comparator<String>() {
            public int compare(String o1, String o2) {
                int i = getNodeNumber(o1, node_name).compareTo(getNodeNumber(o2, node_name));
                return i;
            }
        });
        T data = null;
        try {
            String polldata = childrens.get(0);
            String fullPath = root.concat("/").concat(polldata);
            data = zkClient.readData(fullPath);
            zkClient.delete(fullPath);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getNodeNumber(String str,String nodeName){
        int index = str.lastIndexOf(nodeName);
        if (index>=0){
            index += node_name.length();
            System.out.println(index);
            System.out.println("index <= str.length()===========>");
            System.out.println(index <= str.length() ? str.substring(index) : "");
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }

}
