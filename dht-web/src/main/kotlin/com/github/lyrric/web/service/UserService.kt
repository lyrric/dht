package com.github.lyrric.web.service

import com.github.lyrric.web.entity.UserRecord

interface UserService {

    fun recordUser(record: UserRecord)
}