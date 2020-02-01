package com.github.lyrric.down.service

import com.github.lyrric.common.constant.RedisConstant
import com.github.lyrric.common.entity.DownloadMsgInfo
import com.github.lyrric.common.entity.TorrentInfo
import com.github.lyrric.down.entity.Torrent
import com.github.lyrric.down.mapper.TorrentMapper
import com.github.lyrric.down.task.BlockingExecutor
import com.github.lyrric.down.task.DownloadTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.BoundListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import tk.mybatis.mapper.weekend.Weekend
import java.util.*
import java.util.concurrent.Executors
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource
import kotlin.collections.ArrayList

@Service
class TorrentService {

    @Resource
    private lateinit var torrentMapper: TorrentMapper

    @Value("\${download.num.thread}")
    private var nThreads:Int = 40

    @Resource
    private lateinit var redisTemplate: RedisTemplate<String, Any?>

    private var blockingExecutor:BlockingExecutor? = null

    val torrents = ArrayList<Torrent?>(50)

    private val log:Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 初始化启动两个线程
     */
    @PostConstruct
    fun init() {
        //下载种子信息
        Thread().run { downTorrent() }
        //保存种子
        Thread().run { saveBT() }
    }


    fun saveBT() {
        while(true){
            val torrentInfo: Any? = redisTemplate.opsForList().leftPop(RedisConstant.KEY_HASH_INFO)
            if(torrentInfo != null && torrentInfo is TorrentInfo){
                val torrent = Torrent();
                torrent.infoHash = torrentInfo.infoHash
                torrent.fileName = torrentInfo.fileName
                torrent.fileSize = torrentInfo.fileSize
                torrent.files = torrentInfo.files
                torrent.torrentCreateTime = Date(torrentInfo.createDate)
                torrent.addTime = Date()
                saveBTSync(torrent)
            }

        }

    }

    @Synchronized
    private fun saveBTSync(torrent: Torrent){
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

    fun downTorrent() {
        //max task bound 5000
        blockingExecutor = BlockingExecutor(Executors.newFixedThreadPool(nThreads), 5000)
        while(true){
            val msg: Any? = redisTemplate.opsForList().leftPop(RedisConstant.KEY_HASH_INFO)
            if(msg != null && msg is DownloadMsgInfo){
                blockingExecutor?.execute(DownloadTask(msg))
            }
        }

    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    fun autoFinalize() {
        //定时强制回收 Finalizer 队列里的 Socket 对象（有个抽象父类重写了 finalize 方法，
        //频繁创建 Socket 会导致 Socket 得不到及时回收频繁发生 FGC）
        System.runFinalization()
    }

    @PreDestroy
    fun destroy() {
        blockingExecutor?.shutdownNow()
    }
}