package com.github.lyrric.server.model;

import lombok.Data;

/**
 * Created on 2020-02-25.
 *
 * @author wangxiaodong
 */
@Data
public class RequestMessage {

    private String transactionId;
    /**
     *  消息类型find_node, get_peers
     */
    private String type;
    /**
     * 种子hash
     */
    private String hashInfo;


    public RequestMessage(String transactionId, String type, String hashInfo) {
        this.transactionId = transactionId;
        this.type = type;
        this.hashInfo = hashInfo;
    }

    public RequestMessage() {
    }
}
