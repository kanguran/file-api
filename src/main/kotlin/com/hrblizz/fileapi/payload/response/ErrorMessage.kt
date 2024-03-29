package com.hrblizz.fileapi.payload.response

class ErrorMessage {
    var message: String? = null
    var code: String? = null

    constructor(message: String?) {
        this.message = message
    }

    constructor(message: String, code: String) {
        this.message = message
        this.code = code
    }
}
