package com.hrblizz.fileapi.payload.response

class FileMetaResponse<T> {
    var files: T? = null

    var errors: List<ErrorMessage>? = null

    var status: Int = 0
        private set

    constructor(files: T?, status: Int) : this(files, null, status) {}

    constructor(files: T?, errors: List<ErrorMessage>?, status: Int) {
        this.files = files
        this.errors = errors
        this.status = status
    }
}
