package it.trade.android.sdk.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.model.*
import spock.lang.Specification

class TradeItLinkedBrokerCacheSpec extends Specification {
    Context context = Mock(Context)
    TradeItLinkedBrokerCache linkedBrokerCache = new TradeItLinkedBrokerCache(context);
    TradeItApiClient apiClient = Mock(TradeItApiClient)
    SharedPreferences sharedPreferences = Mock(SharedPreferences)
    SharedPreferences.Editor editor = Mock(SharedPreferences.Editor)
    String userId = "My userId"
    TradeItLinkedLogin linkedLogin

    def setup() {
        TradeItLinkLoginRequest linkLoginRequest = new TradeItLinkLoginRequest("my id", "my password", "broker")
        TradeItLinkLoginResponse linkLoginResponse = new TradeItLinkLoginResponse()
        linkLoginResponse.userId = userId
        linkLoginResponse.userToken = "My userToken"
        linkedLogin = new TradeItLinkedLogin(linkLoginRequest, linkLoginResponse)


        context.getSharedPreferences(_, Context.MODE_PRIVATE) >> {
            return sharedPreferences
        }

        sharedPreferences.edit() >> {
            return editor
        }
    }

    def "Cache handles a linked broker not yet cached with an empty cache"() {
        given: "a linked broker with one account"
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);
            TradeItLinkedBrokerAccount account1 = new TradeItLinkedBrokerAccount(linkedBroker, Mock(TradeItBrokerAccount));
            account1.accountName = "My Account Name"
            account1.accountNumber = "My Account Number"
            account1.accountBaseCurrency = "My Account base currency"
            account1.balance = new TradeItGetAccountOverviewResponse()
            account1.balance.availableCash = 20000
            linkedBroker.accountsLastUpdated = new Date()
            linkedBroker.accounts = [account1]

        and: "an empty cache"

            sharedPreferences.getStringSet(_, new HashSet<String>()) >> {
                return new HashSet<>()
            }

            int stored = 0
            Set<String> linkedBrokerCacheJson = null
            editor.putStringSet(_, _) >> { args ->
                linkedBrokerCacheJson = args[1]
                return;
            }
            String linkedBrokerAccountCacheJson = null
            editor.putString(_, _) >> { args ->
                linkedBrokerAccountCacheJson = args[1]
                return
            }
            editor.apply() >> {
                stored++
                return;
            }


        when: "caching the linkedBroker"
            linkedBrokerCache.cache(linkedBroker)

        then: "expects the linkedBroker to be serialized and stored"
            stored == 1
            linkedBrokerCacheJson.size() == 1
            linkedBrokerCacheJson.contains(userId)
            linkedBrokerAccountCacheJson.count("accountsLastUpdated") ==1
            linkedBrokerAccountCacheJson.contains(linkedBroker.accounts[0].accountName)
            linkedBrokerAccountCacheJson.contains(linkedBroker.accounts[0].accountNumber)
            linkedBrokerAccountCacheJson.contains(linkedBroker.accounts[0].accountBaseCurrency)

    }

    def "Cache update a linked broker already cached"() {
        given: "a linked broker with one account"
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);
            TradeItLinkedBrokerAccount account1 = new TradeItLinkedBrokerAccount(linkedBroker, Mock(TradeItBrokerAccount));
            account1.accountName = "My Account Name"
            account1.accountNumber = "My Account Number"
            account1.accountBaseCurrency = "My Account base currency"
            account1.balance = new TradeItGetAccountOverviewResponse()
            account1.balance.availableCash = 20000
            linkedBroker.accountsLastUpdated = new Date()
            linkedBroker.accounts = [account1]

        and: "an already cached linkedBroker"

            sharedPreferences.getStringSet(_, new HashSet<String>()) >> {
                Set<String> set = new HashSet<>()
                set.add(userId)
                return set
            }

            int stored = 0
            Set<String> linkedBrokerCacheJson = null
            editor.putStringSet(_, _) >> { args ->
                linkedBrokerCacheJson = args[1]
                return;
            }
            String linkedBrokerAccountCacheJson = null
            editor.putString(_, _) >> { args ->
                linkedBrokerAccountCacheJson = args[1]
                return
            }
            editor.apply() >> {
                stored++
                return;
            }


        when: "caching the linkedBroker"
            linkedBrokerCache.cache(linkedBroker)

        then: "expects the linkedBroker to be serialized and stored"
            stored == 1
            linkedBrokerCacheJson.size() == 1
            linkedBrokerCacheJson.contains(userId)
            linkedBrokerAccountCacheJson.count(linkedBroker.accounts[0].accountName) == 1
            linkedBrokerAccountCacheJson.count(linkedBroker.accounts[0].accountNumber) == 1
            linkedBrokerAccountCacheJson.count(linkedBroker.accounts[0].accountBaseCurrency) == 1
            linkedBrokerAccountCacheJson.count("accountsLastUpdated") ==1
            linkedBrokerAccountCacheJson.count("availableCash") == 1
    }

    def "SyncFromCache handles a linkedBroker cached"() {
        given: "a linked broker loaded from the keystore"
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);

        and: "a linkedBroker cached"
            sharedPreferences.getStringSet(_, new HashSet<String>()) >> {
                Set<String> set = new HashSet<>()
                set.add(userId)
                return set
            }
            sharedPreferences.getString({it.contains(userId)}, "") >> {
                TradeItLinkedBroker linkedBrokerCached = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);
                TradeItLinkedBrokerAccount account1 = new TradeItLinkedBrokerAccount(linkedBrokerCached, Mock(TradeItBrokerAccount));
                account1.accountName = "My Account Name"
                account1.accountNumber = "My Account Number"
                account1.accountBaseCurrency = "My Account base currency"
                account1.balance = new TradeItGetAccountOverviewResponse()
                account1.balance.availableCash = 20000
                linkedBrokerCached.accountsLastUpdated = new Date()
                linkedBrokerCached.accounts = [account1]
                return new Gson().toJson(linkedBrokerCached)
            }

        when: "syncFromCache called: "
            linkedBrokerCache.syncFromCache(linkedBroker)

        then: "expects the linkedBroker to be populated with the cache"
            linkedBroker.linkedLogin.userId == userId
            linkedBroker.accountsLastUpdated != null
            linkedBroker.accounts.size() == 1
            linkedBroker.accounts[0].balance.availableCash == 20000

        and: "the linked broker is set on the linked broekr account"
            linkedBroker.accounts[0].linkedBroker == linkedBroker
    }

    def "SyncFromCache handles a linkedBroker non cached"() {
        given: "a linked broker loaded from the keystore"
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);

        and: "This linkedBroker is not cached"
            sharedPreferences.getStringSet(_, new HashSet<String>()) >> {
                Set<String> set = new HashSet<>()
                set.add("an other userId")
                return set
            }
            sharedPreferences.getString({it.contains("an other userId")}, "") >> {
                TradeItLinkedBroker linkedBrokerCached = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);
                linkedBrokerCached.linkedLogin.userId == "an other userId"
                TradeItLinkedBrokerAccount account1 = new TradeItLinkedBrokerAccount(linkedBrokerCached, Mock(TradeItBrokerAccount));
                account1.accountName = "My Account Name"
                account1.accountNumber = "My Account Number"
                account1.accountBaseCurrency = "My Account base currency"
                account1.balance = new TradeItGetAccountOverviewResponse()
                account1.balance.availableCash = 20000
                linkedBrokerCached.accountsLastUpdated = new Date()
                linkedBrokerCached.accounts = [account1]
                return new Gson().toJson(linkedBrokerCached)
            }

        when: "syncFromCache called: "
            linkedBrokerCache.syncFromCache(linkedBroker)

        then: "expects the linkedBroker to be populated with the cache"
            linkedBroker.linkedLogin.userId == userId
            linkedBroker.accountsLastUpdated == null
            linkedBroker.accounts.size() == 0
    }

    def "removeFromCache handles a linkedBroker cached"() {
        given: "a linked broker loaded from the keystore"
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);

        and: "a linkedBroker cached"
            Set<String> set = new HashSet<>()
            sharedPreferences.getStringSet(_, new HashSet<String>()) >> {
                set.add(userId)
                return set
            }

            sharedPreferences.getString({it.contains(userId)}, "") >> {
                TradeItLinkedBroker linkedBrokerCached = new TradeItLinkedBroker(apiClient, linkedLogin, linkedBrokerCache);
                TradeItLinkedBrokerAccount account1 = new TradeItLinkedBrokerAccount(linkedBrokerCached, Mock(TradeItBrokerAccount));
                account1.accountName = "My Account Name"
                account1.accountNumber = "My Account Number"
                account1.accountBaseCurrency = "My Account base currency"
                account1.balance = new TradeItGetAccountOverviewResponse()
                account1.balance.availableCash = 20000
                linkedBrokerCached.accountsLastUpdated = new Date()
                linkedBrokerCached.accounts = [account1]
                return new Gson().toJson(linkedBrokerCached)
            }

        when: "removing from cache"
            linkedBrokerCache.removeFromCache(linkedBroker)

        then: "linkedBroker was removed from the set of string"
            set.size() == 0

        and: "expects the following method called"
            1 * editor.putStringSet(TradeItLinkedBrokerCache.LINKED_BROKER_CACHE_KEY_PREFIX, set)
            1 * editor.remove(TradeItLinkedBrokerCache.LINKED_BROKER_CACHE_KEY_PREFIX + userId)
            1 * editor.apply();
    }
}
