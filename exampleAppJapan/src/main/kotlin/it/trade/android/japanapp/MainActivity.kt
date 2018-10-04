package it.trade.android.japanapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import it.trade.android.japanapp.R
import it.trade.android.japanapp.ui.orderinput.OrderInputFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, OrderInputFragment.newInstance("8704"))
                    .commitNow()
        }
    }

}
