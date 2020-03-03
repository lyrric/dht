package com.github.lyrric.common.constant

class RedisConstant {

    companion object{
        /**
         * 存储HASH的前缀
         */
        const val KEY_HASH_PREFIX:String = "DHT:HASH_INFO:"
        /**
         * 存储种子hash的key，分布式队列下载种子信息
         */
        const val KEY_HASH_INFO:String = "DHT:DOWNLOAD_HASH_INFO"
        /**
         * 存储种子的key，分布式队列保存到数据库
         */
        const val KEY_TORRENT:String = "DHT:TORRENT"
        /**
         * 存储消息的prefix
         */
        const val KEY_MESSAGE_PREFIX:String = "DHT:MESSAGE:"
        /**
         * ip nodes,存储已经发送过find_node请求的ip
         */
        const val KEY_NODE_IP:String = "DHT:NODE:IP:"
    }
}