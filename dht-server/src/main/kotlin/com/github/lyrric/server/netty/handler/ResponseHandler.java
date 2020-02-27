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
    @Resource
    private RouteTable routeTable;

    private AtomicInteger findNodeNum = new AtomicInteger(0);

    private AtomicInteger findPeerNum = new AtomicInteger(0);

    public void hand(Map<String, ?> map, InetSocketAddress sender){
        //消息 id
        byte[] id = (byte[]) map.get("t");
        String transactionId;
        try {
            transactionId = String.valueOf(ByteUtil.byteArrayToInt(id));
        }catch (Exception e){
            return;
        }
        RequestMessage message = (RequestMessage) redisTemplate.boundValueOps(RedisConstant.KEY_MESSAGE_PREFIX+transactionId).get();
        if(message == null){
            //未知的消息类型，不处理
            log.info("未知的消息类型，不处理, transactionId{}",transactionId);
            return;
        }
        String type = message.getType();
        //log.info("on response type {}", type);
        @SuppressWarnings("unchecked")
        Map<String, ?> r = (Map<String, ?>) map.get("r");
        switch (type) {
            case "find_node":
                resolveNodes(r);
                findNodeNum.incrementAndGet();
                if((findNodeNum.get() % 100) == 0){
                    log.info("findNodeNum count:{}", findNodeNum.get());
                }
                break;
            case "ping":

                break;
            case "get_peers":
                resolvePeers(r, message);
                findPeerNum.incrementAndGet();
                if((findPeerNum.get() % 100) == 0){
                    log.info("findPeerNum count:{}", findPeerNum.get());
                }
                break;
            case "announce_peer":

                break;
            default:
        }
    }



    /**
     * 解析响应内容中的 DHT 节点信息
     *
     * @param r
     */
    private void resolvePeers(Map<String, ?> r, RequestMessage message) {
        if (r.get("values") != null){
            byte[] peers = (byte[]) r.get("nodes");
            for (int i = 0; i < peers.length; i += 6) {
                try {
                    InetAddress ip = InetAddress.getByAddress(new byte[]{peers[i], peers[i + 1], peers[i + 2], peers[i + 3]});
                    InetSocketAddress address = new InetSocketAddress(ip, (0x0000FF00 & (peers[i + 4] << 8)) | (0x000000FF & peers[i + 5]));
                    DownloadMsgInfo downloadMsgInfo =
                            new DownloadMsgInfo(address.getHostName(), address.getPort(), NetworkUtil.SELF_NODE_ID, message.getHashInfo());
                    log.info("resolvePeers peers ,transaction id = {} infoHash={} address=[{}] ", message.getTransactionId(), message.getHashInfo(), address);
                    redisTemplate.boundListOps(RedisConstant.KEY_HASH_INFO).leftPush(downloadMsgInfo);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            //如果peer达到了五个，就手动删除transaction Id，以后该消息的回复，都不再处理，避免重复下载
            if(peers.length >= 6 * 5){
                redisTemplate.delete(RedisConstant.KEY_MESSAGE_PREFIX+message.getTransactionId());
                return;
            }
        }

        if (r.get("nodes") != null){
            byte[] nodes = (byte[]) r.get("nodes");
            for (int i = 0; i < nodes.length; i += 26) {
                try {
                    InetAddress ip = InetAddress.getByAddress(new byte[]{nodes[i + 20], nodes[i + 21], nodes[i + 22], nodes[i + 23]});
                    InetSocketAddress address = new InetSocketAddress(ip, (0x0000FF00 & (nodes[i + 24] << 8)) | (0x000000FF & nodes[i + 25]));
                    log.info("resolvePeers nodes ,transaction id = {} infoHash={} address=[{}] ", message.getTransactionId(), message.getHashInfo(), address);
                    dhtServer.sendGetPeers(message.getHashInfo(), address, message.getTransactionId());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
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
        for (int i = 0; i < nodes.length; i += 26) {
            try {
                InetAddress ip = InetAddress.getByAddress(new byte[]{nodes[i + 20], nodes[i + 21], nodes[i + 22], nodes[i + 23]});
                InetSocketAddress address = new InetSocketAddress(ip, (0x0000FF00 & (nodes[i + 24] << 8)) | (0x000000FF & nodes[i + 25]));
                byte[] nid = new byte[20];
                System.arraycopy(nodes, i, nid, 0, 20);
                Node node = new Node(nid, address);
                DHTServerHandler.NODES_QUEUE.offer(node);
                routeTable.add(node);
            } catch (Exception e) {
                log.error("", e);
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
        Integer transactionId = MessageIdUtil.generatorIntId();
        RequestMessage requestMessage = new RequestMessage(transactionId.toString(), MethodEnum.FIND_NODE.name, null);
        redisTemplate.opsForValue().setIfAbsent(RedisConstant.KEY_MESSAGE_PREFIX+requestMessage.getTransactionId(), requestMessage,5, TimeUnit.MINUTES);
        DatagramPacket packet = NetworkUtil.createPacket(ByteUtil.intToByteArray(transactionId), "q", "find_node", map, address);
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
