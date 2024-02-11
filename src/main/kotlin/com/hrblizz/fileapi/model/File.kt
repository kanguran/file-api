package com.hrblizz.fileapi.model

import com.hrblizz.fileapi.model.enumeration.FileSource
import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.UUID

class File {
    @Id
    lateinit var token: UUID
    lateinit var name: String
    lateinit var contentType: String
    var meta: String? = null // JSON of additional meta. Example: {"creatorEmployeeId": 1}
    lateinit var source: FileSource
    var expireTime: Instant? = null
    lateinit var createTime: Instant
    lateinit var content: ByteArray
    var size: Long = 0
}
