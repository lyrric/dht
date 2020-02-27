package com.github.lyrric.server.netty.handler;

import com.github.lyrric.common.constant.MethodEnum;
import com.github.lyrric.common.constant.RedisConstant;
import com.github.lyrric.common.entity.DownloadMsgInfo;
import com.github.lyrric.common.util.MessageIdUtil;
import com.github.lyrric.common.util.NetworkUtil;
import com.github.lyrric.common.util.NodeIdUtil;
import com.github.lyrric.server.mapper.InfoHashListMapper;
import com.github.lyrric.server.model.RequestMessage;
import com.github.lyrric.server.netty.DHTServer;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
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
public class RequestHandler {

    @Resource
    private DHTServer dhtServer;
    @Resource(name = "dhtRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private InfoHashListMapper infoHashListMapper;

    private AtomicInteger pingNum = new AtomicInteger(0);
    private AtomicInteger findNodeNum = new AtomicInteger(0);
    private AtomicInteger findPeerNum = new AtomicInteger(0);
    private AtomicInteger announceNum = new AtomicInteger(0);
    /**
     * 收到的hash数量
     */
    private AtomicInteger hashCount = new AtomicInteger(0);

    /**
     * 解析查询请求
     *
     * @param map
     * @param sender
     */
    public void hand(Map<String, ?> map, InetSocketAddress sender){
         //transaction id 会话ID
        byte[] t = (byte[]) map.get("t");

        String q = new String((byte[]) map.get("q"));
        Map<String, ?> a = (Map<String, ?>) map.get("a");
        //log.info("onQuery type:{}", q);
        switch (q) {
            case "ping":
                responsePing(t, (byte[]) a.get("id"), sender);
//                pingNum.incrementAndGet();
//                if((pingNum.get() % 1000) == 0){
//                    log.info("pingNum count:{}", pingNum.get());
//                }
                break;
            case "find_node":
                responseFindNode(t, (byte[]) a.get("id"), sender);
//                findNodeNum.incrementAndGet();
//                if((findNodeNum.get() % 10000) == 0){
//                    log.info("findNodeNum count:{}", findNodeNum.get());
//                }
                break;
            case "get_peers":
                responseGetPeers(t, (byte[]) a.get("info_hash"), sender);
//                findPeerNum.incrementAndGet();
//                if((findPeerNum.get() % 10000) == 0){
//                    log.info("findPeerNum count:{}", findPeerNum.get());
//                }
                break;
            case "announce_peer":
                responseAnnouncePeer(t, a, sender);
//                announceNum.incrementAndGet();
//                if((announceNum.get() % 1000) == 0){
//                    log.info("announceNum count:{}", announceNum.get());
//                }
                break;
            default:
        }
    }

    /**
     * 回复 ping 请求
     * Response = {"t":"aa", "y":"r", "r": {"id":"自身节点ID"}}
     *
     * @param t
     * @param sender
     */
    private void responsePing(byte[] t, byte[]nid, InetSocketAddress sender) {
        Map r = new HashMap<String, Object>();
        r.put("id", NodeIdUtil.getNeighbor(NetworkUtil.SELF_NODE_ID, nid));
        DatagramPacket packet = NetworkUtil.createPacket(t, "r", null, r, sender);
        dhtServer.sendKRPCWithLimit(packet);
    }

    /**
     * 回复 find_node 请求, 由于是模拟的 DHT 节点，所以直接回复一个空的 node 集合即可
     * Response = {"t":"aa", "y":"r", "r": {"id":"0123456789abcdefghij", "nodes": "def456..."}}
     *
     * @param t
     * @param sender
     */
    private void responseFindNode(byte[] t, byte[] nid, InetSocketAddress sender) {
        HashMap<String, Object> r = new HashMap<>();
        r.put("id", NodeIdUtil.getNeighbor(NetworkUtil.SELF_NODE_ID, nid));
        r.put("nodes", new byte[]{});
        DatagramPacket packet = NetworkUtil.createPacket(t, "r", null, r, sender);
        dhtServer.sendKRPCWithLimit(packet);
    }

    /**
     * 回复 get_peers 请求，必须回复，不然收不到 announce_peer 请求
     * Response with closest nodes = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "nodes": "def456..."}}
     *
     * @param t
     * @param sender
     */
    private void responseGetPeers(byte[] t, byte[] info_hash, InetSocketAddress sender) {
        if(info_hash == null){
            return;
        }
        String hashStr = new BigInteger(info_hash).toString(16);
        if(infoHashListMapper.selectCountByHash(hashStr) > 0){
            return ;
        }
        HashMap<String, Object> r = new HashMap<>();
        r.put("token", new byte[]{info_hash[0], info_hash[1]});
        r.put("nodes", new byte[]{});
        r.put("id", NodeIdUtil.getNeighbor(NetworkUtil.SELF_NODE_ID, info_hash));
        DatagramPacket packet = NetworkUtil.createPacket(t, "r", null, r, sender);
        dhtServer.sendKRPCWithLimit(packet);

        //sendGetPeers(hashStr);
    }

    /**
     * 送get_peers请求，请求种子peers
     * @param infoHash 磁力hash
     */
    private void sendGetPeers(String infoHash){
        RequestMessage message = new RequestMessage(MessageIdUtil.generatorIntId().toString(), MethodEnum.GET_PEERS.name(), infoHash);
        //有效期为三分钟
        redisTemplate.opsForValue().setIfAbsent(RedisConstant.KEY_MESSAGE_PREFIX+message.getTransactionId(), message, 3, TimeUnit.MINUTES);
        for (InetSocketAddress addr : DHTServer.BOOTSTRAP_NODES) {
            dhtServer.sendGetPeers(infoHash, addr, message.getTransactionId());

        }
    }
    /**
     * 回复 announce_peer 请求，该请求中包含了对方正在下载的 torrent 的 info_hash 以及 端口号
     * Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
     *
     * @param t
     * @param a      请求参数 a：
     *               {
     *               "id" : "",
     *               "implied_port": <0 or 1>,    //为1时表示当前自身的端口就是下载端口
     *               "info_hash" : "<20-byte infohash of target torrent>",
     *               "port" : ,
     *               "token" : "" //get_peer 中回复的 token，用于检测是否一致
     *               }
     * @param sender
     */
    private void responseAnnouncePeer(byte[] t, Map a, InetSocketAddress sender) {
        try {
            byte[] infoHash = (byte[]) a.get("info_hash");
            byte[] token = (byte[]) a.get("token");
            String hashStr = new BigInteger(1,infoHash).toString(16);

            if(infoHashListMapper.selectCountByHash(hashStr) > 0){
                return ;
            }
            try {
                infoHashListMapper.insert(hashStr);
            }catch (Exception e){
                //hash冲突报错，不打印日志
                if(!(e instanceof DuplicateKeyException)){
                    e.printStackTrace();
                }
            }
            byte[] id = (byte[]) a.get("id");
            if(token.length != 2 || infoHash[0] != token[0] || infoHash[1] != token[1]) {
                return;
            }
            int port;
            if (a.containsKey("implied_port") && ((BigInteger) a.get("implied_port")).shortValue() != 0) {
                port = sender.getPort();
            } else {
                port = ((BigInteger) a.get("port")).intValue();
            }

            //HashMap<String, Object> r = new HashMap<>();
            byte[] nodeId = NodeIdUtil.getNeighbor(NetworkUtil.SELF_NODE_ID, id);
            //r.put("id", nodeId);
            //DatagramPacket packet = NetworkUtil.createPacket(t, "r", null, r, sender);

            //保存到下载队列
            redisTemplate.opsForList().rightPush(RedisConstant.KEY_HASH_INFO, new DownloadMsgInfo(sender.getHostString(), port, nodeId, infoHash));
            //回复消息
            //dhtServer.sendKRPCWithLimit(packet);

            hashCount.incrementAndGet();
            if(hashCount.get() % 1000 == 0){
                log.info("info hash count:{}", hashCount.get());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
