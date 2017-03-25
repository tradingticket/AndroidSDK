package it.trade.android.sdk.model

import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification

class TradeItLinkedBrokerAccountSpec extends Specification {

    TradeItBrokerAccount account = Mock(TradeItBrokerAccount)
    TradeItLinkedBroker linkedBroker = Mock(TradeItLinkedBroker)
    TradeItLinkedLogin linkedLogin = Mock(TradeItLinkedLogin)
    TradeItApiClient tradeItApiClient = Mock(TradeItApiClient)
    TradeItLinkedBrokerAccount linkedBrokerAccount

    void setup() {
        account.accountBaseCurrency >> "My account base currency"
        account.accountNumber >> "My account number"
        account.name >> "My account name"
        linkedBroker.getTradeItApiClient() >> tradeItApiClient
        linkedBroker.getLinkedLogin() >> linkedLogin
        linkedLogin.userId >> "My user ID"

        linkedBrokerAccount = new TradeItLinkedBrokerAccount(linkedBroker, account)
    }

    def "RefreshBalance handles a successful response from trade it"() {
        given: "a successful response from trade it api"
                int successCallbackCount = 0
                int errorCallbackCount = 0
                tradeItApiClient.getAccountOverview(_, _) >> { args ->
                    Callback<TradeItGetAccountOverviewResponse> callback = args[1]
                    Call<TradeItGetAccountOverviewResponse> call = Mock(Call)
                    TradeItGetAccountOverviewResponse tradeItGetAccountOverviewResponse = new TradeItGetAccountOverviewResponse()
                    tradeItGetAccountOverviewResponse.availableCash = 1200.54
                    tradeItGetAccountOverviewResponse.buyingPower = 2604.45
                    tradeItGetAccountOverviewResponse.dayAbsoluteReturn = 100
                    tradeItGetAccountOverviewResponse.dayPercentReturn = 0.45
                    tradeItGetAccountOverviewResponse.totalAbsoluteReturn = -234.98
                    tradeItGetAccountOverviewResponse.totalPercentReturn = -2.34
                    tradeItGetAccountOverviewResponse.totalValue = 12983.34
                    tradeItGetAccountOverviewResponse.status = TradeItResponseStatus.SUCCESS
                    Response<TradeItGetAccountOverviewResponse> response = Response.success(tradeItGetAccountOverviewResponse)
                    callback.onResponse(call, response)

                }

        when: "calling refresh balance on the linked broker account"
            TradeItGetAccountOverviewResponse balance = null
            linkedBrokerAccount.refreshBalance(new TradeItCallBackImpl<TradeItGetAccountOverviewResponse>() {
                @Override
                void onSuccess(TradeItGetAccountOverviewResponse response) {
                    balance = response
                    successCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

        then: "expects the successCallback called once"
            successCallbackCount == 1
            errorCallbackCount == 0

        then: "expects balance correctly populated"
            balance.availableCash == 1200.54
            balance.buyingPower == 2604.45
            balance.dayAbsoluteReturn == 100
            balance.dayPercentReturn == 0.45
            balance.totalAbsoluteReturn == -234.98
            balance.totalPercentReturn == -2.34
            balance.totalValue == 12983.34

        and: "the linked broker account should have his balance property updated"
            linkedBrokerAccount.getBalance() == balance

    }

    def "RefreshBalance handles an error response from trade it"() {
        given: "an error response from trade it api"
            int successCallbackCount = 0
            int errorCallbackCount = 0
            tradeItApiClient.getAccountOverview(_, _) >> { args ->
                Callback<TradeItGetAccountOverviewResponse> callback = args[1]
                Call<TradeItGetAccountOverviewResponse> call = Mock(Call)
                TradeItGetAccountOverviewResponse tradeItGetAccountOverviewResponse = new TradeItGetAccountOverviewResponse()
                tradeItGetAccountOverviewResponse.code = TradeItErrorCode.SESSION_EXPIRED
                tradeItGetAccountOverviewResponse.status = TradeItResponseStatus.ERROR
                tradeItGetAccountOverviewResponse.shortMessage = "My short message"
                tradeItGetAccountOverviewResponse.longMessages = ["My long message"]

                Response<TradeItGetAccountOverviewResponse> response = Response.success(tradeItGetAccountOverviewResponse)
                callback.onResponse(call, response)

            }

        when: "calling refresh balance on the linked broker account"
            TradeItErrorResult errorResult = null
            linkedBrokerAccount.refreshBalance(new TradeItCallBackImpl<TradeItGetAccountOverviewResponse>() {
                @Override
                void onSuccess(TradeItGetAccountOverviewResponse response) {
                    successCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorResult = error
                    errorCallbackCount++
                }
            })

            then: "expects the errorCallbackCount called once"
                successCallbackCount == 0
                errorCallbackCount == 1

            then: "expects error result correctly populated"
                errorResult.errorCode == TradeItErrorCode.SESSION_EXPIRED
                errorResult.shortMessage == "My short message"
                errorResult.longMessages == ["My long message"]
    }

    def "RefreshPositions handles a successful response from trade it"() {
        given: "a successful response from trade it api"
            int successCallbackCount = 0
            int errorCallbackCount = 0
            TradeItPosition position1 = new TradeItPosition()
            position1.quantity = 12
            position1.symbol = "GE"
            position1.lastPrice = 29.84
            TradeItPosition position2 = new TradeItPosition()
            position2.quantity = 22
            position2.symbol = "AAPL"
            position2.lastPrice = 109.84

            tradeItApiClient.getPositions(_, _) >> { args ->
                Callback<TradeItGetPositionsResponse> callback = args[1]
                Call<TradeItGetPositionsResponse> call = Mock(Call)
                TradeItGetPositionsResponse tradeItGetPositionsResponse = new TradeItGetPositionsResponse()
                tradeItGetPositionsResponse.positions = [position1, position2]
                tradeItGetPositionsResponse.status = TradeItResponseStatus.SUCCESS
                Response<TradeItGetPositionsResponse> response = Response.success(tradeItGetPositionsResponse)
                callback.onResponse(call, response)

            }

        when: "calling refresh balance on the linked broker account"
            List<TradeItPosition> positionsResult = null
            linkedBrokerAccount.refreshPositions(new TradeItCallBackImpl<List<TradeItPosition>>() {
                @Override
                void onSuccess(List<TradeItPosition> positions) {
                    positionsResult = positions
                    successCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

            then: "expects the successCallback called once"
                successCallbackCount == 1
                errorCallbackCount == 0

        then: "expects positions to be returned"
            positionsResult == [position1, position2]

        and: "the linked broker account should have his positions property updated"
            linkedBrokerAccount.getPositions() == positionsResult


    }

    def "RefreshPositions handles an error response from trade it"() {
        given: "an error response from trade it api"
            int successCallbackCount = 0
            int errorCallbackCount = 0
            tradeItApiClient.getPositions(_, _) >> { args ->
                Callback<TradeItGetPositionsResponse> callback = args[1]
                Call<TradeItGetPositionsResponse> call = Mock(Call)
                TradeItGetPositionsResponse tradeItGetPositionsResponse = new TradeItGetPositionsResponse()
                tradeItGetPositionsResponse.code = TradeItErrorCode.SESSION_EXPIRED
                tradeItGetPositionsResponse.status = TradeItResponseStatus.ERROR
                tradeItGetPositionsResponse.shortMessage = "My short message"
                tradeItGetPositionsResponse.longMessages = ["My long message"]

                Response<TradeItGetPositionsResponse> response = Response.success(tradeItGetPositionsResponse)
                callback.onResponse(call, response)

            }

        when: "calling refresh balance on the linked broker account"
            TradeItErrorResult errorResult = null
            linkedBrokerAccount.refreshPositions(new TradeItCallBackImpl<List<TradeItPosition>>() {
                @Override
                void onSuccess(List<TradeItPosition> positions) {
                    successCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorResult = error
                    errorCallbackCount++
                }
            })

        then: "expects the errorCallbackCount called once"
            successCallbackCount == 0
            errorCallbackCount == 1

        then: "expects error result correctly populated"
            errorResult.errorCode == TradeItErrorCode.SESSION_EXPIRED
            errorResult.shortMessage == "My short message"
            errorResult.longMessages == ["My long message"]


    }
}
