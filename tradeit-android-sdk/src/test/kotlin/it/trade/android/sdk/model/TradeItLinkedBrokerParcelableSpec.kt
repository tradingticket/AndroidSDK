package it.trade.android.sdk.model

import com.nhaarman.mockitokotlin2.*
import it.trade.model.TradeItErrorResult
import it.trade.model.TradeItSecurityQuestion
import it.trade.model.callback.AuthenticationCallback
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl
import it.trade.model.reponse.*
import junit.framework.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import retrofit2.Callback
import retrofit2.Response

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeItLinkedBrokerParcelableSpec {
    private val apiClient: TradeItApiClientParcelable = mock()
    private val linkedLogin: TradeItLinkedLoginParcelable = mock()
    private val linkedBrokerCache: TradeItLinkedBrokerCache = mock()
    private var linkedBroker: TradeItLinkedBrokerParcelable = TradeItLinkedBrokerParcelable(apiClient, linkedLogin!!, linkedBrokerCache)

    @BeforeEach
    fun init() {
        clearInvocations(apiClient)
    }

    @Nested
    inner class AuthenticateTestCases {
        @Test
        fun `authenticate handles a successful response from trade it api`() {
            // given A successful response from trade it api
            var successCallBackCount = 0
            var securityQuestionCallbackCount = 0
            var errorCallBackCount = 0
            val account1 = TradeItBrokerAccount()
            account1.accountNumber = "My account number 1"
            account1.name = "My account name 1"

            val orderCapability = OrderCapability()
            val action = DisplayLabelValue("Buy", "buy")
            orderCapability.instrument = Instrument.EQUITIES
            orderCapability.actions = arrayListOf(action)
            account1.orderCapabilities = arrayListOf(orderCapability)


            val account2 = TradeItBrokerAccount()
            account2.accountNumber = "My account number 2"
            account2.name = "My account name 2"
            account2.userCanDisableMargin = true
            val accountsExpected = arrayListOf(
                TradeItLinkedBrokerAccountParcelable(linkedBroker, account1),
                TradeItLinkedBrokerAccountParcelable(linkedBroker, account2)
            )
            val orderCapabilitiesExpected = arrayListOf(TradeItOrderCapabilityParcelable(orderCapability))
            whenever(apiClient.authenticate(eq(linkedLogin), any())).then {
                val callback = it.getArgument<
                    AuthenticationCallback<TradeItAuthenticateResponse, TradeItSecurityQuestion>
                    >(1)
                val tradeItAuthenticateResponse = TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.SUCCESS
                tradeItAuthenticateResponse.accounts = arrayListOf(account1, account2)
                val response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(mock(), response)
            }

            // when calling authenticate
            var accountsResult: List<TradeItLinkedBrokerAccountParcelable>? = null
            linkedBroker.authenticate(
                object : TradeItCallbackWithSecurityQuestionImpl<
                    List<TradeItLinkedBrokerAccountParcelable>
                    >() {
                    override fun onSuccess(accounts: List<TradeItLinkedBrokerAccountParcelable>?) {
                        successCallBackCount++
                        accountsResult = accounts
                    }

                    override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion?) {
                        securityQuestionCallbackCount++
                    }

                    override fun onError(error: TradeItErrorResult?) {
                        errorCallBackCount++
                    }
                }
            )

            // then expects the successCallback called once
            Assert.assertEquals(successCallBackCount, 1)
            Assert.assertEquals(securityQuestionCallbackCount, 0)
            Assert.assertEquals(errorCallBackCount, 0)

            // and expects a list of TradeItLinkedBrokerAccountParcelable
            Assert.assertEquals(accountsResult, accountsExpected)

            // and the list is kept in memory"
            Assert.assertEquals(linkedBroker.accounts, accountsExpected)

            // and The orderCapabilities is set to empty list if no value
            Assert.assertEquals(linkedBroker.accounts[0].orderCapabilities, orderCapabilitiesExpected)
            Assert.assertEquals(
                linkedBroker.accounts[1].orderCapabilities,
                emptyList<TradeItOrderCapabilityParcelable>()
            )
        }

        @Test
        fun `authenticate handles a successful response with a security question from trade it api`() {
            // given a successful response with security question from trade it api
            var successCallBackCount = 0
            var securityQuestionCallbackCount = 0
            var errorCallBackCount = 0

            whenever(apiClient.authenticate(eq(linkedLogin), any())).then {
                val callback = it.getArgument<Callback<TradeItAuthenticateResponse>>(1)
                val tradeItAuthenticateResponse = TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.INFORMATION_NEEDED
                tradeItAuthenticateResponse.securityQuestion = "My security question"
                val response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(mock(), response);
            }

            // when calling authenticate
            var tradeItSecurityQuestion: TradeItSecurityQuestion? = null
            linkedBroker.authenticate(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                override fun onSuccess(type: List<TradeItLinkedBrokerAccountParcelable>?) {
                    successCallBackCount++
                }

                override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion?) {
                    tradeItSecurityQuestion = securityQuestion
                    securityQuestionCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallBackCount++
                }
            })

            // then expects the securityQuestionCallbackCount called once
            Assert.assertEquals(successCallBackCount, 0)
            Assert.assertEquals(securityQuestionCallbackCount, 1)
            Assert.assertEquals(errorCallBackCount, 0)

            // and expects a security question
            Assert.assertEquals(tradeItSecurityQuestion!!.securityQuestion, "My security question")
        }

        @Test
        fun `authenticate handles an error response from trade it api`() {
            // given An error response from trade it api
            var successCallBackCount = 0
            var securityQuestionCallbackCount = 0
            var errorCallBackCount = 0

            whenever(apiClient.authenticate(eq(linkedLogin), any())).then {
                val callback = it.getArgument<Callback<TradeItAuthenticateResponse>>(1)
                val tradeItAuthenticateResponse = TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.ERROR
                tradeItAuthenticateResponse.shortMessage = "My short error message"
                tradeItAuthenticateResponse.longMessages = arrayListOf("My long error message")
                tradeItAuthenticateResponse.code = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR

                val response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(mock(), response);
            }

            // when calling authenticate
            var tradeItErrorResult: TradeItErrorResult? = null
            linkedBroker.authenticate(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                override fun onSuccess(type: List<TradeItLinkedBrokerAccountParcelable>?) {
                    successCallBackCount++
                }

                override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion?) {
                    securityQuestionCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    tradeItErrorResult = error
                    errorCallBackCount++
                }
            })

            // then expects the errorCallbackCount called once
            Assert.assertEquals(successCallBackCount, 0)
            Assert.assertEquals(securityQuestionCallbackCount, 0)
            Assert.assertEquals(errorCallBackCount, 1)

            // and expects a trade it error result
            Assert.assertEquals(tradeItErrorResult!!.shortMessage, "My short error message")
            Assert.assertEquals(tradeItErrorResult!!.longMessages, arrayListOf("My long error message"))
            Assert.assertEquals(tradeItErrorResult!!.errorCode, TradeItErrorCode.BROKER_AUTHENTICATION_ERROR)
            Assert.assertEquals(tradeItErrorResult!!.httpCode, 200)
        }
    }

    @Nested
    inner class AuthenticateIfNeededTestCases {
        @Test
        fun `authenticateIfNeeded calls authenticate if there is an error that requires authentication`() {
            // given An error that requires authentication
            var successCallbackCount = 0
            var securityQuestionCallbackCount = 0
            var errorCallbackCount = 0
            val errorResult = TradeItErrorResultParcelable()
            errorResult.errorCode = TradeItErrorCode.BROKER_ACCOUNT_ERROR
            linkedBroker.error = errorResult

            val account1 = TradeItBrokerAccount();
            account1.accountNumber = "My account number 1"
            account1.name = "My account name 1"
            val account2 = TradeItBrokerAccount();
            account2.accountNumber = "My account number 2"
            account2.name = "My account name 2"
            val accountsExpected = arrayListOf(
                TradeItLinkedBrokerAccountParcelable(linkedBroker, account1),
                TradeItLinkedBrokerAccountParcelable(linkedBroker, account2)
            )

            whenever(apiClient.authenticate(eq(linkedLogin), any())).then {
                val callback = it.getArgument<AuthenticationCallback<
                    TradeItAuthenticateResponse, TradeItSecurityQuestion>
                    >(1)
                val tradeItAuthenticateResponse = TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.SUCCESS
                tradeItAuthenticateResponse.accounts = arrayListOf(account1, account2)
                val response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(mock(), response)
            }

            // when calling authenticateIfNeeded
            var accountsResult: List<TradeItLinkedBrokerAccountParcelable>? = null
            linkedBroker.authenticateIfNeeded(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                override fun onSuccess(accounts: List<TradeItLinkedBrokerAccountParcelable>?) {
                    successCallbackCount++
                    accountsResult = accounts
                }

                override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion?) {
                    securityQuestionCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expects the successCallBackCount called once
            Assert.assertEquals(successCallbackCount, 1)
            Assert.assertEquals(securityQuestionCallbackCount, 0)
            Assert.assertEquals(errorCallbackCount, 0)

            // and expects a list of TradeItLinkedBrokerAccountParcelable
            Assert.assertEquals(accountsResult, accountsExpected)

            // and the list is kept in memory
            Assert.assertEquals(linkedBroker.accounts, accountsExpected)

            // and the error is set to null
            Assert.assertEquals(linkedBroker.error, null)

        }

        @Test
        fun `authenticateIfNeeded returns an error if there is an error that requires relink`() {
            // given an error that requires relink
            var successCallbackCount = 0
            var securityQuestionCallbackCount = 0
            var errorCallbackCount = 0
            val errorResult = TradeItErrorResultParcelable()
            errorResult.errorCode = TradeItErrorCode.TOKEN_INVALID_OR_EXPIRED
            linkedBroker.error = errorResult

            // when calling authenticateIfNeeded
            var expectedError: TradeItErrorResultParcelable? = null
            linkedBroker.authenticateIfNeeded(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                override fun onSuccess(type: List<TradeItLinkedBrokerAccountParcelable>?) {
                    successCallbackCount++
                }

                override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion?) {
                    securityQuestionCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    expectedError = error as TradeItErrorResultParcelable
                    errorCallbackCount++
                }
            })

            // then expects the successCallBackCount called once
            Assert.assertEquals(successCallbackCount, 0)
            Assert.assertEquals(securityQuestionCallbackCount, 0)
            Assert.assertEquals(errorCallbackCount, 1)

            // and expects a relink error
            Assert.assertEquals(expectedError, errorResult)

            // and the error is still set to relink error
            Assert.assertEquals(linkedBroker.error, errorResult)

        }

        @Test
        fun `authenticateIfNeeded is successfull if it is an other error than requires authentication or relink`() {
            // given an error that doesn't require authentication or relink
            var successCallbackCount = 0
            var securityQuestionCallbackCount = 0
            var errorCallbackCount = 0
            val errorResult = TradeItErrorResultParcelable()
            errorResult.errorCode = TradeItErrorCode.BROKER_EXECUTION_ERROR
            linkedBroker.error = errorResult

            // when calling authenticateIfNeeded
            var accountsResult: List<TradeItLinkedBrokerAccountParcelable>? = null
            linkedBroker.authenticateIfNeeded(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                override fun onSuccess(accounts: List<TradeItLinkedBrokerAccountParcelable>?) {
                    successCallbackCount++
                    accountsResult = accounts
                }

                override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion?) {
                    securityQuestionCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expects the successCallBackCount called once
            Assert.assertEquals(successCallbackCount, 1)
            Assert.assertEquals(securityQuestionCallbackCount, 0)
            Assert.assertEquals(errorCallbackCount, 0)

            // and authenticate has not been called
            verify(apiClient, never()).authenticate(any(), any())

            // and the error is still set
            Assert.assertEquals(linkedBroker.error, errorResult)

        }
    }

    @Nested
    inner class GetLinkedBrokerAccountTestCases {
        @Test
        fun `getLinkedBrokerAccount returns a TradeItLinkedBrokerAccountParcelable if the given account number exists`() {
            // given
            val myAccountNumberInput = "MyAccountNumber1"
            val accountParcelable: TradeItLinkedBrokerAccountParcelable = mock()
            whenever(accountParcelable.accountNumber).thenReturn("MyAccountNumber0")

            val accountParcelable1: TradeItLinkedBrokerAccountParcelable = mock()
            whenever(accountParcelable1.accountNumber).thenReturn("MyAccountNumber1")

            val accountParcelable2: TradeItLinkedBrokerAccountParcelable = mock()
            whenever(accountParcelable2.accountNumber).thenReturn("MyAccountNumber2")

            linkedBroker.accounts = arrayListOf(accountParcelable, accountParcelable1, accountParcelable2)

            // when
            val accountParcelableResult = linkedBroker.getLinkedBrokerAccount(myAccountNumberInput)

            // then
            Assert.assertNotNull(accountParcelableResult)
            Assert.assertEquals(accountParcelableResult!!.accountNumber, myAccountNumberInput)
        }

        @Test
        fun `getLinkedBrokerAccount returns null if the given account number doesn't exist`() {
            // given
            val myAccountNumberInput = "MyAccountNumberXXX"

            val accountParcelable: TradeItLinkedBrokerAccountParcelable = mock()
            whenever(accountParcelable.accountNumber).thenReturn("MyAccountNumber0")

            val accountParcelable1: TradeItLinkedBrokerAccountParcelable = mock()
            whenever(accountParcelable1.accountNumber).thenReturn("MyAccountNumber1")

            val accountParcelable2: TradeItLinkedBrokerAccountParcelable = mock()
            whenever(accountParcelable2.accountNumber).thenReturn("MyAccountNumber2")

            linkedBroker.accounts = arrayListOf(
                accountParcelable,
                accountParcelable1,
                accountParcelable2
            )

            // when
            val accountParcelableResult = linkedBroker.getLinkedBrokerAccount(myAccountNumberInput)

            // then
            Assert.assertNull(accountParcelableResult)
        }
    }
}
