package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.entities.File
import com.hrblizz.fileapi.data.repository.FileRepository
import com.hrblizz.fileapi.dto.FileResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class FileController(
    private val entityRepository: FileRepository,
) {
    // TODO getStatus should not Save data by SOLID principles.
    @RequestMapping("/status", method = [RequestMethod.GET])
    fun getStatus(): FileResponse<Map<String, Any>> {
        entityRepository.save(
            File().also {
                it.name = UUID.randomUUID().toString()
                it.value = "asd"
            },
        )

        return FileResponse(
            mapOf(
                "ok" to true,
            ),
            HttpStatus.OK.value(),
        )
    }
}
