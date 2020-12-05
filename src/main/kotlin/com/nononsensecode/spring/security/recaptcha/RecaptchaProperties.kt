package com.nononsensecode.spring.security.recaptcha

import com.nononsensecode.spring.security.config.YamlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ConfigurationProperties(prefix = "recaptcha")
@PropertySource("classpath:recaptcha.yml", factory = YamlPropertySourceFactory::class)
class RecaptchaProperties {
    lateinit var secretKey: String
}