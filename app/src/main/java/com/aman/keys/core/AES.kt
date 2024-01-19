package com.aman.keys.core

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/* TODO Checked on 12/10/2022
    All is ok
 */
class AES {
    private val KEY_SIZE = 256
    @Throws(Exception::class)
    fun init() {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(KEY_SIZE)
        key = generator.generateKey()
    }



    @Throws(Exception::class)
    private fun encrypt(message: String): String {
        val messageInBytes = message.toByteArray()
        val encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(T_LEN, IV)
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val encryptedBytes = encryptionCipher.doFinal(messageInBytes)
        return encode(encryptedBytes)
    }

    @Throws(Exception::class)
    private fun decrypt(encryptedMessage: String?): String {
        val messageInBytes = decode(encryptedMessage)
        val decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(T_LEN, IV)
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec)
        val decryptedBytes = decryptionCipher.doFinal(messageInBytes)
        return String(decryptedBytes)
    }

    fun singleEncryption(data: String): String {
        return try {
            encrypt(data)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun singleDecryption(encryptionString: String?): String {
        return try {
            decrypt(encryptionString)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
    fun encode(data: ByteArray?): String {
        return Base64.getEncoder().encodeToString(data)
    }

    fun decode(data: String?): ByteArray {
        return Base64.getDecoder().decode(data)
    }

    private fun exportKeys() {
//        System.err.println("SecretKey : " + encode(key!!.encoded))
//        System.err.println("IV : " + encode(IV))
    }

    companion object {
        private var sInstance: AES? = null
        const val TAG = "AES"
        private var key: SecretKey? = null
        private const val T_LEN = 128
        private lateinit var IV: ByteArray

        fun initFromStrings(aesKey: String, aesIv: String) {
            key = SecretKeySpec(sInstance!!.decode(aesKey), "AES")
            IV = sInstance!!.decode(aesIv)
        }

        fun getInstance(aesKey: String, aes_Iv: String): AES? {
            if (sInstance == null) {
                sInstance = AES()
            }
            initFromStrings(aesKey, aes_Iv)
            return sInstance
        }
    }
}