package it.trade.android.sdk.model


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.util.*

class TradeItLinkedBrokerCache(private val context: Context) {
    internal var gson = Gson()

    fun cache(linkedBroker: TradeItLinkedBrokerParcelable) {
        val linkedBrokerSerializedJson = gson.toJson(linkedBroker)

        val sharedPreferences = context.getSharedPreferences(TRADE_IT_SDK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, HashSet())
        val userId = linkedBroker.linkedLogin!!.userId

        if (linkedBrokerCache!!.contains(userId)) {
            editor.putString(LINKED_BROKER_CACHE_KEY_PREFIX + userId, linkedBrokerSerializedJson)
        } else {
            linkedBrokerCache.add(userId)
            editor.putString(LINKED_BROKER_CACHE_KEY_PREFIX + userId, linkedBrokerSerializedJson)
        }

        editor.putStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, linkedBrokerCache)
        editor.apply()
    }

    fun syncFromCache(linkedBroker: TradeItLinkedBrokerParcelable) {
        val userId = linkedBroker.linkedLogin!!.userId
        val sharedPreferences = context.getSharedPreferences(TRADE_IT_SDK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        val linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, HashSet())

        if (linkedBrokerCache!!.contains(userId)) {
            val linkedBrokerSerialized = sharedPreferences.getString(LINKED_BROKER_CACHE_KEY_PREFIX + userId, "")
            try {
                val linkedBrokerDeserialized = gson.fromJson<TradeItLinkedBrokerParcelable>(linkedBrokerSerialized, TradeItLinkedBrokerParcelable::class.java)
                linkedBroker.accounts = linkedBrokerDeserialized.accounts
                linkedBroker.accountsLastUpdated = linkedBrokerDeserialized.accountsLastUpdated

                if (linkedBrokerDeserialized.isAccountLinkDelayedError) {
                    linkedBroker.error = linkedBrokerDeserialized.error
                }

                for (linkedBrokerAccount in linkedBroker.accounts) {
                    linkedBrokerAccount.linkedBroker = linkedBroker
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fails to deserialize to TradeItLinkedBrokerParcelable", e)
                val editor = sharedPreferences.edit()
                editor.remove(LINKED_BROKER_CACHE_KEY_PREFIX + userId)
                linkedBrokerCache.remove(userId)
                editor.putStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, linkedBrokerCache)
                editor.apply()
                linkedBroker.accounts = ArrayList()
            }

        }
    }

    fun removeFromCache(linkedBroker: TradeItLinkedBrokerParcelable) {
        val sharedPreferences = context.getSharedPreferences(TRADE_IT_SDK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val linkedBrokerCache = sharedPreferences.getStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, HashSet())
        val userId = linkedBroker.linkedLogin!!.userId

        if (linkedBrokerCache!!.contains(userId)) {
            editor.remove(LINKED_BROKER_CACHE_KEY_PREFIX + userId)
            linkedBrokerCache.remove(userId)
            editor.putStringSet(LINKED_BROKER_CACHE_KEY_PREFIX, linkedBrokerCache)
            editor.apply()
        }
    }

    companion object {
        val LINKED_BROKER_CACHE_KEY_PREFIX = "TRADE_IT_LINKED_BROKER_CACHE_"
        val TRADE_IT_SDK_SHARED_PREFS_KEY = "TRADE_IT_SDK_SHARED_PREFS_KEY"
        private val TAG = TradeItLinkedBrokerCache::class.java.getName()
    }
}
