package com.github.lyrric.web.service.impl

import com.github.lyrric.web.entity.UserRecord
import com.github.lyrric.web.mapper.UserRecordMapper
import com.github.lyrric.web.service.UserService
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class UserServiceImpl : UserService {

    @Resource
    private lateinit var userRecordMapper: UserRecordMapper

    override fun recordUser(record: UserRecord) {
        record.addTime = Date()
        record.id = null
        userRecordMapper.insert(record)

    }
}