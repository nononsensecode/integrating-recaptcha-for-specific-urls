package com.nononsensecode.spring.security.recaptcha

import mu.KotlinLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.servlet.HandlerExceptionResolver
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

private val logger = KotlinLogging.logger {  }

class RecaptchaValidatingFilter: GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val grecaptcha = request.getParameter("grecaptcha") ?: ""
        try {
            if (grecaptcha.isBlank()) {
                logger.error("There is no recaptcha token is present")
                throw IllegalAccessException("Recaptcha token is not present")
            }

            val restTemplate = getBean(RestTemplate::class.java, request)
            val verifyRequest = createRequest(grecaptcha, request)
            val verificationResponse = restTemplate
                .postForObject("https://www.google.com/recaptcha/api/siteverify",
                    verifyRequest, ReCaptchaResponse::class.java)!!
            logger.debug(verificationResponse)

            if (!verificationResponse.isSuccess() || (verificationResponse.score ?: 0.0) < 0.9) {
                logger.error("Recaptcha score (${verificationResponse.score}) is less than 0.9")
                throw IllegalAccessException("Only human are allowed to access this URL")
            }

            logger.info("Recaptcha verification was a success")
            chain.doFilter(request, response)
        } catch (e: Exception) {
            logger.error(e)
            logger.error("Exception occurred and now redirecting it to resolver")
            val handler = getBean("handlerExceptionResolver", HandlerExceptionResolver::class.java, request)
            handler.resolveException(request as HttpServletRequest, response as HttpServletResponse, null, e)
        }
    }

    private fun getWepAppContext(request: ServletRequest): WebApplicationContext {
        val servletContext = request.servletContext
        return WebApplicationContextUtils.getWebApplicationContext(servletContext)!!
    }

    private fun <T> getBean(clazz: Class<T>, request: ServletRequest): T {
        return getWepAppContext(request).getBean(clazz)
    }

    private fun <T> getBean(qualifier: String, clazz: Class<T>, request: ServletRequest): T {
        return getWepAppContext(request).getBean(qualifier, clazz)
    }

    private fun createRequest(response: String, request: ServletRequest): HttpEntity<LinkedMultiValueMap<String, String>> {
        val params = LinkedMultiValueMap<String, String>()
        val recaptchaProperties = getBean(RecaptchaProperties::class.java, request)
        params.add("secret", recaptchaProperties.secretKey)
        params.add("response", response)
        params.add("remoteip", clientIP(request))

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        return HttpEntity(params, headers)
    }

    private fun clientIP(servletRequest: ServletRequest): String {
        val xfHeader = (servletRequest as HttpServletRequest).getHeader("X-Forwarded-For")
        return if (xfHeader == null)
            servletRequest.remoteAddr
        else
            xfHeader.split(",")[0]
    }
}