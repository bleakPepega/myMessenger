package com.bihnerdranch.example.messenger

import android.util.Log
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class MessageEncryption() {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    init {
        keyPairGenerator.initialize(2048)
    }
    val keyPair = keyPairGenerator.genKeyPair()
    val privateKey = keyPair.private
    val publicKey = keyPair.public
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
fun test() {

}
