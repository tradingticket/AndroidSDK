package trade.it.android.sdk.model

import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.model.TradeItAuthenticateResponse
import it.trade.tradeitapi.model.TradeItAuthenticateResponse.Account
import it.trade.tradeitapi.model.TradeItErrorCode
import it.trade.tradeitapi.model.TradeItResponseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification

class TradeItLinkedBrokerSpec extends Specification {

    TradeItApiClient apiClient = Mock(TradeItApiClient);
    TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient)

    void setup() {

    }

    def "authenticate handles a successful response from trade it api"() {
        given: "A successful response from trade it api"
            int successCallBackCount = 0
            int securityQuestionCallbackCount = 0
            int errorCallBackCount = 0
            Account account1 = new Account();
            account1.accountNumber = "My account number 1"
            account1.name = "My account name 1"
            Account account2 = new Account();
            account2.accountNumber = "My account number 2"
            account2.name = "My account name 2"
            List<Account> accountsExpected = [account1, account2]
            1 * apiClient.authenticate(_) >> { args ->
                Callback<TradeItAuthenticateResponse> callback = args[0]
                Call<TradeItAuthenticateResponse> call = Mock(Call)
                TradeItAuthenticateResponse tradeItAuthenticateResponse = new TradeItAuthenticateResponse()
                tradeItAuthenticateResponse.sessionToken = "My session token"
                tradeItAuthenticateResponse.longMessages = null
                tradeItAuthenticateResponse.status = TradeItResponseStatus.SUCCESS
                tradeItAuthenticateResponse.accounts = accountsExpected

                Response<TradeItAuthenticateResponse> response = Response.success(tradeItAuthenticateResponse);
                callback.onResponse(call, response);
            }

        when: "calling authenticate"
            List<Account> accountsResult = null
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<Account>>() {

                @Override
                void onSuccess(List<Account> accounts) {
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

        and: "expects a list of accounts"
            accountsResult == accountsExpected
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
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<Account>>() {

                @Override
                void onSuccess(List<Account> accounts) {
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

        and: "expects a securoty question"
            tradeItSecurityQuestion.securityQuestionOptions[0] == "My security question"
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
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<Account>>() {

                @Override
                void onSuccess(List<Account> accounts) {
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

}
