package com.github.lyrric.web.controller

import com.github.lyrric.web.entity.UserRecord
import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.model.BusinessException
import com.github.lyrric.web.model.PageResult
import com.github.lyrric.web.model.dto.SearchDTO
import com.github.lyrric.web.service.DHTService
import com.github.lyrric.web.service.UserService
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
    @Resource
    private lateinit var userService: UserService

    @Value("\${app.latest-version}")
    private lateinit var latestVersion:String

    @Value("\${app.download-url}")
    private lateinit var downloadUrl:String

    @Value("\${app.search-url}")
    private lateinit var searchUrl:String

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
     * 检查是否有新版本
     * @return 新版本下载地址，null表示没有更新
     */
    @GetMapping(value = ["/app/update"])
    fun update(version:String):String?{
        return if(version == latestVersion) null else downloadUrl
    }
    /**
     * 获取搜索接口Url
     * @return
     */
    @GetMapping(value = ["/search/url"])
    fun update():String?{
        return searchUrl
    }
    /**
     * 下载App
     */
    @GetMapping(value = ["/app/download"])
    fun downApp(httpResponse: HttpServletResponse){
        httpResponse.contentType = "application/octet-stream"
        httpResponse.setHeader("Content-Disposition","attachment;filename=app.apk")
        val home = ApplicationHome(javaClass)
        val jarFile = home.source
        val path = jarFile.parentFile.canonicalPath
        val list = File(path).listFiles()
        for(file in list){
            //app-0.1.apk
            if(file.name.endsWith(".apk")){
                httpResponse.setHeader("Content-Length", file.length().toString())
                IOUtils.copy(file.inputStream(), httpResponse.outputStream)
                return
            }
        }
    }
    /**
     * 记录用户
     */
    @PostMapping(value = ["/user/record"])
    fun recordUser(@RequestBody record: UserRecord){
        userService.recordUser(record)
    }
}