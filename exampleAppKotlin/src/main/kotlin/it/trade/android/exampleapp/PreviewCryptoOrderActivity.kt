package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import it.trade.android.exampleapp.MainActivity.Companion.PREVIEW_ORDER_PARAMETER
import it.trade.android.sdk.model.TradeItCryptoOrderParcelable
import it.trade.android.sdk.model.TradeItPlaceCryptoOrderResponseParcelable
import it.trade.android.sdk.model.TradeItPreviewCryptoOrderResponseParcelable
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback

class PreviewCryptoOrderActivity : AppCompatActivity() {
    private var orderId: String? = null
    private var order: TradeItCryptoOrderParcelable? = null
    private var textView: TextView? = null
    private var cancelOrderButton: Button? = null
    internal var orderNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_order)
        textView = this.findViewById<View>(R.id.preview_order_textview) as TextView
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        order = intent.getParcelableExtra(PREVIEW_ORDER_PARAMETER)
        val placeOrderButton = this.findViewById<View>(R.id.place_trade_button) as Button
        cancelOrderButton = this.findViewById<View>(R.id.cancel_order_button) as Button
        order!!.previewCryptoOrder(object : TradeItCallback<TradeItPreviewCryptoOrderResponseParcelable> {
            override fun onSuccess(response: TradeItPreviewCryptoOrderResponseParcelable) {
                textView!!.text = response.toString()
                orderId = response.orderId
                placeOrderButton.isEnabled = true
            }

            override fun onError(error: TradeItErrorResult) {
                Log.e(TAG, "ERROR previewCryptoOrder: $error")
                textView!!.text = "ERROR previewCryptoOrder: $error"
            }
        })
    }

    fun placeTrade(view: View) {
        order!!.placeCryptoOrder(orderId!!, object : TradeItCallback<TradeItPlaceCryptoOrderResponseParcelable> {
            override fun onSuccess(placeOrderResponse: TradeItPlaceCryptoOrderResponseParcelable) {
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
        order!!.linkedBrokerAccount.cancelOrder(orderNumber!!, object : TradeItCallback<TradeItOrderStatusParcelable> {
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
        private val TAG = PreviewCryptoOrderActivity::class.java.name
    }
}
