package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.AppIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.annotation.Resource

abstract class AbstractControllerIT : AppIntegrationTest() {
    @Resource
    private val ctx: WebApplicationContext? = null

    protected var mockMvc: MockMvc? = null

    @BeforeEach
    @Throws(Exception::class)
    fun beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx!!).build()
        MockitoAnnotations.initMocks(this)
    }
}
