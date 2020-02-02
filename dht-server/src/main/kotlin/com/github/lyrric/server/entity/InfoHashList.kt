package com.github.lyrric.server.entity

import javax.persistence.Column
import javax.persistence.Table

@Table(name = "info_hash_list")
class InfoHashList : BaseEntity() {

    /**
     * hash
     */
    @Column(name = "info_hash")
    var infoHash: String? = null


     fun setInfoHashAndReturn(infoHash:String):InfoHashList {
         this.infoHash = infoHash
         return this
     }

}