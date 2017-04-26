package it.trade.android.exampleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker;

public class BrokerAdapter extends ArrayAdapter<Broker> {

    public BrokerAdapter(Context context, List<Broker> items) {
        super(context, android.R.layout.simple_spinner_item, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Broker broker = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        // Lookup view for data population
        TextView textView = (TextView) convertView;

        // Populate the data into the template view using the data object
        textView.setText(broker.longName);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Broker broker = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        }

        TextView textView = (TextView) convertView;
        textView.setText(broker.longName);

        return convertView;
    }
}
