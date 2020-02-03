package com.github.lyrric.web.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Table

@Table(name = "torrent_stat")
class TorrentStat : BaseEntity() {
    /**
     * 获取当前种子数量
     *
     * @return amount - 当前种子数量
     */
    /**
     * 设置当前种子数量
     *
     * @param amount 当前种子数量
     */
    /**
     * 当前种子数量
     */
    var amount: Long? = null
    /**
     * 获取种子增加数量
     *
     * @return increment - 种子增加数量
     */
    /**
     * 设置种子增加数量
     *
     * @param increment 种子增加数量
     */
    /**
     * 种子增加数量
     */
    var increment: Int? = null
    /**
     * 获取统计日期
     *
     * @return stat_date - 统计日期
     */
    /**
     * 设置统计日期
     *
     * @param statDate 统计日期
     */
    /**
     * 统计日期
     */
    @Column(name = "stat_date")
    var statDate: Date? = null

}