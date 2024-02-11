package com.hrblizz.fileapi.payload.response

class FileUploadResponse<T> {
    var token: String? = null

    var errors: List<ErrorMessage>? = null

    var status: Int = 0
        private set

    constructor(token: String?, status: Int) : this(token, null, status) {}

    constructor(token: String?, errors: List<ErrorMessage>?, status: Int) {
        this.token = token
        this.errors = errors
        this.status = status
    }
}
