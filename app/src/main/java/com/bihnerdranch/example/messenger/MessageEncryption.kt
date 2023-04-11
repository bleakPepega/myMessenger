package com.bihnerdranch.example.messenger

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.utils.io.core.*
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.text.String
import kotlin.text.toByteArray

class MessageEncryption {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    init {
        keyPairGenerator.initialize(2048)
    }
    private val keyPair: KeyPair = keyPairGenerator.genKeyPair()
    val privateKey: PrivateKey = keyPair.private
    val publicKey: PublicKey = keyPair.public
    @RequiresApi(Build.VERSION_CODES.O)
    fun toPrivateKey(privateKeyString: String): PrivateKey? {
        val decodedKey = Base64.getDecoder().decode(privateKeyString.toByteArray(StandardCharsets.UTF_8))
        val keySpec = PKCS8EncodedKeySpec(decodedKey)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun toPublicKey(publicKeyString: String): PublicKey? {
        val decodedKey = Base64.getDecoder().decode(publicKeyString.toByteArray(StandardCharsets.UTF_8))
        val keySpec = X509EncodedKeySpec(decodedKey)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun keyToString(privateKey: PrivateKey): String? {
        val keyBytes = privateKey.encoded
        return Base64.getEncoder().encodeToString(keyBytes)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun keyToString(publicKey: PublicKey): String? {
        val keyBytes = publicKey.encoded
        return Base64.getEncoder().encodeToString(keyBytes)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun keyToString(publicKey: String): String? {
        val keyBytes = publicKey.toByteArray()
        println(Base64.getEncoder().encodeToString(keyBytes))
        return Base64.getEncoder().encodeToString(keyBytes)
    }

}
    fun encryptMessage(message: String, publicKey: PublicKey): ByteArray? {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(message.toByteArray())
    }
    fun decryptMessage(message: ByteArray?, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(message)
        return String(decryptedBytes)

    }