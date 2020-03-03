package com.github.lyrric.server.netty.handler;

import com.github.lyrric.common.constant.MethodEnum;
import com.github.lyrric.common.constant.RedisConstant;
import com.github.lyrric.common.entity.DownloadMsgInfo;
import com.github.lyrric.common.util.ByteUtil;
import com.github.lyrric.common.util.MessageIdUtil;
import com.github.lyrric.common.util.NetworkUtil;
import com.github.lyrric.common.util.NodeIdUtil;
import com.github.lyrric.server.model.Node;
import com.github.lyrric.server.model.RequestMessage;
import com.github.lyrric.server.netty.DHTServer;
import com.github.lyrric.server.util.RouteTable;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2020-02-25.
 *
 * @author wangxiaodong
 */
@Component
@Slf4j
public class ResponseHandler {

    @Resource
    private DHTServer dhtServer;
    @Resource(name = "dhtRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    private AtomicInteger response = new AtomicInteger(0);

    public void hand(Map<String, ?> map, InetSocketAddress sender){
        //消息 id
         byte[] id = (byte[]) map.get("t");
        @SuppressWarnings("unchecked")
        Map<String, ?> r = (Map<String, ?>) map.get("r");
        response.incrementAndGet();
        if((response.get() % 10000) == 0){
            log.info("on response count:{}", response.get());
        }
        resolveNodes(r);
    }

    /**
     * 解析响应内容中的 DHT 节点信息
     *
     * @param r
     */
    private void resolveNodes(Map<String, ?> r) {
        byte[] nodes = (byte[]) r.get("nodes");
        if (nodes == null){
            return ;
        }
        if(DHTServerHandler.NODES_QUEUE.size() > 50000){
            return;
        }
        for (int i = 0; i < nodes.length; i += 26) {
            try {
                InetAddress ip = InetAddress.getByAddress(new byte[]{nodes[i + 20], nodes[i + 21], nodes[i + 22], nodes[i + 23]});
                InetSocketAddress address = new InetSocketAddress(ip, (0x0000FF00 & (nodes[i + 24] << 8)) | (0x000000FF & nodes[i + 25]));
                byte[] nid = new byte[20];
                System.arraycopy(nodes, i, nid, 0, 20);
                Node node = new Node(nid, address);
                DHTServerHandler.NODES_QUEUE.offer(node);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
    /**
     * 加入 DHT 网络
     */
    public void joinDHT() {
        for (InetSocketAddress addr : DHTServer.BOOTSTRAP_NODES) {
            findNode(addr, null, NetworkUtil.SELF_NODE_ID);
        }
    }


    /**
     * 发送查询 DHT 节点请求
     *
     * @param address 请求地址
     * @param nid     请求节点 ID
     * @param target  目标查询节点
     */
    private void findNode(InetSocketAddress address, byte[] nid, byte[] target) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("target", target);
        if (nid != null) {
            map.put("id",  NodeIdUtil.getNeighbor(NetworkUtil.SELF_NODE_ID, target));
        }
        DatagramPacket packet = NetworkUtil.createPacket(ByteUtil.intToByteArray(MessageIdUtil.generatorIntId()), "q", "find_node", map, address);
        dhtServer.sendKRPCWithLimit(packet);
    }
    /**
     * 查询 DHT 节点线程，用于持续获取新的 DHT 节点
     *
     * @date 2019/2/17
     **/
    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    private Thread findNodeTask = new Thread(() -> {
        while (true) {
            try {
                Node node = DHTServerHandler.NODES_QUEUE.take();
                findNode(node.getAddr(), node.getNodeId(), NodeIdUtil.createRandomNodeId());
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }

    });
    @PostConstruct
    public void init() {
        findNodeTask.start();
    }

    @PreDestroy
    public void stop() {
        findNodeTask.interrupt();
    }
}
