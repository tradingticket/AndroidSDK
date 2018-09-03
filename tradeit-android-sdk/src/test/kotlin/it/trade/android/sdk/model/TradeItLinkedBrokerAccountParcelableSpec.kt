package it.trade.android.sdk.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeItLinkedBrokerAccountParcelableSpec {

    private val account: TradeItBrokerAccount = mock()
    private val linkedBroker: TradeItLinkedBrokerParcelable = mock()
    private val linkedLogin: TradeItLinkedLoginParcelable = mock()
    private val tradeItApiClient: TradeItApiClientParcelable = mock()
    private var linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable? = null

    @BeforeEach
    fun init() {
        account.accountBaseCurrency = "My account base currency"
        account.accountNumber = "My account number"
        account.name = "My account name"
        whenever(linkedBroker.getApiClient()).thenReturn(tradeItApiClient)
        whenever(linkedBroker.linkedLogin).thenReturn(linkedLogin)
        whenever(linkedLogin.getUserId()).thenReturn("My user ID")

        linkedBrokerAccount = TradeItLinkedBrokerAccountParcelable(linkedBroker, account)
    }

    @Nested
    inner class RefreshBalanceTestCases {
        @Test
        fun `RefreshBalance handles a successful response from trade it`() {
            // given a successful response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0
            whenever(tradeItApiClient.getAccountOverview(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItAccountOverviewResponse>>(1)
                val accountOverviewResponse = TradeItAccountOverviewResponse()

                val tradeItAccountOverview = TradeItAccountOverview()
                tradeItAccountOverview.availableCash = 1200.54
                tradeItAccountOverview.buyingPower = 2604.45
                tradeItAccountOverview.dayAbsoluteReturn = 100.0
                tradeItAccountOverview.dayPercentReturn = 0.45
                tradeItAccountOverview.totalAbsoluteReturn = -234.98
                tradeItAccountOverview.totalPercentReturn = -2.34
                tradeItAccountOverview.totalValue = 12983.34
                accountOverviewResponse.accountOverview = tradeItAccountOverview
                accountOverviewResponse.status = TradeItResponseStatus.SUCCESS
                callback.onSuccess(accountOverviewResponse)
            }


            // when calling refresh balance on the linked broker account
            var balance: TradeItBalanceParcelable? = null
            linkedBrokerAccount?.refreshBalance(object : TradeItCallback<TradeItLinkedBrokerAccountParcelable> {
                override fun onSuccess(linkedBrokerAccountParcelable: TradeItLinkedBrokerAccountParcelable?) {
                    balance = linkedBrokerAccountParcelable?.balance
                    successCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }

            })

            // then expects the successCallback called once
            Assertions.assertEquals(successCallbackCount, 1)
            Assertions.assertEquals(errorCallbackCount, 0)

            // and expects balance correctly populated
            Assertions.assertEquals(balance?.availableCash, 1200.54)
            Assertions.assertEquals(balance?.buyingPower, 2604.45)
            Assertions.assertEquals(balance?.dayAbsoluteReturn, 100.0)
            Assertions.assertEquals(balance?.dayPercentReturn, 0.45)
            Assertions.assertEquals(balance?.totalAbsoluteReturn, -234.98)
            Assertions.assertEquals(balance?.totalPercentReturn, -2.34)
            Assertions.assertEquals(balance?.totalValue, 12983.34)

            // and the linked broker account should have his balance property updated
            Assertions.assertEquals(linkedBrokerAccount?.balance, balance)
        }

        @Test
        fun `RefreshBalance handles an error response from trade it`() {
            // given an error response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0
            whenever(tradeItApiClient.getAccountOverview(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItAccountOverviewResponse>>(1)
                callback.onError(
                        TradeItErrorResult(
                                TradeItErrorCode.SESSION_EXPIRED,
                                "My short message",
                                arrayListOf("My long message")
                        )
                )
            }

            // when calling refresh balance on the linked broker account
            var errorResult: TradeItErrorResult? = null
            linkedBrokerAccount?.refreshBalance(object : TradeItCallback<TradeItLinkedBrokerAccountParcelable> {
                override fun onSuccess(linkedBrokerAccountParcelable: TradeItLinkedBrokerAccountParcelable?) {
                    successCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorResult = error
                    errorCallbackCount++
                }
            })

            // then expects the errorCallbackCount called once
            Assertions.assertEquals(successCallbackCount,  0)
            Assertions.assertEquals(errorCallbackCount, 1)

            // and expects error result correctly populated
            Assertions.assertEquals(errorResult?.errorCode, TradeItErrorCode.SESSION_EXPIRED)
            Assertions.assertEquals(errorResult?.shortMessage, "My short message")
            Assertions.assertEquals(errorResult?.longMessages, arrayListOf("My long message"))
        }
    }

    @Nested
    inner class RefreshPositionsTestCases {
        @Test
        fun `RefreshPositions handles a successful response from trade it`() {
            //given a successful response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0

            val position1 = TradeItPositionParcelable()
            position1.quantity = 12.0
            position1.symbol = "GE"
            position1.lastPrice = 29.84
            val position2 = TradeItPositionParcelable()
            position2.quantity = 22.0
            position2.symbol = "AAPL"
            position2.lastPrice = 109.84

            whenever(tradeItApiClient.getPositions(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<List<TradeItPosition>>>(1)
                callback.onSuccess(arrayListOf(position1, position2))
            }

            // when calling refresh balance on the linked broker account
            var positionsResult: List<TradeItPositionParcelable>? = null
            linkedBrokerAccount?.refreshPositions(object: TradeItCallback<List<TradeItPositionParcelable>> {
                override fun onSuccess(positions: List<TradeItPositionParcelable>?) {
                    positionsResult = positions
                    successCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expects the successCallback called once
            Assertions.assertEquals(successCallbackCount, 1)
            Assertions.assertEquals(errorCallbackCount, 0)

            // and "expects positions to be returned"
            Assertions.assertEquals(positionsResult, arrayListOf(position1, position2))

            // and the linked broker account should have his positions property updated
            Assertions.assertEquals(linkedBrokerAccount?.positions, positionsResult)
        }
    }

    @Nested
    inner class RefreshOrdersTestCases {
        @Test
        fun `RefreshOrdersStatus handles a successful response from trade it`() {
            // given a successful response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0
            val orderStatusDetails1 = OrderStatusDetails()
            orderStatusDetails1.orderExpiration = "GTC"
            orderStatusDetails1.orderType = "EQUITY_OR_ETF"
            orderStatusDetails1.orderStatus = "OPEN"
            orderStatusDetails1.orderNumber = "1"
            var orderLeg = OrderLeg()
            orderLeg.symbol = "GE"
            orderLeg.filledQuantity = 0
            orderLeg.orderedQuantity = 10
            orderLeg.action = "BUY"
            var priceInfo = PriceInfo()
            priceInfo.type = "LIMIT"
            priceInfo.limitPrice = 20.3
            orderLeg.priceInfo = priceInfo
            orderStatusDetails1.orderLegs = arrayListOf(orderLeg)

            val orderStatusDetails2 = OrderStatusDetails()
            orderStatusDetails2.orderNumber = "2"
            orderStatusDetails2.orderExpiration = "DAY"
            orderStatusDetails2.orderType = "EQUITY_OR_ETF"
            orderStatusDetails2.orderStatus = "OPEN"
            orderStatusDetails2.orderNumber = "1"
            orderLeg = OrderLeg()
            orderLeg.symbol = "AAPL"
            orderLeg.filledQuantity = 0
            orderLeg.orderedQuantity = 10
            orderLeg.action = "BUY"
            priceInfo = PriceInfo()
            priceInfo.type = "MARKET"
            orderLeg.priceInfo = priceInfo
            orderStatusDetails2.orderLegs = arrayListOf(orderLeg)


            whenever(tradeItApiClient.getAllOrderStatus(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<List<OrderStatusDetails>>>(1)
                val orders = arrayListOf(orderStatusDetails1, orderStatusDetails2)
                callback.onSuccess(orders)
            }

            // when calling refresh orders status on the linked broker account
            var orderStatusResult: List<TradeItOrderStatusParcelable>? = null
            linkedBrokerAccount?.refreshOrdersStatus(object: TradeItCallback<List<TradeItOrderStatusParcelable>> {
                override fun onSuccess(orderStatusParcelableList: List<TradeItOrderStatusParcelable>?) {
                    orderStatusResult = orderStatusParcelableList
                    successCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expects the successCallback called once
            Assertions.assertEquals(successCallbackCount, 1)
            Assertions.assertEquals(errorCallbackCount, 0)

            // and expects orders status to be returned
            Assertions.assertEquals(
                orderStatusResult,
                arrayListOf(
                    TradeItOrderStatusParcelable(orderStatusDetails1),
                    TradeItOrderStatusParcelable(orderStatusDetails2)
                )
            )

            // and the linked broker account should have his orders status property updated
            Assertions.assertEquals(linkedBrokerAccount?.ordersStatus, orderStatusResult)
        }

        @Test
        fun `RefreshOrders handles an error response from trade it`() {
            // given an error response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0
            whenever(tradeItApiClient.getAllOrderStatus(any(), any())).then {
                val callback = it.getArgument<TradeItCallback<List<TradeItOrderStatusParcelable>> >(1)
                    callback.onError(
                        TradeItErrorResult(
                            TradeItErrorCode.SESSION_EXPIRED,
                            "My short message",
                            arrayListOf("My long message")
                        )
                    )
            }

            // when calling order status on the linked broker account
            var errorResult: TradeItErrorResult? = null
            linkedBrokerAccount?.refreshOrdersStatus(object: TradeItCallback<List<TradeItOrderStatusParcelable>> {
                override fun onSuccess(type: List<TradeItOrderStatusParcelable>?) {
                    successCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorResult = error
                    errorCallbackCount++
                }
            })

            // then expects the errorCallbackCount called once
            Assertions.assertEquals(successCallbackCount, 0)
            Assertions.assertEquals(errorCallbackCount, 1)

            // and expects error result correctly populated
            Assertions.assertEquals(errorResult?.errorCode, TradeItErrorCode.SESSION_EXPIRED)
            Assertions.assertEquals(errorResult?.shortMessage, "My short message")
            Assertions.assertEquals(errorResult?.longMessages, arrayListOf("My long message"))
        }
    }

    @Nested
    inner class CancelOrderTestCases {
        @Test
        fun `CancelOrder handles a successful response from trade it`() {
            // given a successful response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0
            val orderStatusDetails = OrderStatusDetails()
            orderStatusDetails.orderExpiration = "GTC"
            orderStatusDetails.orderType = "EQUITY_OR_ETF"
            orderStatusDetails.orderStatus = "CANCELED"
            orderStatusDetails.orderNumber = "1"
            val orderLeg = OrderLeg()
            orderLeg.symbol = "GE"
            orderLeg.filledQuantity = 0
            orderLeg.orderedQuantity = 10
            orderLeg.action = "BUY"
            val priceInfo = PriceInfo()
            priceInfo.type = "LIMIT"
            priceInfo.limitPrice = 20.3
            orderLeg.priceInfo = priceInfo
            orderStatusDetails.orderLegs = arrayListOf(orderLeg)


            whenever(tradeItApiClient.cancelOrder(any(), any(), any())).then {
                val callback = it.getArgument<TradeItCallback<OrderStatusDetails> >(2)
                callback.onSuccess(orderStatusDetails)
            }

            // when calling cancel order on the linked broker account
            var orderStatusParcelableResult: TradeItOrderStatusParcelable? = null
            linkedBrokerAccount?.cancelOrder("orderNumber", object: TradeItCallback<TradeItOrderStatusParcelable> {
                override fun onSuccess(orderStatusParcelable: TradeItOrderStatusParcelable?) {
                    orderStatusParcelableResult = orderStatusParcelable
                    successCallbackCount++
                }

                override fun onError(error: TradeItErrorResult?) {
                    errorCallbackCount++
                }
            })

            // then expects the successCallback called once
            Assertions.assertEquals(successCallbackCount, 1)
            Assertions.assertEquals(errorCallbackCount, 0)

            // then expects orders status to be returned
            Assertions.assertEquals(orderStatusParcelableResult, TradeItOrderStatusParcelable(orderStatusDetails))
        }

        @Test
        fun `CancelOrder handles an error response from trade it`() {
            // given an error response from trade it api
            var successCallbackCount = 0
            var errorCallbackCount = 0
            whenever(tradeItApiClient.cancelOrder(any(), any(), any())).then {
                val callback = it.getArgument<TradeItCallback<TradeItOrderStatusParcelable>>(2)
                callback.onError(
                    TradeItErrorResult(
                        TradeItErrorCode.SESSION_EXPIRED,
                        "My short message",
                        arrayListOf("My long message")
                    )
                )
            }

            // when calling cancel order on the linked broker account
            var errorResult: TradeItErrorResult? = null
            linkedBrokerAccount?.cancelOrder(
                    "orderNumber",
                    object: TradeItCallback<TradeItOrderStatusParcelable> {
                        override fun onSuccess(type: TradeItOrderStatusParcelable?) {
                            successCallbackCount++
                        }

                        override fun onError(error: TradeItErrorResult?) {
                            errorResult = error
                            errorCallbackCount++
                        }
                    }
            )

            // then expects the errorCallbackCount called once
            Assertions.assertEquals(successCallbackCount, 0)
            Assertions.assertEquals(errorCallbackCount, 1)

            // then expects error result correctly populated
            Assertions.assertEquals(errorResult?.errorCode, TradeItErrorCode.SESSION_EXPIRED)
            Assertions.assertEquals(errorResult?.shortMessage, "My short message")
            Assertions.assertEquals(errorResult?.longMessages, arrayListOf("My long message"))
        }
    }

}