package it.trade.android.sdk.internal

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import it.trade.android.sdk.exceptions.*
import it.trade.android.sdk.model.TradeItLinkedLoginParcelable
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class TradeItKeystoreService @Throws(TradeItKeystoreServiceCreateKeyException::class)
constructor(private val context: Context) {

    private val secretKey: SecretKey
    private var sharedPreferences: SharedPreferences? = null

    val linkedLogins: List<TradeItLinkedLoginParcelable>
        @Throws(TradeItRetrieveLinkedLoginException::class)
        get() {
            try {
                val linkedLoginEncryptedJsonSet = sharedPreferences!!.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, HashSet())

                val linkedLoginList = ArrayList<TradeItLinkedLoginParcelable>()
                val gson = Gson()

                for (linkedLoginEncryptedJson in linkedLoginEncryptedJsonSet!!) {
                    val linkedLoginJson = decryptString(linkedLoginEncryptedJson)
                    val linkedLogin = gson.fromJson<TradeItLinkedLoginParcelable>(
                        linkedLoginJson,
                        TradeItLinkedLoginParcelable::class.java
                    )
                    linkedLoginList.add(linkedLogin)
                }

                return linkedLoginList

            } catch (e: Exception) {
                throw TradeItRetrieveLinkedLoginException("Error getting linkedLogins ", e)
            }

        }

    init {

        try {
            this.sharedPreferences = context.getSharedPreferences(TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
            this.secretKey = createKeyIfNotExists()
        } catch (e: Exception) {
            throw TradeItKeystoreServiceCreateKeyException("Error creating key store with TradeItKeystoreService", e)
        }

    }

    @Throws(TradeItSaveLinkedLoginException::class)
    fun saveLinkedLogin(linkedLogin: TradeItLinkedLoginParcelable, accountLabel: String) {
        try {
            linkedLogin.label = accountLabel
            val gson = Gson()
            val linkedLoginJson = gson.toJson(linkedLogin)

            val linkedLoginEncryptedJsonSet = HashSet(sharedPreferences!!.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, HashSet())!!)
            val encryptedString = encryptString(linkedLoginJson)
            linkedLoginEncryptedJsonSet.add(encryptedString)

            sharedPreferences!!.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, linkedLoginEncryptedJsonSet)
                    .commit()
        } catch (e: Exception) {
            throw TradeItSaveLinkedLoginException("Error saving linkedLogin for accountLabel: $accountLabel", e)
        }

    }

    @Throws(TradeItUpdateLinkedLoginException::class)
    fun updateLinkedLogin(linkedLogin: TradeItLinkedLoginParcelable) {
        try {
            val linkedLoginEncryptedJsonSet = HashSet(sharedPreferences!!.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, HashSet())!!)
            val gson = Gson()

            for (linkedLoginEncryptedJson in linkedLoginEncryptedJsonSet) {
                var linkedLoginJson = decryptString(linkedLoginEncryptedJson)
                if (linkedLoginJson.contains(linkedLogin.userId)) {
                    linkedLoginJson = gson.toJson(linkedLogin)
                    val encryptedString = encryptString(linkedLoginJson)
                    linkedLoginEncryptedJsonSet.remove(linkedLoginEncryptedJson)
                    linkedLoginEncryptedJsonSet.add(encryptedString)
                    break
                }
            }
            sharedPreferences!!.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, linkedLoginEncryptedJsonSet)
                    .commit()
        } catch (e: Exception) {
            throw TradeItUpdateLinkedLoginException("Error updating linkedLogin " + linkedLogin.userId, e)
        }

    }

    @Throws(TradeItDeleteLinkedLoginException::class)
    fun deleteLinkedLogin(linkedLogin: TradeItLinkedLoginParcelable) {
        try {
            val linkedLoginEncryptedJsonSet = HashSet(sharedPreferences!!.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, HashSet())!!)

            for (linkedLoginEncryptedJson in linkedLoginEncryptedJsonSet) {
                val linkedLoginJson = decryptString(linkedLoginEncryptedJson)
                if (linkedLoginJson.contains(linkedLogin.userId)) {
                    linkedLoginEncryptedJsonSet.remove(linkedLoginEncryptedJson)
                    break
                }
            }

            sharedPreferences!!.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, linkedLoginEncryptedJsonSet)
                    .commit()
        } catch (e: Exception) {
            throw TradeItDeleteLinkedLoginException("Error deleting linkedLogin " + linkedLogin.userId, e)
        }

    }

    @Throws(TradeItDeleteLinkedLoginException::class)
    fun deleteAllLinkedLogins() {
        try {
            sharedPreferences!!.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, emptySet<String>())
                    .commit()
        } catch (e: Exception) {
            throw TradeItDeleteLinkedLoginException("Error deleting all linkedLogins ", e)
        }

    }

    @Throws(IOException::class)
    private fun loadSecretKeyFromPrivateData(): SecretKey? {
        var secretKey: SecretKey?
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = this.context.openFileInput(SECRET_KEY_FILE_NAME)
            val keyBuffer = ByteArray(fileInputStream!!.channel.size().toInt())
            fileInputStream.read(keyBuffer)
            secretKey = SecretKeySpec(keyBuffer, 0, keyBuffer.size, "AES")
        } catch (e: FileNotFoundException) {
            return null
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "loadSecretKeyFromPrivateData - error closing FileInputStream for $SECRET_KEY_FILE_NAME", ex)
                }

            }
        }
        return secretKey
    }

    @Throws(IOException::class)
    private fun storeSecretKeyInPrivateData(secretKey: SecretKey) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = this.context.openFileOutput(SECRET_KEY_FILE_NAME, Context.MODE_PRIVATE)
            fileOutputStream!!.write(secretKey.encoded)
            fileOutputStream.close()
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "storeSecretKeyInPrivateData - error closing FileOutputStream for $SECRET_KEY_FILE_NAME", ex)
                }

            }
        }
    }

    @Throws(TradeItKeystoreServiceCreateKeyException::class)
    private fun createKeyIfNotExists(): SecretKey {
        var secretKey: SecretKey?
        try {
            secretKey = loadSecretKeyFromPrivateData()
            if (secretKey == null) {
                val startTime = System.currentTimeMillis()
                secretKey = generateKey()
                storeSecretKeyInPrivateData(secretKey)
                deleteOldKey()
                Log.d("TRADEIT", "=====> Elapsed time to generate key: " + (System.currentTimeMillis() - startTime) + "ms")
            }
        } catch (e: Exception) {
            throw TradeItKeystoreServiceCreateKeyException("Error creating key with TradeItKeystoreService", e)
        }

        return secretKey
    }

    @Throws(TradeItKeystoreServiceCreateKeyException::class, TradeItKeystoreServiceDeleteKeyException::class)
    fun regenerateKey() {
        deleteKey()
        createKeyIfNotExists()
    }

    @Throws(TradeItKeystoreServiceDeleteKeyException::class)
    private fun deleteKey() {
        this.context.deleteFile(SECRET_KEY_FILE_NAME)
    }

    @Throws(TradeItKeystoreServiceDeleteKeyException::class)
    private fun deleteOldKey() {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val alias = "TRADE_IT_LINKED_BROKERS_ALIAS"
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
                deleteAllLinkedLogins()
            }
        } catch (e: Exception) {
            //ignore it
        }

    }

    @Throws(TradeItKeystoreServiceEncryptException::class)
    private fun encryptString(stringToEncrypt: String): String {
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey)

            val encrypted = cipher.doFinal(stringToEncrypt.toByteArray(charset("UTF-8")))

            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            throw TradeItKeystoreServiceEncryptException("Error encrypting the string: $stringToEncrypt", e)
        }

    }

    private fun decryptString(encryptedString: String): String {
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey)
            val original = cipher.doFinal(Base64.decode(encryptedString, Base64.DEFAULT))
            return String(original, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            //don't throw an exception, to not crash existing app
            Log.e(TAG, "Error decrypting the string: $encryptedString", e)
            return encryptedString
        }

    }

    companion object {
        private val SECRET_KEY_FILE_NAME = "TRADE_IT_SECRET_KEY"
        private val TAG = TradeItKeystoreService::class.java.getName()

        @JvmField
        val TRADE_IT_SHARED_PREFS_KEY = "TRADE_IT_SHARED_PREFS_KEY"
        private val TRADE_IT_LINKED_BROKERS_KEY = "TRADE_IT_LINKED_BROKERS_KEY"

        @Throws(NoSuchAlgorithmException::class)
        private fun generateKey(): SecretKey {
            // Generate a 256-bit key
            val outputKeyLength = 256

            val secureRandom = SecureRandom()
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(outputKeyLength, secureRandom)
            return keyGenerator.generateKey()
        }
    }

}
