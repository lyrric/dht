package com.github.lyrric.web.service.impl

import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.es.service.EsService
import com.github.lyrric.web.mapper.TorrentMapper
import com.github.lyrric.web.mapper.TorrentStatMapper
import com.github.lyrric.web.model.BusinessException
import com.github.lyrric.web.model.PageResult
import com.github.lyrric.web.model.dto.SearchDTO
import com.github.lyrric.web.service.DHTService
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest


@Service
class DHTServiceImpl:DHTService {

    @Resource
    private lateinit var torrentStatMapper: TorrentStatMapper
    @Resource
    private lateinit var torrentMapper: TorrentMapper
    @Resource
    private lateinit var stringRedisTemplate: StringRedisTemplate
    @Resource
    private lateinit var esService: EsService

    private val IP_LIMIT_PREFIX = "IP:LIMIT:"


    private val log:Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun search(searchDTO: SearchDTO, httpRequest: HttpServletRequest): PageResult<EsTorrent> {
        val ip = getRequestIP(httpRequest)
        if(StringUtils.isEmpty(ip)){
            throw BusinessException("无效的请求")
        }
        //每个IP每秒只能搜索一次
        val success = stringRedisTemplate
                .opsForValue()
                .setIfAbsent(IP_LIMIT_PREFIX+ip, "", Duration.ofSeconds(1L))
        if(success != null && success){
            return esService.search(searchDTO)
        }else{
            throw BusinessException("每秒只能搜索一次")
        }
    }

    /**
     * 获取请求IP
     */
    fun getRequestIP(request: HttpServletRequest):String?{
        var tmp: String? = request.getHeader("x-forwarded-for")
        if (StringUtils.isNotEmpty(tmp)) {
            log.info("x-forwarded-for :{}", tmp)
        }
        tmp = request.getHeader("Proxy-Client-IP")
        if (StringUtils.isNotEmpty(tmp)) {
            log.info("Proxy-Client-IP :{}", tmp)
        }
        tmp = request.getHeader("WL-Proxy-Client-IP")
        if (StringUtils.isNotEmpty(tmp)) {
            log.info("WL-Proxy-Client-IP :{}", tmp);
        }
        tmp = request.getHeader("HTTP_CLIENT_IP")
        if (StringUtils.isNotEmpty(tmp)) {
            log.info("HTTP_CLIENT_IP :{}", tmp);
        }
        tmp = request.getHeader("HTTP_X_FORWARDED_FOR")
        if (StringUtils.isNotEmpty(tmp)) {
            log.info("HTTP_X_FORWARDED_FOR :{}", tmp);
        }
        tmp = request.remoteAddr
        if (StringUtils.isNotEmpty(tmp)) {
            log.info("remoteAddr :{}", tmp);
        }
        var ip: String? = request.getHeader("x-forwarded-for")
        if (StringUtils.isEmpty(ip) || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (StringUtils.isEmpty(ip) || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (StringUtils.isEmpty(ip) || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (StringUtils.isEmpty(ip) || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (StringUtils.isEmpty(ip) || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        log.info("#########################last ip  :{}", ip);
        return ip
    }
}