package com.github.lyrric.common.constant;

/**
 * Created on 2020-02-25.
 *
 * @author wangxiaodong
 */
public enum MethodEnum {

    PING("ping"),
    FIND_NODE("find_node"),
    GET_PEERS("get_peers"),
    ANNOUNCE_PEES("announce_peer");

    /**
     * 名称
     */
    public final String value;

     MethodEnum(String value) {
        this.value = value;
    }
}
