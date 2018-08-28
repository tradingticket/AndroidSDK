package it.trade.android.sdk.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import it.trade.model.request.TradeItPreviewStockOrEtfOrderRequest
import junit.framework.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeItOrderParcelableSpec {
    private val linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable = mock()
    private val order = TradeItOrderParcelable(linkedBrokerAccount, "My symbol")

    @BeforeEach
    fun init() {
        whenever(linkedBrokerAccount.tradeItApiClient).thenReturn(mock())
        whenever(linkedBrokerAccount.accountNumber).thenReturn("My account number")
        whenever(linkedBrokerAccount.accountName).thenReturn("My account name")
    }

    @Nested
    inner class PreviewOrderTestCases {
        @Test
        fun `previewOrder handles a successful response from trade it`() {
            // given a successful response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0
            var userDisabledMarginFlag = true
            order.action = TradeItOrderAction.BUY
            order.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
            order.quantity = 1.0
            order.isUserDisabledMargin = userDisabledMarginFlag

            whenever(linkedBrokerAccount.tradeItApiClient!!.previewStockOrEtfOrder(any(), any())).then {
                val request = it.getArgument<TradeItPreviewStockOrEtfOrderRequest>(0)
                userDisabledMarginFlag = request.userDisabledMargin
                val callback = it.getArgument<TradeItCallback<TradeItPreviewStockOrEtfOrderResponse>>(1)
                val tradeItPreviewStockOrEtfOrderResponse = TradeItPreviewStockOrEtfOrderResponse()
                tradeItPreviewStockOrEtfOrderResponse.sessionToken = "My session token"
                tradeItPreviewStockOrEtfOrderResponse.longMessages = null
                tradeItPreviewStockOrEtfOrderResponse.status = TradeItResponseStatus.REVIEW_ORDER
                tradeItPreviewStockOrEtfOrderResponse.orderId = "My Order Id"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails = OrderDetails()
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderAction = "buy"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderSymbol = "My symbol"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderExpiration = "day"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderQuantity = 1.0
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderPrice = "market"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.estimatedTotalValue = 25.0
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderCommissionLabel = "MyOrderCommissionLabel"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderValueLabel = "MyOrderValueLabel"
                tradeItPreviewStockOrEtfOrderResponse.orderDetails.orderMessage = "MyOrder message"
                callback.onSuccess(tradeItPreviewStockOrEtfOrderResponse);
            }

            // when calling preview order
            var previewResponse: TradeItPreviewStockOrEtfOrderResponseParcelable? = null
            order.previewOrder(object : TradeItCallback<TradeItPreviewStockOrEtfOrderResponseParcelable> {
                override fun onSuccess(response: TradeItPreviewStockOrEtfOrderResponseParcelable?) {
                    previewResponse = response
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expect the sucess callback called
            Assert.assertEquals(successfulCallbackCount, 1)
            Assert.assertEquals(errorCallbackCount, 0)

            // and the preview response is correctly filled
            previewResponse!!.orderId == "My Order Id"
            previewResponse!!.orderDetails!!.orderAction == "buy"
            previewResponse!!.orderDetails!!.orderSymbol == "My symbol"
            previewResponse!!.orderDetails!!.orderExpiration == "day"
            previewResponse!!.orderDetails!!.orderQuantity == 1.0
            previewResponse!!.orderDetails!!.orderPrice == "market"
            previewResponse!!.orderDetails!!.estimatedTotalValue == 25.0
            previewResponse!!.orderDetails!!.orderCommissionLabel == "MyOrderCommissionLabel"
            previewResponse!!.orderDetails!!.orderMessage == "MyOrder message"
            previewResponse!!.orderDetails!!.orderValueLabel == "MyOrderValueLabel"

            // and The userDisabledMargin flag was set on the request
            Assert.assertTrue(userDisabledMarginFlag)
        }

        @Test
        fun `previewOrder handles an error response from trade it`() {
            // given an error response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0
            order.action = TradeItOrderAction.BUY
            order.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
            order.quantity = 1.0

            whenever(linkedBrokerAccount.tradeItApiClient!!.previewStockOrEtfOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPreviewStockOrEtfOrderResponse>>(1)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.BROKER_ACCOUNT_ERROR,
                        "My short error message",
                        arrayListOf("My long error message")
                    )
                )
            }

            // when calling preview order
            var errorResult: TradeItErrorResult? = null
            order.previewOrder(object : TradeItCallback<TradeItPreviewStockOrEtfOrderResponseParcelable> {
                override fun onSuccess(type: TradeItPreviewStockOrEtfOrderResponseParcelable?) {
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                    errorResult = error
                }
            })

            // then expect the success callback to be called
            Assert.assertEquals(successfulCallbackCount, 0)
            Assert.assertEquals(errorCallbackCount, 1)

            // and the error is correctly populated
            Assert.assertEquals(errorResult!!.errorCode, TradeItErrorCode.BROKER_ACCOUNT_ERROR)
            Assert.assertEquals(errorResult!!.shortMessage, "My short error message")
            Assert.assertEquals(errorResult!!.longMessages, arrayListOf("My long error message"))
            Assert.assertEquals(errorResult!!.httpCode, 200)
        }
    }

    @Nested
    inner class PlaceOrderTestCases {
        @Test
        fun `placeOrder handles a successful response from trade it`() {
            // given a successful response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0

            whenever(linkedBrokerAccount.tradeItApiClient!!.placeStockOrEtfOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPlaceStockOrEtfOrderResponse>>(1)
                val tradeItPlaceStockOrEtfOrderResponse = TradeItPlaceStockOrEtfOrderResponse()
                tradeItPlaceStockOrEtfOrderResponse.sessionToken = "My session token"
                tradeItPlaceStockOrEtfOrderResponse.longMessages = null
                tradeItPlaceStockOrEtfOrderResponse.status = TradeItResponseStatus.SUCCESS
                tradeItPlaceStockOrEtfOrderResponse.orderNumber = "My Order Id"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo = OrderInfo()
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.action = "buy"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.symbol = "My symbol"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.expiration = "day"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.quantity = 1.0
                val price = Price()
                price.type = "market"
                tradeItPlaceStockOrEtfOrderResponse.orderInfo.price = price
                callback.onSuccess(tradeItPlaceStockOrEtfOrderResponse);
            }

            // when calling place order
            var placeOrderResponse: TradeItPlaceStockOrEtfOrderResponseParcelable? = null
            order.placeOrder("My Order Id", object : TradeItCallback<TradeItPlaceStockOrEtfOrderResponseParcelable> {
                override fun onSuccess(response: TradeItPlaceStockOrEtfOrderResponseParcelable?) {
                    placeOrderResponse = response
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expect the sucess callback called
            Assert.assertEquals(successfulCallbackCount, 1)
            Assert.assertEquals(errorCallbackCount, 0)

            // and the place order response is correctly filled
            Assert.assertEquals(placeOrderResponse!!.orderNumber, "My Order Id")
            Assert.assertEquals(placeOrderResponse!!.orderInfo!!.action, "buy")
            Assert.assertEquals(placeOrderResponse!!.orderInfo!!.symbol, "My symbol")
            Assert.assertEquals(placeOrderResponse!!.orderInfo!!.expiration, "day")
            Assert.assertEquals(placeOrderResponse!!.orderInfo!!.quantity, 1.0)
        }

        @Test
        fun `placeOrder handles an error response from trade it`() {
            // given an error response from trade it
            var successfulCallbackCount = 0
            var errorCallbackCount = 0
            whenever(linkedBrokerAccount.tradeItApiClient!!.placeStockOrEtfOrder(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItPlaceStockOrEtfOrderResponse>>(1)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.PARAMETER_ERROR,
                        "My short error message",
                        arrayListOf("My long error message")
                    )
                )
            }

            // when: calling preview order
            var errorResult: TradeItErrorResult? = null
            order.placeOrder("My Order Id", object : TradeItCallback<TradeItPlaceStockOrEtfOrderResponseParcelable> {
                override fun onSuccess(type: TradeItPlaceStockOrEtfOrderResponseParcelable?) {
                    successfulCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                    errorResult = error
                }
            })

            // then expect the sucess callback called
            Assert.assertEquals(successfulCallbackCount, 0)
            Assert.assertEquals(errorCallbackCount, 1)

            // and the error is correctly populated
            Assert.assertEquals(errorResult!!.errorCode, TradeItErrorCode.PARAMETER_ERROR)
            Assert.assertEquals(errorResult!!.shortMessage, "My short error message")
            Assert.assertEquals(errorResult!!.longMessages, arrayListOf("My long error message"))
            Assert.assertEquals(errorResult!!.httpCode, 200)
        }
    }
}
