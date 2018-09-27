package it.trade.android.exampleapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.TextView

import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable

import it.trade.android.exampleapp.MainActivity.Companion.ORDERS_STATUS_PARAMETER

class OrdersStatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_status)
        val textView = this.findViewById(R.id.orders_status_textview) as TextView?
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        val ordersStatusDetailsList = intent.getParcelableArrayListExtra<TradeItOrderStatusParcelable>(MainActivity.ORDERS_STATUS_PARAMETER)
        textView.text = ordersStatusDetailsList.toString()

    }
}
