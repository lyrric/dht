package com.github.lyrric.common.util;

/**
 * Created on 2020-02-25.
 *
 * @author wangxiaodong
 */
public class MessageIdUtil {

    private static final byte[] lock = new byte[0];
    /**
     * 现在的id
     */
    private static Integer nowId = 0x0;

    /**
     * 最大ID
     */
    private static  final int maxId = 0xffff;

    /**
     * 获取下一个ID
     * @return
     */
    public static String nextId(){
        synchronized (lock){
            if(nowId > maxId){
                nowId = 0;
            }else{
                nowId++;
            }
            return String.format("%04d",nowId);
        }
    }
}
