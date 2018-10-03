package it.trade.android.japanapp.ui.orderinput

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class OrderInputViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private lateinit var orderForm: MutableLiveData<OrderForm>

    fun getOrderModel(): LiveData<OrderForm> {
        if (!this::orderForm.isInitialized) {
            orderForm = MutableLiveData()
            orderForm.value = OrderForm(
                    TradeItSDKHolder.getSymoblProvider().getJapanSymbol("8703"),
                    TradeItSDKHolder.getBuyingPower())
        }
        return orderForm
    }

}

interface JapanSymbolProvider {
    fun getJapanSymbol(symol: String): JapanSymbol
}

class SampleJapanSymbol : JapanSymbolProvider {
    override fun getJapanSymbol(symol: String): JapanSymbol {
        return JapanSymbol("カブドットコム証券(株)",
                "8703", "東証1部", 386.0,
                384.0, 286.0, 486.0, 100)
    }
}

object TradeItSDKHolder {
    fun getSymoblProvider(): JapanSymbolProvider {
        return SampleJapanSymbol()
    }
    // need broker/account
    fun getBuyingPower(): BuyingPower {
        return BuyingPower(500000.toDouble(), 100000.toDouble())
    }
}

class OrderForm(val symbol: JapanSymbol, val buyingPower: BuyingPower) {
    val orderInfo: OrderInfo = OrderInfo(
            quantity = symbol.lotSize,
            limitPrice = symbol.price,
            type = OrderType.LIMIT,
            expiry = OrderExpiry.DAY,
            accountType = AccountType.SPECIFIC
    )

    val estimatedValue: Double = orderInfo.quantity * orderInfo.limitPrice

    val priceChange: Double = symbol.price - symbol.previousDayPrice

    val priceChangePercentage: Double = priceChange / symbol.previousDayPrice

    val availableExpiry: List<OrderExpiry>
        get() {
            return if (orderInfo.type == OrderType.LIMIT) {
                listOf(OrderExpiry.DAY, OrderExpiry.WEEK, OrderExpiry.TILL_DATE, OrderExpiry.OPENING, OrderExpiry.CLOSING, OrderExpiry.FUNARI)
            } else {
                listOf(OrderExpiry.DAY, OrderExpiry.OPENING, OrderExpiry.CLOSING)
            }
        }
}

data class JapanSymbol(val name: String, val symbol: String, val exchange: String,
                       val price: Double, val previousDayPrice: Double,
                       val priceLowerLimit: Double, val priceUpperLimit: Double,
                       val lotSize: Int)

data class OrderInfo(val quantity: Int, val limitPrice: Double, val type: OrderType,
                     val expiry: OrderExpiry, val accountType: AccountType)

data class BuyingPower(val availableCash: Double, val availableNisaLimit: Double)

enum class OrderType {
    LIMIT, MARKET
}

enum class OrderExpiry {
    DAY, WEEK, TILL_DATE, OPENING, CLOSING, FUNARI
}

enum class AccountType {
    SPECIFIC, GENERAL, NISA
}