package com.github.lyrric.web.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Table

@Table(name = "user_record")
class UserRecord : BaseEntity() {

    /**
     * 手机名称
     */
    @Column(name = "cellphone_name")
    var cellphoneName: String? = null
        set(cellphoneName) {
            field = cellphoneName?.trim { it <= ' ' }
        }
    /**
     * 手机ID
     */
    @Column(name = "cellphone_id")
    var cellphoneId: String? = null
        set(cellphoneId) {
            field = cellphoneId?.trim { it <= ' ' }
        }
    /**
     * 添加时间
     */
    @Column(name = "add_time")
    var addTime: Date? = null

}