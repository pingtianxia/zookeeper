package com.hyp.master;
import java.io.Serializable;

/**
 * @作者 霍云平
 * @包名 com.hyp.master
 * @日期 2018/10/27 8:16
 * @描述 10
 */
public class UserCenter implements Serializable {

    // 机器ID
    private int m_id;
    // 机器名称
    private String m_name;

    @Override
    public String toString() {
        return "UserCenter{" +
                "m_id=" + m_id +
                ", m_name='" + m_name + '\'' +
                '}';
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }
}
