package com.hrblizz.fileapi.repository

import com.hrblizz.fileapi.model.File
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface FileRepository : MongoRepository<File, UUID>
