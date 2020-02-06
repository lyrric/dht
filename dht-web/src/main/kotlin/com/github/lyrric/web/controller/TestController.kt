package com.github.lyrric.web.controller

import com.github.lyrric.web.service.DHTService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource


@RestController
@RequestMapping(value = ["/"])
class TestController{

    @Resource
    private lateinit var dhtService: DHTService
    /**
     * 周统计
     */
    @GetMapping(value = ["/test"])
    fun test(){
        dhtService.test()
    }


}