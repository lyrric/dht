package com.github.lyrric.server.netty.config;

import com.github.lyrric.server.netty.handler.DHTServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DHTChannelInitializer extends ChannelInitializer<DatagramChannel> {

	@Resource
	private DHTServerHandler dhtServerHandler;

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("handler", dhtServerHandler);
	}
}
