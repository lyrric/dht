package com.github.lyrric.server

import com.github.lyrric.server.netty.stream.MessageStreams
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.scheduling.annotation.EnableScheduling

@EnableBinding(MessageStreams::class)
@SpringBootApplication
@EnableScheduling
class DhtServerApplication

fun main(args: Array<String>) {
    runApplication<DhtServerApplication>(*args)
}
