package com.github.lyrric.web.controller

import com.github.lyrric.web.task.Task
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource


@RestController
@RequestMapping(value = ["/"])
class TestController{

    @Resource
    private lateinit var task: Task;
    /**
     * 周统计
     */
    @GetMapping(value = ["/test"])
    fun test(){
        task.syncToEs()
    }


}