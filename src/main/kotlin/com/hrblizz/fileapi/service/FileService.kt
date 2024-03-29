package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.library.JsonUtil
import com.hrblizz.fileapi.model.File
import com.hrblizz.fileapi.model.FileMeta
import com.hrblizz.fileapi.model.enumeration.FileSource
import com.hrblizz.fileapi.payload.response.ErrorMessage
import com.hrblizz.fileapi.payload.response.FileMetaResponse
import com.hrblizz.fileapi.payload.response.FileResponse
import com.hrblizz.fileapi.payload.response.FileUploadResponse
import com.hrblizz.fileapi.repository.FileRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
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

    fun getFileBodyResponse(token: UUID): ResponseEntity<Any> {
        try {
            val file = entityRepository.findById(token)
            if (file.isPresent) {
                val resource = ByteArrayResource(file.get().content)
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.get().contentType.toString()))
                    .header("X-Filename", file.get().name)
                    .header("X-Filesize", file.get().size.toString())
                    .header("X-CreateTime", file.get().createTime.toString())
                    .body(resource)
            } else {
                return ResponseEntity.ok().body(null)
            }
        } catch (e: Exception) {
            return ResponseEntity.ok().body(null)
        }
    }

    fun getFilesMetasResponse(tokens: FileMeta): FileMetaResponse<Map<String, Any>> {
        var uuidList: ArrayList<UUID> = ArrayList()

        try {
            tokens.tokens.onEach {
                uuidList.add(UUID.fromString(it))
            }

            val files = entityRepository.findAllById(uuidList)

            if (files.count() > 0) {
                val filesMap = mutableMapOf<String, MutableMap<String, String>>()

                files.associateTo(filesMap) {
                    it.token.toString() to
                        mutableMapOf(
                            "token" to it.token.toString(),
                            "filename" to it.name,
                            "size" to it.size.toString(),
                            "contentType" to it.contentType,
                            "createTime" to it.createTime.toString(),
                            "meta" to it.meta.toString(),
                        )
                }

                return FileMetaResponse(
                    filesMap,
                    HttpStatus.OK.value(),
                )
            } else {
                return FileMetaResponse(
                    mapOf("ok" to false),
                    listOf(error("FileMeta data not found")),
                    HttpStatus.NOT_FOUND.value(),
                )
            }
        } catch (e: Exception) {
            return FileMetaResponse(
                mapOf("ok" to false),
                listOf(error("FileMeta data search operation failed: " + e.message)),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
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
        expireTime: Instant?,
    ): FileUploadResponse<Map<String, Any>> {
        var metaJson = meta
        try {
            metaJson = meta?.let { JsonUtil.toJson(meta) }
        } catch (e: Error) {
            return composeFileUploadResponse(
                "",
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
                    it.createTime = Instant.now()
                    it.expireTime = expireTime
                    it.content = files.bytes
                    it.size = files.size
                },
            )

            return composeFileUploadResponse(
                newFileUUID.toString(),
                "",
                HttpStatus.OK,
            )
        } catch (e: Exception) {
            return composeFileUploadResponse(
                newFileUUID.toString(),
                "Files save operation failed: " + e.message,
                HttpStatus.SERVICE_UNAVAILABLE,
            )
        }
    }

    fun deleteFile(token: UUID): FileMetaResponse<Map<String, Any>> {
        /**
         1. check UUID is valid
         2. check file exists with given UUID
         3. Delete file by Uuid.
         */
        try {
            var fileTokens = FileMeta()
            fileTokens.tokens = listOf(token.toString())
            var fileResponse = getFilesMetasResponse(fileTokens)

            if (getFileResponse(token).status == HttpStatus.OK.value()) {
                entityRepository.deleteById(token)
            }
            return fileResponse
        } catch (e: Exception) {
            return composeFileMetaResponse(
                mapOf(),
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
        if (httpStatusCode == HttpStatus.OK) {
            return FileResponse(
                response,
                null,
                httpStatusCode.value(),
            )
        } else {
            return FileResponse(
                response,
                listOf(ErrorMessage(message, httpStatusCode.value().toString())),
                httpStatusCode.value(),
            )
        }
    }

    fun composeFileMetaResponse(
        response: Map<String, Any>,
        message: String,
        httpStatusCode: HttpStatus,
    ): FileMetaResponse<Map<String, Any>> {
        if (httpStatusCode == HttpStatus.OK) {
            return FileMetaResponse(
                response,
                null,
                httpStatusCode.value(),
            )
        } else {
            return FileMetaResponse(
                response,
                listOf(ErrorMessage(message, httpStatusCode.value().toString())),
                httpStatusCode.value(),
            )
        }
    }

    fun composeFileUploadResponse(
        response: String,
        message: String,
        httpStatusCode: HttpStatus,
    ): FileUploadResponse<Map<String, Any>> {
        if (httpStatusCode == HttpStatus.OK) {
            return FileUploadResponse(
                response,
                null,
                httpStatusCode.value(),
            )
        } else {
            return FileUploadResponse(
                response,
                listOf(ErrorMessage(message, httpStatusCode.value().toString())),
                httpStatusCode.value(),
            )
        }
    }
}
