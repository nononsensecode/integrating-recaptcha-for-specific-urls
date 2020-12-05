package com.nononsensecode.spring.security.recaptcha

enum class ReCaptchaErrorCode(
    val code: String
) {
    MissingInputSecret("missing-input-secret"),
    InvalidInputSecret("invalid-input-secret"),
    MissingInputResponse("missing-input-response"),
    InvalidInputResponse("invalid-input-response"),
    BadRequest("bad-request"),
    TimeoutOrDuplicate("timeout-or-duplicate");

    companion object {
        @JvmStatic
        fun parse(errorCode: String?): ReCaptchaErrorCode {
            return values().find { it.code.equals(errorCode, true) }
                ?: throw NoSuchFieldException()
        }
    }
}
