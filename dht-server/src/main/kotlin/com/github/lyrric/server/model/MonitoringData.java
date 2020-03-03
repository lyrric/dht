package com.github.lyrric.server.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 2020-02-28.
 * 监控数据
 * @author wangxiaodong
 */
public class MonitoringData {
    public static long now = System.currentTimeMillis()/10;

    public static AtomicLong reqPingCount = new AtomicLong(0);
    public static AtomicLong reqFindNodeCount = new AtomicLong(0);
    public static AtomicLong reqGetPeersCount = new AtomicLong(0);
    public static AtomicLong reqAnnounceCount = new AtomicLong(0);

    public static AtomicLong resFindNodeCount = new AtomicLong(0);
    public static AtomicLong resGetPeersCount = new AtomicLong(0);


}
