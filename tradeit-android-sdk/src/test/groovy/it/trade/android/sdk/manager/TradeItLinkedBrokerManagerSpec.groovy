package it.trade.android.sdk.manager

import it.trade.android.sdk.internal.TradeItKeystoreService
import it.trade.android.sdk.model.TradeItApiClientParcelable
import it.trade.android.sdk.model.TradeItLinkedBrokerCache
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable
import it.trade.android.sdk.model.TradeItLinkedLoginParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker
import it.trade.model.request.TradeItEnvironment
import it.trade.model.request.TradeItLinkedLogin
import it.trade.model.request.TradeItOAuthAccessTokenRequest
import spock.lang.Specification

class TradeItLinkedBrokerManagerSpec extends Specification {

    TradeItKeystoreService keystoreService = Mock(TradeItKeystoreService)
    TradeItLinkedBrokerManager linkedBrokerManager
    String accountLabel = "My account label"
    String myUserId = "My trade it userId"
    String myUserToken = "My trade it userToken"
    String apiKey = "test api key"
    TradeItEnvironment environment = TradeItEnvironment.QA
    TradeItLinkedBrokerCache linkedBrokerCache = Mock(TradeItLinkedBrokerCache)
    TradeItApiClientParcelable apiClient = Mock(TradeItApiClientParcelable)

    void setup() {
        keystoreService.getLinkedLogins() >> []
        apiClient.getEnvironment() >> TradeItEnvironment.QA
        apiClient.getApiKey() >> apiClient
        linkedBrokerManager = new TradeItLinkedBrokerManager(apiClient, linkedBrokerCache, keystoreService);
    }

    def "GetAvailableBrokers handles a successful response from trade it api"() {
        given: "a successful response from trade it"
            1 * apiClient.getAvailableBrokers(_) >> { args ->
                TradeItCallback<List<Broker>> callback = args[0]
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
                callback.onSuccess(brokerList)
            }


        when: "calling getAvailableBrokers"
            int successCallBackCount1 = 0
            int successCallBackCount2 = 0
            int errorCallBackCount1 = 0
            int errorCallBackCount2 = 0

            List<TradeItAvailableBrokersResponse.Broker> brokerList1 = null
            List<TradeItAvailableBrokersResponse.Broker> brokerList2 = null

            linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<Broker>>() {
                @Override
                public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerListResponse) {
                    successCallBackCount1++
                    brokerList1 = brokerListResponse
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    errorCallBackCount1++
                }
            });

            linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<Broker>>() {
                @Override
                public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerListResponse) {
                    successCallBackCount2++
                    brokerList2 = brokerListResponse
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    errorCallBackCount2++
                }
            });

        then: "expects the successCallback called once"
            successCallBackCount1 == 1
            errorCallBackCount1 == 0
            successCallBackCount2 == 1
            errorCallBackCount2 == 0

        and: "expects a list of 3 brokers"
            brokerList1?.size() == 3
            brokerList1[0].shortName == "Broker1"
            brokerList1[0].longName == "My long Broker1"
            brokerList1[1].shortName == "Broker2"
            brokerList1[1].longName == "My long Broker2"
            brokerList1[2].shortName == "Broker3"
            brokerList1[2].longName == "My long Broker3"

            brokerList2?.size() == 3
            brokerList2[0].shortName == "Broker1"
            brokerList2[0].longName == "My long Broker1"
            brokerList2[1].shortName == "Broker2"
            brokerList2[1].longName == "My long Broker2"
            brokerList2[2].shortName == "Broker3"
            brokerList2[2].longName == "My long Broker3"
    }

    def "GetAvailableBrokers handles an error response from trade it api"() {
        given: "an error response from trade it"
            1 * apiClient.getAvailableBrokers(_) >> { args ->
                TradeItCallback<List<Broker>> callback = args[0]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.TOKEN_INVALID_OR_EXPIRED, "This is the short message for the session expired error", ["This is the long message for the session expired error"]))
            }

        when: "calling getAvailableBrokers"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<TradeItAvailableBrokersResponse.Broker>>() {
                @Override
                public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerListResponse) {
                    successCallBackCount++
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            });

        then: "expects the errorCallBack called once"
            successCallBackCount == 0
            errorCallBackCount == 1
    }

    def "linkBroker handles a successful response from trade it api"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * apiClient.linkBrokerAccount("My username", "My password", "My broker 1",  _ as TradeItCallback) >> { args ->
                TradeItCallback<TradeItLinkedLogin> callback = args[3]
                callback.onSuccess(new TradeItLinkedLogin("My broker 1", myUserId, myUserToken))
            }

        when: "calling linkBroker"
            TradeItLinkedBrokerParcelable linkedBrokerResult = null
            linkedBrokerManager.linkBroker(accountLabel, "My broker 1", "My username", "My password", new TradeItCallback<TradeItLinkedBrokerParcelable>() {

                @Override
                void onSuccess(TradeItLinkedBrokerParcelable linkedBroker) {
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

        and: "the saveLinkedLogin was called"
            1 * keystoreService.saveLinkedLogin(_, _)

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
        1 * apiClient.linkBrokerAccount("My username", "My password", "My broker 1",  _ as TradeItCallback) >> { args ->
            TradeItCallback<TradeItLinkedLogin> callback = args[3]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.BROKER_AUTHENTICATION_ERROR, shortMessage, null))
            }

        when: "calling linkBroker"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.linkBroker(accountLabel, "My broker 1", "My username", "My password", new TradeItCallback<TradeItLinkedBrokerParcelable>() {

                @Override
                void onSuccess(TradeItLinkedBrokerParcelable linkedBroker) {
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
            1 * apiClient.getOAuthLoginPopupUrlForMobile(_, _, _) >> { args ->
                TradeItCallback<String> callback = args[2]
                callback.onSuccess(mySpecialUrl)
            }

        when: "calling getOAuthLoginPopupUrl"
            TradeItErrorResult errorResult = null
            String oAuthUrlResult = null
            linkedBrokerManager.getOAuthLoginPopupUrl("My broker 1", "my internal app callback", new TradeItCallback<String>() {

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
            1 * apiClient.getOAuthLoginPopupUrlForMobile("My broker 1", "my internal app callback", _ as TradeItCallback) >> { args ->
                TradeItCallback<String> callback = args[2]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.BROKER_AUTHENTICATION_ERROR, shortMessage, null))
            }

        when: "calling getOAuthLoginPopupUrl"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.getOAuthLoginPopupUrl("My broker 1", "my internal app callback", new TradeItCallback<String>() {

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

            1 * apiClient.linkBrokerWithOauthVerifier(_, _) >> { args ->
                TradeItCallback<TradeItLinkedLogin> callback = args[1]
                callback.onSuccess(new TradeItLinkedLogin("My broker 1", myUserId, myUserToken))
            }


        when: "calling linkBrokerWithOauthVerifier"
            TradeItLinkedBrokerParcelable linkedBrokerResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My oAuthVerifier", new TradeItCallback<TradeItLinkedBrokerParcelable>() {

                @Override
                void onSuccess(TradeItLinkedBrokerParcelable linkedBroker) {
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

        and: "saveLinkedLogin method was called"
            1 * keystoreService.saveLinkedLogin(_, _)

        and: "expects a linkedBroker containing userId and userToken"
            linkedBrokerResult.getLinkedLogin().userId == myUserId
            linkedBrokerResult.getLinkedLogin().userToken == myUserToken
            linkedBrokerResult.getLinkedLogin().broker == "My broker 1"
    }

    def "linkBrokerWithOauthVerifier handles successful response from trade it api with an already existing userId (token update)"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * apiClient.linkBrokerWithOauthVerifier(_, _) >> { args ->
                TradeItCallback<TradeItLinkedLogin> callback = args[1]
                callback.onSuccess(new TradeItLinkedLogin("My broker 1", myUserId, myUserToken))
            }

        and: "an already linked broker with this user id"
            TradeItOAuthAccessTokenRequest request = new TradeItOAuthAccessTokenRequest()
            TradeItOAuthAccessTokenResponse response = new TradeItOAuthAccessTokenResponse()
            response.userId = myUserId
            response.userToken = "My old userToken"
            response.broker = "My broker 1"
            TradeItLinkedLoginParcelable linkedLogin = new TradeItLinkedLoginParcelable(request, response);
            TradeItApiClientParcelable apiClient = Mock(TradeItApiClientParcelable)
            TradeItLinkedBrokerParcelable existingLinkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache)
            linkedBrokerManager.linkedBrokers = [existingLinkedBroker]



        when: "calling linkBrokerWithOauthVerifier"
            TradeItLinkedBrokerParcelable linkedBrokerResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My oAuthVerifier", new TradeItCallback<TradeItLinkedBrokerParcelable>() {

                @Override
                void onSuccess(TradeItLinkedBrokerParcelable linkedBroker) {
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

        and: "the updateLinkedLogin method was called"
            1 * keystoreService.updateLinkedLogin(_)

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
            1 * apiClient.linkBrokerWithOauthVerifier(_, _) >> { args ->
                TradeItCallback<String> callback = args[1]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.BROKER_AUTHENTICATION_ERROR, shortMessage, null))
            }

        when: "calling linkBrokerWithOauthVerifier"
            TradeItErrorResult errorResult = null
            linkedBrokerManager.linkBrokerWithOauthVerifier(accountLabel, "My oAuthVerifier", new TradeItCallback<TradeItLinkedBrokerParcelable>() {

                @Override
                void onSuccess(TradeItLinkedBrokerParcelable linkedBroker) {
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
            TradeItApiClientParcelable apiClient = Mock(TradeItApiClientParcelable)
            TradeItLinkedLoginParcelable linkedLogin = Mock(TradeItLinkedLoginParcelable)
            TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache)

        and: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * this.apiClient.unlinkBrokerAccount(_, _) >> { args ->
                TradeItCallback callback = args[1]
                TradeItResponse tradeItResponse = new TradeItResponse()
                tradeItResponse.sessionToken = "My session token"
                tradeItResponse.longMessages = null
                tradeItResponse.status = TradeItResponseStatus.SUCCESS
                callback.onSuccess(tradeItResponse)
            }
            linkedBrokerManager.linkedBrokers = [linkedBroker]

        when: "calling unlinkBroker"
            linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallback<TradeItResponse>() {
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

        and: "the deleteLinkedLogin method was called"
            1 * keystoreService.deleteLinkedLogin(_)

        and: "The linkedBroker is removed from cache"
            1 * linkedBrokerCache.removeFromCache(linkedBroker)

        and: "the linkedbrokers list is empty"
            linkedBrokerManager.linkedBrokers.size() == 0
    }

    def "unlinkBrokerByUserId handles a successful response from trade it api"() {
        given: "a userId to unlink"
            String userId = "MyUserId"

        and: "The linked broker exist for this userId"
            TradeItLinkedLoginParcelable linkedLogin = Mock(TradeItLinkedLoginParcelable)
            linkedLogin.userId >> userId
            TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache)
            linkedBroker.linkedLogin.userId = userId
            linkedBrokerManager.linkedBrokers = [linkedBroker]

        and: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            1 * this.apiClient.unlinkBrokerAccount(_, _) >> { args ->
                TradeItCallback callback = args[1]
                TradeItResponse tradeItResponse = new TradeItResponse()
                tradeItResponse.sessionToken = "My session token"
                tradeItResponse.longMessages = null
                tradeItResponse.status = TradeItResponseStatus.SUCCESS
                callback.onSuccess(tradeItResponse)
            }

        when: "calling unlinkBrokerByUserId"
            linkedBrokerManager.unlinkBrokerByUserId(userId, new TradeItCallback<TradeItResponse>() {
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

        and: "the deleteLinkedLogin method was called"
            1 * keystoreService.deleteLinkedLogin(_)

        and: "The linkedBroker is removed from cache"
            1 * linkedBrokerCache.removeFromCache(linkedBroker)

        and: "the linkedbrokers list is empty"
            linkedBrokerManager.linkedBrokers.size() == 0
    }

    def "unlinkBrokerByUserId returns a TradeItError when the linkedBroker is not found"() {
        given: "a userId to unlink"
            String userId = "MyUserId"

        and: "The linked broker doesn't exist for this userId"
            linkedBrokerManager.linkedBrokers = []


        when: "calling unlinkBrokerByUserId"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            linkedBrokerManager.unlinkBrokerByUserId(userId, new TradeItCallback<TradeItResponse>() {
                @Override
                void onSuccess(TradeItResponse response) {
                    successCallBackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallBackCount++
                }
            })

        then: "expects the errorCallback called once"
            successCallBackCount == 0
            errorCallBackCount == 1

        and: "the method on the api was not called"
            0 * this.apiClient.unlinkBrokerAccount(_, _)
    }

    def "getOAuthLoginPopupForTokenUpdateUrl handles a successful response from trade it api"() {
        given: "a successful response from trade it api"
            int successCallBackCount = 0
            int errorCallBackCount = 0
            String mySpecialUrl = "http://myspecialoauthurl.com?oAuthTempToken=2bae6cc8-8d37-4b4a-ae5e-6bbde9209ac4"

            TradeItLinkedLoginParcelable linkedLogin = Mock(TradeItLinkedLoginParcelable.class)
            linkedLogin.userId = "My userId"

            TradeItLinkedBrokerParcelable linkedBroker = Mock(TradeItLinkedBrokerParcelable.class)
            1 * linkedBroker.getBrokerName() >> "My broker 1"
            1 * linkedBroker.getLinkedLogin() >> linkedLogin

            1 * apiClient.getOAuthLoginPopupUrlForTokenUpdate("My broker 1", "My userId", "my internal app callback", _) >> { broker, userId, userToken,  TradeItCallback<String> callback ->
                callback.onSuccess(mySpecialUrl);
            }

        when: "calling getOAuthLoginPopupForTokenUpdateUrl"
            String oAuthUrlResult = null
            linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl(linkedBroker, "my internal app callback", new TradeItCallback<String>() {

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
