package org.mira.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import software.amazon.awssdk.services.dynamodb.streams.endpoints.internal.Value
import java.time.Instant
import java.util.Date

class TokenGenerator(
    secret: String,
    private val issuer: String = "mira-auth"
) {

    private val algorithm = Algorithm.HMAC256(secret)
    private val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun generateJwt(
        subject: String,
        audience: String = "mira-clients",
        expiresInSeconds: Long = 15 * 60 // 15 minutes
    ): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(expiresInSeconds)

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(subject)          // typically user id
            .withAudience(audience)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiry))
            .sign(algorithm)
    }

    fun generateRefreshToken(
        subject: String,
        audience: String = "mira-clients",
        expiresInSeconds: Long = 7 * 24 * 60 * 60 // 7 days
    ): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(expiresInSeconds)

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(subject)
            .withAudience(audience)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiry))
            .withClaim("type", "refresh")  // helps you distinguish token types
            .sign(algorithm)
    }

    fun validate(token: String?): Boolean {
        if (token == null) return false
        return try {
            verifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }
}
