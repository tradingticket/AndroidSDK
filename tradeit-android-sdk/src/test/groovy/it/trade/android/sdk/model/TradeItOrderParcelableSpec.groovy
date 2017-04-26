package it.trade.android.sdk.model

import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpiration
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallBackImpl
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import spock.lang.Specification

class TradeItOrderParcelableSpec extends Specification {
    private TradeItLinkedBrokerAccountParcelable linkedBrokerAccount = Mock(TradeItLinkedBrokerAccountParcelable)
    private TradeItOrderParcelable order = new TradeItOrderParcelable(linkedBrokerAccount, "My symbol")

    void setup() {
        linkedBrokerAccount.getTradeItApiClient() >> Mock(TradeItApiClientParcelable)
        linkedBrokerAccount.accountNumber >> "My account number"
        linkedBrokerAccount.accountName >> "My account name"
    }

    def "previewOrder handles a successful response from trade it"() {
        given: "a successful response from trade it"
            int successfulCallbackCount = 0
            int errorCallbackCount = 0
            order.setAction(TradeItOrderAction.BUY)
            order.setExpiration(TradeItOrderExpiration.GOOD_FOR_DAY)
            order.setQuantity(1)

            linkedBrokerAccount.getTradeItApiClient().previewStockOrEtfOrder(_, _) >> { args ->
                TradeItCallback<TradeItPreviewStockOrEtfOrderResponse> callback = args[1]
                TradeItPreviewStockOrEtfOrderResponse tradeItPreviewStockOrEtfOrderResponse = new TradeItPreviewStockOrEtfOrderResponse()
                tradeItPreviewStockOrEtfOrderResponse.sessionToken = "My session token"
                tradeItPreviewStockOrEtfOrderResponse.longMessages = null
                tradeItPreviewStockOrEtfOrderResponse.status = TradeItResponseStatus.REVIEW_ORDER
                tradeItPreviewStockOrEtfOrderResponse.orderId = "My Order Id"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails = new OrderDetails()
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderAction = "buy"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderSymbol = "My symbol"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderExpiration = "day"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderQuantity = 1
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderPrice = "market"

                callback.onSuccess(tradeItPreviewStockOrEtfOrderResponse);
            }

        when: "calling preview order"
            TradeItPreviewStockOrEtfOrderResponse previewResponse = null
            order.previewOrder(new TradeItCallBackImpl<TradeItPreviewStockOrEtfOrderResponse>() {
                @Override
                void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
                    previewResponse = response
                    successfulCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

        then: "expect the sucess callback called"
            successfulCallbackCount == 1
            errorCallbackCount == 0

        and: "the preview response is correctly filled"
            previewResponse.status == TradeItResponseStatus.REVIEW_ORDER
            previewResponse.orderId == "My Order Id"
            previewResponse.orderDetails.orderAction == "buy"
            previewResponse.orderDetails.orderSymbol == "My symbol"
            previewResponse.orderDetails.orderExpiration == "day"
            previewResponse.orderDetails.orderQuantity == 1.0
            previewResponse.orderDetails.orderPrice == "market"
    }

    def "previewOrder handles an error response from trade it"() {
        given: "an error response from trade it"
            int successfulCallbackCount = 0
            int errorCallbackCount = 0
            order.setAction(TradeItOrderAction.BUY)
            order.setExpiration(TradeItOrderExpiration.GOOD_FOR_DAY)
            order.setQuantity(1)

            linkedBrokerAccount.getTradeItApiClient().previewStockOrEtfOrder(_, _) >> { args ->
                TradeItCallback<TradeItPreviewStockOrEtfOrderResponse> callback = args[1]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.BROKER_ACCOUNT_ERROR, "My short error message", ["My long error message"]))
            }

        when: "calling preview order"
            TradeItErrorResult errorResult = null
            order.previewOrder(new TradeItCallBackImpl<TradeItPreviewStockOrEtfOrderResponse>() {
                @Override
                void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
                    successfulCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                    errorResult = error
                }
            })

        then: "expect the sucess callback called"
            successfulCallbackCount == 0
            errorCallbackCount == 1

        and: "the error is correctly populated"
            errorResult.errorCode == TradeItErrorCode.BROKER_ACCOUNT_ERROR
            errorResult.shortMessage == "My short error message"
            errorResult.longMessages == ["My long error message"]
            errorResult.httpCode == 200
    }

    def "placeOrder handles a successful response from trade it"() {
        given: "a successful response from trade it"
            int successfulCallbackCount = 0
            int errorCallbackCount = 0

            linkedBrokerAccount.getTradeItApiClient().placeStockOrEtfOrder(_, _) >> { args ->
                TradeItCallback<TradeItPlaceStockOrEtfOrderResponse> callback = args[1]
                TradeItPlaceStockOrEtfOrderResponse tradeItPlaceStockOrEtfOrderResponse = new TradeItPlaceStockOrEtfOrderResponse()
                tradeItPlaceStockOrEtfOrderResponse.sessionToken = "My session token"
                tradeItPlaceStockOrEtfOrderResponse.longMessages = null
                tradeItPlaceStockOrEtfOrderResponse.status = TradeItResponseStatus.SUCCESS
                tradeItPlaceStockOrEtfOrderResponse.orderNumber = "My Order Id"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo = new OrderInfo()
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.action = "buy"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.symbol = "My symbol"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.expiration = "day"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.quantity = 1
                Price price  = new Price()
                price.type = "market"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.price = price
                callback.onSuccess(tradeItPlaceStockOrEtfOrderResponse);
            }

        when: "calling place order"
            TradeItPlaceStockOrEtfOrderResponse placeOrderResponse = null
            order.placeOrder("My Order Id", new TradeItCallBackImpl<TradeItPlaceStockOrEtfOrderResponse>() {
                @Override
                void onSuccess(TradeItPlaceStockOrEtfOrderResponse response) {
                    placeOrderResponse= response
                    successfulCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                }
            })

        then: "expect the sucess callback called"
            successfulCallbackCount == 1
            errorCallbackCount == 0

        and: "the place order response is correctly filled"
            placeOrderResponse.status == TradeItResponseStatus.SUCCESS
            placeOrderResponse.orderNumber == "My Order Id"
            placeOrderResponse.orderInfo.action == "buy"
            placeOrderResponse.orderInfo.symbol == "My symbol"
            placeOrderResponse.orderInfo.expiration == "day"
            placeOrderResponse.orderInfo.quantity == 1.0
    }

    def "placeOrder handles an error response from trade it"() {
        given: "an error response from trade it"
            int successfulCallbackCount = 0
            int errorCallbackCount = 0
            linkedBrokerAccount.getTradeItApiClient().placeStockOrEtfOrder(_, _) >> { args ->
                TradeItCallback<TradeItPlaceStockOrEtfOrderResponse> callback = args[1]
                callback.onError(new TradeItErrorResult(TradeItErrorCode.PARAMETER_ERROR, "My short error message", ["My long error message"]))
            }

        when: "calling preview order"
            TradeItErrorResult errorResult = null
            order.placeOrder("My Order Id", new TradeItCallBackImpl<TradeItPlaceStockOrEtfOrderResponse>() {
                @Override
                void onSuccess(TradeItPlaceStockOrEtfOrderResponse response) {
                    successfulCallbackCount++
                }

                @Override
                void onError(TradeItErrorResult error) {
                    errorCallbackCount++
                    errorResult = error
                }
            })

        then: "expect the sucess callback called"
            successfulCallbackCount == 0
            errorCallbackCount == 1

        and: "the error is correctly populated"
            errorResult.errorCode == TradeItErrorCode.PARAMETER_ERROR
            errorResult.shortMessage == "My short error message"
            errorResult.longMessages == ["My long error message"]
            errorResult.httpCode == 200
    }
}
