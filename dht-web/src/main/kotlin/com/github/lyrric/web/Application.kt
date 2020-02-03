package com.github.lyrric.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import tk.mybatis.spring.annotation.MapperScan


@MapperScan(basePackages = ["com.github.lyrric.web.mapper"])
@SpringBootApplication
@EnableScheduling
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
