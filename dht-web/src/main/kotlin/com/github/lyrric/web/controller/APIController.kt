package com.github.lyrric.web.controller

import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.model.BusinessException
import com.github.lyrric.web.model.PageResult
import com.github.lyrric.web.model.dto.SearchDTO
import com.github.lyrric.web.service.DHTService
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.system.ApplicationHome
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Paths
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping(value = ["/api"])
class APIController{

    @Resource
    private lateinit var dhtService: DHTService

    private val log = LoggerFactory.getLogger(this::class.java)
    /**
     * 搜索接口
     */
    @PostMapping(value = ["/search"])
    fun search(@RequestBody searchDTO: SearchDTO, httpRequest: HttpServletRequest): PageResult<EsTorrent> {
        if(searchDTO.key == null || searchDTO.key!!.length < 2){
            throw BusinessException("搜索关键字不能小于2个字符")
        }
        if(searchDTO.pageNum == null || searchDTO.pageNum!! !in 1..10){
            searchDTO.pageNum = 1
        }
        searchDTO.pageSize = 20
        return dhtService.search(searchDTO, httpRequest)
    }

    /**
     * 用户点击了下载，则把hot+1
     */
    @GetMapping(value = ["/hot"])
    fun addHot(id: String, httpServletRequest: HttpServletRequest){
        if(StringUtils.isEmpty(id)){
            throw BusinessException("参数错误")
        }
        dhtService.addHot(id, httpServletRequest)
    }

    /**
     * 获取APP最新版本号
     */
    @GetMapping(value = ["app/version"])
    fun getAPPLatestVersion():String?{
        val home = ApplicationHome(javaClass)
        val jarFile = home.source
        val path = jarFile.parentFile.canonicalPath
        val list = File(path).list()
        for(fileName in list){
            //app-0.1.apk
            if(fileName.endsWith("apk")){
                log.info("$fileName success")
                return fileName.replace("app-", "").replace(".apk", "");
            }
        }
        return null
    }
    /**
     * 下载App
     */
    @GetMapping(value = ["app/download"])
    fun downApp(httpResponse: HttpServletResponse){
        httpResponse.contentType = "application/octet-stream"
        val home = ApplicationHome(javaClass)
        val jarFile = home.source
        val path = jarFile.parentFile.canonicalPath
        val list = File(path).listFiles()
        for(file in list){
            //app-0.1.apk
            if(file.name.endsWith(".apk")){
                IOUtils.copy(file.inputStream(), httpResponse.outputStream)

            }
        }
    }

    private fun getAppPath(){}
}