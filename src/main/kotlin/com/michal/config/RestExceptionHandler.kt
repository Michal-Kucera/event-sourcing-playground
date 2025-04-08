package com.michal.config

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(value = [Exception::class])
    fun handle(e: Exception): ResponseEntity<String> {
        e.printStackTrace()
        return badRequest().body(e.message)
    }
}
