package trade.it.android.sdk.model

import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.model.TradeItAuthenticateResponse
import it.trade.tradeitapi.model.TradeItErrorCode
import it.trade.tradeitapi.model.TradeItGetAccountOverviewResponse
import it.trade.tradeitapi.model.TradeItGetPositionsResponse
import it.trade.tradeitapi.model.TradeItResponseStatus
import it.trade.tradeitapi.model.TradeItGetPositionsResponse.Position
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification

class TradeItLinkedBrokerAccountSpec extends Specification {

    TradeItAuthenticateResponse.Account account = Mock(TradeItAuthenticateResponse.Account)
    TradeItLinkedBroker linkedBroker = Mock(TradeItLinkedBroker)
    TradeItApiClient tradeItApiClient = Mock(TradeItApiClient)
    TradeItLinkedBrokerAccount linkedBrokerAccount

    void setup() {
        account.accountBaseCurrency >> "My account base currency"
        account.accountNumber >> "My account number"
        account.name >> "My account name"
        linkedBroker.getTradeItApiClient() >> tradeItApiClient

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
            Position position1 = new Position()
            position1.quantity = 12
            position1.symbol = "GE"
            position1.lastPrice = 29.84
            Position position2 = new Position()
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
            List<Position> positions = null
            linkedBrokerAccount.refreshPositions(new TradeItCallBackImpl<TradeItGetPositionsResponse>() {
                @Override
                void onSuccess(TradeItGetPositionsResponse response) {
                    positions = response.positions
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
            positions == [positions1, positions2]

        and: "the linked broker account should have his positions property updated"
            linkedBrokerAccount.getPositions() == balance


    }

    def "RefreshPositions handles an error response from trade it"() {
        given: "an error response from trade it api"
            int successCallbackCount = 0
            int errorCallbackCount = 0
            tradeItApiClient.getPositions(_, _) >> { args ->
                Callback<TradeItGetPositionsResponse> callback = args[1]
                Call<TradeItGetPositionsResponse> call = Mock(Call)
                tradeItGetPositionsResponse tradeItGetPositionsResponse = new TradeItGetPositionsResponse()
                tradeItGetPositionsResponse.code = TradeItErrorCode.SESSION_EXPIRED
                tradeItGetPositionsResponse.status = TradeItResponseStatus.ERROR
                tradeItGetPositionsResponse.shortMessage = "My short message"
                tradeItGetPositionsResponse.longMessages = ["My long message"]

                Response<TradeItGetPositionsResponse> response = Response.success(tradeItGetPositionsResponse)
                callback.onResponse(call, response)

            }

        when: "calling refresh balance on the linked broker account"
            TradeItErrorResult errorResult = null
            linkedBrokerAccount.refreshPositions(new TradeItCallBackImpl<TradeItGetPositionsResponse>() {
                @Override
                void onSuccess(TradeItGetPositionsResponse response) {
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
