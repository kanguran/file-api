package com.hrblizz.fileapi.model

import org.springframework.data.annotation.Id
import java.util.Date
import java.util.UUID

class File {
    @Id
    lateinit var token: UUID
    lateinit var name: String
    lateinit var contentType: String
    lateinit var meta: String // JSON of additional meta. Example: {"creatorEmployeeId": 1}

    // TODO create Source enum once all the values are known.
    lateinit var source: String // timesheet, mss, hrb, ...
    var expireTime: Date? = null
    lateinit var createTime: Date

    // TODO lateinit var content: File  // file content
    var size: Long = 0
}
