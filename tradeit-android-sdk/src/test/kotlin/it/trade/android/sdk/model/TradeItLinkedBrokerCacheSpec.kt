package it.trade.android.sdk.model

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import it.trade.model.reponse.TradeItBrokerAccount
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse
import it.trade.model.request.TradeItOAuthAccessTokenRequest
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.anySet
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.contains
import java.util.*
import kotlin.collections.HashSet

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeItLinkedBrokerCacheSpec {
    val context: Context = mock()
    val linkedBrokerCache = TradeItLinkedBrokerCache(context)
    val apiClient: TradeItApiClientParcelable = mock()
    val sharedPreferences: SharedPreferences = mock()
    val editor: Editor = mock()
    val userId = "My userId"
    var linkedLogin: TradeItLinkedLoginParcelable? = null
    val oAuthAccessTokenRequest = TradeItOAuthAccessTokenRequest(
        "My Api Key",
        "oauth verifier"
    )
    val OAuthAccessTokenResponse = TradeItOAuthAccessTokenResponse()

    @BeforeEach
    fun init() {
        clearInvocations(editor)
        OAuthAccessTokenResponse.userId = userId
        OAuthAccessTokenResponse.userToken = "My userToken"
        linkedLogin = TradeItLinkedLoginParcelable(oAuthAccessTokenRequest, OAuthAccessTokenResponse)

        whenever(context.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE)))
            .thenReturn(sharedPreferences)

        whenever(sharedPreferences.edit()).thenReturn(editor)
    }

    @Nested
    inner class CacheTestCases {
        @Test
        fun `Cache handles a linked broker not yet cached with an empty cache`() {
            // given a linked broker with one account
            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin!!, linkedBrokerCache)
            val account = TradeItBrokerAccount()
            account.name = "My Account Name"
            account.accountNumber = "My Account Number"
            account.accountBaseCurrency = "My Account base currency"
            val account1 = TradeItLinkedBrokerAccountParcelable(linkedBroker, account)
            account1.balance = TradeItBalanceParcelable()
            account1.balance?.availableCash = 20000.0
            linkedBroker.accountsLastUpdated = Date()
            linkedBroker.accounts = arrayListOf(account1)

            // and an empty cache

            whenever(sharedPreferences.getStringSet(anyString(), eq(HashSet<String>()))).thenReturn(HashSet())

            var stored = 0
            var linkedBrokerCacheJson: Set<String>? = null
            whenever(editor.putStringSet(anyString(), anySet())).then {
                linkedBrokerCacheJson = it.getArgument<Set<String>>(1)
                editor
            }
            var linkedBrokerAccountCacheJson: String? = null
            whenever(editor.putString(anyString(), anyString())).thenAnswer {
                linkedBrokerAccountCacheJson = it.getArgument<String?>(1)
                editor
            }

            whenever(editor.apply()).then {
                stored++
            }

            // when caching the linkedBroker
            linkedBrokerCache.cache(linkedBroker)

            // then expects the linkedBroker to be serialized and stored
            Assertions.assertEquals(stored, 1)
            Assertions.assertTrue(linkedBrokerCacheJson!!.size == 1)
            Assertions.assertTrue(linkedBrokerCacheJson!!.contains(userId))
            Assertions.assertNotEquals(
                linkedBrokerAccountCacheJson!!.indexOf("accountsLastUpdated"), -1
            )
            Assertions.assertTrue(
                linkedBrokerAccountCacheJson!!.contains(linkedBroker.accounts[0].accountName!!)
            )
            Assertions.assertTrue(
                linkedBrokerAccountCacheJson!!.contains(linkedBroker.accounts[0].accountNumber!!)
            )
            Assertions.assertTrue(
                linkedBrokerAccountCacheJson!!.contains(
                    linkedBroker.accounts[0].accountBaseCurrency!!
                )
            )
        }

        @Test
        fun `Cache update a linked broker already cached`() {
            // given a linked broker with one account
            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin!!, linkedBrokerCache);
            val account = TradeItBrokerAccount()
            account.name = "My Account Name"
            account.accountNumber = "My Account Number"
            account.accountBaseCurrency = "My Account base currency"

            val account1 = TradeItLinkedBrokerAccountParcelable(linkedBroker, account)
            account1.balance = TradeItBalanceParcelable()
            account1.balance?.availableCash = 20000.0
            linkedBroker.accountsLastUpdated = Date()
            linkedBroker.accounts = arrayListOf(account1)

            // and an already cached linkedBroker
            whenever(sharedPreferences.getStringSet(anyString(), eq(HashSet<String>()))).then {
                val set = HashSet<String>()
                set.add(userId)
                set
            }

            var stored = 0
            var linkedBrokerCacheJson: Set<String>? = null
            whenever(editor.putStringSet(anyString(), anySet())).then {
                linkedBrokerCacheJson = it.getArgument<Set<String>>(1)
                editor
            }
            var linkedBrokerAccountCacheJson: String? = null
            whenever(editor.putString(anyString(), anyString())).then {
                linkedBrokerAccountCacheJson = it.getArgument<String>(1)
                editor
            }
            whenever(editor.apply()).then {
                stored++
            }


            // when caching the linkedBroker
            linkedBrokerCache.cache(linkedBroker)

            // then expects the linkedBroker to be serialized and stored
            Assertions.assertEquals(stored, 1)
            Assertions.assertTrue(linkedBrokerCacheJson!!.size == 1)
            Assertions.assertTrue(linkedBrokerCacheJson!!.contains(userId))
            Assertions.assertNotEquals(
                linkedBrokerAccountCacheJson!!.indexOf(linkedBroker.accounts[0].accountName!!),
                -1
            )
            Assertions.assertNotEquals(
                linkedBrokerAccountCacheJson!!.indexOf(linkedBroker.accounts[0].accountNumber!!),
                -1
            )
            Assertions.assertNotEquals(
                linkedBrokerAccountCacheJson!!.indexOf(linkedBroker.accounts[0].accountBaseCurrency!!),
                -1
            )
            Assertions.assertNotEquals(
                linkedBrokerAccountCacheJson!!.indexOf("accountsLastUpdated"),
                -1
            )
            Assertions.assertNotEquals(
                linkedBrokerAccountCacheJson!!.indexOf("availableCash"),
                -1
            )
        }
    }

    @Nested
    inner class SyncFromCacheTestCases {
        @Test
        fun `SyncFromCache handles a linkedBroker cached`() {
            // given a linked broker loaded from the keystore
            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin!!, linkedBrokerCache)

            // and a linkedBroker cached
            whenever(sharedPreferences.getStringSet(anyString(), eq(HashSet<String>()))).then {
                val set = HashSet<String>()
                set.add(userId)
                set
            }
            whenever(sharedPreferences.getString(contains(userId), eq(""))).then {
                val linkedBrokerCached = TradeItLinkedBrokerParcelable(
                    apiClient,
                    linkedLogin!!,
                    linkedBrokerCache
                )
                val account = TradeItBrokerAccount()
                account.name = "My Account Name"
                account.accountNumber = "My Account Number"
                account.accountBaseCurrency = "My Account base currency"
                val account1 = TradeItLinkedBrokerAccountParcelable(linkedBrokerCached, account)
                account1.balance = TradeItBalanceParcelable()
                account1.balance!!.availableCash = 20000.0
                linkedBrokerCached.accountsLastUpdated = Date()
                linkedBrokerCached.accounts = arrayListOf(account1)
                Gson().toJson(linkedBrokerCached)
            }

            // when syncFromCache called
            linkedBrokerCache.syncFromCache(linkedBroker)

            // then expects the linkedBroker to be populated with the cache
            Assertions.assertEquals(linkedBroker.linkedLogin!!.userId, userId)
            Assertions.assertNotEquals(linkedBroker.accountsLastUpdated, null)
            Assertions.assertTrue(linkedBroker.accounts.size == 1)
            Assertions.assertEquals(linkedBroker.accounts[0].balance!!.availableCash, 20000.0)

            // and the linked broker is set on the linked broker account
            Assertions.assertEquals(linkedBroker.accounts[0].linkedBroker, linkedBroker)
        }

        @Test
        fun `SyncFromCache handles a linkedBroker non cached`() {
            // given a linked broker loaded from the keystore
            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin!!, linkedBrokerCache)

            // and This linkedBroker is not cached
            whenever(sharedPreferences.getStringSet(anyString(), eq(HashSet<String>()))).then {
                val set = HashSet<String>()
                set.add("an other userId")
                set
            }
            whenever(sharedPreferences.getString(contains("an other userId"), eq(""))).then {
                val linkedBrokerCached = TradeItLinkedBrokerParcelable(
                    apiClient,
                    linkedLogin!!,
                    linkedBrokerCache
                )
                linkedBrokerCached.linkedLogin!!.userId = "an other userId"
                val account1 = TradeItLinkedBrokerAccountParcelable(linkedBrokerCached, mock())
                account1.accountName = "My Account Name"
                account1.accountNumber = "My Account Number"
                account1.accountBaseCurrency = "My Account base currency"
                account1.balance = TradeItBalanceParcelable()
                account1.balance!!.availableCash = 20000.0
                linkedBrokerCached.accountsLastUpdated = Date()
                linkedBrokerCached.accounts = arrayListOf(account1)
                Gson().toJson(linkedBrokerCached)
            }

            // when syncFromCache called
            linkedBrokerCache.syncFromCache(linkedBroker)

            // then expects the linkedBroker to not be populated with the cache
            Assertions.assertEquals(linkedBroker.linkedLogin!!.userId, userId)
            Assertions.assertEquals(linkedBroker.accountsLastUpdated, null)
            Assertions.assertEquals(linkedBroker.accounts.size, 0)
        }
    }

    @Nested
    inner class RemoveCacheTestCases {
        @Test
        fun `removeFromCache handles a linkedBroker cached`() {
            // given a linked broker loaded from the keystore
            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin!!, linkedBrokerCache)

            // and a linkedBroker cached
            val set = HashSet<String>()
            whenever(sharedPreferences.getStringSet(anyString(), eq(HashSet<String>()))).then {
                set.add(userId)
                set
            }

            whenever(sharedPreferences.getString(contains(userId), eq(""))).then {
                val linkedBrokerCached = TradeItLinkedBrokerParcelable(
                    apiClient,
                    linkedLogin!!,
                    linkedBrokerCache
                )
                val account1 = TradeItLinkedBrokerAccountParcelable(linkedBrokerCached, mock())
                account1.accountName = "My Account Name"
                account1.accountNumber = "My Account Number"
                account1.accountBaseCurrency = "My Account base currency"
                account1.balance = TradeItBalanceParcelable()
                account1.balance!!.availableCash = 20000.0
                linkedBrokerCached.accountsLastUpdated = Date()
                linkedBrokerCached.accounts = arrayListOf(account1)
                Gson().toJson(linkedBrokerCached)
            }

            // when removing from cache
            linkedBrokerCache.removeFromCache(linkedBroker)

            // then linkedBroker was removed from the set of string
            Assertions.assertEquals(set.size, 0)

            // and expects the following method called
            verify(editor, times(1))
                .putStringSet(TradeItLinkedBrokerCache.LINKED_BROKER_CACHE_KEY_PREFIX, set)

            verify(editor, times(1))
                .remove(TradeItLinkedBrokerCache.LINKED_BROKER_CACHE_KEY_PREFIX + userId)

            verify(editor, times(1)).apply()
        }
    }
}
