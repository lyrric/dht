package com.github.lyrric.common.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2020-02-25.
 *
 * @author wangxiaodong
 */
public class MessageIdUtil {

    /**
     * 现在的id
     */
    private static AtomicInteger nowId = new AtomicInteger(1);

    /**
     * 最大ID
     */
    private static final int MAX_ID = (int)Math.pow(2,32);

    /**
     * 获取下一个ID
     * @return
     */
    public static Integer generatorIntId(){
        int result;
        //当大于阈值时,重置
        if ((result = nowId.getAndIncrement()) > MAX_ID) {
            nowId.lazySet(1);
        }
        return result;
    }

}
