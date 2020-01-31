package com.github.lyrric.common.entity;

import com.github.lyrric.common.util.SystemClock;
import lombok.Data;

import java.io.Serializable;

/***
 * Peer Wire 下载消息信息
 *
 * @author Mr.Xu
 * @date 2019-02-20 15:53
 **/
@Data
public class DownloadMsgInfo implements Serializable {

	private String ip;
	private int port;
	private byte[] nodeId;		//make sure download peer id is same to DHT server
	private String infoHash;
	private long timestamp;

	public DownloadMsgInfo() {

	}

	public DownloadMsgInfo(String ip, int port, byte[] nodeId, String infoHash) {
		this.ip = ip;
		this.port = port;
		this.nodeId = nodeId;
		this.infoHash = infoHash;
		timestamp = SystemClock.now();
	}
}
