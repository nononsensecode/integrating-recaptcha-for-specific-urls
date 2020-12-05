package com.nononsensecode.spring.security.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("/api/v1/greetings/")
class GreetingsController {

    @GetMapping("/hello-world")
    fun helloWorld(): ResponseEntity<String> {
        return ResponseEntity("Hello World!", HttpStatus.OK)
    }

    @GetMapping("/hello-in-spanish")
    fun helloInSpanish(): ResponseEntity<String> {
        return ResponseEntity("Adios", HttpStatus.OK)
    }

    @GetMapping("name/{name}")
    fun helloWithName(@PathVariable("name") name: String): ResponseEntity<String> {
        return ResponseEntity("Hello $name", HttpStatus.OK)
    }
}