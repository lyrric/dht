package com.github.lyrric.web.task

import com.github.lyrric.web.entity.Torrent
import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.es.repository.EsTorrentRepository
import com.github.lyrric.web.mapper.TorrentMapper
import com.github.lyrric.web.mapper.TorrentStatMapper
import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.annotation.Resource

@Component
class Task {

    @Resource
    private lateinit var torrentStatMapper: TorrentStatMapper
    @Resource
    private lateinit var esTorrentRepository: EsTorrentRepository
    @Resource
    private lateinit var torrentMapper: TorrentMapper

    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)


    /**
     * 定时任务，每天凌晨00:01运行，统计前一天数据
     */
    @Scheduled(cron = "0 1 0 * * ?")
    fun stat(){
        log.info("################################ start to stat torrent #################################")
        torrentStatMapper.stat()
    }

    /**
     * 定时任务，每天凌晨00:02运行，将前一天的数据同步到es中
     */
    //@Scheduled(cron = "0 2 0 * * ?")
    //@PostConstruct
    fun syncToEs(){
        log.info("################################ start to save torrent to es #################################")
        var total = 0
        var pageNum = 1
        val pageSize = 100
        while(true){
            PageHelper.startPage<Torrent>(pageNum++, pageSize);
            val page = torrentMapper.selectLastDayData()
            val data:List<EsTorrent> = page.stream().map { t->EsTorrent(t)}.collect(Collectors.toList())
            total+=data.size
            if(data.isEmpty()) break
            esTorrentRepository.saveAll(data)
            log.info("save to es, finished count:{}", total)
        }
        log.info("################################ save torrent to es finish , total : {} #################################", total)
    }
}