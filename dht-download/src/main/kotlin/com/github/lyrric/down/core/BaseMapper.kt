package com.github.lyrric.down.core

import tk.mybatis.mapper.common.Mapper
import tk.mybatis.mapper.common.MySqlMapper

/**
 * Created on 2019-08-21.
 * @author wangxiaodong
 */
interface BaseMapper<T> :Mapper<T>,MySqlMapper<T>