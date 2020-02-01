package com.github.lyrric.down.entity

import java.util.*
import javax.persistence.Column

class Torrent : BaseEntity() {
    /**
     * 获取种子hash
     *
     * @return info_hash - 种子hash
     */
    /**
     * 设置种子hash
     *
     * @param infoHash 种子hash
     */
    /**
     * 种子hash
     */
    @Column(name = "info_hash")
    var infoHash: String? = null
        set(infoHash) {
            field = infoHash?.trim { it <= ' ' }
        }
    /**
     * 获取名称
     *
     * @return file_name - 名称
     */
    /**
     * 设置名称
     *
     * @param fileName 名称
     */
    /**
     * 名称
     */
    @Column(name = "file_name")
    var fileName: String? = null
        set(fileName) {
            field = fileName?.trim { it <= ' ' }
        }
    /**
     * 获取文件大小
     *
     * @return file_size - 文件大小
     */
    /**
     * 设置文件大小
     *
     * @param fileSize 文件大小
     */
    /**
     * 文件大小
     */
    @Column(name = "file_size")
    var fileSize: Long? = null
    /**
     * 获取创建时间
     *
     * @return torrent_create_time - 创建时间
     */
    /**
     * 设置创建时间
     *
     * @param torrentCreateTime 创建时间
     */
    /**
     * 创建时间
     */
    @Column(name = "torrent_create_time")
    var torrentCreateTime: Date? = null
    /**
     * 获取入库时间
     *
     * @return add_time - 入库时间
     */
    /**
     * 设置入库时间
     *
     * @param addTime 入库时间
     */
    /**
     * 入库时间
     */
    @Column(name = "add_time")
    var addTime: Date? = null
    /**
     * 获取文件列表
     *
     * @return files - 文件列表
     */
    /**
     * 设置文件列表
     *
     * @param files 文件列表
     */
    /**
     * 文件列表
     */
    var files: String? = null
        set(files) {
            field = files?.trim { it <= ' ' }
        }

}