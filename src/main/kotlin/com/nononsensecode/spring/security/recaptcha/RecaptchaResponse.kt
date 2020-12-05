package com.nononsensecode.spring.security.recaptcha

import com.fasterxml.jackson.annotation.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("success", "score", "action", "challenge_ts", "hostname", "error-codes")
data class ReCaptchaResponse(
    @JsonProperty("success")
    val success: Boolean?,

    @JsonProperty("score")
    val score: Double?,

    @JsonProperty("action")
    val action: String?,

    @JsonProperty("challenge_ts")
    val challengeTs: String?,

    @JsonProperty("hostname")
    val hostname: String?,

    @JsonProperty("error-codes")
    val errorCodes: Array<ReCaptchaErrorCode> = emptyArray()
) {
    @JsonIgnore
    fun errorCodesAsList(): List<String> {
        return this.errorCodes.map { it.code }
    }

    @JsonProperty("success")
    fun isSuccess(): Boolean {
        return success ?: false
    }

    @JsonIgnore
    fun isSuspicious(required: Double) = (this.score ?: 0.0) < required
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReCaptchaResponse

        if (success != other.success) return false
        if (score != other.score) return false
        if (action != other.action) return false
        if (challengeTs != other.challengeTs) return false
        if (hostname != other.hostname) return false
        if (!errorCodes.contentEquals(other.errorCodes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = success?.hashCode() ?: 0
        result = 31 * result + (score?.hashCode() ?: 0)
        result = 31 * result + (action?.hashCode() ?: 0)
        result = 31 * result + (challengeTs?.hashCode() ?: 0)
        result = 31 * result + (hostname?.hashCode() ?: 0)
        result = 31 * result + errorCodes.contentHashCode()
        return result
    }
}
