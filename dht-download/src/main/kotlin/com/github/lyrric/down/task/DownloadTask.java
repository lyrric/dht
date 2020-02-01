package com.github.lyrric.down.task;

import com.github.lyrric.common.constant.RedisConstant;
import com.github.lyrric.common.entity.DownloadMsgInfo;
import com.github.lyrric.common.util.SystemClock;
import com.github.lyrric.down.client.Constants;
import com.github.lyrric.down.client.PeerWireClient;
import com.github.lyrric.down.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

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


	public DownloadTask(DownloadMsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	@Override
	public void run() {
		//log.info("download-message-in, info hash is {}", new BigInteger(msgInfo.getInfoHash()).toString(16));
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
			RedisTemplate redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);
			redisTemplate.boundListOps(RedisConstant.KEY_TORRENT).rightPush(torrent);
//			log.info("[{}:{}] Download torrent success, info hash is {}",
//					msgInfo.getIp(),
//					msgInfo.getPort(),
//					torrent.getInfoHash());
		});
		wireClient.downloadMetadata(new InetSocketAddress(msgInfo.getIp(), msgInfo.getPort()), msgInfo.getNodeId(), msgInfo.getInfoHash());
	}
}
