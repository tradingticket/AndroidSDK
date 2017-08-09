package it.trade.android.sdk.model;

import java.util.ArrayList;
import java.util.List;

public class TradeItLinkedBrokerData {

    public String broker;
    public String userId;
    public String userToken;
    public boolean isLinkActivationPending = false;
    public List<TradeItLinkedBrokerAccountData> linkedBrokerAccountDataList = new ArrayList<>();

    public TradeItLinkedBrokerData(String broker, String userId, String userToken) {
        this.broker = broker;
        this.userId = userId;
        this.userToken = userToken;
    }

    public TradeItLinkedBrokerData withLinkActivationPending(boolean isLinkActivationPending) {
        this.isLinkActivationPending = isLinkActivationPending;
        return this;
    }

    public void injectAccount(TradeItLinkedBrokerAccountData linkedBrokerAccountData) {
        this.linkedBrokerAccountDataList.add(linkedBrokerAccountData);
    }

    public TradeItLinkedBrokerData(TradeItLinkedLoginParcelable linkedLoginParcelable) {
        this.broker = linkedLoginParcelable.broker;
        this.userId = linkedLoginParcelable.userId;
        this.userToken = linkedLoginParcelable.userToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBrokerData that = (TradeItLinkedBrokerData) o;

        return (userId.equals(that.userId) && userToken.equals(that.userToken));
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (userToken != null ? userToken.hashCode() : 0);
        return result;
    }
}
