package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.data.repository.FileRepository
import org.springframework.stereotype.Service

@Service
class FileService(
    var entityRepository: FileRepository,
) {
    // TODO use FileService in FileController
}
