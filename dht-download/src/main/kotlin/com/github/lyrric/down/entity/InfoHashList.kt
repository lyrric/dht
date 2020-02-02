package com.github.lyrric.down.entity

import javax.persistence.Column
import javax.persistence.Table

@Table(name = "info_hash_list")
class InfoHashList : BaseEntity() {
    /**
     * hash
     */
    @Column(name = "info_hash")
    var infoHash: String? = null
        set(infoHash) {
            field = infoHash?.trim { it <= ' ' }
        }

}