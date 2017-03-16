package it.trade.android.sdk.model

import it.trade.android.sdk.TradeItSDK
import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.model.TradeItAuthenticateResponse
import it.trade.tradeitapi.model.TradeItBrokerAccount
import it.trade.tradeitapi.model.TradeItErrorCode
import it.trade.tradeitapi.model.TradeItResponseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification

class TradeItLinkedBrokerSpec extends Specification {
    TradeItApiClient apiClient = Mock(TradeItApiClient);
    TradeItLinkedBrokerCache linkedBrokerCache = Mock(TradeItLinkedBrokerCache)
    TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient)

    void setup() {
        TradeItSDK.linkedBrokerCache = linkedBrokerCache
    }

    def "authenticate handles a successful response from trade it api"() {
        given: "A successful response from trade it api"
            int successCallBackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallBackCount = 0
            TradeItBrokerAccount account1 = new TradeItBrokerAccount();
            account1.accountNumber = "My account number 1"
            account1.name = "My account name 1"
            TradeItBrokerAccount account2 = new TradeItBrokerAccount();
            account2.accountNumber = "My account number 2"
            account2.name = "My account name 2"
            List<TradeItLinkedBrokerAccount> accountsExpected = [new TradeItLinkedBrokerAccount(linkedBroker, account1), new TradeItLinkedBrokerAccount(linkedBroker, account2)]
            1 * apiClient.authenticate(_) >> { args ->
                Callback<TradeItAuthenticateResponse> callback = args[0]
                Call<TradeItAuthenticateResponse> call = Mock(Call)
                TradeItAuthenticateResponse tradeItAuthenticateResponse = new TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.SUCCESS
                tradeItAuthenticateResponse.accounts = [account1, account2]
                Response<TradeItAuthenticateResponse> response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(call, response);
            }

        when: "calling authenticate"
            List<TradeItBrokerAccount> accountsResult = null
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItBrokerAccount>>() {

                @Override
                void onSuccess(List<TradeItBrokerAccount> accounts) {
                    successCallBackCount++
                    accountsResult = accounts
                }

                @Override
                void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    securityQuestionCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            securityQuestionCallbackCount == 0
            errorCallBackCount == 0

        and: "expects a list of TradeItLinkedBrokerAccount"
            accountsResult == accountsExpected

        and: "the list is kept in memory"
            linkedBroker.getAccounts() == accountsExpected
    }

    def "authenticate handles a successful response with a security question from trade it api"() {
        given: "A successful response with security question from trade it api"
            int successCallBackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallBackCount = 0

            1 * apiClient.authenticate(_) >> { args ->
                Callback<TradeItAuthenticateResponse> callback = args[0]
                Call<TradeItAuthenticateResponse> call = Mock(Call)
                TradeItAuthenticateResponse tradeItAuthenticateResponse = new TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.INFORMATION_NEEDED
                tradeItAuthenticateResponse.securityQuestion = "My security question"
                Response<TradeItAuthenticateResponse> response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(call, response);
            }

        when: "calling authenticate"
            TradeItSecurityQuestion tradeItSecurityQuestion = null
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItBrokerAccount>>() {

                @Override
                void onSuccess(List<TradeItBrokerAccount> accounts) {
                    successCallBackCount++
                }

                @Override
                void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    tradeItSecurityQuestion = securityQuestion
                    securityQuestionCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the securityQuestionCallbackCount called once"
            successCallBackCount == 0
            securityQuestionCallbackCount == 1
            errorCallBackCount == 0

        and: "expects a security question"
            tradeItSecurityQuestion.securityQuestion == "My security question"
    }

    def "authenticate handles an error response from trade it api"() {
        given: "An error response from trade it api"
            int successCallBackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallBackCount = 0

            1 * apiClient.authenticate(_) >> { args ->
                Callback<TradeItAuthenticateResponse> callback = args[0]
                Call<TradeItAuthenticateResponse> call = Mock(Call)
                TradeItAuthenticateResponse tradeItAuthenticateResponse = new TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.ERROR
                tradeItAuthenticateResponse.shortMessage = "My short error message"
                tradeItAuthenticateResponse.longMessages = ["My long error message"]
                tradeItAuthenticateResponse.code = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR

                Response<TradeItAuthenticateResponse> response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(call, response);
            }

        when: "calling authenticate"
            TradeItErrorResult tradeItErrorResult = null
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItBrokerAccount>>() {

                @Override
                void onSuccess(List<TradeItBrokerAccount> accounts) {
                    successCallBackCount++
                }

                @Override
                void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    securityQuestionCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    tradeItErrorResult = error
                    errorCallBackCount++
                }
            })

        then: "expects the errorCallbackCount called once"
            successCallBackCount == 0
            securityQuestionCallbackCount == 0
            errorCallBackCount == 1

        and: "expects a trade it error result"
            tradeItErrorResult.shortMessage == "My short error message"
            tradeItErrorResult.longMessages == ["My long error message"]
            tradeItErrorResult.errorCode == TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            tradeItErrorResult.httpCode == 200
    }

    def "authenticateIfNeeded calls authenticate if there is an error that requires authentication"() {
        given: "An error that requires authentication"
            int successCallbackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallbackCount = 0
            TradeItErrorResult errorResult = new TradeItErrorResult()
            errorResult.errorCode = TradeItErrorCode.BROKER_ACCOUNT_ERROR
            linkedBroker.error = errorResult

            TradeItBrokerAccount account1 = new TradeItBrokerAccount();
            account1.accountNumber = "My account number 1"
            account1.name = "My account name 1"
            TradeItBrokerAccount account2 = new TradeItBrokerAccount();
            account2.accountNumber = "My account number 2"
            account2.name = "My account name 2"
            List<TradeItLinkedBrokerAccount> accountsExpected = [new TradeItLinkedBrokerAccount(linkedBroker, account1), new TradeItLinkedBrokerAccount(linkedBroker, account2)]

            1 * apiClient.authenticate(_) >> { args ->
                Callback<TradeItAuthenticateResponse> callback = args[0]
                Call<TradeItAuthenticateResponse> call = Mock(Call)
                TradeItAuthenticateResponse tradeItAuthenticateResponse = new TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.SUCCESS
                tradeItAuthenticateResponse.accounts = [account1, account2]
                Response<TradeItAuthenticateResponse> response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(call, response);
            }

        when: "calling authenticateIfNeeded"
            List<TradeItBrokerAccount> accountsResult = null
            linkedBroker.authenticateIfNeeded(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItBrokerAccount>>() {

                @Override
                void onSuccess(List<TradeItBrokerAccount> accounts) {
                    successCallbackCount++
                    accountsResult = accounts
                }

                @Override
                void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    securityQuestionCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

        then: "expects the successCallBackCount called once"
            successCallbackCount == 1
            securityQuestionCallbackCount == 0
            errorCallbackCount == 0

        and: "expects a list of TradeItLinkedBrokerAccount"
            accountsResult == accountsExpected

        and: "the list is kept in memory"
            linkedBroker.getAccounts() == accountsExpected

        and: "the error is set to null"
            linkedBroker.error == null

    }

    def "authenticateIfNeeded return an error if there is an error that requires relink"() {
        given: "An error that requires relink"
            int successCallbackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallbackCount = 0
            TradeItErrorResult errorResult = new TradeItErrorResult()
            errorResult.errorCode = TradeItErrorCode.TOKEN_INVALID_OR_EXPIRED
            linkedBroker.error = errorResult

        when: "calling authenticateIfNeeded"
            TradeItErrorResult expectedError = null
            linkedBroker.authenticateIfNeeded(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItBrokerAccount>>() {
                @Override
                void onSuccess(List<TradeItBrokerAccount> accounts) {
                    successCallbackCount++
                    accountsResult = accounts
                }

                @Override
                void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    securityQuestionCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    expectedError = error
                    errorCallbackCount++
                }
            })

        then: "expects the successCallBackCount called once"
            successCallbackCount == 0
            securityQuestionCallbackCount == 0
            errorCallbackCount == 1

        and: "expects a relink error"
            expectedError == errorResult

        and: "the error is still set to relink error"
            linkedBroker.error == errorResult

    }

    def "authenticateIfNeeded is successfull if it is an other error than requires authentication or relink"() {
        given: "An error that doesn't require authentication or relink"
            int successCallbackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallbackCount = 0
            TradeItErrorResult errorResult = new TradeItErrorResult()
            errorResult.errorCode = TradeItErrorCode.BROKER_EXECUTION_ERROR
            linkedBroker.error = errorResult

        when: "calling authenticateIfNeeded"
            List<TradeItBrokerAccount> accountsResult = null
            linkedBroker.authenticateIfNeeded(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItBrokerAccount>>() {

                @Override
                void onSuccess(List<TradeItBrokerAccount> accounts) {
                    successCallbackCount++
                    accountsResult = accounts
                }

                @Override
                void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    securityQuestionCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

        then: "expects the successCallBackCount called once"
            successCallbackCount == 1
            securityQuestionCallbackCount == 0
            errorCallbackCount == 0

        and: "authenticate has not been called"
            0 * apiClient.authenticate(_) >> {}

        and: "the error is still set"
            linkedBroker.error == errorResult

    }

}
