package com.hrblizz.fileapi.library

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.IOException
import java.text.SimpleDateFormat

object JsonUtil {
    /**
     * Safely writes the input object into a JSON string
     */
    fun toJson(
        obj: Any,
        usePrettyWriter: Boolean = false,
        formatDates: Boolean = false,
    ): String? {
        try {
            if (!isJSONValid(obj.toString())) {
                // TODO throw right exception
                throw error("Not valid json")
            }

            val mapper = ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

            if (formatDates) {
                mapper.dateFormat = SimpleDateFormat(com.hrblizz.fileapi.DATE_TIME_FORMAT)
            }

            var writer = mapper.writer()

            if (usePrettyWriter) {
                writer = writer.withDefaultPrettyPrinter()
            }
            return writer.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            // TODO Log Error
        }

        return null
    }

    fun isJSONValid(jsonInString: String?): Boolean {
        try {
            val mapper = ObjectMapper()
            mapper.readTree(jsonInString)
            return true
        } catch (e: IOException) {
            return false
        }
    }
}
