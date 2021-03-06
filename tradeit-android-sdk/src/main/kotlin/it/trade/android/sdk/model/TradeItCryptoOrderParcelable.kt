package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.android.sdk.enums.TradeItOrderQuantityType
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.TradeItPlaceCryptoOrderResponse
import it.trade.model.reponse.TradeItPreviewCryptoOrderResponse
import it.trade.model.request.TradeItPreviewCryptoOrderRequest
import java.math.BigDecimal

class TradeItCryptoOrderParcelable(
    val linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable,
    val symbolPair: SymbolPairParcelable,
    var action: TradeItOrderAction = TradeItOrderAction.BUY
): Parcelable {

    var priceType: TradeItOrderPriceType = TradeItOrderPriceType.MARKET
        set(value) {
            field = value
            if (!requiresExpiration()) {
                expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
            }
            if (!requiresLimitPrice()) {
                limitPrice = null
            }
            if (!requiresStopPrice()) {
                stopPrice = null
            }
        }

    var expiration: TradeItOrderExpirationType = TradeItOrderExpirationType.GOOD_FOR_DAY

    var quantity: BigDecimal? = null

    var limitPrice: BigDecimal? = null

    var stopPrice: BigDecimal? = null

    var orderQuantityType: TradeItOrderQuantityType = TradeItOrderQuantityType.QUOTE_CURRENCY

    var quoteLastPrice: BigDecimal? = null

    fun requiresLimitPrice(): Boolean {
        return priceType in arrayListOf(
            TradeItOrderPriceType.LIMIT,
            TradeItOrderPriceType.STOP_LIMIT
        )
    }

    fun requiresStopPrice(): Boolean {
        return priceType in arrayListOf(
            TradeItOrderPriceType.STOP_MARKET,
            TradeItOrderPriceType.STOP_LIMIT
        )
    }

    fun requiresExpiration(): Boolean {
        return priceType in arrayListOf(
            TradeItOrderPriceType.LIMIT,
            TradeItOrderPriceType.STOP_MARKET,
            TradeItOrderPriceType.STOP_LIMIT
        )
    }

    fun getQuantitySymbol(): String? {
        return when (orderQuantityType) {
            TradeItOrderQuantityType.QUOTE_CURRENCY -> symbolPair.quoteSymbol
            TradeItOrderQuantityType.BASE_CURRENCY -> symbolPair.baseSymbol
            else -> null
        }
    }

    fun getEstimateSymbol(): String? {
        return when (orderQuantityType) {
            TradeItOrderQuantityType.BASE_CURRENCY -> symbolPair.quoteSymbol
            TradeItOrderQuantityType.QUOTE_CURRENCY -> symbolPair.baseSymbol
            else -> null
        }
    }

    fun userCanDisableMargin(): Boolean {
        return linkedBrokerAccount.userCanDisableMargin
    }

    fun previewCryptoOrder(callback: TradeItCallback<TradeItPreviewCryptoOrderResponseParcelable>) {
        val request = TradeItPreviewCryptoOrderRequest()
        request.accountNumber = this.linkedBrokerAccount.accountNumber
        request.orderAction = this.action.actionValue
        request.orderQuantity = this.quantity?.toDouble()
        request.orderPair = this.symbolPair.baseSymbol + "/" + this.symbolPair.quoteSymbol
        request.orderPriceType = this.priceType.priceTypeValue
        request.orderExpiration = this.expiration.expirationValue
        request.orderLimitPrice = this.limitPrice?.toDouble()
        request.orderStopPrice = this.stopPrice?.toDouble()
        request.orderQuantityType = this.orderQuantityType.name

        val order = this
        this.linkedBrokerAccount.tradeItApiClient?.previewCryptoOrder(
            request,
            object: TradeItCallback<TradeItPreviewCryptoOrderResponse> {
                override fun onSuccess(response: TradeItPreviewCryptoOrderResponse) {
                    callback.onSuccess(TradeItPreviewCryptoOrderResponseParcelable(response))
                }

                override fun onError(error: TradeItErrorResult) {
                    val errorResultParcelable = TradeItErrorResultParcelable(error)
                    order.linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                    callback.onError(errorResultParcelable)
                }
            }
        )
    }

    fun placeCryptoOrder(orderId: String, callback: TradeItCallback<TradeItPlaceCryptoOrderResponseParcelable>) {
        val order = this
        this.linkedBrokerAccount.tradeItApiClient?.placeCryptoOrder(
            orderId,
            object: TradeItCallback<TradeItPlaceCryptoOrderResponse> {
                override fun onSuccess(response: TradeItPlaceCryptoOrderResponse) {
                    callback.onSuccess(TradeItPlaceCryptoOrderResponseParcelable(response))
                }

                override fun onError(error: TradeItErrorResult) {
                    val errorResultParcelable = TradeItErrorResultParcelable(error)
                    order.linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                    callback.onError(errorResultParcelable)
                }
            }
        )
    }

    fun isValid(): Boolean {
        return validateQuantity()
            && validateOrderPriceType()
    }

    private fun validateQuantity(): Boolean {
        return quantity?.let { quantity -> return isGreaterThanZero(quantity) } ?: false
    }

    private fun validateOrderPriceType(): Boolean {
        return when (priceType) {
            TradeItOrderPriceType.MARKET -> true
            TradeItOrderPriceType.LIMIT -> validateLimit()
            TradeItOrderPriceType.STOP_MARKET -> validateStopMarket()
            TradeItOrderPriceType.STOP_LIMIT -> validateStopLimit()
            else -> false
        }
    }

    private fun validateLimit(): Boolean {
        return limitPrice?.let { limitPrice -> return isGreaterThanZero(limitPrice) } ?: false
    }

    private fun validateStopMarket(): Boolean {
        return stopPrice?.let { stopPrice -> return isGreaterThanZero(stopPrice) } ?: false
    }

    private fun validateStopLimit(): Boolean {
        return validateLimit() && validateStopMarket()
    }

    private fun isGreaterThanZero(value: BigDecimal): Boolean {
        return value.compareTo(BigDecimal.ZERO) > 0
    }

    constructor(source: Parcel) : this(
        source.readParcelable<TradeItLinkedBrokerAccountParcelable>(TradeItLinkedBrokerAccountParcelable::class.java.classLoader),
        source.readParcelable<SymbolPairParcelable>(SymbolPairParcelable::class.java.classLoader),
        TradeItOrderAction.values()[source.readInt()]
    ) {
        this.priceType = TradeItOrderPriceType.values()[source.readInt()]
        this.expiration = TradeItOrderExpirationType.values()[source.readInt()]
        this.quantity = source.readSerializable() as? BigDecimal
        this.limitPrice = source.readSerializable() as? BigDecimal
        this.stopPrice = source.readSerializable() as? BigDecimal
        this.orderQuantityType = TradeItOrderQuantityType.values()[source.readInt()]
        this.quoteLastPrice = source.readSerializable() as? BigDecimal
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(linkedBrokerAccount, 0)
        writeParcelable(symbolPair, 0)
        writeInt(action.ordinal)
        writeInt(priceType.ordinal)
        writeInt(expiration.ordinal)
        writeSerializable(quantity)
        writeSerializable(limitPrice)
        writeSerializable(stopPrice)
        writeInt(orderQuantityType.ordinal)
        writeSerializable(quoteLastPrice)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItCryptoOrderParcelable> = object : Parcelable.Creator<TradeItCryptoOrderParcelable> {
            override fun createFromParcel(source: Parcel): TradeItCryptoOrderParcelable = TradeItCryptoOrderParcelable(source)
            override fun newArray(size: Int): Array<TradeItCryptoOrderParcelable?> = arrayOfNulls(size)
        }
    }
}