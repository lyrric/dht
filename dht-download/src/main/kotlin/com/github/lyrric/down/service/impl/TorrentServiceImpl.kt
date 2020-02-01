package com.github.lyrric.down.service.impl

import com.github.lyrric.common.entity.TorrentInfo
import com.github.lyrric.down.entity.Torrent
import com.github.lyrric.down.mapper.TorrentMapper
import com.github.lyrric.down.service.TorrentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service
import tk.mybatis.mapper.weekend.Weekend
import java.util.*
import javax.annotation.Resource
import kotlin.collections.ArrayList

@Service
class TorrentServiceImpl : TorrentService{

    @Resource
    private lateinit var torrentMapper: TorrentMapper

    private val torrents = ArrayList<Torrent?>(50)


    private val log:Logger = LoggerFactory.getLogger(this.javaClass)

    @StreamListener("torrent-message")
    override fun torrentMessageIn(torrentInfo: TorrentInfo) {
        val torrent = Torrent();
        torrent.infoHash = torrentInfo.infoHash
        torrent.fileName = torrentInfo.fileName
        torrent.fileSize = torrentInfo.fileSize
        torrent.fileType = torrentInfo.fileType
        torrent.files = torrentInfo.files
        torrent.torrentCreateTime = Date(torrentInfo.createDate)
        torrent.addTime = Date()
        sync(torrent)
    }

    @Synchronized
    fun sync(torrent: Torrent){
        try {
            val weekend:Weekend<Torrent> = Weekend(Torrent::class.java)
            weekend.weekendCriteria()
                    .andEqualTo(Torrent::infoHash.name, torrent.infoHash)
            if(torrentMapper.selectCountByExample(weekend) > 0){
                return
            }
            if(torrents.stream().anyMatch{t->t?.infoHash == torrent.infoHash} ){
                //判断列表中是否有相同hash，几率小，但是还是要避免，否则批量插入会失败
                return
            }
            torrents.add(torrent)
            //每满50个添加进数据库
            if(torrents.size >= 50){
                log.info("-------------------------------save 50 torrent to db---------------")
                torrentMapper.insertList(torrents)
                torrents.clear()
            }
        }catch (e:Exception){
            e.printStackTrace();
        }

    }
}