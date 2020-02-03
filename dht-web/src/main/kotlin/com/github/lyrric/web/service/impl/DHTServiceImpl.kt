package com.github.lyrric.web.service.impl

import com.github.lyrric.web.mapper.TorrentStatMapper
import com.github.lyrric.web.service.DHTService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class DHTServiceImpl:DHTService {

    @Resource
    private lateinit var torrentStatMapper: TorrentStatMapper

    private val log:Logger = LoggerFactory.getLogger(this.javaClass.name)

    /**
     * 定时任务，每天凌晨00:01运行，统计前一天数据
     */
    @Scheduled(cron = "0 1 0 * * ?")
    fun stat(){
        log.info("################################ start to stat torrent #################################")
        torrentStatMapper.stat()
    }

}