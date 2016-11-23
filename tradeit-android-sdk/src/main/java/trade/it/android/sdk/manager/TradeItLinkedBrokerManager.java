package trade.it.android.sdk.manager;

import java.util.ArrayList;
import java.util.List;

import it.trade.tradeitapi.API.TradeItAccountLinker;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import trade.it.android.sdk.model.TradeItCallback;

public class TradeItLinkedBrokerManager {

    protected TradeItAccountLinker accountLinker;

    public TradeItLinkedBrokerManager(String apiKey, TradeItEnvironment environment) {
        this.accountLinker = new TradeItAccountLinker(apiKey, environment);
    }

    public void getAvailableBrokers(final TradeItCallback<List<Broker>> callback) {
        accountLinker.getAvailableBrokers(new Callback<TradeItAvailableBrokersResponse>() {
            List<Broker> brokerList = new ArrayList<>();
            public void onResponse(Call<TradeItAvailableBrokersResponse> call, Response<TradeItAvailableBrokersResponse> response) {
                if (response.isSuccessful() && response.body().status == TradeItResponseStatus.SUCCESS) {
                    TradeItAvailableBrokersResponse availableBrokersResponse = response.body();
                    brokerList = availableBrokersResponse.brokerList;
                }
                callback.onSuccess(brokerList);
            }

            public void onFailure(Call call, Throwable t) {
                callback.onSuccess(brokerList);
            }
        });
    }
}
