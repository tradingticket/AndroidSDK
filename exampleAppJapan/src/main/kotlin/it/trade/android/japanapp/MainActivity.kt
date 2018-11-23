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
                    // TODO this symbol is only for testing. and it's chosen as different to
                    // the 8703 in Fragment class, so to distinguish how Fragment is initialized.
                    // actual value should be provided by symbol lookup Fragment(not yet created.)
                    .replace(R.id.container, OrderInputFragment.newInstance("8704"))
                    .commitNow()
        }
    }

}
