package it.trade.android.sdk.internal;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;


public class LinkedBrokersParcelableList extends ArrayList<TradeItLinkedBrokerParcelable> {

    public LinkedBrokersParcelableList(List<TradeItLinkedBrokerParcelable> linkedBrokers) {
        this.addAll(linkedBrokers);
    }

    public boolean containsSameAccounts(TradeItLinkedBrokerParcelable linkedBrokerParcelable) {
        for(TradeItLinkedBrokerParcelable o : this) {
            if(o != null && o.equals(linkedBrokerParcelable) && o.equalsAccounts(linkedBrokerParcelable)) {
                return true;
            }
        }
        return false;
    }
}
