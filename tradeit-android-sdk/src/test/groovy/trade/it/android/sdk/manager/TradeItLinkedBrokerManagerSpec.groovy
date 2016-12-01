package trade.it.android.sdk.manager

import android.content.Context
import it.trade.tradeitapi.API.TradeItAccountLinker
import it.trade.tradeitapi.model.*
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker
import org.junit.Rule
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification
import trade.it.android.sdk.model.TradeItCallBackImpl
import trade.it.android.sdk.model.TradeItErrorResult

/**
 * Note: if you run this with android studio, you may need to add '-noverify' in the VM options because of a bug in PowerMock
 */
@PrepareForTest([TradeItAccountLinker.class])
class TradeItLinkedBrokerManagerSpec extends Specification {

    Context context = Mock(Context)
    TradeItAccountLinker accountLinker = Mock(TradeItAccountLinker)
    TradeItLinkedBrokerManager linkedBrokerManager
    Context context = Mock(Context)
    String accountLabel = "My account label"
    String myUserId = "My trade it userId"
    String myUserToken = "My trade it userToken"

    @Rule
    PowerMockRule powerMockRule = new PowerMockRule();

    void setup() {
    }

    def "GetAvailableBrokers handles a successful response from trade it api"() {
        given: "a successful response from trade it"
            1 * accountLinker.getAvailableBrokers(_) >> { args ->
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
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);

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
            1 * accountLinker.getAvailableBrokers(_) >> { args ->
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
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);

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
            1 * accountLinker.linkBrokerAccount(_, _) >> { args ->
                Callback<TradeItLinkAccountResponse> callback = args[1]
                Call<TradeItLinkAccountResponse> call = Mock(Call)
                TradeItLinkAccountResponse tradeItLinkAccountResponse = new TradeItLinkAccountResponse()
                tradeItLinkAccountResponse.sessionToken = "My session token"
                tradeItLinkAccountResponse.longMessages = null
                tradeItLinkAccountResponse.status = TradeItResponseStatus.SUCCESS
                tradeItLinkAccountResponse.userId = myUserId
                tradeItLinkAccountResponse.userToken = myUserToken
                Response<TradeItLinkAccountResponse> response = Response.success(tradeItLinkAccountResponse);
                callback.onResponse(call, response);
            }
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);


        when: "calling linkBroker"
            PowerMockito.doNothing().when(TradeItAccountLinker.class);
            TradeItLinkedAccount linkedBroker = null
            linkedBrokerManager.linkBroker(accountLabel, "My broker 1", "My username", "My password", new TradeItCallBackImpl<TradeItLinkedAccount>() {

                @Override
                void onSuccess(TradeItLinkedAccount linkedAccount) {
                    successCallBackCount++
                    linkedBroker = linkedAccount
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "the accountLinker static method save was called"
            PowerMockito.verifyStatic()

        and: "expects a linkedBroker containing userId and userToken"
            linkedBroker.userId == myUserId
            linkedBroker.userToken == myUserToken
            linkedBroker.broker == "My broker 1"
    }

    def "linkBroker handles an error response from trade it api"() {
        given: "An error response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            TradeItErrorCode errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            String shortMessage = "My error when linking broker"
            1 * accountLinker.linkBrokerAccount(_, _) >> { args ->
                Callback<TradeItLinkAccountResponse> callback = args[1]
                Call<TradeItLinkAccountResponse> call = Mock(Call)
                TradeItLinkAccountResponse tradeItLinkAccountResponse = new TradeItLinkAccountResponse()
                tradeItLinkAccountResponse.sessionToken = "My session token"
                tradeItLinkAccountResponse.longMessages = null
                tradeItLinkAccountResponse.status = TradeItResponseStatus.ERROR
                tradeItLinkAccountResponse.code = errorCode
                tradeItLinkAccountResponse.shortMessage = shortMessage
                tradeItLinkAccountResponse.userId = null
                tradeItLinkAccountResponse.userToken = null
                Response<TradeItLinkAccountResponse> response = Response.success(tradeItLinkAccountResponse);
                callback.onResponse(call, response);
            }
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);

        when: "calling linkBroker"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.linkBroker(accountLabel, "My broker 1", "My username", "My password", new TradeItCallBackImpl<TradeItLinkedAccount>() {

                @Override
                void onSuccess(TradeItLinkedAccount linkedAccount) {
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
            1 * accountLinker.getOAuthLoginPopupUrlForMobile(_, _) >> { args ->
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
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);

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
            1 * accountLinker.getOAuthLoginPopupUrlForMobile(_, _) >> { args ->
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
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);

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
            1 * accountLinker.getOAuthAccessToken(_, _) >> { args ->
                Callback<TradeItOAuthAccessTokenResponse> callback = args[1]
                Call<TradeItOAuthAccessTokenResponse> call = Mock(Call)
                TradeItOAuthAccessTokenResponse tradeItOAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse()
                tradeItOAuthAccessTokenResponse.sessionToken = "My session token"
                tradeItOAuthAccessTokenResponse.longMessages = null
                tradeItOAuthAccessTokenResponse.status = TradeItResponseStatus.SUCCESS
                tradeItOAuthAccessTokenResponse.userId = myUserId
                tradeItOAuthAccessTokenResponse.userToken = myUserToken
                Response<TradeItLinkAccountResponse> response = Response.success(tradeItOAuthAccessTokenResponse);
                callback.onResponse(call, response);
            }
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);


        when: "calling linkBrokerWithOauthVerifier"
            PowerMockito.doNothing().when(TradeItAccountLinker.class);
            TradeItLinkedAccount linkedBroker = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My broker 1", "My oAuthVerifier", new TradeItCallBackImpl<TradeItLinkedAccount>() {

                @Override
                void onSuccess(TradeItLinkedAccount linkedAccount) {
                    successCallBackCount++
                    linkedBroker = linkedAccount
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallBackCount == 1
            errorCallBackCount == 0

        and: "the accountLinker static method save was called"
            PowerMockito.verifyStatic()

        and: "expects a linkedBroker containing userId and userToken"
            linkedBroker.userId == myUserId
            linkedBroker.userToken == myUserToken
            linkedBroker.broker == "My broker 1"
    }

    def "linkBrokerWithOauthVerifier handles an error response from trade it api"() {
        given: "An error response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            TradeItErrorCode errorCode = TradeItErrorCode.BROKER_AUTHENTICATION_ERROR
            String shortMessage = "My error when linking broker"
            1 * accountLinker.getOAuthAccessToken(_, _) >> { args ->
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
                Response<TradeItLinkAccountResponse> response = Response.success(tradeItOAuthAccessTokenResponse);
                callback.onResponse(call, response);
            }
            PowerMockito.mockStatic(TradeItAccountLinker.class)
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, accountLinker);

        when: "calling linkBroker"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My broker 1", "My oAuthVerifier", new TradeItCallBackImpl<TradeItLinkedAccount>() {

                @Override
                void onSuccess(TradeItLinkedAccount linkedAccount) {
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
}
