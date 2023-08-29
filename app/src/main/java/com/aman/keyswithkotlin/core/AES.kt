package com.aman.keyswithkotlin.core

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import com.aman.keyswithkotlin.core.Constants.AES_ALIES_NAME
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
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
//        val secretKey: Key = keysStoreInstance!!.getKey(AES_ALIES_NAME, null)
//        println("secretKey11: $secretKey")
        val spec = GCMParameterSpec(T_LEN, IV)
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val encryptedBytes = encryptionCipher.doFinal(messageInBytes)
        return encode(encryptedBytes)
    }

    @Throws(Exception::class)
    private fun decrypt(encryptedMessage: String?): String {
        val messageInBytes = decode(encryptedMessage)
        val decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding")
//        val secretKey: Key = keysStoreInstance!!.getKey(AES_ALIES_NAME, null)
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
        System.err.println("SecretKey : " + encode(key!!.encoded))
        System.err.println("IV : " + encode(IV))
    }

    companion object {
        private var sInstance: AES? = null
        private var keysStoreInstance:KeyStore? = null
        const val TAG = "AES"
        private var key: SecretKey? = null
        private const val T_LEN = 128
        private lateinit var IV: ByteArray

        fun initFromStrings(aesKey: String, aesIv: String) {
            key = SecretKeySpec(sInstance!!.decode(aesKey), "AES")
            IV = sInstance!!.decode(aesIv)
        }
        fun getKeyStoreInstance():KeyStore?{
            if (keysStoreInstance == null) {
                keysStoreInstance = KeyStore.getInstance("AndroidKeyStore").apply {
                    load(null)
                }
            }
            return keysStoreInstance
        }

        fun getInstance(aesKey: String, aes_Iv: String, enableDoubleEncryption:Boolean = true): AES? {
            if (sInstance == null) {
                sInstance = AES()
            }
            initFromStrings(aesKey, aes_Iv)
//            if (enableDoubleEncryption){
//                val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
//                    load(null)
//                }
//                val secretKey = keyStore.getKey(AES_ALIES_NAME, null) as SecretKey
//                println("secretKey: ${secretKey.encoded}")
//            }
            return sInstance
        }
    }

    fun storeAESKeyAndIVInKeystore(aesKeyAlias: String, aesKey: String) {
        // Initialize Android Keystore
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
//        // Generate AES Key
//        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
//        val keySpec = KeyGenParameterSpec.Builder(
//            aesKeyAlias,
//            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//        ).build()
//        keyGenerator.init(keySpec)
        val secretKey = SecretKeySpec(decode(aesKey), "AES")
        println("secretKey: ${secretKey}")

        // Store AES Key in Keystore
        val keyProtectionParams = KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()

        val keyEntry = KeyStore.SecretKeyEntry(secretKey)
        keyStore.setEntry(aesKeyAlias, keyEntry, keyProtectionParams)
    }

}