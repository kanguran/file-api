package com.hrblizz.fileapi

import com.hrblizz.fileapi.model.File
import com.hrblizz.fileapi.model.enumeration.FileSource
import java.time.Instant
import java.util.UUID

object TestDataFactory {
    val NOW = Instant.now()
    val TOKEN1 = UUID.fromString("c835ccd4-4624-4bc7-a878-4048d6acb7ae")
    val file =
        byteArrayOf(77, 121, 32, 116, 101, 115, 116, 32, 70, 105, 108, 101)

    fun getFile() =
        File().apply {
            token = TOKEN1
            name = "String"
            contentType = "image/jpeg"
            meta = "{creatorEmployeeId: 1}"
            source = FileSource.TIMESHEET
            expireTime = NOW
            createTime = NOW
            content = file
            size = file.size.toLong()
        }
}
