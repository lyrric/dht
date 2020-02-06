package com.github.lyrric.web.service.impl

import com.github.lyrric.common.entity.Node
import com.github.lyrric.common.entity.Tree
import com.github.lyrric.common.util.JSONUtil
import com.github.lyrric.web.entity.Torrent
import com.github.lyrric.web.entity.Torrentt
import com.github.lyrric.web.mapper.TorrentMapper
import com.github.lyrric.web.mapper.TorrentStatMapper
import com.github.lyrric.web.mapper.TorrenttMapper
import com.github.lyrric.web.service.DHTService
import com.github.pagehelper.PageHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tk.mybatis.mapper.weekend.Weekend
import javax.annotation.Resource

@Service
class DHTServiceImpl:DHTService {

    @Resource
    private lateinit var torrentStatMapper: TorrentStatMapper
    @Resource
    private lateinit var torrentMapper: TorrentMapper
    @Resource
    private lateinit var torrenttMapper: TorrenttMapper

    private val log:Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun test(){
        val weekend:Weekend<Torrent> = Weekend<Torrent>(Torrent::class.java)
        weekend.weekendCriteria()
                .andIsNotNull(Torrent::files.name)
        weekend.orderBy(Torrent::addTime.name).asc()
        var pageNum = 15
        val pageSize = 100
        while(true){
            val page = PageHelper.startPage<Torrent>(pageNum++, pageSize).doSelectPage<Torrent>{torrentMapper.selectByExample(weekend)}
            if(page.isEmpty())break
            val data = ArrayList<Torrentt>(100);
            page.stream().forEach { torrent->
                run {
                    val json = torrent.files
                    val tree = JSONUtil.parseObject(json, Tree::class.java)
                    var nodes = tree.leafList()
                    torrent.files = JSONUtil.toJSONString(nodes)
                            .replace("\"filename\"", "\"fileName\"")
                            .replace("\"filesize\"", "\"fileSize\"")
                    val torrentt = Torrentt()
                    torrentt.infoHash = torrent.infoHash
                    torrentt.fileName = torrent.fileName
                    torrentt.fileSize = torrent.fileSize
                    torrentt.files = torrent.files
                    torrentt.addTime = torrent.addTime
                    data.add(torrentt)
                }
            }
            torrenttMapper.insertList(data)
            log.info("pageNum:{}", pageNum)
        }
    }
}