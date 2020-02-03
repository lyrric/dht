package com.github.lyrric.web.entity

import javax.persistence.Column
import javax.persistence.Table

@Table(name = "info_hash_list")
class InfoHashList : BaseEntity() {
    /**
     * 获取hash
     *
     * @return info_hash - hash
     */
    /**
     * 设置hash
     *
     * @param infoHash hash
     */
    /**
     * hash
     */
    @Column(name = "info_hash")
    var infoHash: String? = null
        set(infoHash) {
            field = infoHash?.trim { it <= ' ' }
        }

}