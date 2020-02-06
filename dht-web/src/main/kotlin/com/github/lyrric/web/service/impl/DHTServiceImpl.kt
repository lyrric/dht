package com.github.lyrric.web.service.impl

import com.github.lyrric.web.mapper.TorrentStatMapper
import com.github.lyrric.web.service.DHTService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.annotation.Resource

@Service
class DHTServiceImpl:DHTService {

    @Resource
    private lateinit var torrentStatMapper: TorrentStatMapper

    private val log:Logger = LoggerFactory.getLogger(this.javaClass.name)


}