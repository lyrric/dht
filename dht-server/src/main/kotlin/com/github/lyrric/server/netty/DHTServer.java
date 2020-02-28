package com.github.lyrric.server.netty;


import com.github.lyrric.common.constant.MethodEnum;
import com.github.lyrric.common.util.ByteUtil;
import com.github.lyrric.common.util.NetworkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/***
 * 模拟 DHT 节点服务器
 *
 * @author Mr.Xu
 * @date 2019-02-15 14:44
 **/
@Slf4j
@Component
public class DHTServer {

	@Resource
	private Bootstrap bootstrap;

	@Resource
	private InetSocketAddress inetSocketAddress;

	private ChannelFuture serverChannelFuture;

	@Resource(name = "dhtRedisTemplate")
	private RedisTemplate<String, Object> redisTemplate;

    @Value("${netty.so.send-limit}")
    private int sendLimit;
	/**
	 * 每秒发送了多少次请求
	 */
	private long secondSend = 0;
	/**
	 * 当前秒
	 */
	private long now = System.currentTimeMillis() / 1000;
	public static final int SECRET = 888;

	/**
	 * 启动节点列表
	 */
	public static final List<InetSocketAddress> BOOTSTRAP_NODES = new ArrayList<>(Arrays.asList(
			new InetSocketAddress("router.bittorrent.com", 6881),
			new InetSocketAddress("dht.transmissionbt.com", 6881),
			new InetSocketAddress("router.utorrent.com", 6881),
			new InetSocketAddress("dht.aelitis.com", 6881),
			new InetSocketAddress("dht.libtorrent.org", 25401)
	));

	/**
	 * 随 SpringBoot 启动 DHT 服务器
	 *
	 * @throws Exception
	 */
	@PostConstruct
	public void start() throws Exception {
		log.info("Starting dht server at " + inetSocketAddress);
		serverChannelFuture = bootstrap.bind(inetSocketAddress).sync();
		serverChannelFuture.channel().closeFuture();
	}

	/**
	 * 发送 KRPC 协议数据报文
	 *
	 * @param packet
	 */
	public void  sendKRPCWithLimit(DatagramPacket packet) {
		if(count()){
			return;
		}
 		sendKRPC(packet);
	}


	/**
	 * 发送get_peers，不限制发送频率
	 * @param infoHash
	 */
	public void sendGetPeers(byte[] infoHash, InetSocketAddress address, String transactionId) {
		count();
		HashMap<String, Object> map = new HashMap<>(5);
		map.put("info_hash", infoHash);
		map.put("id", NetworkUtil.SELF_NODE_ID);
		DatagramPacket packet = NetworkUtil.createPacket(ByteUtil.intToByteArray(Integer.parseInt(transactionId)), "q",
				MethodEnum.GET_PEERS.value, map, address);
		sendKRPC(packet);
	}

	private void sendKRPC(DatagramPacket packet){
		if(serverChannelFuture.channel().isWritable()){
			serverChannelFuture.channel().writeAndFlush(packet).addListener(future -> {
				if (!future.isSuccess()) {
					log.warn("unexpected push. msg:{} fail:{}", packet, future.cause().getMessage());
				}
			});
		}else{
			try {
				serverChannelFuture.channel().writeAndFlush(packet).sync();
			} catch (InterruptedException e) {
				log.info("write and flush msg exception. host {}, err msg:[{}]", packet.sender().getHostString(), e);
			}
		}
	}

	/**
	 * 计数
	 * @return
	 */
	private boolean count(){
		long time = System.currentTimeMillis()/1000;
		if(time == now){
			if(secondSend >= sendLimit){
				return true;
			}
			secondSend++;
		}else{
			if((time % 10) == 0 && secondSend != 0){
				log.info("本秒共发送:{} 次请求", secondSend);
			}
			secondSend = 0;
			now = time;
		}
		return false;
	}
}
