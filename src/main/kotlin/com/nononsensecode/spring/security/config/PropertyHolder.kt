package com.nononsensecode.spring.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PropertyHolder(
    @Value("\${recaptcha.verifyUrl}")
    val verifyUrl: String
)