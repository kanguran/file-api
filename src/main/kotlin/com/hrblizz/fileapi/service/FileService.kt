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
        try {
            val file = entityRepository.findById(token)
            if (file.isPresent) {
                return composeFileResponse(
                    mapOf("ok" to true),
                    "",
                    HttpStatus.OK,
                )
            } else {
                return composeFileResponse(
                    mapOf("ok" to false),
                    "File Not found",
                    HttpStatus.NOT_FOUND,
                )
            }
        } catch (e: Exception) {
            return composeFileResponse(
                mapOf("ok" to false),
                "Get File operation failed: " + e.message,
                HttpStatus.SERVICE_UNAVAILABLE,
            )
        }
    }

    fun getFilesMetasResponse(tokens: FileMeta): FileResponse<Map<String, Any>> {
        var uuidList: ArrayList<UUID> = ArrayList()

        try {
            tokens.tokens.onEach {
                uuidList.add(UUID.fromString(it))
            }

            val files = entityRepository.findAllById(uuidList)

            if (files.count() > 0) {
                // TODO Return list of metas
                return composeFileResponse(
                    mapOf("ok" to true),
                    "",
                    HttpStatus.NOT_FOUND,
                )
            } else {
                return composeFileResponse(
                    mapOf("ok" to false),
                    "FileMeta data not found",
                    HttpStatus.NOT_FOUND,
                )
            }
        } catch (e: Exception) {
            return composeFileResponse(
                mapOf("ok" to false),
                "FileMeta data search operation failed: " + e.message,
                HttpStatus.SERVICE_UNAVAILABLE,
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
            return composeFileResponse(
                mapOf("" to String),
                "Meta JSON parsing operation failed",
                HttpStatus.SERVICE_UNAVAILABLE,
            )
        }

        val newFileUUID = getFreeUUID()

        try {
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
            return composeFileResponse(
                mapOf(newFileUUID.toString() to String),
                "Files saved: ",
                HttpStatus.OK,
            )
        } catch (e: Exception) {
            return composeFileResponse(
                mapOf(newFileUUID.toString() to String),
                "Files save operation failed: " + e.message,
                HttpStatus.SERVICE_UNAVAILABLE,
            )
        }
    }

    fun deleteFile(token: UUID): FileResponse<Map<String, Any>> {
        /**
         1. check UUID is valid
         2. check file exists with given UUID
         3. Delete file by Uuid.
         */
        try {
            // TODO load metadata
            if (getFileResponse(token).status == HttpStatus.OK.value()) {
                entityRepository.deleteById(token)

                return composeFileResponse(mapOf("ok" to true), "Deleted", HttpStatus.OK)
            } else {
                return composeFileResponse(
                    mapOf("ok" to false),
                    "Files with Given token not found",
                    HttpStatus.NOT_FOUND,
                )
            }
        } catch (e: Exception) {
            return composeFileResponse(
                mapOf("ok" to false),
                "Files Delete operation failed:" + e.message,
                HttpStatus.SERVICE_UNAVAILABLE,
            )
        }
    }

    fun composeFileResponse(
        response: Map<String, Any>,
        message: String,
        httpStatusCode: HttpStatus,
    ): FileResponse<Map<String, Any>> {
        return FileResponse(
            response,
            listOf(ErrorMessage(message, httpStatusCode.value().toString())),
            httpStatusCode.value(),
        )
    }
}
