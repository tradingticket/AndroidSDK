package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import it.trade.android.sdk.model.TradeItPositionParcelable


class PositionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positions)
        val textView = this.findViewById(R.id.positions_textview) as TextView?
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        val positions = intent.getParcelableArrayListExtra<TradeItPositionParcelable>(MainActivity.POSITIONS_PARAMETER)
        textView.text = positions.toString()
    }
}
