package com.github.lyrric.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import tk.mybatis.spring.annotation.MapperScan


@MapperScan(basePackages = ["com.github.lyrric.web.mapper"])
@SpringBootApplication
@EnableScheduling
@EnableElasticsearchRepositories(basePackages = ["com.github.lyrric.web.es.repository"])
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
