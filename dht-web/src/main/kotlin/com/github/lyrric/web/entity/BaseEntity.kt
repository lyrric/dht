package com.github.lyrric.web.entity

import tk.mybatis.mapper.annotation.KeySql
import tk.mybatis.mapper.code.IdentityDialect
import java.math.BigInteger
import javax.persistence.Column
import javax.persistence.Id

/**
 * Created on 2019-08-21.
 * @author wangxiaodong
 */

open class BaseEntity{

    @Id
    @KeySql(dialect = IdentityDialect.MYSQL)
    @Column(insertable = false, updatable = false)
    var id:BigInteger? = null
}