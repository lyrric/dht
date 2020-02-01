package com.github.lyrric.down.task;

import com.github.lyrric.common.entity.DownloadMsgInfo;
import com.github.lyrric.down.util.SpringContextUtil;
import com.github.lyrric.common.util.SystemClock;
import com.github.lyrric.down.client.Constants;
import com.github.lyrric.down.client.PeerWireClient;
import com.github.lyrric.down.stream.MessageStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

import java.net.InetSocketAddress;

/***
 * Torrent 下载线程
 *
 * @author Mr.Xu
 * @date 2019-02-22 10:43
 **/
@Slf4j
public class DownloadTask implements Runnable {

	private DownloadMsgInfo msgInfo;
	private MessageStreams messageStreams;

	public DownloadTask(DownloadMsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	@Override
	public void run() {
		log.info("download-message-in, info hash is {}", msgInfo.getInfoHash());
		//由于下载线程消费的速度总是比 dht server 生产的速度慢，所以要做一下时间限制，否则程序越跑越慢
		if (SystemClock.now() - msgInfo.getTimestamp() >= Constants.MAX_LOSS_TIME) {
			return;
		}
		PeerWireClient wireClient = new PeerWireClient();
		//设置下载完成监听器
		wireClient.setOnFinishedListener((torrent) -> {
			if (torrent == null) {  //下载失败
				return;
			}
			messageStreams = (MessageStreams) SpringContextUtil.getBean(MessageStreams.class);
			//丢进 kafka 消息队列进行入库及索引操作
			messageStreams.torrentMessageOutput()
					.send(MessageBuilder.withPayload(torrent)
							.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
							.build());
			log.info("[{}:{}] Download torrent success, info hash is {}",
					msgInfo.getIp(),
					msgInfo.getPort(),
					torrent.getInfoHash());
		});
		wireClient.downloadMetadata(new InetSocketAddress(msgInfo.getIp(), msgInfo.getPort()), msgInfo.getNodeId(), msgInfo.getInfoHash());
	}
}
