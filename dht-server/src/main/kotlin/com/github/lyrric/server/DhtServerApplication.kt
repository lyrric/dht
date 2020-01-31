package com.github.lyrric.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
//import tk.mybatis.spring.annotation.MapperScan


//@MapperScan(basePackages = ["com.github.lyrric.server.mapper"])
@SpringBootApplication
@EnableScheduling
class DhtServerApplication

fun main(args: Array<String>) {
	runApplication<DhtServerApplication>(*args)
}
