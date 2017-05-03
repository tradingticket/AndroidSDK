package it.trade.android.sdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.trade.api.TradeItApiClient;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItBrokerAccount;
import it.trade.model.reponse.TradeItGetAccountOverviewResponse;
import it.trade.model.reponse.TradeItPosition;

public class TradeItLinkedBrokerAccountParcelable implements Parcelable {
    private static Map<String, TradeItLinkedBrokerParcelable> linkedBrokersMap = new HashMap<>(); //used for parcelable
    private String accountName;
    private String accountNumber;
    private String accountBaseCurrency;

    public transient TradeItLinkedBrokerParcelable linkedBroker;
    private TradeItBalanceParcelable balance;
    private Date balanceLastUpdated;
    private List<TradeItPositionParcelable> positions;
    private String userId;

    public TradeItLinkedBrokerAccountParcelable(TradeItLinkedBrokerParcelable linkedBroker, TradeItBrokerAccount account) {
        this.linkedBroker =  linkedBroker;
        this.accountName = account.name;
        this.accountNumber = account.accountNumber;
        this.accountBaseCurrency = account.accountBaseCurrency;
        this.userId = linkedBroker.getLinkedLogin().userId;
    }

    protected TradeItApiClient getTradeItApiClient() {
        return this.linkedBroker.getApiClient();
    }

    protected void setErrorOnLinkedBroker(TradeItErrorResultParcelable errorResult) {
        this.linkedBroker.setError(errorResult);
    }

    public String getBrokerName() {
        return this.linkedBroker.getBrokerName();
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountBaseCurrency() {
        return accountBaseCurrency;
    }

    public TradeItBalanceParcelable getBalance() {
        return balance;
    }

    public void setBalance(TradeItBalanceParcelable balance) {
        this.balance = balance;
    }

    public Date getBalanceLastUpdated() { return this.balanceLastUpdated; }

    public void setBalanceLastUpdated(Date balanceLastUpdated) {
        this.balanceLastUpdated = balanceLastUpdated;
    }

    public List<TradeItPositionParcelable> getPositions() {
        return positions;
    }

    void setLinkedBroker(TradeItLinkedBrokerParcelable linkedBroker) {
        this.linkedBroker = linkedBroker;
    }

    void setPositions(List<TradeItPositionParcelable> positions) {
        this.positions = positions;
    }

    public void refreshBalance(final TradeItCallback<TradeItBalanceParcelable> callback) {
        final TradeItLinkedBrokerAccountParcelable linkedBrokerAccount = this;
        this.getTradeItApiClient().getAccountOverview(accountNumber, new TradeItCallback<TradeItGetAccountOverviewResponse>() {
            @Override
            public void onSuccess(TradeItGetAccountOverviewResponse response) {
                TradeItBalanceParcelable balance = new TradeItBalanceParcelable(response);
                linkedBrokerAccount.balance = balance;
                linkedBrokerAccount.balanceLastUpdated = new Date();
                linkedBroker.cache();
                callback.onSuccess(balance);
            }

            @Override
            public void onError(TradeItErrorResult errorResult) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(errorResult);
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void refreshPositions(final TradeItCallback<List<TradeItPositionParcelable>> callback) {
        final TradeItLinkedBrokerAccountParcelable linkedBrokerAccount = this;
        this.getTradeItApiClient().getPositions(accountNumber, new TradeItCallback<List<TradeItPosition>>() {
            @Override
            public void onSuccess(List<TradeItPosition> positions) {
                List<TradeItPositionParcelable> positionsParcelable = mapPositionsToPositionsParcelable(positions);
                linkedBrokerAccount.positions = positionsParcelable;
                callback.onSuccess(positionsParcelable);
            }

            @Override
            public void onError(TradeItErrorResult errorResult) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(errorResult);
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable);
                callback.onError(errorResultParcelable);
            }
        });
    }

    private List<TradeItPositionParcelable> mapPositionsToPositionsParcelable(List<TradeItPosition> positions) {
        List<TradeItPositionParcelable> positionsParcelable = new ArrayList<>();
        for (TradeItPosition position : positions) {
            positionsParcelable.add(new TradeItPositionParcelable(position));
        }
        return positionsParcelable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBrokerAccountParcelable that = (TradeItLinkedBrokerAccountParcelable) o;

        if (accountName != null ? !accountName.equals(that.accountName) : that.accountName != null)
            return false;
        if (accountNumber != null ? !accountNumber.equals(that.accountNumber) : that.accountNumber != null)
            return false;
        return accountBaseCurrency != null ? accountBaseCurrency.equals(that.accountBaseCurrency) : that.accountBaseCurrency == null;

    }

    @Override
    public int hashCode() {
        int result = accountName != null ? accountName.hashCode() : 0;
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (accountBaseCurrency != null ? accountBaseCurrency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TradeItLinkedBrokerAccountParcelable{" +
                "accountBaseCurrency='" + accountBaseCurrency + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accountName);
        dest.writeString(this.accountNumber);
        dest.writeString(this.accountBaseCurrency);
        dest.writeParcelable(this.balance, flags);
        dest.writeLong(this.balanceLastUpdated != null ? this.balanceLastUpdated.getTime() : -1);
        dest.writeList(this.positions);
        dest.writeString(this.userId);
        linkedBrokersMap.put(this.userId, linkedBroker);
    }

    protected TradeItLinkedBrokerAccountParcelable(Parcel in) {
        this.accountName = in.readString();
        this.accountNumber = in.readString();
        this.accountBaseCurrency = in.readString();
        this.balance = in.readParcelable(TradeItBalanceParcelable.class.getClassLoader());
        long tmpBalanceLastUpdated = in.readLong();
        this.balanceLastUpdated = tmpBalanceLastUpdated == -1 ? null : new Date(tmpBalanceLastUpdated);
        this.positions = new ArrayList<TradeItPositionParcelable>();
        in.readList(this.positions, TradeItPositionParcelable.class.getClassLoader());
        this.userId = in.readString();
        this.linkedBroker = linkedBrokersMap.get(this.userId);
    }

    public static final Parcelable.Creator<TradeItLinkedBrokerAccountParcelable> CREATOR = new Parcelable.Creator<TradeItLinkedBrokerAccountParcelable>() {
        @Override
        public TradeItLinkedBrokerAccountParcelable createFromParcel(Parcel source) {
            return new TradeItLinkedBrokerAccountParcelable(source);
        }

        @Override
        public TradeItLinkedBrokerAccountParcelable[] newArray(int size) {
            return new TradeItLinkedBrokerAccountParcelable[size];
        }
    };
}
