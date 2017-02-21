package it.trade.android.sdk.model

import it.trade.android.sdk.model.TradeItCallBackImpl
import it.trade.android.sdk.model.TradeItErrorResult
import it.trade.android.sdk.model.TradeItLinkedBrokerAccount
import it.trade.android.sdk.model.TradeItOrder
import it.trade.tradeitapi.API.TradeItApiClient
import it.trade.tradeitapi.model.TradeItErrorCode
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse.OrderInfo
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse.Price
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderResponse
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderResponse.OrderDetails
import it.trade.tradeitapi.model.TradeItResponseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpiration

class TradeItOrderSpec extends Specification {
    private TradeItLinkedBrokerAccount linkedBrokerAccount = Mock(TradeItLinkedBrokerAccount)
    private TradeItOrder order = new TradeItOrder(linkedBrokerAccount, "My symbol")

    void setup() {
        linkedBrokerAccount.getTradeItApiClient() >> Mock(TradeItApiClient)
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
                Callback<TradeItPreviewStockOrEtfOrderResponse> callback = args[1]
                Call<TradeItPreviewStockOrEtfOrderResponse> call = Mock(Call)
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


                Response<TradeItPreviewStockOrEtfOrderResponse> response = Response.success(tradeItPreviewStockOrEtfOrderResponse);
                callback.onResponse(call, response);
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
                Callback<TradeItPreviewStockOrEtfOrderResponse> callback = args[1]
                Call<TradeItPreviewStockOrEtfOrderResponse> call = Mock(Call)
                TradeItPreviewStockOrEtfOrderResponse tradeItPreviewStockOrEtfOrderResponse = new TradeItPreviewStockOrEtfOrderResponse()
                tradeItPreviewStockOrEtfOrderResponse.sessionToken = "My session token"
                tradeItPreviewStockOrEtfOrderResponse.shortMessage = "My short error message"
                tradeItPreviewStockOrEtfOrderResponse.longMessages = ["My long error message"]
                tradeItPreviewStockOrEtfOrderResponse.status = TradeItResponseStatus.ERROR
                tradeItPreviewStockOrEtfOrderResponse.code = TradeItErrorCode.BROKER_ACCOUNT_ERROR

                Response<TradeItPreviewStockOrEtfOrderResponse> response = Response.success(tradeItPreviewStockOrEtfOrderResponse);
                callback.onResponse(call, response);
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
                Callback<TradeItPlaceStockOrEtfOrderResponse> callback = args[1]
                Call<TradeItPlaceStockOrEtfOrderResponse> call = Mock(Call)
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


                Response<TradeItPlaceStockOrEtfOrderResponse> response = Response.success(tradeItPlaceStockOrEtfOrderResponse);
                callback.onResponse(call, response);
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
                Callback<TradeItPlaceStockOrEtfOrderResponse> callback = args[1]
                Call<TradeItPlaceStockOrEtfOrderResponse> call = Mock(Call)
                TradeItPlaceStockOrEtfOrderResponse tradeItPlaceStockOrEtfOrderResponse = new TradeItPlaceStockOrEtfOrderResponse()
                tradeItPlaceStockOrEtfOrderResponse.sessionToken = "My session token"
                tradeItPlaceStockOrEtfOrderResponse.shortMessage = "My short error message"
                tradeItPlaceStockOrEtfOrderResponse.longMessages = ["My long error message"]
                tradeItPlaceStockOrEtfOrderResponse.status = TradeItResponseStatus.ERROR
                tradeItPlaceStockOrEtfOrderResponse.code = TradeItErrorCode.PARAMETER_ERROR

                Response<TradeItPreviewStockOrEtfOrderResponse> response = Response.success(tradeItPlaceStockOrEtfOrderResponse);
                callback.onResponse(call, response);
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
