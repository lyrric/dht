package com.github.lyrric.server.netty.handler;

import com.github.lyrric.common.constant.RedisConstant;
import com.github.lyrric.common.entity.DownloadMsgInfo;
import com.github.lyrric.common.util.NodeIdUtil;
import com.github.lyrric.common.util.bencode.BencodingInputStream;
import com.github.lyrric.common.util.bencode.BencodingUtils;
import com.github.lyrric.server.mapper.InfoHashListMapper;
import com.github.lyrric.server.model.Node;
import com.github.lyrric.server.model.UniqueBlockingQueue;
import com.github.lyrric.server.netty.DHTServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 参见 Bittorrent 协议：
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 * @author Mr.Xu
 **/
@Slf4j
@Component
@ChannelHandler.Sharable
public class DHTServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	@Resource
	private RequestHandler requestHandler;
	@Resource
	private ResponseHandler responseHandler;

	private ExecutorService pool = Executors.newFixedThreadPool(50);

	public static final UniqueBlockingQueue NODES_QUEUE = new UniqueBlockingQueue();


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
		byte[] buff = new byte[packet.content().readableBytes()];
		packet.content().readBytes(buff);
		pool.submit(()->{
			try {
				Map<String, ?> map = BencodingUtils.decode(buff);
				if (map == null || map.get("y") == null) {
					return;
				}
				String y = new String((byte[]) map.get("y"));
				if ("q".equals(y)) {
					//请求 Queries
					requestHandler.hand(map, packet.sender());
				} else if ("r".equals(y)) {
					//回复 Responses
					responseHandler.hand(map, packet.sender());
				}else{
					List<?> e  = (List<?>)map.get("e");
					log.warn("error y :{}， code：{}, msg: {}, host:{}", y,e.get(0).toString(), new String((byte[]) e.get(1)),packet.sender().getHostString());
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		});
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
	}
	/**
	 * 异常捕获
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("发生异常:{}",cause.getMessage());
		// ctx.close();
	}
}