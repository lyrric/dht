package com.github.lyrric.down.service.impl

import com.github.lyrric.common.entity.TorrentInfo
import com.github.lyrric.down.entity.Torrent
import com.github.lyrric.down.mapper.TorrentMapper
import com.github.lyrric.down.service.TorrentService
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service
import tk.mybatis.mapper.weekend.Weekend
import java.util.*
import javax.annotation.Resource

@Service
class TorrentServiceImpl : TorrentService{

    @Resource
    private lateinit var torrentMapper: TorrentMapper

    private val torrents:LinkedList<Torrent> = LinkedList();

    private val random:Int  = Random().nextInt();

    private val log:Logger = LoggerFactory.getLogger(this.javaClass)

    @StreamListener("torrent-message")
    override fun torrentMessageIn(torrentInfo: TorrentInfo) {
        log.info("torrentMessageIn---------------{}, size: {}, random:{}", torrentInfo.infoHash, torrents.size, random)
        val weekend:Weekend<Torrent> = Weekend(Torrent::class.java)
        weekend.weekendCriteria()
                .andEqualTo(Torrent::getInfoHash.name, torrentInfo.infoHash)
        if(torrentMapper.selectCountByExample(weekend) > 0){
            log.info(" torrentMapper exist in db")
            return
        }
        val torrent = Torrent();
        torrent.infoHash = torrentInfo.infoHash
        torrent.fileName = torrentInfo.fileName
        torrent.fileSize = torrentInfo.fileSize
        torrent.fileType = torrentInfo.fileType
        torrent.files = torrentInfo.files
        torrent.torrentCreateTime = Date(torrentInfo.createDate)
        torrent.addTime = Date()
        synchronized(this){
            if(torrents.stream().anyMatch{t->t.infoHash == torrent.infoHash} ){
                //判断列表中是否有相同hash，几率小，但是还是要避免，否则批量插入会失败
                log.info(" torrentMapper exist in list")
                return
            }
            torrents.addLast(torrent)
            //每满50个添加进数据库
            if(torrents.size >= 50){
                log.info(" torrentMapper.insertList(torrents)---------------")
                torrentMapper.insertList(torrents)
                torrents.clear()
            }
        }
    }
}