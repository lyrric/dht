package com.github.lyrric.server

import com.github.lyrric.common.util.ByteUtil
import com.github.lyrric.common.util.MessageIdUtil
import com.github.lyrric.common.util.NetworkUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DhtServerApplication{

    @Autowired
    private lateinit var redisConnectionFactory: RedisConnectionFactory

    @Bean(name = ["dhtRedisTemplate"])
    fun dhtRedisTemplate(): RedisTemplate<String, Any>? {
        val redisTemplate = RedisTemplate<String, Any>()
        initDomainRedisTemplate(redisTemplate, redisConnectionFactory)
        return redisTemplate
    }

    private fun initDomainRedisTemplate(redisTemplate: RedisTemplate<String, Any>, factory: RedisConnectionFactory) {
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = JdkSerializationRedisSerializer()
        redisTemplate.valueSerializer = JdkSerializationRedisSerializer()
        redisTemplate.setConnectionFactory(factory)
    }
}

fun main(args: Array<String>) {
    println(ByteUtil.byteArrayToHex(NetworkUtil.SELF_NODE_ID))
    runApplication<DhtServerApplication>(*args)
}
