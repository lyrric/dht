package com.github.lyrric.server.model

import java.net.InetSocketAddress

/***
 * DHT 节点
 *
 * @author: Mr.Xu
 * @create: 2019-02-17 14:26
 */
class Node(val nodeId: ByteArray, val addr: InetSocketAddress)