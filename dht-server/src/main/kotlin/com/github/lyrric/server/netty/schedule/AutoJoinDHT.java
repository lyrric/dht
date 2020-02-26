package com.github.lyrric.server.netty.schedule;

import com.github.lyrric.server.netty.DHTServer;
import com.github.lyrric.server.netty.handler.DHTServerHandler;
import com.github.lyrric.server.netty.handler.RequestHandler;
import com.github.lyrric.server.netty.handler.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/***
 * 定时检测本地节点数并自动加入 DHT 网络
 *
 * @author Mr.Xu
 * @date 2019-02-16 22:04
 **/
@Slf4j
@Component
public class AutoJoinDHT {

	@Resource
	private ResponseHandler responseHandler;

	@Scheduled(fixedDelay = 60*60 * 1000, initialDelay = 10 * 1000)
	public void doJob() {
		if (DHTServerHandler.NODES_QUEUE.isEmpty()) {
			log.info("本地 DHT 节点数为0，自动重新加入 DHT 网络中...");
			responseHandler.joinDHT();
		}
	}
}
