package com.nononsensecode.spring.security.recaptcha

data class RecaptchaParams(
    val secret: String,
    val response: String,
    val remoteIp: String
)