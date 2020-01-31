package com.github.lyrric.server.netty.config;

import com.github.lyrric.server.netty.handler.DHTServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/***
 * Netty 服务器配置
 *
 * @author: Mr.Xu
 * @create: 2019-02-15 14:50
 **/
@Configuration
@ConfigurationProperties(prefix = "netty")
public class NettyConfig implements ApplicationListener<ContextClosedEvent> {

	@Value("${netty.udp.port}")
	private int udpPort;
	@Value("${netty.so.rcvbuf}")
	private int rcvbuf;
	@Value("${netty.so.sndbuf}")
	private int sndbuf;

	private EventLoopGroup group;


	@Resource
	private DHTServerHandler dhtServerHandler;

	@Bean
	public Bootstrap bootstrap() {
		group = group();
		Bootstrap b = new Bootstrap();
		b.group(group)
				.channel(NioDatagramChannel.class)
				.handler(dhtServerHandler);
		Map<ChannelOption<?>, Object> udpChannelOptions = udpChannelOptions();
		Set<ChannelOption<?>> keySet = udpChannelOptions.keySet();
		for (@SuppressWarnings("rawtypes")
                ChannelOption option : keySet) {
			b.option(option, udpChannelOptions.get(option));
		}
		return b;
	}

	@Bean
	public EventLoopGroup group() {
		return new NioEventLoopGroup();
	}

	@Bean
	public InetSocketAddress udpPort() {
		return new InetSocketAddress(udpPort);
	}

	@Bean
	public Map<ChannelOption<?>, Object> udpChannelOptions() {
		Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
		options.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		options.put(ChannelOption.SO_BROADCAST, true);
		options.put(ChannelOption.SO_RCVBUF, rcvbuf);
		options.put(ChannelOption.SO_SNDBUF, sndbuf);
		return options;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
		if(contextClosedEvent.getApplicationContext().getParent() == null) {
			group.shutdownGracefully();
		}
	}
}
