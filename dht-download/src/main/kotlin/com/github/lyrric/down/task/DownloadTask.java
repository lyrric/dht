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
import java.util.concurrent.atomic.AtomicInteger;

/***
 * Torrent 下载线程
 *
 * @author Mr.Xu
 * @date 2019-02-22 10:43
 **/
@Slf4j
public class DownloadTask implements Runnable {

	private DownloadMsgInfo msgInfo;

	/**
	 * 失败次数
	 */
	private static AtomicInteger failedCount = new AtomicInteger(0);

	/**
	 * 总共次数
	 */
	private static AtomicInteger count = new AtomicInteger(0);

	public DownloadTask(DownloadMsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	@Override
	public void run() {
		//由于下载线程消费的速度总是比 dht server 生产的速度慢，所以要做一下时间限制，否则程序越跑越慢
//		if (SystemClock.now() - msgInfo.getTimestamp() >= Constants.MAX_LOSS_TIME) {
//			return;
//		}
		count.incrementAndGet();
		PeerWireClient wireClient = new PeerWireClient();
		//设置下载完成监听器
		wireClient.setOnFinishedListener((torrent) -> {
			if(count.get() % 10000 == 0){
				log.info("download torrent count:{}, failed:{}, success:{}", count.get(), failedCount.get(), count.get()-failedCount.get());
			}
			if(torrent == null){
				failedCount.incrementAndGet();
				return;
			}
			//noinspection unchecked
			RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>)SpringContextUtil.getBean("dhtRedisTemplate");
			redisTemplate.opsForList().rightPush(RedisConstant.KEY_TORRENT, torrent);
//			log.info("[{}:{}] Download torrent success, info hash is {}",
//					msgInfo.getIp(),
//					msgInfo.getPort(),
//					torrent.getInfoHash());
		});
		wireClient.downloadMetadata(new InetSocketAddress(msgInfo.getIp(), msgInfo.getPort()), msgInfo.getNodeId(), msgInfo.getInfoHash());
	}
}
