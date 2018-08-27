package it.trade.android.exampleapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker

class BrokerAdapter(context: Context, items: List<Broker>) : ArrayAdapter<Broker>(context, android.R.layout.simple_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get the data item for this position
        val broker = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val view = convertView
                ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)

        // Lookup view for data population
        val textView = view as TextView
        // Populate the data into the template view using the data object
        textView.text = broker!!.longName

        // Return the completed view to render on screen
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val broker = getItem(position)
        val view = convertView
                ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)

        val textView = view as TextView
        textView.text = broker!!.longName

        return view
    }
}
