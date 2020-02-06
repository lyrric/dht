package com.github.lyrric.web.es.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.github.lyrric.web.constant.EsConstant
import com.github.lyrric.web.entity.Torrent
import org.springframework.data.elasticsearch.annotations.*
import org.springframework.format.annotation.DateTimeFormat
import java.util.*
import javax.persistence.Id


@Document(indexName = EsConstant.INDEX, type = "torrent")
class EsTorrent{

    /**
     * es的id
     */
    @Id
    val id:String? = null
    /**
     * 种子hash
     */
    @Field(type = FieldType.Text)
    var infoHash: String? = null
        set(infoHash) {
            field = infoHash?.trim { it <= ' ' }
        }
    /**
     * 名称
     */
    @Field(type=FieldType.Text, analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    var fileName: String? = null
        set(fileName) {
            field = fileName?.trim { it <= ' ' }
        }
    /**
     * 文件大小
     */
    @Field(type = FieldType.Long)
    var fileSize: Long? = null

    /**
     * 入库时间
     */
    @Field(type = FieldType.Date)
    var addTime: Date? = null

    /**
     * 文件列表
     */
    @Field(type = FieldType.Keyword, index = false)
    var files: String? = null
        set(files) {
            field = files?.trim { it <= ' ' }
        }
    /**
     * 被点击次数
     */
    @Field(type = FieldType.Long)
    var hot:Long? = null

    constructor()

    constructor(torrent: Torrent){
        this.infoHash = torrent.infoHash
        this.fileName = torrent.fileName
        this.fileSize = torrent.fileSize
        this.addTime = torrent.addTime
        this.files = torrent.files
        this.hot = 0
    }
}