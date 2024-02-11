package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.repository.FileRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FileController_FilesIT(
    @Autowired val fileRepository: FileRepository,
    @Autowired val fileController: FileController,
) : AbstractControllerIT() {
    @AfterEach
    fun afterEach() {
        fileRepository.deleteAll()
    }

    @Test
    fun testFileSavingWorksThroughFileController() {
        // Given

    }


}
