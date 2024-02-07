package com.hrblizz.fileapi.payload.response

class FileResponse<T> {
    var data: T? = null

    var errors: List<ErrorMessage>? = null

    var status: Int = 0
        private set

    constructor(data: T?, status: Int) : this(data, null, status) {}

    constructor(data: T?, errors: List<ErrorMessage>?, status: Int) {
        this.data = data
        this.errors = errors
        this.status = status
    }
}
