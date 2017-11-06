package it.trade.android.sdk.model

import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import spock.lang.Specification

class TradeItLinkedBrokerAccountParcelableSpec extends Specification {

    TradeItBrokerAccount account = Mock(TradeItBrokerAccount)
    TradeItLinkedBrokerParcelable linkedBroker = Mock(TradeItLinkedBrokerParcelable)
    TradeItLinkedLoginParcelable linkedLogin = Mock(TradeItLinkedLoginParcelable)
    TradeItApiClientParcelable tradeItApiClient = Mock(TradeItApiClientParcelable)
    TradeItLinkedBrokerAccountParcelable linkedBrokerAccount

    void setup() {
        account.accountBaseCurrency >> "My account base currency"
        account.accountNumber >> "My account number"
        account.name >> "My account name"
        linkedBroker.getApiClient() >> tradeItApiClient
        linkedBroker.getLinkedLogin() >> linkedLogin
        linkedLogin.userId >> "My user ID"

        linkedBrokerAccount = new TradeItLinkedBrokerAccountParcelable(linkedBroker, account)
    }

    def "RefreshBalance handles a successful response from trade it"() {
        given: "a successful response from trade it api"
                int successCallbackCount = 0
                int errorCallbackCount = 0
                tradeItApiClient.getAccountOverview(_, _) >> { args ->
                    TradeItCallback<TradeItAccountOverviewResponse> callback = args[1]
                    TradeItAccountOverviewResponse accountOverviewResponse = new TradeItAccountOverviewResponse()

                    TradeItAccountOverview tradeItAccountOverview = new TradeItAccountOverview()
                    tradeItAccountOverview.availableCash = 1200.54
                    tradeItAccountOverview.buyingPower = 2604.45
                    tradeItAccountOverview.dayAbsoluteReturn = 100
                    tradeItAccountOverview.dayPercentReturn = 0.45
                    tradeItAccountOverview.totalAbsoluteReturn = -234.98
                    tradeItAccountOverview.totalPercentReturn = -2.34
                    tradeItAccountOverview.totalValue = 12983.34
                    accountOverviewResponse.accountOverview = tradeItAccountOverview
                    accountOverviewResponse.status = TradeItResponseStatus.SUCCESS
                    callback.onSuccess(accountOverviewResponse)

                }

        when: "calling refresh balance on the linked broker account"
            TradeItBalanceParcelable balance = null
            linkedBrokerAccount.refreshBalance(new TradeItCallback<TradeItLinkedBrokerAccountParcelable>() {
                @Override
                void onSuccess(TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable) {
                    balance = linkedBrokerAccountParcelable.balance
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
                TradeItCallback<TradeItAccountOverviewResponse> callback = args[1]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.SESSION_EXPIRED, "My short message", ["My long message"]))

            }

        when: "calling refresh balance on the linked broker account"
            TradeItErrorResult errorResult = null
            linkedBrokerAccount.refreshBalance(new TradeItCallback<TradeItLinkedBrokerAccountParcelable>() {
                @Override
                void onSuccess(TradeItLinkedBrokerAccountParcelable accountParcelable) {
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
            TradeItPositionParcelable position1 = new TradeItPositionParcelable()
            position1.quantity = 12
            position1.symbol = "GE"
            position1.lastPrice = 29.84
            TradeItPositionParcelable position2 = new TradeItPositionParcelable()
            position2.quantity = 22
            position2.symbol = "AAPL"
            position2.lastPrice = 109.84

            tradeItApiClient.getPositions(_, _) >> { args ->
                TradeItCallback<List<TradeItPosition>> callback = args[1]
                def positions = [position1, position2]
                callback.onSuccess(positions)
            }

        when: "calling refresh balance on the linked broker account"
            List<TradeItPosition> positionsResult = null
            linkedBrokerAccount.refreshPositions(new TradeItCallback<List<TradeItPositionParcelable>>() {
                @Override
                void onSuccess(List<TradeItPositionParcelable> positions) {
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
                TradeItCallback<TradeItGetPositionsResponse> callback = args[1]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.SESSION_EXPIRED, "My short message", ["My long message"]))

            }

        when: "calling refresh balance on the linked broker account"
            TradeItErrorResult errorResult = null
            linkedBrokerAccount.refreshPositions(new TradeItCallback<List<TradeItPosition>>() {
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

    def "RefreshOrdersStatus handles a successful response from trade it"() {
        given: "a successful response from trade it api"
            int successCallbackCount = 0
            int errorCallbackCount = 0
            OrderStatusDetails orderStatusDetails1 = new OrderStatusDetails()
            orderStatusDetails1.orderExpiration = "GTC"
            orderStatusDetails1.orderType = "EQUITY_OR_ETF"
            orderStatusDetails1.orderStatus = "OPEN"
            orderStatusDetails1.orderNumber = "1"
            OrderLeg orderLeg = new OrderLeg()
            orderLeg.symbol = "GE"
            orderLeg.filledQuantity = 0
            orderLeg.orderedQuantity = 10
            orderLeg.action = "BUY"
            PriceInfo priceInfo = new PriceInfo()
            priceInfo.type = "LIMIT"
            priceInfo.limitPrice = 20.3
            orderLeg.priceInfo = priceInfo
            orderStatusDetails1.orderLegs = [orderLeg]

            OrderStatusDetails orderStatusDetails2 = new OrderStatusDetails()
            orderStatusDetails2.orderNumber = 2
            orderStatusDetails2.orderExpiration = "DAY"
            orderStatusDetails2.orderType = "EQUITY_OR_ETF"
            orderStatusDetails2.orderStatus = "OPEN"
            orderStatusDetails2.orderNumber = "1"
            orderLeg = new OrderLeg()
            orderLeg.symbol = "AAPL"
            orderLeg.filledQuantity = 0
            orderLeg.orderedQuantity = 10
            orderLeg.action = "BUY"
            priceInfo = new PriceInfo()
            priceInfo.type = "MARKET"
            orderLeg.priceInfo = priceInfo
            orderStatusDetails2.orderLegs = [orderLeg]


            tradeItApiClient.getAllOrderStatus(_, _) >> { args ->
                TradeItCallback<List<TradeItPosition>> callback = args[1]
                def orders = [orderStatusDetails1, orderStatusDetails2]
                callback.onSuccess(orders)
            }

        when: "calling refresh orders status on the linked broker account"
            List<TradeItOrderStatusParcelable> orderStatusResult = null
            linkedBrokerAccount.refreshOrdersStatus(new TradeItCallback<List<TradeItOrderStatusParcelable>>() {
                @Override
                void onSuccess(List<TradeItOrderStatusParcelable> orderStatusParcelableList) {
                    orderStatusResult = orderStatusParcelableList
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

        then: "expects orders status to be returned"
            orderStatusResult == [new TradeItOrderStatusParcelable(orderStatusDetails1), new TradeItOrderStatusParcelable(orderStatusDetails2)]

        and: "the linked broker account should have his orders status property updated"
            linkedBrokerAccount.getOrdersStatus() == orderStatusResult


    }

    def "RefreshOrders handles an error response from trade it"() {
        given: "an error response from trade it api"
        int successCallbackCount = 0
        int errorCallbackCount = 0
        tradeItApiClient.getAllOrderStatus(_, _) >> { args ->
            TradeItCallback<TradeItOrderStatusParcelable> callback = args[1]
            callback.onError(new TradeItErrorResult(TradeItErrorCode.SESSION_EXPIRED, "My short message", ["My long message"]))
        }

        when: "calling order status on the linked broker account"
            TradeItErrorResult errorResult = null
            linkedBrokerAccount.refreshOrdersStatus(new TradeItCallback<List<TradeItOrderStatusParcelable>>() {
                @Override
                void onSuccess(List<TradeItOrderStatusParcelable> orders) {
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
