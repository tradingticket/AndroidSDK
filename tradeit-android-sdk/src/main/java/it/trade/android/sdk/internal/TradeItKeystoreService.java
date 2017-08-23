package it.trade.android.sdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import it.trade.android.sdk.exceptions.TradeItDeleteLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceCreateKeyException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceDeleteKeyException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceEncryptException;
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItSaveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItUpdateLinkedLoginException;
import it.trade.android.sdk.model.TradeItLinkedLoginParcelable;


public class TradeItKeystoreService {

    private final SecretKey secretKey;
    private final static String SECRET_KEY_FILE_NAME = "TRADE_IT_SECRET_KEY";
    private final Context context;
    private static final String TAG = TradeItKeystoreService.class.getName();
    private SharedPreferences sharedPreferences;

    public static final String TRADE_IT_SHARED_PREFS_KEY = "TRADE_IT_SHARED_PREFS_KEY";
    private static final String TRADE_IT_LINKED_BROKERS_KEY = "TRADE_IT_LINKED_BROKERS_KEY";

    public TradeItKeystoreService(Context context) throws TradeItKeystoreServiceCreateKeyException {
        this.context = context;

        try {
            this.sharedPreferences = context.getSharedPreferences(TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
            this.secretKey = createKeyIfNotExists();
        } catch (Exception e) {
            throw new TradeItKeystoreServiceCreateKeyException("Error creating key store with TradeItKeystoreService", e);
        }
    }

    public void saveLinkedLogin(TradeItLinkedLoginParcelable linkedLogin, String accountLabel) throws TradeItSaveLinkedLoginException {
        try {
            linkedLogin.label = accountLabel;
            Gson gson = new Gson();
            String linkedLoginJson = gson.toJson(linkedLogin);

            Set<String> linkedLoginEncryptedJsonSet = new HashSet<String>(sharedPreferences.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, new HashSet<String>()));
            String encryptedString = encryptString(linkedLoginJson);
            linkedLoginEncryptedJsonSet.add(encryptedString);

            sharedPreferences.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, linkedLoginEncryptedJsonSet)
                    .commit();
        } catch (Exception e) {
            throw new TradeItSaveLinkedLoginException("Error saving linkedLogin for accountLabel: " + accountLabel, e);
        }
    }

    public List<TradeItLinkedLoginParcelable> getLinkedLogins() throws TradeItRetrieveLinkedLoginException {
        try {
            Set<String> linkedLoginEncryptedJsonSet = sharedPreferences.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, new HashSet<String>());

            List<TradeItLinkedLoginParcelable> linkedLoginList = new ArrayList<>();
            Gson gson = new Gson();

            for (String linkedLoginEncryptedJson : linkedLoginEncryptedJsonSet) {
                String linkedLoginJson = decryptString(linkedLoginEncryptedJson);
                TradeItLinkedLoginParcelable linkedLogin = gson.fromJson(linkedLoginJson, TradeItLinkedLoginParcelable.class);
                linkedLoginList.add(linkedLogin);
            }

            return linkedLoginList;

        } catch (Exception e) {
            throw new TradeItRetrieveLinkedLoginException("Error getting linkedLogins ", e);
        }
    }

    public void updateLinkedLogin(TradeItLinkedLoginParcelable linkedLogin) throws TradeItUpdateLinkedLoginException {
        try {
            Set<String> linkedLoginEncryptedJsonSet = new HashSet<String>(sharedPreferences.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, new HashSet<String>()));
            Gson gson = new Gson();

            for (String linkedLoginEncryptedJson : linkedLoginEncryptedJsonSet) {
                String linkedLoginJson = decryptString(linkedLoginEncryptedJson);
                if (linkedLoginJson.contains(linkedLogin.userId)) {
                    linkedLoginJson = gson.toJson(linkedLogin);
                    String encryptedString = encryptString(linkedLoginJson);
                    linkedLoginEncryptedJsonSet.remove(linkedLoginEncryptedJson);
                    linkedLoginEncryptedJsonSet.add(encryptedString);
                    break;
                }
            }
            sharedPreferences.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, linkedLoginEncryptedJsonSet)
                    .commit();
        } catch (Exception e) {
            throw new TradeItUpdateLinkedLoginException("Error updating linkedLogin " + linkedLogin.userId, e);
        }
    }

    public void deleteLinkedLogin(TradeItLinkedLoginParcelable linkedLogin) throws TradeItDeleteLinkedLoginException {
        try {
            Set<String> linkedLoginEncryptedJsonSet = new HashSet<String>(sharedPreferences.getStringSet(TRADE_IT_LINKED_BROKERS_KEY, new HashSet<String>()));

            for (String linkedLoginEncryptedJson : linkedLoginEncryptedJsonSet) {
                String linkedLoginJson = decryptString(linkedLoginEncryptedJson);
                if (linkedLoginJson.contains(linkedLogin.userId)) {
                    linkedLoginEncryptedJsonSet.remove(linkedLoginEncryptedJson);
                    break;
                }
            }

            sharedPreferences.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, linkedLoginEncryptedJsonSet)
                    .commit();
        } catch (Exception e) {
            throw new TradeItDeleteLinkedLoginException("Error deleting linkedLogin " + linkedLogin.userId, e);
        }
    }

    public void deleteAllLinkedLogins() throws TradeItDeleteLinkedLoginException {
        try {
            sharedPreferences.edit()
                    .putStringSet(TRADE_IT_LINKED_BROKERS_KEY, Collections.<String>emptySet())
                    .commit();
        } catch (Exception e) {
            throw new TradeItDeleteLinkedLoginException("Error deleting all linkedLogins ", e);
        }
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        return keyGenerator.generateKey();
    }

    private SecretKey loadSecretKeyFromPrivateData() throws IOException {
        SecretKey secretKey = null;
        FileInputStream fis = null;
        try {
            fis = this.context.openFileInput(SECRET_KEY_FILE_NAME);
            byte[] keyBuffer = new byte[(int) fis.getChannel().size()];
            fis.read(keyBuffer);
            secretKey = new SecretKeySpec(keyBuffer, 0, keyBuffer.length, "AES");
        } catch(FileNotFoundException e) {
                return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Log.e(TAG, "loadSecretKeyFromPrivateData - error closing fis", ex);
                }
            }
        }
        return secretKey;
    }

    private void storeSecretKeyInPrivateData(SecretKey secretKey) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = this.context.openFileOutput(SECRET_KEY_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(secretKey.getEncoded());
            fos.close();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Log.e(TAG, "storeSecretKeyInPrivateData - error closing fos", ex);
                }
            }
        }
    }

    private SecretKey createKeyIfNotExists() throws TradeItKeystoreServiceCreateKeyException {
        SecretKey secretKey = null;
        try {
            secretKey = loadSecretKeyFromPrivateData();
            if (secretKey == null) {
                long startTime = System.currentTimeMillis();
                secretKey = generateKey();
                storeSecretKeyInPrivateData(secretKey);
                deleteOldKey();
                Log.d("TRADEIT", "=====> Elapsed time to generate key: " + (System.currentTimeMillis() - startTime) + "ms");
            }
        } catch (Exception e) {
            throw new TradeItKeystoreServiceCreateKeyException("Error creating key with TradeItKeystoreService", e);
        }
        return secretKey;
    }

    public void regenerateKey() throws TradeItKeystoreServiceCreateKeyException, TradeItKeystoreServiceDeleteKeyException {
        deleteKey();
        createKeyIfNotExists();
    }

    private void deleteKey() throws TradeItKeystoreServiceDeleteKeyException{
            this.context.deleteFile(SECRET_KEY_FILE_NAME);
    }

    private void deleteOldKey() throws TradeItKeystoreServiceDeleteKeyException{
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            String alias = "TRADE_IT_LINKED_BROKERS_ALIAS";
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias);
                deleteAllLinkedLogins();
            }
        } catch (Exception e) {
            //ignore it
        }
    }

    private String encryptString(String stringToEncrypt) throws TradeItKeystoreServiceEncryptException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);

            byte[] encrypted = cipher.doFinal(stringToEncrypt.getBytes("UTF-8"));

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            throw new TradeItKeystoreServiceEncryptException("Error encrypting the string: "+stringToEncrypt, e);
        }
    }

    private String decryptString(String encryptedString) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            byte[] original = cipher.doFinal(Base64.decode(encryptedString, Base64.DEFAULT));
            return new String(original, "UTF-8");
        } catch (Exception e) {
            //don't throw an exception, to not crash existing app
            Log.e(TAG, "Error decrypting the string: " + encryptedString, e);
            return encryptedString;
        }
    }

}
