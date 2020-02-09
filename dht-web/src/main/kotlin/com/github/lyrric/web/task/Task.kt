package com.github.lyrric.web.task

import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.es.repository.EsTorrentRepository
import com.github.lyrric.web.mapper.TorrentMapper
import com.github.lyrric.web.mapper.TorrentStatMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.stream.Collectors
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
    fun syncToEs(){
        log.info("################################ start to save torrent to es #################################")
        var offset = 0L
        val pageSize = 1000
        var total = 0L
        while(true){
            try {
                val page = torrentMapper.selectLastDayData(offset, pageSize)
                val data:List<EsTorrent> = page.stream().map { t->EsTorrent(t)}.collect(Collectors.toList())
                if(data.isEmpty()) break
                offset+=pageSize
                esTorrentRepository.saveAll(data)
                total+=data.size
            }catch (e:Exception){
                log.warn("################################ save torrent to es error , offset : {} #################################", offset)
            }

        }
        log.info("################################ save torrent to es finish , total : {} #################################", total)
    }
}