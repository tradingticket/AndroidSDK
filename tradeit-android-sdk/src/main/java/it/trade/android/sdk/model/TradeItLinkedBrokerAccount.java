package it.trade.android.sdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.internal.DefaultCallback;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItBrokerAccount;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewRequest;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewResponse;
import it.trade.tradeitapi.model.TradeItGetPositionsRequest;
import it.trade.tradeitapi.model.TradeItGetPositionsResponse;
import it.trade.tradeitapi.model.TradeItLinkedLogin;
import it.trade.tradeitapi.model.TradeItPosition;
import retrofit2.Response;

public class TradeItLinkedBrokerAccount implements Parcelable {
    private static Map<String, TradeItLinkedBroker> linkedBrokersMap = new HashMap<>(); //used for parcelable
    private String accountName;
    private String accountNumber;
    private String accountBaseCurrency;

    private transient TradeItLinkedBroker linkedBroker;
    private TradeItGetAccountOverviewResponse balance;
    private List<TradeItPosition> positions;
    private TradeItLinkedLogin linkedLogin;

    public TradeItLinkedBrokerAccount(TradeItLinkedBroker linkedBroker, TradeItBrokerAccount account) {
        this.linkedBroker =  linkedBroker;
        this.accountName = account.name;
        this.accountNumber = account.accountNumber;
        this.accountBaseCurrency = account.accountBaseCurrency;
        this.linkedLogin = linkedBroker.getLinkedLogin();
    }

    protected TradeItApiClient getTradeItApiClient() {
        return this.linkedBroker.getTradeItApiClient();
    }

    protected void setErrorOnLinkedBroker(TradeItErrorResult errorResult) {
        this.linkedBroker.setError(errorResult);
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

    public TradeItGetAccountOverviewResponse getBalance() {
        return balance;
    }

    void setBalance(TradeItGetAccountOverviewResponse balance) {
        this.balance = balance;
    }

    public List<TradeItPosition> getPositions() {
        return positions;
    }

    void setPositions(List<TradeItPosition> positions) {
        this.positions = positions;
    }

    public void refreshBalance(final TradeItCallback<TradeItGetAccountOverviewResponse> callback) {
        TradeItGetAccountOverviewRequest balanceRequest = new TradeItGetAccountOverviewRequest(accountNumber);
        final TradeItLinkedBrokerAccount linkedBrokerAccount = this;
        this.getTradeItApiClient().getAccountOverview(balanceRequest, new DefaultCallback<TradeItGetAccountOverviewResponse, TradeItGetAccountOverviewResponse>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItGetAccountOverviewResponse> response) {
                balance = response.body();
                callback.onSuccess(response.body());
            }

            @Override
            public void onErrorResponse(TradeItErrorResult errorResult) {
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResult);
                callback.onError(errorResult);
            }
        });
    }

    public void refreshPositions(final TradeItCallback<List<TradeItPosition>> callback) {
        TradeItGetPositionsRequest positionsRequest = new TradeItGetPositionsRequest(accountNumber, null);
        final TradeItLinkedBrokerAccount linkedBrokerAccount = this;
        this.getTradeItApiClient().getPositions(positionsRequest, new DefaultCallback<TradeItGetPositionsResponse, List<TradeItPosition>>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItGetPositionsResponse> response) {
                positions = response.body().positions;
                callback.onSuccess(positions);
            }

            @Override
            public void onErrorResponse(TradeItErrorResult errorResult) {
                linkedBrokerAccount.setErrorOnLinkedBroker(errorResult);
                callback.onError(errorResult);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBrokerAccount that = (TradeItLinkedBrokerAccount) o;

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
        return "TradeItLinkedBrokerAccount{" +
                "accountBaseCurrency='" + accountBaseCurrency + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
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
        dest.writeList(this.positions);
        dest.writeParcelable(this.linkedLogin, flags);
        linkedBrokersMap.put(this.linkedLogin.userId, linkedBroker);
    }

    protected TradeItLinkedBrokerAccount(Parcel in) {
        this.accountName = in.readString();
        this.accountNumber = in.readString();
        this.accountBaseCurrency = in.readString();
        this.balance = in.readParcelable(TradeItGetAccountOverviewResponse.class.getClassLoader());
        this.positions = new ArrayList<TradeItPosition>();
        in.readList(this.positions, TradeItPosition.class.getClassLoader());
        this.linkedLogin = in.readParcelable(TradeItLinkedLogin.class.getClassLoader());
        this.linkedBroker = linkedBrokersMap.get(this.linkedLogin.userId);
    }

    public static final Parcelable.Creator<TradeItLinkedBrokerAccount> CREATOR = new Parcelable.Creator<TradeItLinkedBrokerAccount>() {
        @Override
        public TradeItLinkedBrokerAccount createFromParcel(Parcel source) {
            return new TradeItLinkedBrokerAccount(source);
        }

        @Override
        public TradeItLinkedBrokerAccount[] newArray(int size) {
            return new TradeItLinkedBrokerAccount[size];
        }
    };
}
