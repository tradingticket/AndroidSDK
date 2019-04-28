package it.trade.android.sdk.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.api.TradeItApiClient
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.*
import it.trade.model.reponse.TradeItErrorCode.BROKER_EXECUTION_ERROR
import it.trade.model.reponse.TradeItErrorCode.PARAMETER_ERROR
import java.util.ArrayList
import java.util.Date
import kotlin.collections.HashMap

class TradeItLinkedBrokerAccountParcelable : Parcelable {

    @SerializedName("accountName")
    var accountName: String

    @SerializedName("accountNumber")
    var accountNumber: String

    @SerializedName("accountBaseCurrency")
    var accountBaseCurrency: String

    @SerializedName("accountIndex")
    var accountIndex: String? = ""

    @SerializedName("tradable")
    var tradable: Boolean = true

    @SerializedName("userCanDisableMargin")
    var userCanDisableMargin: Boolean = false

    @SerializedName("orderCapabilities")
    var orderCapabilities: List<TradeItOrderCapabilityParcelable>

    @Transient
    var linkedBroker: TradeItLinkedBrokerParcelable?
        internal set

    @SerializedName("balance")
    var balance: TradeItBalanceParcelable? = null

    @SerializedName("fxBalance")
    var fxBalance: TradeItFxBalanceParcelable? = null

    @SerializedName("balanceLastUpdated")
    var balanceLastUpdated: Date? = null

    @SerializedName("positions")
    var positions: List<TradeItPositionParcelable>? = null
        internal set

    @SerializedName("ordersStatus")
    var ordersStatus: List<TradeItOrderStatusParcelable>? = null
        private set

    @SerializedName("userId")
    private var userId: String? = null

    val tradeItApiClient: TradeItApiClient?
        get() = this.linkedBroker?.getApiClient()


    val brokerName: String
        get() = this.linkedBroker?.brokerName ?: ""

    constructor(linkedBroker: TradeItLinkedBrokerParcelable, account: TradeItBrokerAccount) {
        this.linkedBroker = linkedBroker
        this.accountName = account.name
        this.accountNumber = account.accountNumber
        this.accountIndex = account.accountIndex
        this.tradable = account.tradable
        this.accountBaseCurrency = account.accountBaseCurrency
        this.userCanDisableMargin = account.userCanDisableMargin
        this.orderCapabilities = TradeItOrderCapabilityParcelable.mapOrderCapabilitiesToTradeItOrderCapabilityParcelables(account.orderCapabilities)
        this.userId = linkedBroker.linkedLogin!!.userId
    }

    fun setErrorOnLinkedBroker(errorResult: TradeItErrorResultParcelable) {
        if (errorResult.errorCode != BROKER_EXECUTION_ERROR && errorResult.errorCode != PARAMETER_ERROR) {
            this.linkedBroker?.error = errorResult
        }
    }

    fun getOrderCapabilityForInstrument(instrument: Instrument): TradeItOrderCapabilityParcelable? {
        for (orderCapability in orderCapabilities) {
            if (orderCapability.instrument == instrument) {
                return orderCapability
            }
        }
        return null
    }

    fun refreshBalance(callback: TradeItCallback<TradeItLinkedBrokerAccountParcelable>) {
        val linkedBrokerAccount = this
        this.tradeItApiClient!!.getAccountOverview(accountNumber, object : TradeItCallback<TradeItAccountOverviewResponse> {
            override fun onSuccess(response: TradeItAccountOverviewResponse) {
                if (response.accountOverview != null) {
                    val balance = TradeItBalanceParcelable(response.accountOverview)
                    linkedBrokerAccount.balance = balance
                    linkedBrokerAccount.fxBalance = null
                } else if (response.fxAccountOverview != null) {
                    val fxBalance = TradeItFxBalanceParcelable(response.fxAccountOverview)
                    linkedBrokerAccount.balance = null
                    linkedBrokerAccount.fxBalance = fxBalance
                }
                linkedBrokerAccount.balanceLastUpdated = Date()
                linkedBroker?.cache()
                callback.onSuccess(linkedBrokerAccount)
            }

            override fun onError(errorResult: TradeItErrorResult) {
                val errorResultParcelable = TradeItErrorResultParcelable(errorResult)
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                callback.onError(errorResultParcelable)
            }
        })
    }

    fun refreshPositions(callback: TradeItCallback<List<TradeItPositionParcelable>>) {
        val linkedBrokerAccount = this
        this.tradeItApiClient!!.getPositions(accountNumber, object : TradeItCallback<List<TradeItPosition>> {
            override fun onSuccess(positions: List<TradeItPosition>) {
                val positionsParcelable = mapPositionsToPositionsParcelable(positions)
                linkedBrokerAccount.positions = positionsParcelable
                callback.onSuccess(positionsParcelable)
            }

            override fun onError(errorResult: TradeItErrorResult) {
                val errorResultParcelable = TradeItErrorResultParcelable(errorResult)
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                callback.onError(errorResultParcelable)
            }
        })
    }

    fun refreshOrdersStatus(callback: TradeItCallback<List<TradeItOrderStatusParcelable>>) {
        val linkedBrokerAccount = this
        this.tradeItApiClient!!.getAllOrderStatus(accountNumber, object : TradeItCallback<List<OrderStatusDetails>> {
            override fun onSuccess(orderStatusDetailsList: List<OrderStatusDetails>) {
                val orderStatusParcelables = TradeItOrderStatusParcelable.mapOrderStatusDetailsToTradeItOrderStatusParcelable(orderStatusDetailsList)
                linkedBrokerAccount.ordersStatus = orderStatusParcelables
                callback.onSuccess(orderStatusParcelables)
            }

            override fun onError(error: TradeItErrorResult) {
                val errorResultParcelable = TradeItErrorResultParcelable(error)
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                callback.onError(errorResultParcelable)
            }
        })
    }

    fun cancelOrder(orderNumber: String, callback: TradeItCallback<TradeItOrderStatusParcelable>) {
        val linkedBrokerAccount = this
        this.tradeItApiClient!!.cancelOrder(accountNumber, orderNumber, object : TradeItCallback<OrderStatusDetails> {
            override fun onSuccess(orderStatusDetails: OrderStatusDetails) {
                callback.onSuccess(TradeItOrderStatusParcelable(orderStatusDetails))
            }

            override fun onError(error: TradeItErrorResult) {
                val errorResultParcelable = TradeItErrorResultParcelable(error)
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                callback.onError(errorResultParcelable)
            }
        })
    }

    fun getCryptoQuote(pair: String, callback: TradeItCallback<TradeItCryptoQuoteResponseParcelable>) {
        val linkedBrokerAccount = this
        this.tradeItApiClient?.getCryptoQuote(
            this.accountNumber,
            pair,
            object: TradeItCallback<TradeItCryptoQuoteResponse> {
                override fun onSuccess(response: TradeItCryptoQuoteResponse) {
                    callback.onSuccess(TradeItCryptoQuoteResponseParcelable(response))
                }

                override fun onError(error: TradeItErrorResult) {
                    val errorResultParcelable = TradeItErrorResultParcelable(error)
                    linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable)
                    callback.onError(errorResultParcelable)
                }

            }
        )
    }

    fun getProxyVoteUrl(symbol: String, callback: TradeItCallback<String>) {
        this.tradeItApiClient?.getProxyVoteUrl(
            accountNumber,
            symbol,
            object : TradeItCallback<TradeItProxyVoteUrlResponse> {
                override fun onSuccess(response: TradeItProxyVoteUrlResponse) {
                    response.proxyVoteUrl?.let {  callback.onSuccess(it)}
                        ?: callback.onError(TradeItErrorResultParcelable(
                            TradeItErrorCode.SYSTEM_ERROR,
                            "Position not eligible",
                            arrayListOf("This position is not eligible to proxy voting")
                        ))
                }

                override fun onError(error: TradeItErrorResult) {
                    val errorResultParcelable = TradeItErrorResultParcelable(error)
                    callback.onError(errorResultParcelable)
                }
            }
        )
    }

    private fun mapPositionsToPositionsParcelable(positions: List<TradeItPosition>): List<TradeItPositionParcelable> {
        val positionsParcelable = ArrayList<TradeItPositionParcelable>()
        for (position in positions) {
            positionsParcelable.add(TradeItPositionParcelable(position))
        }
        return positionsParcelable
    }


    override fun toString(): String {
        return "TradeItLinkedBrokerAccountParcelable{" +
                "accountBaseCurrency='" + accountBaseCurrency + '\''.toString() +
                ", accountName='" + accountName + '\''.toString() +
                ", accountNumber='" + accountNumber + '\''.toString() +
                ", accountIndex='" + accountIndex + '\''.toString() +
                ", tradable='" + tradable + '\''.toString() +
                ", userCanDisableMargin='" + userCanDisableMargin + '\''.toString() +
                ", balance='" + balance + '\''.toString() +
                ", orderCapabilities='" + orderCapabilities + '\''.toString() +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.accountName)
        dest.writeString(this.accountNumber)
        dest.writeString(this.accountBaseCurrency)
        dest.writeByte((if (userCanDisableMargin) 1 else 0).toByte())
        dest.writeList(this.orderCapabilities)
        dest.writeParcelable(this.balance, flags)
        dest.writeLong(if (this.balanceLastUpdated != null) this.balanceLastUpdated!!.time else -1)
        dest.writeList(this.positions)
        dest.writeString(this.userId)
        userId?.let {userId ->  linkedBrokersMap[userId] = this.linkedBroker }
        dest.writeString(this.accountIndex)
        dest.writeByte((if (tradable) 1 else 0).toByte())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItLinkedBrokerAccountParcelable

        if (accountName != other.accountName) return false
        if (accountNumber != other.accountNumber) return false
        if (accountBaseCurrency != other.accountBaseCurrency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accountName.hashCode()
        result = 31 * result + accountNumber.hashCode()
        result = 31 * result + accountBaseCurrency.hashCode()
        result = 31 * result + (accountIndex?.hashCode() ?: 0)
        result = 31 * result + tradable.hashCode()
        result = 31 * result + userCanDisableMargin.hashCode()
        result = 31 * result + orderCapabilities.hashCode()
        result = 31 * result + (linkedBroker?.hashCode() ?: 0)
        result = 31 * result + (balance?.hashCode() ?: 0)
        result = 31 * result + (fxBalance?.hashCode() ?: 0)
        result = 31 * result + (balanceLastUpdated?.hashCode() ?: 0)
        result = 31 * result + (positions?.hashCode() ?: 0)
        result = 31 * result + (ordersStatus?.hashCode() ?: 0)
        result = 31 * result + (userId?.hashCode() ?: 0)
        return result
    }

    protected constructor(`in`: Parcel) {
        this.accountName = `in`.readString()
        this.accountNumber = `in`.readString()
        this.accountBaseCurrency = `in`.readString()
        this.userCanDisableMargin = `in`.readByte().toInt() != 0
        this.orderCapabilities = ArrayList()
        `in`.readList(this.orderCapabilities, TradeItOrderCapabilityParcelable::class.java.getClassLoader())
        this.balance = `in`.readParcelable(TradeItBalanceParcelable::class.java.getClassLoader())
        val tmpBalanceLastUpdated = `in`.readLong()
        this.balanceLastUpdated = if (tmpBalanceLastUpdated.equals(-1)) null else Date(tmpBalanceLastUpdated)
        this.positions = ArrayList()
        `in`.readList(this.positions, TradeItPositionParcelable::class.java.getClassLoader())
        this.userId = `in`.readString()
        this.linkedBroker = this.userId?.let { userId -> linkedBrokersMap[userId] }
        this.linkedBroker?.accounts?.indexOf(this)?.let { indexAccount ->
            if (indexAccount != -1) { // updating account reference on the linkedBroker as we created a new object
                this.linkedBroker?.accounts?.removeAt(indexAccount)
                this.linkedBroker?.accounts?.add(indexAccount, this)
            }
        }
        this.accountIndex = `in`.readString()
        this.tradable = `in`.readByte().toInt() != 0
    }

    companion object {
        private val linkedBrokersMap = HashMap<String, TradeItLinkedBrokerParcelable?>() //used for parcelable
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItLinkedBrokerAccountParcelable> = object : Parcelable.Creator<TradeItLinkedBrokerAccountParcelable> {
            override fun createFromParcel(source: Parcel): TradeItLinkedBrokerAccountParcelable {
                return TradeItLinkedBrokerAccountParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItLinkedBrokerAccountParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
