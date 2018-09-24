package it.trade.android.japanapp.ui.orderinput

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.trade.android.japanapp.R

class OrderInputFragment : Fragment() {

    companion object {
        fun newInstance() = OrderInputFragment()
    }

    private lateinit var viewModel: OrderInputViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.order_input_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderInputViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
