package it.trade.android.japanapp.ui.orderinput

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import kotlin.math.exp

private const val TAG = "OrderInputViewModel"

class OrderInputViewModel(private val symbol: String) : ViewModel() {
    private lateinit var orderForm: MutableLiveData<OrderForm>

    fun getOrderModel(): LiveData<OrderForm> {
        if (!this::orderForm.isInitialized) {
            Log.d(TAG, "initialized.")
            orderForm = MutableLiveData()
            orderForm.value = OrderForm(
                    TradeItSDKHolder.getSymbolProvider().getJapanSymbol(symbol),
                    TradeItSDKHolder.getBuyingPower())
        }
        return orderForm
    }

    fun increaseQuantity() {
        orderForm.value = orderForm.value?.apply {
            orderInfo = orderInfo.copy(quantity = orderInfo.quantity + symbol.lotSize)
        }
    }

    fun decreaseQuantity() {
        val value = orderForm.value
        if (value?.orderInfo?.quantity != value?.symbol?.lotSize) {
            // do not decrease further when it's lotsize already
            orderForm.value = value?.apply {
                orderInfo = orderInfo.copy(quantity = orderInfo.quantity - symbol.lotSize)
            }
        }
    }

    fun increasePrice() {
        orderForm.value = orderForm.value?.apply {
            if (orderInfo.limitPrice < symbol.priceUpperLimit) {
                orderInfo = orderInfo.copy(limitPrice = orderInfo.limitPrice + 1)
            }
        }
    }

    fun decreasePrice() {
        orderForm.value = orderForm.value?.apply {
            if (orderInfo.limitPrice > symbol.priceLowerLimit) {
                orderInfo = orderInfo.copy(limitPrice = orderInfo.limitPrice - 1)
            }
        }
    }

    fun setMarketOrder() {
        orderForm.value = orderForm.value?.apply {
            val expiry = if (orderInfo.expiry in listOf(OrderExpiry.WEEK, OrderExpiry.TILL_DATE, OrderExpiry.FUNARI))
                OrderExpiry.DAY
            else
                orderInfo.expiry
            orderInfo = orderInfo.copy(type = OrderType.MARKET, limitPrice = symbol.price, expiry = expiry)
        }
    }

    fun setLimitOrder() {
        orderForm.value = orderForm.value?.apply {
            orderInfo = orderInfo.copy(type = OrderType.LIMIT)
        }
    }

    fun setLimitPrice(price: String): Boolean {
        val newPrice = try {
            price.toDouble()
        } catch (e: NumberFormatException) {
            return false
        }
        if (newPrice == orderForm.value?.orderInfo?.limitPrice) {
            // to avoid infinite loop, don't update model when value is not actually updated
            return true
        }
        var isValid = false
        val newValue = orderForm.value?.apply {
            if (newPrice >= symbol.priceLowerLimit && newPrice <= symbol.priceUpperLimit) {
                orderInfo = orderInfo.copy(limitPrice = newPrice)
                isValid = true
            }
        }
        if (isValid) {
            orderForm.value = newValue
        }
        return isValid
    }

    fun setQuantity(quantity: String): Boolean {
        val newQuantity = try {
            quantity.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        if (newQuantity == orderForm.value?.orderInfo?.quantity) {
            // to avoid infinite loop, don't update model when value is not actually updated
            return true
        }
        var isValid = false
        val newValue = orderForm.value?.apply {
            if (newQuantity % symbol.lotSize == 0) {
                orderInfo = orderInfo.copy(quantity = newQuantity)
                isValid = true
            }
        }
        if (isValid) {
            orderForm.value = newValue
        }
        return isValid
    }

    fun setExpiry(expiry: OrderExpiry) {
        orderForm.value = orderForm.value?.apply {
            orderInfo = orderInfo.copy(expiry = expiry)
        }
    }

    fun dummyUpdate() {
        orderForm.value = orderForm.value
    }
}

class OrderInputViewModelFactory(private val symbol: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return OrderInputViewModel(symbol) as T
    }

}

class OrderForm(val symbol: JapanSymbol, val buyingPower: BuyingPower) {
    var orderInfo: OrderInfo = OrderInfo(
            quantity = symbol.lotSize,
            limitPrice = symbol.price,
            type = OrderType.LIMIT,
            expiry = OrderExpiry.DAY,
            accountType = AccountType.SPECIFIC
    )

    val estimatedValue: Double
        get() {
            return orderInfo.quantity * orderInfo.limitPrice
        }

    val priceChange: Double
        get() {
            return symbol.price - symbol.previousDayPrice
        }

    val priceChangePercentage: Double
        get() {
            return priceChange / symbol.previousDayPrice
        }
}

//TODO temp solutions below
interface JapanSymbolProvider {
    fun getJapanSymbol(symbol: String): JapanSymbol
}

class SampleJapanSymbol : JapanSymbolProvider {
    override fun getJapanSymbol(symbol: String): JapanSymbol {
        return JapanSymbol("カブドットコム証券(株)",
                symbol, "東証1部", 386.0,
                384.0, 286.0, 486.0, 100)
    }
}

object TradeItSDKHolder {
    fun getSymbolProvider(): JapanSymbolProvider {
        return SampleJapanSymbol()
    }

    // need broker/account
    fun getBuyingPower(): BuyingPower {
        return BuyingPower(500000.toDouble(), 100000.toDouble())
    }
}

// TODO need to separate the price portion out
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