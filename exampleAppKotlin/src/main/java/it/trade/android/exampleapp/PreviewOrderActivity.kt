package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import it.trade.android.sdk.model.TradeItOrderParcelable
import it.trade.android.sdk.model.TradeItPlaceStockOrEtfOrderResponseParcelable
import it.trade.android.sdk.model.TradeItPreviewStockOrEtfOrderResponseParcelable
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback

class PreviewOrderActivity : AppCompatActivity() {
    private var orderId: String? = null
    private var order: TradeItOrderParcelable? = null
    private var textView: TextView? = null
    private var cancelOrderButton: Button? = null
    internal var orderNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_order)
        textView = this.findViewById(R.id.preview_order_textview) as TextView?
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        order = intent.getParcelableExtra(MainActivity.PREVIEW_ORDER_PARAMETER)
        val placeOrderButton = this.findViewById(R.id.place_trade_button) as Button?
        cancelOrderButton = this.findViewById(R.id.cancel_order_button) as Button?
        order!!.previewOrder(object : TradeItCallback<TradeItPreviewStockOrEtfOrderResponseParcelable> {
            override fun onSuccess(response: TradeItPreviewStockOrEtfOrderResponseParcelable) {
                textView!!.text = response.toString()
                orderId = response.orderId
                placeOrderButton!!.isEnabled = true
            }

            override fun onError(error: TradeItErrorResult) {
                Log.e(TAG, "ERROR previewOrder: $error")
                textView!!.text = "ERROR previewOrder: $error"
            }
        })
    }

    fun placeTrade(view: View) {
        order!!.placeOrder(orderId, object : TradeItCallback<TradeItPlaceStockOrEtfOrderResponseParcelable> {
            override fun onSuccess(placeOrderResponse: TradeItPlaceStockOrEtfOrderResponseParcelable) {
                textView!!.append(placeOrderResponse.toString())
                cancelOrderButton!!.isEnabled = true
                orderNumber = placeOrderResponse.orderNumber
            }

            override fun onError(error: TradeItErrorResult) {
                Log.e(TAG, "ERROR placeOrder: " + error.toString())
                textView!!.text = "ERROR placeOrder: $error"
            }
        })
    }

    fun cancelOrder(view: View) {
        order!!.linkedBrokerAccount.cancelOrder(orderNumber, object : TradeItCallback<TradeItOrderStatusParcelable> {
            override fun onSuccess(orderStatusParcelable: TradeItOrderStatusParcelable) {
                textView!!.append(orderStatusParcelable.toString())
            }

            override fun onError(error: TradeItErrorResult) {
                Log.e(TAG, "ERROR cancelOrder: " + error.toString())
                textView!!.text = "ERROR cancelOrder: $error"
            }
        })
    }

    companion object {
        private val TAG = MainActivity::class.java.name
    }
}
