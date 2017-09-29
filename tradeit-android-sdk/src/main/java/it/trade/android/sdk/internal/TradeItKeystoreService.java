package it.trade.android.sdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import it.trade.android.sdk.exceptions.TradeItDeleteLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceCreateKeyException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceDecryptException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceDeleteKeyException;
import it.trade.android.sdk.exceptions.TradeItKeystoreServiceEncryptException;
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItSaveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItUpdateLinkedLoginException;
import it.trade.android.sdk.model.TradeItLinkedLoginParcelable;


public class TradeItKeystoreService {

    private final String alias;
    private final KeyStore keyStore;
    private final int keySize;
    private final Context context;
    private static final String keyStoreType = "AndroidKeyStore";
    private SharedPreferences sharedPreferences;

    private static final String TRADE_IT_LINKED_BROKERS_ALIAS = "TRADE_IT_LINKED_BROKERS_ALIAS";
    public static final String TRADE_IT_SHARED_PREFS_KEY = "TRADE_IT_SHARED_PREFS_KEY";
    private static final String TRADE_IT_LINKED_BROKERS_KEY = "TRADE_IT_LINKED_BROKERS_KEY";

    private static boolean KEY_STORE_ENABLED = true;

    public TradeItKeystoreService(String alias, Context context) throws TradeItKeystoreServiceCreateKeyException {
        this(alias, context, 2560);
    }

    public TradeItKeystoreService(String alias, Context context, int keySize) throws TradeItKeystoreServiceCreateKeyException {
        this.alias = alias;
        this.keySize = keySize;
        this.context = context;

        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null);

            createKeyIfNotExists();

            this.sharedPreferences = context.getSharedPreferences(TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        } catch (Exception e) {
            throw new TradeItKeystoreServiceCreateKeyException("Error creating key store with TradeItKeystoreService", e);
        }
    }

    public void saveLinkedLogin(TradeItLinkedLoginParcelable linkedLogin, String accountLabel) throws TradeItSaveLinkedLoginException {
        if (KEY_STORE_ENABLED) {
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
    }

    public List<TradeItLinkedLoginParcelable> getLinkedLogins() throws TradeItRetrieveLinkedLoginException {
        if (KEY_STORE_ENABLED) {
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
        } else {
            return new ArrayList<>();
        }
    }

    public void updateLinkedLogin(TradeItLinkedLoginParcelable linkedLogin) throws TradeItUpdateLinkedLoginException {
        if (KEY_STORE_ENABLED) {
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
    }

    public void deleteLinkedLogin(TradeItLinkedLoginParcelable linkedLogin) throws TradeItDeleteLinkedLoginException {
        if (KEY_STORE_ENABLED) {
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

    public boolean keyExists() {
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            // this should never happen, as long as keyStore was loaded in the constructor.
            return false;
        }
    }

    private void createKeyIfNotExists() throws TradeItKeystoreServiceCreateKeyException {
        try {
            if (!keyStore.containsAlias(alias)) {
                long startTime = System.currentTimeMillis();
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 50);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=TradeIt Link Accounts, O=TradeIt, C=US"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .setKeySize(keySize)
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();

                Log.d("TRADEIT", "=====> Elapsed time to generate key: " + (System.currentTimeMillis() - startTime) + "ms");
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                KEY_STORE_ENABLED = false;
            } else {
                throw new TradeItKeystoreServiceCreateKeyException("Error creating key with TradeItKeystoreService", e);
            }
        }
    }

    public void regenerateKey() throws TradeItKeystoreServiceCreateKeyException, TradeItKeystoreServiceDeleteKeyException {
        deleteKey();
        createKeyIfNotExists();
    }

    private void deleteKey() throws TradeItKeystoreServiceDeleteKeyException{
        try {
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias);
            }
        } catch (Exception e) {
            throw new TradeItKeystoreServiceDeleteKeyException("Error deleting key with TradeItKeystoreService", e);

        }
    }

    private String encryptString(String stringToEncode) throws TradeItKeystoreServiceEncryptException {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);

            cipherOutputStream.write(stringToEncode.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);

        } catch (Exception e) {
            throw new TradeItKeystoreServiceEncryptException("Error encrypting the string: "+stringToEncode, e);
        }
    }

    private String decryptString(String stringToDecode) throws TradeItKeystoreServiceDecryptException {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(stringToDecode, Base64.DEFAULT)), output);

            List<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }
            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            return new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            throw new TradeItKeystoreServiceDecryptException("Error decrypting the string: "+stringToDecode, e);
        }
    }

}
