package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.model.enumeration.FileSource
import com.hrblizz.fileapi.payload.response.FileResponse
import com.hrblizz.fileapi.service.FileService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.Date
import java.util.UUID

@RestController
class FileController(
    private val fileService: FileService,
) {
    /*File download endpoint*

    GET /file/{token}

    returns the file in the body + additional headers:

    > HTTP 200
    X-Filename: "example.pdf"
    X-Filesize: "525"
    X-CreateTime: "2019-11-21T15:42:22Z"
    Content-Type: "application/pdf"*/

    @GetMapping("/file/{token}")
    fun getFile(
        @PathVariable token: UUID,
    ): FileResponse<Map<String, Any>> {
        return fileService.getFileResponse(token)
    }

/*    *File upload*

    POST /files

    It's a multipart/form-data request with the following form fields:

    - name
    - contentType
    - meta # JSON of additional meta. Example: {"creatorEmployeeId": 1}
    - source # timesheet, mss, hrb, ...
    - expireTime # optional
    - content # file content

    And the response is JSON:
    > HTTP 201
    {
        "token": "file-token-1"
    }

      name and contentType request fields are not needed because they can be read from MultipartFile
*/
    @PostMapping("/files", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun handleFileUpload(
        @RequestPart("content") files: MultipartFile,
        @RequestPart("meta") meta: String,
        @RequestPart("source") source: String,
// TODO @RequestPart("source") source: FileSource,
// TODO @RequestPart("expireTime") expireTime: Date?,
        @RequestPart("expireTime") expireTime: String,
    ): FileResponse<Map<String, Any>> {
        return fileService.saveFiles(files, meta, FileSource.valueOf(source), Date())
    }

/*  *GET file metdata endpoint*
    User can request metadata for the token, multiple tokens metadata can be queried for in a single request*

    POST /files/metas
    {
        "tokens": [
        "file-token-1",
        ...
        ]
    }

    Response;

    >HTTP 200
    {
        "files": {
        "file-token-1": {
        "token": "file-token-1",
        "filename": "Example.pdf",
        "size": 525,
        "contentType": "application/pdf",
        "createTime": "2019-11-21T15:42:22Z", // file upload time
        "meta": {
        "creatorEmployeeId": 1
    }
    },
        ...
    }
    }*/

    @PostMapping("/files/metas")
    fun getFilesMetas(
        @RequestPart("tokens") tokens: List<String>,
    ): FileResponse<Map<String, Any>> {
        // TODO> call getFilesMetasResponse
        return fileService.getFileResponse(UUID.fromString(tokens.last()))
    }

    @DeleteMapping("/file/{token}")
    fun deleteFile(
        @PathVariable token: String,
    ): FileResponse<Map<String, Any>> {
        // TODO *GET file metadata endpoint*
        return fileService.deleteFile(UUID.fromString(token))
    }
}
