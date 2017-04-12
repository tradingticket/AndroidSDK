package it.trade.android.sdk.manager

import android.content.Context
import it.trade.android.sdk.model.TradeItCallBackImpl
import it.trade.android.sdk.model.TradeItErrorResult
import it.trade.android.sdk.model.TradeItLinkedBroker
import it.trade.android.sdk.model.TradeItLinkedBrokerCache
import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.API.TradeItBrokerLinker
import it.trade.tradeitapi.model.*
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker
import org.junit.Rule
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification
/**
 * Note: if you run this with android studio, you may need to add '-noverify' in the VM options because of a bug in PowerMock
 */
@PrepareForTest([TradeItBrokerLinker.class])
class TradeItLinkedBrokerManagerSpec extends Specification {

    Context context = Mock(Context)
    TradeItBrokerLinker brokerLinker = Mock(TradeItBrokerLinker)
    TradeItLinkedBrokerManager linkedBrokerManager
    String accountLabel = "My account label"
    String myUserId = "My trade it userId"
    String myUserToken = "My trade it userToken"
    String apiKey = "test api key"
    TradeItEnvironment environment = TradeItEnvironment.QA
    TradeItLinkedBrokerCache linkedBrokerCache = Mock(TradeItLinkedBrokerCache)

    @Rule
    PowerMockRule powerMockRule = new PowerMockRule();

    void setup() {
        brokerLinker.getTradeItEnvironment() >> TradeItEnvironment.QA
        brokerLinker.getLinkedLogins() >> []
    }

    def "GetAvailableBrokers handles a successful response from trade it api"() {
        given: "a successful response from trade it"
            1 * brokerLinker.getAvailableBrokers(_) >> { args ->
                Callback<TradeItAvailableBrokersResponse> callback = args[0]
                Call<TradeItAvailableBrokersResponse> call = Mock(Call)
                TradeItAvailableBrokersResponse tradeItAvailableBrokersResponse = new TradeItAvailableBrokersResponse()

                Broker broker1 = Mock(Broker)
                broker1.shortName = "Broker1"
                broker1.longName = "My long Broker1"

                Broker broker2 = Mock(Broker)
                broker2.shortName = "Broker2"
                broker2.longName = "My long Broker2"

                Broker broker3 = Mock(Broker)
                broker3.shortName = "Broker3"
                broker3.longName = "My long Broker3"

                List<Broker> brokerList = [broker1, broker2, broker3]
                tradeItAvailableBrokersResponse.brokerList = brokerList
                tradeItAvailableBrokersResponse.code = null
                tradeItAvailableBrokersResponse.sessionToken = "My session token"
                tradeItAvailableBrokersResponse.longMessages = null
                tradeItAvailableBrokersResponse.status = TradeItResponseStatus.SUCCESS
                Response<TradeItAvailableBrokersResponse> response = Response.success(tradeItAvailableBrokersResponse);
                callback.onResponse(call, response);
            }

            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);


        when: "calling getAvailableBrokers"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            List<TradeItAvailableBrokersResponse.Broker> brokerList = null
            linkedBrokerManager.getAvailableBrokers(new TradeItCallBackImpl<List<TradeItAvailableBrokersResponse.Broker>>() {
                @Override
                public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerListResponse) {
                    successCallBackCount++
                    brokerList = brokerListResponse
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            });

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "expects a list of 3 brokers"
            brokerList?.size() == 3
            brokerList[0].shortName == "Broker1"
            brokerList[0].longName == "My long Broker1"
            brokerList[1].shortName == "Broker2"
            brokerList[1].longName == "My long Broker2"
            brokerList[2].shortName == "Broker3"
            brokerList[2].longName == "My long Broker3"
    }

    def "GetAvailableBrokers handles an error response from trade it api"() {
        given: "an error response from trade it"
            1 * brokerLinker.getAvailableBrokers(_) >> { args ->
                Callback<TradeItAvailableBrokersResponse> callback = args[0]
                Call<TradeItAvailableBrokersResponse> call = Mock(Call)
                TradeItAvailableBrokersResponse tradeItAvailableBrokersResponse = new TradeItAvailableBrokersResponse()
                tradeItAvailableBrokersResponse.code = TradeItErrorCode.TOKEN_INVALID_OR_EXPIRED
                tradeItAvailableBrokersResponse.status = TradeItResponseStatus.ERROR
                tradeItAvailableBrokersResponse.brokerList = null
                tradeItAvailableBrokersResponse.shortMessage = "This is the short message for the session expired error"
                tradeItAvailableBrokersResponse.longMessages = ["This is the long message for the session expired error"]
                tradeItAvailableBrokersResponse.sessionToken = "My session token"

                Response<TradeItAvailableBrokersResponse> response = Response.success(tradeItAvailableBrokersResponse);
                callback.onResponse(call, response);
            }
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling getAvailableBrokers"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            List<TradeItAvailableBrokersResponse.Broker> brokerList = null
            linkedBrokerManager.getAvailableBrokers(new TradeItCallBackImpl<List<TradeItAvailableBrokersResponse.Broker>>() {
                @Override
                public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerListResponse) {
                    successCallBackCount++
                    brokerList = brokerListResponse
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            });

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "expects an empty list"
            brokerList.isEmpty() == true;
    }

    def "linkBroker handles a successful response from trade it api"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * brokerLinker.linkBrokerAccount(_, _) >> { args ->
                Callback<TradeItLinkLoginResponse> callback = args[1]
                Call<TradeItLinkLoginResponse> call = Mock(Call)
                TradeItLinkLoginResponse tradeItLinkLoginResponse = new TradeItLinkLoginResponse()
                tradeItLinkLoginResponse.sessionToken = "My session token"
                tradeItLinkLoginResponse.longMessages = null
                tradeItLinkLoginResponse.status = TradeItResponseStatus.SUCCESS
                tradeItLinkLoginResponse.userId = myUserId
                tradeItLinkLoginResponse.userToken = myUserToken
                Response<TradeItLinkLoginResponse> response = Response.success(tradeItLinkLoginResponse);
                callback.onResponse(call, response);
            }

            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling linkBroker"
            TradeItLinkedBroker linkedBrokerResult = null
            linkedBrokerManager.linkBroker(accountLabel, "My broker 1", "My username", "My password", new TradeItCallBackImpl<TradeItLinkedBroker>() {

                @Override
                void onSuccess(TradeItLinkedBroker linkedBroker) {
                    successCallBackCount++
                    linkedBrokerResult = linkedBroker
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "the brokerLinker static method save was called"
            1 * brokerLinker.saveLinkedLogin(_, _)

        and: "expects a linkedBroker containing userId and userToken"
            linkedBrokerResult.getLinkedLogin().userId == myUserId
            linkedBrokerResult.getLinkedLogin().userToken == myUserToken
            linkedBrokerResult.getLinkedLogin().broker == "My broker 1"
    }

    def "linkBroker handles an error response from trade it api"() {
        given: "An error response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            TradeItErrorCode errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            String shortMessage = "My error when linking broker"
            1 * brokerLinker.linkBrokerAccount(_, _) >> { args ->
                Callback<TradeItLinkLoginResponse> callback = args[1]
                Call<TradeItLinkLoginResponse> call = Mock(Call)
                TradeItLinkLoginResponse tradeItLinkLoginResponse = new TradeItLinkLoginResponse()
                tradeItLinkLoginResponse.sessionToken = "My session token"
                tradeItLinkLoginResponse.longMessages = null
                tradeItLinkLoginResponse.status = TradeItResponseStatus.ERROR
                tradeItLinkLoginResponse.code = errorCode
                tradeItLinkLoginResponse.shortMessage = shortMessage
                tradeItLinkLoginResponse.userId = null
                tradeItLinkLoginResponse.userToken = null
                Response<TradeItLinkLoginResponse> response = Response.success(tradeItLinkLoginResponse);
                callback.onResponse(call, response);
            }
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling linkBroker"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.linkBroker(accountLabel, "My broker 1", "My username", "My password", new TradeItCallBackImpl<TradeItLinkedBroker>() {

                @Override
                void onSuccess(TradeItLinkedBroker linkedBroker) {
                    successCallBackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                    errorResult = error
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 0
            errorCallBackCount == 1

        and: "expects a populated TradeItErrorResult"
            errorResult.getErrorCode() == errorCode
            errorResult.getShortMessage() == shortMessage
    }

    def "getOAuthLoginPopupUrlForMobile handles a successful response from trade it api"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            String mySpecialUrl = "http://myspecialoauthurl.com?oAuthTempToken=2bae6cc8-8d37-4b4a-ae5e-6bbde9209ac4"
            1 * brokerLinker.getOAuthLoginPopupUrlForMobile(_, _) >> { args ->
                Callback<TradeItOAuthLoginPopupUrlForMobileResponse> callback = args[1]
                Call<TradeItOAuthLoginPopupUrlForMobileResponse> call = Mock(Call)
                TradeItOAuthLoginPopupUrlForMobileResponse tradeItOAuthLoginPopupUrlForMobileResponse = new TradeItOAuthLoginPopupUrlForMobileResponse()
                tradeItOAuthLoginPopupUrlForMobileResponse.sessionToken = "My session token"
                tradeItOAuthLoginPopupUrlForMobileResponse.longMessages = null
                tradeItOAuthLoginPopupUrlForMobileResponse.status = TradeItResponseStatus.SUCCESS
                tradeItOAuthLoginPopupUrlForMobileResponse.oAuthURL = mySpecialUrl
                Response<TradeItOAuthLoginPopupUrlForMobileResponse> response = Response.success(tradeItOAuthLoginPopupUrlForMobileResponse);
                callback.onResponse(call, response);
            }

            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling getOAuthLoginPopupUrlForMobile"
            TradeItErrorResult errorResult = null
            String oAuthUrlResult = null
            linkedBrokerManager.getOAuthLoginPopupUrlForMobile("My broker 1", "my internal app callback", new TradeItCallBackImpl<String>() {

                @Override
                void onSuccess(String oAuthUrl) {
                    successCallBackCount++
                    oAuthUrlResult = oAuthUrl
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "expects the oAuthUrl to be populated"
            oAuthUrlResult == mySpecialUrl
    }

    def "getOAuthLoginPopupUrlForMobile handles an error response from trade it api"() {
        given: "An error response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            TradeItErrorCode errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            String shortMessage = "My error when linking broker"
            1 * brokerLinker.getOAuthLoginPopupUrlForMobile(_, _) >> { args ->
                Callback<TradeItOAuthLoginPopupUrlForMobileResponse> callback = args[1]
                Call<TradeItOAuthLoginPopupUrlForMobileRequest> call = Mock(Call)
                TradeItOAuthLoginPopupUrlForMobileResponse tradeItOAuthLoginPopupUrlForMobileResponse = new TradeItOAuthLoginPopupUrlForMobileResponse()
                tradeItOAuthLoginPopupUrlForMobileResponse.sessionToken = "My session token"
                tradeItOAuthLoginPopupUrlForMobileResponse.longMessages = null
                tradeItOAuthLoginPopupUrlForMobileResponse.status = TradeItResponseStatus.ERROR
                tradeItOAuthLoginPopupUrlForMobileResponse.code = errorCode
                tradeItOAuthLoginPopupUrlForMobileResponse.shortMessage = shortMessage
                tradeItOAuthLoginPopupUrlForMobileResponse.oAuthURL = null

                Response<TradeItOAuthLoginPopupUrlForMobileResponse> response = Response.success(tradeItOAuthLoginPopupUrlForMobileResponse);
                callback.onResponse(call, response);
            }
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling getOAuthLoginPopupUrlForMobile"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.getOAuthLoginPopupUrlForMobile("My broker 1", "my internal app callback", new TradeItCallBackImpl<String>() {

                @Override
                void onSuccess(String oAuthUrl) {
                    successCallBackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                    errorResult = error
                }
            })

        then: "expects the errorCallback called once"
            successCallBackCount == 0
            errorCallBackCount == 1

        and: "expects a populated TradeItErrorResult"
            errorResult.getErrorCode() == errorCode
            errorResult.getShortMessage() == shortMessage

    }

    def "linkBrokerWithOauthVerifier handles a successful response from trade it api"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * brokerLinker.getOAuthAccessToken(_, _) >> { args ->
                Callback<TradeItOAuthAccessTokenResponse> callback = args[1]
                Call<TradeItOAuthAccessTokenResponse> call = Mock(Call)
                TradeItOAuthAccessTokenResponse tradeItOAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse()
                tradeItOAuthAccessTokenResponse.sessionToken = "My session token"
                tradeItOAuthAccessTokenResponse.longMessages = null
                tradeItOAuthAccessTokenResponse.status = TradeItResponseStatus.SUCCESS
                tradeItOAuthAccessTokenResponse.userId = myUserId
                tradeItOAuthAccessTokenResponse.userToken = myUserToken
                tradeItOAuthAccessTokenResponse.broker = "My broker 1"
                Response<TradeItLinkLoginResponse> response = Response.success(tradeItOAuthAccessTokenResponse);
                callback.onResponse(call, response);
            }
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);


        when: "calling linkBrokerWithOauthVerifier"
            TradeItLinkedBroker linkedBrokerResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My oAuthVerifier", new TradeItCallBackImpl<TradeItLinkedBroker>() {

                @Override
                void onSuccess(TradeItLinkedBroker linkedBroker) {
                    successCallBackCount++
                    linkedBrokerResult = linkedBroker
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "the brokerLinker static method save was called"
            1 * brokerLinker.saveLinkedLogin(_, _)

        and: "expects a linkedBroker containing userId and userToken"
            linkedBrokerResult.getLinkedLogin().userId == myUserId
            linkedBrokerResult.getLinkedLogin().userToken == myUserToken
            linkedBrokerResult.getLinkedLogin().broker == "My broker 1"
    }

    def "linkBrokerWithOauthVerifier handles successful response from trade it api with an already existing userId (token update)"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * brokerLinker.getOAuthAccessToken(_, _) >> { args ->
                Callback<TradeItOAuthAccessTokenResponse> callback = args[1]
                Call<TradeItOAuthAccessTokenResponse> call = Mock(Call)
                TradeItOAuthAccessTokenResponse tradeItOAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse()
                tradeItOAuthAccessTokenResponse.sessionToken = "My session token"
                tradeItOAuthAccessTokenResponse.longMessages = null
                tradeItOAuthAccessTokenResponse.status = TradeItResponseStatus.SUCCESS
                tradeItOAuthAccessTokenResponse.userId = myUserId
                tradeItOAuthAccessTokenResponse.userToken = myUserToken
                tradeItOAuthAccessTokenResponse.broker = "My broker 1"
                Response<TradeItLinkLoginResponse> response = Response.success(tradeItOAuthAccessTokenResponse);
                callback.onResponse(call, response);
            }
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        and: "an already linked broker with this user id"
            TradeItOAuthAccessTokenRequest request = new TradeItOAuthAccessTokenRequest()
            TradeItOAuthAccessTokenResponse response = new TradeItOAuthAccessTokenResponse()
            response.userId = myUserId
            response.userToken = "My old userToken"
            response.broker = "My broker 1"
            TradeItLinkedLogin linkedLogin = new TradeItLinkedLogin(request, response);
            TradeItApiClient apiClient = new TradeItApiClient(linkedLogin, TradeItEnvironment.QA)
            TradeItLinkedBroker existingLinkedBroker = new TradeItLinkedBroker(apiClient, linkedBrokerCache)
            linkedBrokerManager.linkedBrokers = [existingLinkedBroker]



        when: "calling linkBrokerWithOauthVerifier"
            TradeItLinkedBroker linkedBrokerResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My oAuthVerifier", new TradeItCallBackImpl<TradeItLinkedBroker>() {

                @Override
                void onSuccess(TradeItLinkedBroker linkedBroker) {
                    successCallBackCount++
                    linkedBrokerResult = linkedBroker
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "the brokerLinker static method update was called"
            1 * brokerLinker.updateLinkedLogin(_)

        and: "expects a linkedBroker containing userId and updated userToken"
            linkedBrokerResult.getLinkedLogin().userId == myUserId
            linkedBrokerResult.getLinkedLogin().userToken == myUserToken
            linkedBrokerResult.getLinkedLogin().broker == "My broker 1"

        and: "expects only one linkedbroker in the list"
            linkedBrokerManager.linkedBrokers.size() == 1
            linkedBrokerManager.linkedBrokers[0].linkedLogin.userId == myUserId
            linkedBrokerManager.linkedBrokers[0].linkedLogin.userToken == myUserToken
    }

    def "linkBrokerWithOauthVerifier handles an error response from trade it api"() {
        given: "An error response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            TradeItErrorCode errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            String shortMessage = "My error when linking broker"
            1 * brokerLinker.getOAuthAccessToken(_, _) >> { args ->
                Callback<TradeItOAuthAccessTokenResponse> callback = args[1]
                Call<TradeItOAuthAccessTokenResponse> call = Mock(Call)
                TradeItOAuthAccessTokenResponse tradeItOAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse()
                tradeItOAuthAccessTokenResponse.sessionToken = "My session token"
                tradeItOAuthAccessTokenResponse.longMessages = null
                tradeItOAuthAccessTokenResponse.status = TradeItResponseStatus.ERROR
                tradeItOAuthAccessTokenResponse.code = errorCode
                tradeItOAuthAccessTokenResponse.shortMessage = shortMessage
                tradeItOAuthAccessTokenResponse.userId = null
                tradeItOAuthAccessTokenResponse.userToken = null
                Response<TradeItLinkLoginResponse> response = Response.success(tradeItOAuthAccessTokenResponse);
                callback.onResponse(call, response);
            }

            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling linkBrokerWithOauthVerifier"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My oAuthVerifier", new TradeItCallBackImpl<TradeItLinkedBroker>() {

                @Override
                void onSuccess(TradeItLinkedBroker linkedBroker) {
                    successCallBackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                    errorResult = error
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 0
            errorCallBackCount == 1

        and: "expects a populated TradeItErrorResult"
            errorResult.getErrorCode() == errorCode
            errorResult.getShortMessage() == shortMessage
    }

    def "unlinkBroker handles a successful response from trade it api "() {
        given: "a linked broker to unlink"
            TradeItApiClient apiClient = Mock(TradeItApiClient)
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient, linkedBrokerCache)
            apiClient.getTradeItLinkedLogin() >> Mock(TradeItLinkedLogin)

        and: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * brokerLinker.unlinkBrokerAccount(_, _) >> { args ->
                Callback<TradeItResponse> callback = args[1]
                Call<TradeItResponse> call = Mock(Call)
                TradeItResponse tradeItResponse = new TradeItResponse()
                tradeItResponse.sessionToken = "My session token"
                tradeItResponse.longMessages = null
                tradeItResponse.status = TradeItResponseStatus.SUCCESS
                Response<TradeItResponse> response = Response.success(tradeItResponse);
                callback.onResponse(call, response);
            }
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);
            linkedBrokerManager.linkedBrokers = [linkedBroker]

        when: "calling unlinkBroker"
            linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallBackImpl<TradeItResponse>() {
                @Override
                void onSuccess(TradeItResponse response) {
                    successCallBackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "the delete brokerLinker static method was called"
            1 * brokerLinker.deleteLinkedLogin(_)

        and: "The linkedBroker is removed from cache"
            1 * linkedBrokerCache.removeFromCache(linkedBroker)

        and: "the linkedbrokers list is empty"
            linkedBrokerManager.linkedBrokers.size() == 0
    }

    def "getOAuthLoginPopupForTokenUpdateUrl handles a successful response from trade it api"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            String mySpecialUrl = "http://myspecialoauthurl.com?oAuthTempToken=2bae6cc8-8d37-4b4a-ae5e-6bbde9209ac4"
            1 * brokerLinker.getOAuthLoginPopupUrlForTokenUpdate(_, _) >> { args ->
                Callback<TradeItOAuthLoginPopupUrlForTokenUpdateResponse> callback = args[1]
                Call<TradeItOAuthLoginPopupUrlForTokenUpdateResponse> call = Mock(Call)
                TradeItOAuthLoginPopupUrlForTokenUpdateResponse tradeItOAuthLoginPopupUrlForTokenUpdateResponse = new TradeItOAuthLoginPopupUrlForTokenUpdateResponse()
                tradeItOAuthLoginPopupUrlForTokenUpdateResponse.sessionToken = "My session token"
                tradeItOAuthLoginPopupUrlForTokenUpdateResponse.longMessages = null
                tradeItOAuthLoginPopupUrlForTokenUpdateResponse.status = TradeItResponseStatus.SUCCESS
                tradeItOAuthLoginPopupUrlForTokenUpdateResponse.oAuthURL = mySpecialUrl
                Response<TradeItOAuthLoginPopupUrlForTokenUpdateResponse> response = Response.success(tradeItOAuthLoginPopupUrlForTokenUpdateResponse);
                callback.onResponse(call, response);
            }

            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);

        when: "calling getOAuthLoginPopupForTokenUpdateUrl"
            String oAuthUrlResult = null
            linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl("My broker 1", "userId", "my internal app callback", new TradeItCallBackImpl<String>() {

                @Override
                void onSuccess(String oAuthUrl) {
                    successCallBackCount++
                    oAuthUrlResult = oAuthUrl
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "expects the oAuthUrl to be populated"
            oAuthUrlResult == mySpecialUrl
    }
}
