package com.nononsensecode.spring.security.config

import com.nononsensecode.spring.security.recaptcha.RecaptchaValidatingFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.context.support.beans
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.web.client.RestTemplate

@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    @Suppress("DEPRECATION")
    fun userService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val user1 = User
            .withUsername("user1")
            .password(passwordEncoder.encode("user1password"))
            .roles("ADMIN")
            .build()
        val noOpEncoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance()
        val user2 =  User
            .withUsername("user2")
            .password(noOpEncoder.encode("user2password"))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user1, user2)
    }

    @Bean
    @Suppress("DEPRECATION")
    fun passwordEncoder(): PasswordEncoder {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder() as DelegatingPasswordEncoder
        val noOpEncoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance()
        encoder.setDefaultPasswordEncoderForMatches(noOpEncoder)
        return encoder
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Order(1)
    @Configuration
    class ReCaptchaConfiguration: WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity?) {
            http {
                securityMatcher("/api/v1/greetings/hello-world")
                addFilterAt(RecaptchaValidatingFilter(), LogoutFilter::class.java)
                authorizeRequests {
                    authorize(anyRequest, permitAll)
                }
                cors {  }
            }
        }
    }

    @Configuration
    class FormLoginConfiguration: WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity?) {
            http {
                authorizeRequests {
                    authorize("/api/v1/greetings/hello-in-spanish", hasRole("USER"))
                    authorize("/api/v1/greetings/name/**", hasRole("ADMIN"))
                    authorize(anyRequest, authenticated)
                }
                formLogin {  }
                cors {  }
            }
        }
    }

}