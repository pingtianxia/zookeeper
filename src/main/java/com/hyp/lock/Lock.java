package com.hyp.lock;

/**
 * @作者 霍云平
 * @包名 com.hyp.lock
 * @日期 2018/10/20 9:42
 * @描述 10
 */
public interface Lock
{
    /**
     * 这个方法是获取锁
     */
    public void lock();

    /**
     * 这个方法是释放锁
     */
    public void unLock();
}
