package com.hyp.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @作者 霍云平
 * @包名 com.hyp.lock
 * @日期 2018/10/20 8:43
 * @描述 10
 */
public class OrderNumFactory
{
    private static Integer initial = 0;

    public String createOrderNum(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return simpleDateFormat.format(new Date()) + ++initial;
    }

//    public static synchronized String createOrderNum(){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
//        return simpleDateFormat.format(new Date()) + ++initial;
//    }
}
