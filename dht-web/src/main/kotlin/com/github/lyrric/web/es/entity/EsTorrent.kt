package com.github.lyrric.web.es.entity

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lyrric.web.constant.EsConstant
import com.github.lyrric.web.entity.Torrent
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.util.*
import javax.persistence.Id


@Document(indexName = EsConstant.TORRENT_INDEX)
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
     * 被点击次数
     */
    @Field(type = FieldType.Long)
    var hot:Long? = null

    @Field(type = FieldType.Nested)
    var files:List<TorrentFile>? = null

    constructor()

    constructor(torrent: Torrent){
        this.infoHash = torrent.infoHash
        this.fileName = torrent.fileName
        this.fileSize = torrent.fileSize
        this.addTime = torrent.addTime
        if(torrent.files != null){
            this.files = ObjectMapper().readValue(torrent.files, object : TypeReference<List<TorrentFile>?>() {})
        }
        this.hot = 0
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class TorrentFile{
    @Field(type = FieldType.Keyword, index = false)
    @JsonAlias("fileName")
    var subFileName:String?= null

    @Field(type = FieldType.Long, index = false)
    @JsonAlias("fileSize")
    var subFileSize:Long? = null
}