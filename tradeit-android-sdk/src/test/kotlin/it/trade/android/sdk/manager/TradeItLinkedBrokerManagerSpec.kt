package it.trade.android.sdk.manager

import com.nhaarman.mockitokotlin2.*
import it.trade.android.sdk.internal.TradeItKeystoreService
import it.trade.android.sdk.model.*
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import it.trade.model.request.TradeItEnvironment
import it.trade.model.request.TradeItLinkedLogin
import it.trade.model.request.TradeItOAuthAccessTokenRequest
import junit.framework.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeItLinkedBrokerManagerSpec {

    private val keystoreService: TradeItKeystoreService = mock()
    private val linkedBrokerCache: TradeItLinkedBrokerCache = mock()
    private val apiClient: TradeItApiClientParcelable = mock()
    private val linkedBrokerManager = TradeItLinkedBrokerManager(apiClient, linkedBrokerCache, keystoreService, true);
    private val accountLabel = "My account label"
    private val myUserId = "My trade it userId"
    private val myUserToken = "My trade it userToken"
    private val apiKey = "test api key"

    @BeforeEach
    fun init() {
        clearInvocations(keystoreService, linkedBrokerCache, apiClient)
        whenever(keystoreService.linkedLogins).thenReturn(emptyList())
        whenever(apiClient.getEnvironment()).thenReturn(TradeItEnvironment.QA)
        whenever(apiClient.getApiKey()).thenReturn(apiKey)
    }

    @Nested
    inner class GetOAuthLoginPopupUrlForMobileTestCases {
        @Test
        fun `getOAuthLoginPopupUrlForMobile handles a successful response from trade it api`() {
            // given a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            val mySpecialUrl = "http://myspecialoauthurl.com?oAuthTempToken=2bae6cc8-8d37-4b4a-ae5e-6bbde9209ac4"
            whenever(apiClient.getOAuthLoginPopupUrlForMobile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), any())).then {
                val callback = it.getArgument<TradeItCallback<String>>(2)
                callback.onSuccess(mySpecialUrl)
            }

            // when calling getOAuthLoginPopupUrl
            var oAuthUrlResult: String? = null
            linkedBrokerManager.getOAuthLoginPopupUrl(
                "My broker 1",
                "my internal app callback",
                object : TradeItCallback<String> {
                    override fun onSuccess(oAuthUrl: String?) {
                        successCallBackCount++
                        oAuthUrlResult = oAuthUrl
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                }
            )

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and "expects the oAuthUrl to be populated"
            Assert.assertEquals(oAuthUrlResult, mySpecialUrl)
        }

        @Test
        fun `getOAuthLoginPopupUrlForMobile handles an error response from trade it api`() {
            // given an error response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            val errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            val shortMessage = "My error when linking broker"
            whenever(apiClient.getOAuthLoginPopupUrlForMobile(
                eq("My broker 1"),
                eq("my internal app callback"),
                any())).then {
                val callback = it.getArgument<TradeItCallback<String>>(2)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.BROKER_AUTHENTICATION_ERROR,
                        shortMessage,
                        null)
                )
            }

            // when calling getOAuthLoginPopupUrl
            var errorResult: TradeItErrorResult? = null
            linkedBrokerManager.getOAuthLoginPopupUrl(
                "My broker 1",
                "my internal app callback",
                object : TradeItCallback<String> {
                    override fun onSuccess(type: String?) {
                        successCallBackCount++
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                        errorResult = error
                    }
                })

            // then expects the errorCallback called once
            Assert.assertEquals(successCallBackCount, 0)
            Assert.assertEquals(errorCallBackCount, 1)

            // and expects a populated TradeItErrorResult
            Assert.assertEquals(errorResult!!.getErrorCode(), errorCode)
            Assert.assertEquals(errorResult!!.getShortMessage(), shortMessage)
        }
    }

    @Nested
    inner class LinkBrokerWithOAuthVerifierTestCases {
        @Test
        fun `linkBrokerWithOauthVerifier handles a successful response from trade it api`() {
            // given a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0

            whenever(apiClient.linkBrokerWithOauthVerifier(ArgumentMatchers.anyString(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItLinkedLogin>>(1)
                callback.onSuccess(TradeItLinkedLogin("My broker 1", myUserId, myUserToken))
            }


            // when calling linkBrokerWithOauthVerifier
            var linkedBrokerResult: TradeItLinkedBrokerParcelable? = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(
                accountLabel,
                "My oAuthVerifier",
                object : TradeItCallback<TradeItLinkedBrokerParcelable> {
                    override fun onSuccess(linkedBroker: TradeItLinkedBrokerParcelable?) {
                        successCallBackCount++
                        linkedBrokerResult = linkedBroker
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                })

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and saveLinkedLogin method was called
            verify(keystoreService, times(1)).saveLinkedLogin(any(), ArgumentMatchers.anyString())

            // and expects a linkedBroker containing userId and userToken
            Assert.assertEquals(linkedBrokerResult!!.linkedLogin!!.userId, myUserId)
            Assert.assertEquals(linkedBrokerResult!!.linkedLogin!!.userToken, myUserToken)
            Assert.assertEquals(linkedBrokerResult!!.linkedLogin!!.broker, "My broker 1")
        }

        @Test
        fun `linkBrokerWithOauthVerifier handles successful response from trade it api with an already existing userId (token update)`() {
            // given a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            whenever(apiClient.linkBrokerWithOauthVerifier(ArgumentMatchers.anyString(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItLinkedLogin>>(1)
                callback.onSuccess(TradeItLinkedLogin("My broker 1", myUserId, myUserToken))
            }

            // and an already linked broker with this user id
            val request = TradeItOAuthAccessTokenRequest("My oAuthVerifier")
            val response = TradeItOAuthAccessTokenResponse()
            response.userId = myUserId
            response.userToken = "My old userToken"
            response.broker = "My broker 1"
            val linkedLogin = TradeItLinkedLoginParcelable(request, response);
            val apiClient: TradeItApiClientParcelable = mock()
            val existingLinkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache)
            linkedBrokerManager.linkedBrokers = arrayListOf(existingLinkedBroker)


            // when calling linkBrokerWithOauthVerifier
            var linkedBrokerResult: TradeItLinkedBrokerParcelable? = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(
                accountLabel,
                "My oAuthVerifier",
                object : TradeItCallback<TradeItLinkedBrokerParcelable> {
                    override fun onSuccess(linkedBroker: TradeItLinkedBrokerParcelable?) {
                        successCallBackCount++
                        linkedBrokerResult = linkedBroker
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                })

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and the updateLinkedLogin method was called
            verify(keystoreService, times(1)).updateLinkedLogin(any())

            // and expects a linkedBroker containing userId and updated userToken
            Assert.assertEquals(linkedBrokerResult!!.linkedLogin!!.userId, myUserId)
            Assert.assertEquals(linkedBrokerResult!!.linkedLogin!!.userToken, myUserToken)
            Assert.assertEquals(linkedBrokerResult!!.linkedLogin!!.broker, "My broker 1")

            // and expects only one linkedbroker in the list
            Assert.assertEquals(linkedBrokerManager.linkedBrokers.size, 1)
            Assert.assertEquals(linkedBrokerManager.linkedBrokers[0].linkedLogin!!.userId, myUserId)
            Assert.assertEquals(linkedBrokerManager.linkedBrokers[0].linkedLogin!!.userToken, myUserToken)
        }

        @Test
        fun `linkBrokerWithOauthVerifier handles an error response from trade it api`() {
            // given an error response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            val errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            val shortMessage = "My error when linking broker"
            whenever(apiClient.linkBrokerWithOauthVerifier(anyString(), any())).then {
                val callback = it.getArgument<TradeItCallback<String>>(1)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.BROKER_AUTHENTICATION_ERROR,
                        shortMessage,
                        null
                    )
                )
            }

            // when calling linkBrokerWithOauthVerifier
            var errorResult: TradeItErrorResult? = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(
                accountLabel,
                "My oAuthVerifier",
                object : TradeItCallback<TradeItLinkedBrokerParcelable> {
                    override fun onSuccess(type: TradeItLinkedBrokerParcelable?) {
                        successCallBackCount++
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                        errorResult = error
                    }
                }
            )

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 0)
            Assert.assertEquals(errorCallBackCount, 1)

            // and expects a populated TradeItErrorResult
            Assert.assertEquals(errorResult!!.getErrorCode(), errorCode)
            Assert.assertEquals(errorResult!!.getShortMessage(), shortMessage)
        }
    }

    @Nested
    inner class UnlinkBrokerTestCases() {
        @Test
        fun `unlinkBroker handles a successful response from trade it api `() {
            // given: a linked broker to unlink
            val linkedLogin: TradeItLinkedLoginParcelable = mock()
            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache)

            // and a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            whenever(apiClient.unlinkBrokerAccount(eq(linkedLogin), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItResponse>>(1)
                val tradeItResponse = TradeItResponse()
                tradeItResponse.sessionToken = "My session token"
                tradeItResponse.longMessages = null
                tradeItResponse.status = TradeItResponseStatus.SUCCESS
                callback.onSuccess(tradeItResponse)
            }
            linkedBrokerManager.linkedBrokers = arrayListOf(linkedBroker)

            // when calling unlinkBroker
            linkedBrokerManager.unlinkBroker(linkedBroker, object : TradeItCallback<TradeItResponse> {
                override fun onSuccess(type: TradeItResponse?) {
                    successCallBackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallBackCount++
                }
            })

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and the deleteLinkedLogin method was called
            verify(keystoreService, times(1)).deleteLinkedLogin(any())

            // and The linkedBroker is removed from cache
            verify(linkedBrokerCache, times(1)).removeFromCache(linkedBroker)

            // and the linkedbrokers list is empty
            Assert.assertEquals(linkedBrokerManager.linkedBrokers.size, 0)
        }

        @Test
        fun `unlinkBrokerByUserId handles a successful response from trade it api`() {
            // given a userId to unlink
            val userId = "MyUserId"

            // and the linked broker exist for this userId
            val linkedLogin: TradeItLinkedLoginParcelable = mock()
            whenever(linkedLogin.getUserId()).thenReturn(userId)

            val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache)
            linkedBroker.linkedLogin!!.userId = userId
            linkedBrokerManager.linkedBrokers = arrayListOf(linkedBroker)

            // and a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            whenever(apiClient.unlinkBrokerAccount(eq(linkedLogin), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItResponse>>(1)
                val tradeItResponse = TradeItResponse()
                tradeItResponse.sessionToken = "My session token"
                tradeItResponse.longMessages = null
                tradeItResponse.status = TradeItResponseStatus.SUCCESS
                callback.onSuccess(tradeItResponse)
            }

            // when calling unlinkBrokerByUserId
            linkedBrokerManager.unlinkBrokerByUserId(userId, object : TradeItCallback<TradeItResponse> {
                override fun onSuccess(type: TradeItResponse?) {
                    successCallBackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallBackCount++
                }
            })

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and the deleteLinkedLogin method was called
            verify(keystoreService, times(1)).deleteLinkedLogin(linkedLogin)

            // and the linkedBroker is removed from cache
            verify(linkedBrokerCache, times(1)).removeFromCache(linkedBroker)

            // and the linkedbrokers list is empty
            Assert.assertEquals(linkedBrokerManager.linkedBrokers.size, 0)
        }

        @Test
        fun `unlinkBrokerByUserId returns a TradeItError when the linkedBroker is not found`() {
            // given a userId to unlink
            val userId = "MyUserId"

            // and The linked broker doesn't exist for this userId
            linkedBrokerManager.linkedBrokers = mutableListOf()

            // when calling unlinkBrokerByUserId
            var successCallBackCount = 0
            var errorCallBackCount = 0
            linkedBrokerManager.unlinkBrokerByUserId(userId, object : TradeItCallback<TradeItResponse> {
                override fun onSuccess(type: TradeItResponse?) {
                    successCallBackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallBackCount++
                }
            })

            // then expects the errorCallback called once
            Assert.assertEquals(successCallBackCount, 0)
            Assert.assertEquals(errorCallBackCount, 1)

            // and the method on the api was not called
            verify(apiClient, never()).unlinkBrokerAccount(any(), any())
        }

    }

    @Nested
    inner class GetOAuthLoginPopupForTokenUpdateTestCases {
        @Test
        fun `getOAuthLoginPopupForTokenUpdateUrl handles a successful response from trade it api`() {
            // given a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            val mySpecialUrl = "http://myspecialoauthurl.com?oAuthTempToken=2bae6cc8-8d37-4b4a-ae5e-6bbde9209ac4"

            val linkedLogin: TradeItLinkedLoginParcelable = mock()
            linkedLogin.userId = myUserId
            linkedLogin.userToken = myUserToken

            val linkedBroker: TradeItLinkedBrokerParcelable = mock()
            whenever(linkedBroker.brokerName).thenReturn("My broker 1")
            whenever(linkedBroker.linkedLogin).thenReturn(linkedLogin)

            whenever(
                apiClient.getOAuthLoginPopupUrlForTokenUpdate(
                    eq("My broker 1"),
                    eq(myUserId),
                    eq(myUserToken),
                    eq("my internal app callback"),
                    any()
                )
            ).then {
                val callback = it.getArgument<TradeItCallback<String>>(4)
                callback.onSuccess(mySpecialUrl);
            }

            // when calling getOAuthLoginPopupForTokenUpdateUrl
            var oAuthUrlResult: String? = null
            linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl(
                linkedBroker,
                "my internal app callback",
                object : TradeItCallback<String> {
                    override fun onSuccess(oAuthUrl: String?) {
                        successCallBackCount++
                        oAuthUrlResult = oAuthUrl
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                })

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and expects the oAuthUrl to be populated
            Assert.assertEquals(oAuthUrlResult, mySpecialUrl)
        }

        @Test
        fun `getOAuthLoginPopupForTokenUpdateUrlByUserId handles a successful response from trade it api`() {
            // given a successful response from trade it api
            var successCallBackCount = 0
            var errorCallBackCount = 0
            val mySpecialUrl = "http://myspecialoauthurl.com?oAuthTempToken=2bae6cc8-8d37-4b4a-ae5e-6bbde9209ac4"

            val linkedLogin: TradeItLinkedLoginParcelable = mock()
            linkedLogin.userId = myUserId
            linkedLogin.userToken = myUserToken

            val linkedBroker: TradeItLinkedBrokerParcelable = mock()
            whenever(linkedBroker.brokerName).thenReturn("My broker 1")
            whenever(linkedBroker.linkedLogin).thenReturn(linkedLogin)
            whenever(
                apiClient.getOAuthLoginPopupUrlForTokenUpdate(
                    eq("My broker 1"),
                    eq(myUserId),
                    eq(myUserToken),
                    eq("my internal app callback"),
                    any()
                )
            ).then {
                val callback = it.getArgument<TradeItCallback<String>>(4)
                callback.onSuccess(mySpecialUrl);
            }
            linkedBrokerManager.linkedBrokers = arrayListOf(linkedBroker)

            // when calling getOAuthLoginPopupForTokenUpdateUrl
            var oAuthUrlResult: String? = null
            linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrlByUserId(
                myUserId,
                "my internal app callback",
                object : TradeItCallback<String> {
                    override fun onSuccess(oAuthUrl: String?) {
                        successCallBackCount++
                        oAuthUrlResult = oAuthUrl
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                }
            )

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and expects the oAuthUrl to be populated
            Assert.assertEquals(oAuthUrlResult, mySpecialUrl)
        }

        @Test
        fun `getOAuthLoginPopupForTokenUpdateUrlByUserId returns a TradeItError when the linkedBroker is not found`() {
            // given the linked broker doesn't exist for this userId
            linkedBrokerManager.linkedBrokers = mutableListOf()

            // when calling getOAuthLoginPopupForTokenUpdateUrlByUserId
            var successCallBackCount = 0
            var errorCallBackCount = 0
            linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrlByUserId(
                myUserId,
                "my internal app callback",
                object: TradeItCallback<String> {
                    override fun onSuccess(type: String?) {
                        successCallBackCount++
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                })

            // then expects the errorCallBackCount called once
            Assert.assertEquals(successCallBackCount, 0)
            Assert.assertEquals(errorCallBackCount, 1)

            // and expects the api eas not called
            verify(apiClient, never()).getOAuthLoginPopupUrlForTokenUpdate(anyString(), eq(myUserId), anyString(), anyString(), any())
        }
    }

    @Test
    fun `syncLinkedBrokers sync correctly`() {
        // given a list of linkedLoginParcelable to synch from
        val linkedBrokerData1 = TradeItLinkedBrokerData("", "MyUserId1", "")
        val linkedBrokerParcelable1 = TradeItLinkedBrokerParcelable(
            apiClient,
            TradeItLinkedLoginParcelable(
                linkedBrokerData1.broker,
                linkedBrokerData1.userId!!,
                linkedBrokerData1.userToken!!
            ),
            linkedBrokerCache
        )
        val linkedBrokerAccountData1 = TradeItLinkedBrokerAccountData(
            "MyAccountName",
            "MyChangedAccountNumber",
            "USD"
        )
        linkedBrokerData1.injectAccount(linkedBrokerAccountData1)

        val linkedBrokerData2 = TradeItLinkedBrokerData("", "MyUserId2", "")
            .withLinkActivationPending(true)
        val linkedLoginParcelable2 = TradeItLinkedLoginParcelable(
            linkedBrokerData2.broker,
            linkedBrokerData2.userId!!,
            linkedBrokerData2.userToken!!
        )
        val linkedBrokerParcelable2 = TradeItLinkedBrokerParcelable(
            apiClient,
            linkedLoginParcelable2,
            linkedBrokerCache
        )

        val linkedBrokerData3 = TradeItLinkedBrokerData("", "MyUserId3", "")
        val linkedLoginParcelable3 = TradeItLinkedLoginParcelable(
            linkedBrokerData3.broker,
            linkedBrokerData3.userId!!,
            linkedBrokerData3.userToken!!
        )
        val linkedBrokerParcelable3 = TradeItLinkedBrokerParcelable(
            apiClient,
            linkedLoginParcelable3,
            linkedBrokerCache
        )
        val linkedBrokerAccountData3 = TradeItLinkedBrokerAccountData(
            "MyAccountName",
            "MyAccountNumber",
            "USD"
        )
        linkedBrokerData3.injectAccount(linkedBrokerAccountData3)

        val listToSyncFrom = arrayListOf(linkedBrokerData1, linkedBrokerData2, linkedBrokerData3)

        // and the following already existing linkedBrokers
        val linkedLoginParcelable  = TradeItLinkedLoginParcelable(
            "",
            "MyUserId1",
            ""
        )
        val linkedBrokerParcelable = TradeItLinkedBrokerParcelable(
            apiClient,
            linkedLoginParcelable,
            linkedBrokerCache
        )
        val account1 = TradeItBrokerAccount()
        account1.accountNumber = linkedBrokerAccountData1.accountNumber
        account1.accountBaseCurrency = linkedBrokerAccountData1.accountBaseCurrency
        account1.accountNumber = "MyAccountNumber"
        linkedBrokerParcelable.accounts.add(
            TradeItLinkedBrokerAccountParcelable(linkedBrokerParcelable, account1)
        )

        val linkedLoginParcelable5  = TradeItLinkedLoginParcelable("", "MyUserId5", "")
        val linkedBrokerParcelable5 = TradeItLinkedBrokerParcelable(
            apiClient,
            linkedLoginParcelable5,
            linkedBrokerCache
        )

        linkedBrokerManager.linkedBrokers = arrayListOf(linkedBrokerParcelable, linkedBrokerParcelable5)

        // when Calling syncLocalLinkedBrokers
        linkedBrokerManager.syncLocalLinkedBrokers(listToSyncFrom)

        // then expects these calls to delete the linkedLogin from the keystore
        verify(keystoreService, times(1)).deleteLinkedLogin(linkedLoginParcelable5)

        // and expects these calls to add the linkedLogin to the keystore
        verify(keystoreService, times(1))
            .saveLinkedLogin(linkedLoginParcelable2, linkedLoginParcelable2.label)
        verify(keystoreService, times(1))
            .saveLinkedLogin(linkedLoginParcelable3, linkedLoginParcelable3.label)

        // and expects these calls to remove from the cache
        verify(linkedBrokerCache, times(1)).removeFromCache(linkedBrokerParcelable5)

        // and expects these calls to add to the cache
        verify(linkedBrokerCache, times(1)).cache(linkedBrokerParcelable2)
        verify(linkedBrokerCache, times(1)).cache(linkedBrokerParcelable3)

        // and linkedBrokers contains linkedBrokerParcelable1, linkedBrokerParcelable2, linkedBrokerParcelable3
        Assert.assertEquals(linkedBrokerManager.linkedBrokers.size, 3)
        Assert.assertTrue(linkedBrokerManager.linkedBrokers.contains(linkedBrokerParcelable1))
        Assert.assertTrue(linkedBrokerManager.linkedBrokers.contains(linkedBrokerParcelable2))
        Assert.assertTrue(linkedBrokerManager.linkedBrokers.contains(linkedBrokerParcelable3))

        Assert.assertEquals(linkedBrokerManager.linkedBrokers[0].accounts.size, 1)
        Assert.assertEquals(
            linkedBrokerManager.linkedBrokers[0].accounts[0].accountNumber,
            linkedBrokerAccountData1.accountNumber
        )
        Assert.assertEquals(
            linkedBrokerManager.linkedBrokers[0].accounts[0].accountName,
            linkedBrokerAccountData1.accountName
        )
        Assert.assertEquals(
            linkedBrokerManager.linkedBrokers[0].accounts[0].accountBaseCurrency,
            linkedBrokerAccountData1.accountBaseCurrency
        )

        Assert.assertEquals(linkedBrokerManager.linkedBrokers[2].accounts.size, 1)
        Assert.assertEquals(
            linkedBrokerManager.linkedBrokers[2].accounts[0].accountNumber,
            linkedBrokerAccountData3.accountNumber
        )
        Assert.assertEquals(
            linkedBrokerManager.linkedBrokers[2].accounts[0].accountName,
            linkedBrokerAccountData3.accountName
        )
        Assert.assertEquals(
            linkedBrokerManager.linkedBrokers[2].accounts[0].accountBaseCurrency,
            linkedBrokerAccountData3.accountBaseCurrency
        )
    }
}
