package trade.it.android.sdk.manager

import it.trade.tradeitapi.API.TradeItAccountLinker
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker
import it.trade.tradeitapi.model.TradeItEnvironment
import it.trade.tradeitapi.model.TradeItErrorCode
import it.trade.tradeitapi.model.TradeItResponseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification
import trade.it.android.sdk.model.TradeItCallBackImpl
import trade.it.android.sdk.model.TradeItErrorResult

class TradeItLinkedBrokerManagerSpec extends Specification {

    TradeItLinkedBrokerManager linkedBrokerManager = new TradeItLinkedBrokerManager("test", TradeItEnvironment.QA);
    TradeItAccountLinker accountLinker = Mock(TradeItAccountLinker)

    void setup() {
        linkedBrokerManager.accountLinker = accountLinker
    }

    def "GetAvailableBrokers handles a successful response from trade it api"() {
        given: "a successful response from trade it"
            1 * accountLinker.getAvailableBrokers(_ ) >> { args ->
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
            1 * accountLinker.getAvailableBrokers(_ ) >> { args ->
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
}
