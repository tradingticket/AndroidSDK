package it.trade.android.sdk.model;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class TradeItLinkedBrokerCache {
    public static final String LINKED_BROKER_CACHE_KEY_PREFIX = "TRADE_IT_LINKED_BROKER_CACHE_";
    public static final String TRADE_IT_SDK_SHARED_PREFS_KEY = "TRADE_IT_SDK_SHARED_PREFS_KEY";
    Gson gson = new Gson();
    private Context context;

    public TradeItLinkedBrokerCache(Context context) {
        this.context = context;
    }

    void cache(TradeItLinkedBrokerParcelable linkedBroker) {
        String linkedBrokerSerializedJson = gson.toJson(linkedBroker);

        SharedPreferences sharedPreferences = context.getSharedPreferences(TRADE_IT_SDK_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, new HashSet<String>());
        String userId = linkedBroker.getLinkedLogin().userId;

        if (linkedBrokerCache.contains(userId)) {
            editor.putString(LINKED_BROKER_CACHE_KEY_PREFIX + userId, linkedBrokerSerializedJson);
        } else {
            linkedBrokerCache.add(userId);
            editor.putString(LINKED_BROKER_CACHE_KEY_PREFIX + userId, linkedBrokerSerializedJson);
        }

        editor.putStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, linkedBrokerCache);
        editor.apply();
    }

    public void syncFromCache(TradeItLinkedBrokerParcelable linkedBroker) {
        String userId = linkedBroker.getLinkedLogin().userId;
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRADE_IT_SDK_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        Set<String> linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, new HashSet<String>());

        if (linkedBrokerCache.contains(userId)) {
            String linkedBrokerSerialized = sharedPreferences.getString(LINKED_BROKER_CACHE_KEY_PREFIX + userId, "");
            TradeItLinkedBrokerParcelable linkedBrokerDeserialized = gson.fromJson(linkedBrokerSerialized, TradeItLinkedBrokerParcelable.class);
            linkedBroker.setAccounts(linkedBrokerDeserialized.getAccounts());
            linkedBroker.setAccountsLastUpdated(linkedBrokerDeserialized.getAccountsLastUpdated());

            for (TradeItLinkedBrokerAccountParcelable linkedBrokerAccount: linkedBroker.getAccounts()) {
                linkedBrokerAccount.setLinkedBroker(linkedBroker);
            }
        }
    }

    public void removeFromCache(TradeItLinkedBrokerParcelable linkedBroker) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRADE_IT_SDK_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, new HashSet<String>());
        String userId = linkedBroker.getLinkedLogin().userId;

        if (linkedBrokerCache.contains(userId)) {
            editor.remove(LINKED_BROKER_CACHE_KEY_PREFIX + userId);
            linkedBrokerCache.remove(userId);
            editor.putStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, linkedBrokerCache);
            editor.apply();
        }
    }
}
