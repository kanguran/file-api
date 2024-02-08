package com.hrblizz.fileapi.model

import org.springframework.data.annotation.Id

class FileMeta {
    @Id
    lateinit var tokens: List<String>
}
