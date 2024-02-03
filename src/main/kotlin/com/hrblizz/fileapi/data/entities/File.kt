package com.hrblizz.fileapi.data.entities

import org.springframework.data.annotation.Id

class File {
    // TODO name is not ID
    @Id
    lateinit var name: String
    lateinit var value: String
}
