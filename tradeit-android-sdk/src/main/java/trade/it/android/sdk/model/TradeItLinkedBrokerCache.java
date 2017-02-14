package trade.it.android.sdk.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import static it.trade.tradeitapi.API.TradeItAccountLinker.TRADE_IT_SHARED_PREFS_KEY;


public class TradeItLinkedBrokerCache {
    private static final String LINKED_BROKER_CACHE_KEY = "TRADE_IT_LINKED_BROKER_CACHE";
    Gson gson = new Gson();

    void cache(Context context, TradeItLinkedBroker linkedBroker) {
            String linkedBrokerSerializedJson = gson.toJson(linkedBroker);

            SharedPreferences sharedPreferences = context.getSharedPreferences(TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Set<String> linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY, new HashSet<String>());
            String userId = linkedBroker.getLinkedAccount().userId;

            if (linkedBrokerCache.contains(userId)) {
                editor.putString(LINKED_BROKER_CACHE_KEY + userId, linkedBrokerSerializedJson);
            } else {
                linkedBrokerCache.add(userId);
                editor.putString(LINKED_BROKER_CACHE_KEY + userId, linkedBrokerSerializedJson);
            }

            editor.putStringSet(LINKED_BROKER_CACHE_KEY, linkedBrokerCache);
            editor.apply();
    }

    public void syncFromCache(Context context, TradeItLinkedBroker linkedBroker) {
        String userId = linkedBroker.getLinkedAccount().userId;
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        Set<String> linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY, new HashSet<String>());
        if (linkedBrokerCache.contains(userId)) {
            String linkedBrokerSerialized = sharedPreferences.getString(LINKED_BROKER_CACHE_KEY + userId, "");
            TradeItLinkedBroker linkedBrokerDeserialized = gson.fromJson(linkedBrokerSerialized, TradeItLinkedBroker.class);
            linkedBroker.setAccounts(linkedBrokerDeserialized.getAccounts());
            linkedBroker.setAccountsLastUpdated(linkedBrokerDeserialized.getAccountsLastUpdated());
        }
    }
}
