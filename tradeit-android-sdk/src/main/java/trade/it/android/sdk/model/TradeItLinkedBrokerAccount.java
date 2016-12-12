package trade.it.android.sdk.model;


import java.util.List;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse.Account;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewRequest;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewResponse;
import it.trade.tradeitapi.model.TradeItGetPositionsRequest;
import it.trade.tradeitapi.model.TradeItGetPositionsResponse;
import it.trade.tradeitapi.model.TradeItGetPositionsResponse.Position;
import retrofit2.Response;
import trade.it.android.sdk.internal.DefaultCallbackWithErrorHandling;

public class TradeItLinkedBrokerAccount {

    private String accountName;
    private String accountNumber;
    private String accountBaseCurrency;
    private transient TradeItLinkedBroker linkedBroker;
    private TradeItGetAccountOverviewResponse balance;
    private List<Position> positions;

    public TradeItLinkedBrokerAccount(TradeItLinkedBroker linkedBroker, Account account) {
        this.linkedBroker =  linkedBroker;
        this.accountName = account.name;
        this.accountNumber = account.accountNumber;
        this.accountBaseCurrency = account.accountBaseCurrency;
    }

    protected TradeItApiClient getTradeItApiClient() {
        return this.linkedBroker.getTradeItApiClient();
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

    public List<Position> getPositions() {
        return positions;
    }

    public void refreshBalance(final TradeItCallback<TradeItGetAccountOverviewResponse> callback) {
        TradeItGetAccountOverviewRequest balanceRequest = new TradeItGetAccountOverviewRequest(accountNumber);
        this.getTradeItApiClient().getAccountOverview(balanceRequest, new DefaultCallbackWithErrorHandling<TradeItGetAccountOverviewResponse, TradeItGetAccountOverviewResponse>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItGetAccountOverviewResponse> response) {
                balance = response.body();
                callback.onSuccess(response.body());
            }
        });
    }

    public void refreshPositions(final TradeItCallback<List<Position>> callback) {
        TradeItGetPositionsRequest positionsRequest = new TradeItGetPositionsRequest(accountNumber, null);
        this.getTradeItApiClient().getPositions(positionsRequest, new DefaultCallbackWithErrorHandling<TradeItGetPositionsResponse, List<Position>>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItGetPositionsResponse> response) {
                positions = response.body().positions;
                callback.onSuccess(positions);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBrokerAccount that = (TradeItLinkedBrokerAccount) o;

        if (!accountName.equals(that.accountName)) return false;
        return accountNumber.equals(that.accountNumber);

    }

    @Override
    public int hashCode() {
        int result = accountName.hashCode();
        result = 31 * result + accountNumber.hashCode();
        return result;
    }
}
