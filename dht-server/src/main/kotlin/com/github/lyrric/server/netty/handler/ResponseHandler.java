package com.github.lyrric.server.netty.handler;

import com.github.lyrric.common.constant.MethodEnum;
import com.github.lyrric.common.util.MessageIdUtil;
import com.github.lyrric.common.util.NetworkUtil;
import com.github.lyrric.common.util.NodeIdUtil;
import com.github.lyrric.server.model.Node;
import com.github.lyrric.server.model.RequestMessage;
import com.github.lyrric.server.netty.DHTServer;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    public void hand(Map<String, ?> map, InetSocketAddress sender){
        //transaction id
        byte[] t = (byte[]) map.get("t");
        //由于在我们发送查询 DHT 节点请求时，构造的查询 transaction id 为字符串 find_node（见 findNode 方法），所以根据字符串判断响应请求即可
        String type = new String(t);
        switch (type) {
            case "find_node":
                resolveNodes((Map) map.get("r"));
                break;
            case "ping":

                break;
            case "get_peers":

                break;
            case "announce_peer":

                break;
            default:
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
     * 送get_peers请求，请求种子peers
     * @param infoHash 磁力hash
     */
    private void sendGetPeers(String infoHash, InetSocketAddress addr){
        HashMap<String, Object> map = new HashMap<>();
        RequestMessage message = new RequestMessage(MessageIdUtil.nextId(), MethodEnum.GET_PEERS.name(), infoHash);
        map.put("info_hash",infoHash);
        DatagramPacket packet = NetworkUtil.createPacket(message.getId().getBytes(), "q", message.getType(), map, addr);
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
        DatagramPacket packet = NetworkUtil.createPacket("find_node".getBytes(), "q", "find_node", map, address);
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
                DHTServerHandler.NODES_QUEUE.offer(new Node(nid, address));
                //log.info("get node address=[{}] ", address);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
    @PostConstruct
    public void init() {
        findNodeTask.start();
    }

    @PreDestroy
    public void stop() {
        findNodeTask.interrupt();
    }
}
