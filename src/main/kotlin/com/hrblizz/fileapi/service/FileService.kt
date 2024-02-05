package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.model.File
import com.hrblizz.fileapi.payload.response.ErrorMessage
import com.hrblizz.fileapi.payload.response.FileResponse
import com.hrblizz.fileapi.repository.FileRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Date
import java.util.UUID

@Service
class FileService(
    var entityRepository: FileRepository,
) {
    fun getFileResponse(token: UUID): FileResponse<Map<String, Any>> {
        // TODO if File exists return FileResponse
        val file = entityRepository.findById(token)
        if (file.isPresent) {
            return FileResponse(
                mapOf(
                    "ok" to true,
                ),
                HttpStatus.OK.value(),
            )
        } else {
            return FileResponse(
                mapOf(
                    "ok" to false,
                ),
                listOf(ErrorMessage("File Not found", HttpStatus.NOT_FOUND.value().toString())),
                HttpStatus.NOT_FOUND.value(),
            )
        }
        // TODO return ErrorResponse on Error with code.
    }

    fun getFilesMetasResponse(tokens: List<String>): FileResponse<Map<String, Any>> {
        // TODO if File exists return FileResponse

        /* loop tokens
            getFileById
            populate Response
        end loop

        return Response           */

        val file = entityRepository.findById(UUID.fromString(tokens.last()))
        if (file.isPresent) {
            return FileResponse(
                mapOf(
                    "ok" to true,
                ),
                HttpStatus.OK.value(),
            )
        } else {
            return FileResponse(
                mapOf(
                    "ok" to false,
                ),
                listOf(ErrorMessage("File Not found", HttpStatus.NOT_FOUND.value().toString())),
                HttpStatus.NOT_FOUND.value(),
            )
        }
    }

    fun getFreeUUID(): UUID {
        val newUUID = UUID.randomUUID()
        if (entityRepository.findById(newUUID).isEmpty) {
            return newUUID // UUID is not in use.
        } else {
            return getFreeUUID() // try next UUID
        }
    }

    fun saveFiles(files: MultipartFile): FileResponse<Map<String, Any>> {
        val newFileUUID = getFreeUUID()
        entityRepository.save(
            File().also {
                it.token = newFileUUID
                it.name = newFileUUID.toString()
                it.contentType = files.contentType
                it.meta = "meta"
                it.source = "timesheet"
                it.createTime = Date()
                it.size = 222
            },
        )

        return FileResponse(
            mapOf(
                newFileUUID.toString() to String,
            ),
            HttpStatus.OK.value(),
        )
    }

    fun getDeleteResponse(token: UUID) {
        /**
         1. check UUID is valid
         2. check file exists with given UUID
         3. Delete file by Uuid.
         */
    }
}
