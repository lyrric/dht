package com.github.lyrric.down


import lombok.extern.slf4j.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.RestController
import tk.mybatis.spring.annotation.MapperScan

@MapperScan(basePackages = ["com.github.lyrric.down.mapper"])
@Slf4j
@EnableScheduling
@RestController
@SpringBootApplication
class DHTDownloadApplication {



    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DHTDownloadApplication::class.java, *args)
        }
    }
}