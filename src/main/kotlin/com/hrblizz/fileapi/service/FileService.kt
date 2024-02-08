package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.library.JsonUtil
import com.hrblizz.fileapi.model.File
import com.hrblizz.fileapi.model.FileMeta
import com.hrblizz.fileapi.model.enumeration.FileSource
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

    fun getFilesMetasResponse(tokens: FileMeta): FileResponse<Map<String, Any>> {
        var uuidList: ArrayList<UUID> = ArrayList()

        tokens.tokens.onEach {
            uuidList.add(UUID.fromString(it))
        }

        val files = entityRepository.findAllById(uuidList)

        if (files.count() > 0) {
            // TODO Return list of metas
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

    fun saveFiles(
        files: MultipartFile,
        meta: String?,
        source: FileSource,
        expireTime: Date?,
    ): FileResponse<Map<String, Any>> {
        var metaJson = meta
        try {
            metaJson = meta?.let { JsonUtil.toJson(meta) }
        } catch (e: Error) {
            return FileResponse(
                mapOf(
                    "" to String,
                ),
                HttpStatus.BAD_REQUEST.value(),
            )
        }

        val newFileUUID = getFreeUUID()

        entityRepository.save(
            File().also {
                it.token = newFileUUID
                it.name = files.originalFilename
                it.contentType = files.contentType
                it.meta = metaJson
                it.source = source
                it.createTime = Date()
                it.expireTime = expireTime
                it.content = files.bytes
                it.size = files.size
            },
        )

        return FileResponse(
            mapOf(
                newFileUUID.toString() to String,
            ),
            HttpStatus.OK.value(),
        )
        // TODO retrieve Error response.
    }

    fun deleteFile(token: UUID): FileResponse<Map<String, Any>> {
        /**
         1. check UUID is valid
         2. check file exists with given UUID
         3. Delete file by Uuid.
         */
        if (getFileResponse(token).status.equals(HttpStatus.OK.value())) {
            // TODO load metadata
            // try
            entityRepository.deleteById(token)

            return FileResponse(
                mapOf(
                    "ok" to true,
                ),
                HttpStatus.OK.value(),
            )
        }

        return FileResponse(
            mapOf(
                "ok" to false,
            ),
            listOf(ErrorMessage("Delete failed", HttpStatus.NOT_FOUND.value().toString())),
            HttpStatus.NOT_FOUND.value(),
        )
    }
}
