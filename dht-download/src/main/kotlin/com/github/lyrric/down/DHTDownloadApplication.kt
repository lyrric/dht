package com.github.lyrric.down


import com.github.lyrric.common.entity.DownloadMsgInfo
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.RestController
import tk.mybatis.spring.annotation.MapperScan


@MapperScan(basePackages = ["com.github.lyrric.down.mapper"])
@Slf4j
@EnableScheduling
@RestController
@SpringBootApplication
class DHTDownloadApplication {


    @Autowired
    private lateinit var redisConnectionFactory: RedisConnectionFactory

    @Bean
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

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DHTDownloadApplication::class.java, *args)
        }
    }
}