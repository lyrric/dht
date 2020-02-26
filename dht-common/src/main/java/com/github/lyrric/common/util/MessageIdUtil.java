package com.github.lyrric.common.util;

import io.netty.util.CharsetUtil;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2020-02-25.
 *
 * @author wangxiaodong
 */
public class MessageIdUtil {

//    private static final byte[] lock = new byte[0];
//    /**
//     * 现在的id
//     */
//    private static long nowId = 0x0;
//
//    /**
//     * 最大ID
//     */
//    private static final long maxId = 0xffff;
//
//    /**
//     * 获取下一个ID
//     * @return
//     */
//    public static String nextId(){
//        synchronized (lock){
//            if(nowId > maxId){
//                nowId = 0;
//            }else{
//                nowId++;
//            }
//            return String.format("%04d",new BigInteger(nowId).toString(16));
//        }
//    }

    //递增刷新阈值
    private static int maxMessageID = 1<<15;
    //用于递增消息ID
    private static AtomicInteger generator = new AtomicInteger(1);

    /**
     * 生成一个递增的t,相当于消息id
     * 使用指定生成器
     */
    public static String generateMessageID() {
        int result;
        //当大于阈值时,重置
        if ((result = generator.getAndIncrement()) > maxMessageID) {
            generator.lazySet(1);
        }
        return new String(int2TwoBytes(result), CharsetUtil.UTF_8);
    }

    /**
     * int 转 2个字节的byte[]
     * 舍弃16位最高位,只保留16位,两个字节的低位.
     * 这个字节数组的顺序需要是这样的.. 目前我收到其他节点的信息,他们的字节数组大多是这样的/
     * 并且按照惯性思维,左边的(也就是byte[0]),的确应该是高位的.
     */
    private static byte[] int2TwoBytes(int value) {
        byte[] des = new byte[2];
        des[1] = (byte) (value & 0xff);
        des[0] = (byte) ((value >> 8) & 0xff);
        return des;
    }
}
