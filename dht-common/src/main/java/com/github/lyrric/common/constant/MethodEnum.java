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
    private String name;

    MethodEnum(String name) {
        this.name = name;
    }
}
