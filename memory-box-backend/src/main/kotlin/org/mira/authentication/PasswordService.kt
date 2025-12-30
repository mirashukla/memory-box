package org.mira.authentication

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordService {

    private companion object {
        private const val ALGORITHM = "PBKDF2WithHmacSHA512"
        private const val ITERATIONS = 30_000
        private const val KEY_LENGTH = 128
        private const val SECRET = "SomeRandomSecret"
    }

    private fun generateRandomSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        return salt
    }

    data class SaltAndHash(val saltBase64: String, val hashHex: String)

    private fun generateHash(password: String, saltBase64: String): String {
        val salt = Base64.getDecoder().decode(saltBase64)
        val combinedSalt = salt + SECRET.toByteArray()

        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
        val key = factory.generateSecret(spec)
        return key.encoded.toHexString()
    }

    fun generateHashAndSalt(password: String): SaltAndHash {
        val saltBytes = generateRandomSalt()
        val saltBase64 = Base64.getEncoder().encodeToString(saltBytes)

        val hash = generateHash(password, saltBase64)

        return SaltAndHash(saltBase64, hash)
    }

    private fun String.hexToByteArray(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    fun verifyPassword(
        password: String,
        storedHashHex: String,
        storedSaltBase64: String,
    ): Boolean {
        val computedHashHex = generateHash(password, storedSaltBase64)

        val computedBytes = computedHashHex.hexToByteArray()
        val storedBytes = storedHashHex.hexToByteArray()

        return MessageDigest.isEqual(computedBytes, storedBytes)
    }
}