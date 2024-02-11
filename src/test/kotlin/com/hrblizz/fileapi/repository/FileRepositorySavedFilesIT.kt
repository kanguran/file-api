package com.hrblizz.fileapi.repository

import com.hrblizz.fileapi.TestDataFactory
import com.hrblizz.fileapi.controller.AbstractControllerIT
import com.hrblizz.fileapi.model.File
import com.hrblizz.fileapi.model.enumeration.FileSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FileRepositorySavedFilesIT(
    @Autowired val fileRepository: FileRepository,
) : AbstractControllerIT() {
    @AfterEach
    fun afterEach() {
        fileRepository.deleteAll()
    }

    @Test
    fun testFileSavingWorksThroughFileRepository() {
        // Given
        fileRepository.saveAll(
            listOf(
                TestDataFactory.getFile().apply {
                    token = TestDataFactory.TOKEN1
                    name = "String"
                    contentType = "image/jpeg"
                    meta = "{creatorEmployeeId: 1}"
                    source = FileSource.TIMESHEET
                    expireTime = TestDataFactory.NOW
                    createTime = TestDataFactory.NOW
                    content = TestDataFactory.file
                    size = TestDataFactory.file.size.toLong()
                },
            ),
        )

        // When
        val file: File = fileRepository.findById(TestDataFactory.TOKEN1).get()

        // Then
        Assertions.assertNotNull(file)
        Assertions.assertEquals(TestDataFactory.TOKEN1, file.token)
        Assertions.assertEquals(FileSource.TIMESHEET, file.source)
        Assertions.assertEquals(TestDataFactory.file.size.toLong(), file.size)
        // TODO Assertions.assertEquals(TestDataFactory.file.asUByteArray() , file.content.asUByteArray())
    }
}
