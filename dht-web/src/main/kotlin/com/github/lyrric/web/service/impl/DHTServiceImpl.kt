package com.github.lyrric.web.service.impl

import com.github.lyrric.common.entity.Node
import com.github.lyrric.common.entity.Tree
import com.github.lyrric.common.util.JSONUtil
import com.github.lyrric.common.util.StringUtil
import com.github.lyrric.web.entity.Torrent
import com.github.lyrric.web.entity.Torrentt
import com.github.lyrric.web.mapper.TorrentMapper
import com.github.lyrric.web.mapper.TorrentStatMapper
import com.github.lyrric.web.mapper.TorrenttMapper
import com.github.lyrric.web.service.DHTService
import com.github.pagehelper.PageHelper
import org.apache.ibatis.session.RowBounds
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
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
        weekend.orderBy("id").asc()
        //461590
        var offset = 0
        val pageSize = 1000
        while(true){
            val row = RowBounds(offset, pageSize)
            val page = torrentMapper.selectByExampleAndRowBounds(weekend,row);
            if(page.isEmpty())break
            val data = ArrayList<Torrentt>(1000);
            page.forEach { torrent->
                run {
                    val json = torrent?.files
                    val torrentt = Torrentt()
                    if(!StringUtils.isEmpty(json)){
                        val tree = JSONUtil.parseObject(json, Tree::class.java)
                        val nodes = tree.leafList()
                        torrent?.files = JSONUtil.toJSONString(nodes)
                                .replace("\"filename\"", "\"fileName\"")
                                .replace("\"filesize\"", "\"fileSize\"")
                        torrentt.files = torrent?.files
                    }

                    torrentt.infoHash = torrent?.infoHash
                    torrentt.fileName = torrent?.fileName
                    torrentt.fileSize = torrent?.fileSize
                    torrentt.addTime = torrent?.addTime
                    data.add(torrentt)
                }
            }
            torrenttMapper.insertList(data)
            log.info("offset:{}", offset)
            offset+=pageSize
        }
    }
}