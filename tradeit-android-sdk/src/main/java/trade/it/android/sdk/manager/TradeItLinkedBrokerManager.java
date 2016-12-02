package trade.it.android.sdk.manager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.trade.tradeitapi.API.TradeItAccountLinker;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItSaveLinkedAccountException;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItLinkAccountRequest;
import it.trade.tradeitapi.model.TradeItLinkAccountResponse;
import it.trade.tradeitapi.model.TradeItLinkedAccount;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenRequest;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenResponse;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForMobileRequest;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForMobileResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import trade.it.android.sdk.internal.CallBackWithDefaultErrorHandling;
import trade.it.android.sdk.model.TradeItCallback;
import trade.it.android.sdk.model.TradeItErrorResult;

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

    public void getOAuthLoginPopupUrlForMobile(String broker, String interAppAddressCallback, final TradeItCallback<String> callback) {
        TradeItOAuthLoginPopupUrlForMobileRequest request = new TradeItOAuthLoginPopupUrlForMobileRequest(broker, interAppAddressCallback);
        accountLinker.getOAuthLoginPopupUrlForMobile(request, new CallBackWithDefaultErrorHandling<TradeItOAuthLoginPopupUrlForMobileResponse, String>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthLoginPopupUrlForMobileResponse> response) {
                callback.onSuccess(response.body().oAuthURL);
            }
        });
    }

    public void linkBrokerWithOauthVerifier(final Context context, final String accountLabel, final String broker, String oAuthVerifier, final TradeItCallback<TradeItLinkedAccount> callback) {
        final TradeItOAuthAccessTokenRequest request = new TradeItOAuthAccessTokenRequest(oAuthVerifier);
        accountLinker.getOAuthAccessToken(request, new CallBackWithDefaultErrorHandling<TradeItOAuthAccessTokenResponse, TradeItLinkedAccount>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthAccessTokenResponse> response) {
                TradeItLinkedAccount linkedAccount = new TradeItLinkedAccount(broker, request, response.body());
                try {
                    TradeItAccountLinker.initKeyStore(context);
                    TradeItAccountLinker.saveLinkedAccount(context, linkedAccount, accountLabel);
                    callback.onSuccess(linkedAccount);
                } catch (TradeItSaveLinkedAccountException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                } catch (TradeItKeystoreServiceCreateKeyException e) {
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                }
            }
        });
    }


    /**
     * @deprecated Use the new OAuth flow and the {@link #linkBrokerWithOauthVerifier(Context, String, String, String, TradeItCallback)} method instead
     */
    @Deprecated
    public void linkBroker(final Context context, final String accountLabel, String broker, String username, String password, final TradeItCallback<TradeItLinkedAccount> callback) {
        final TradeItLinkAccountRequest linkAccountRequest = new TradeItLinkAccountRequest(username, password, broker);
        accountLinker.linkBrokerAccount(linkAccountRequest, new CallBackWithDefaultErrorHandling<TradeItLinkAccountResponse, TradeItLinkedAccount>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItLinkAccountResponse> response) {
                TradeItLinkedAccount linkedAccount = new TradeItLinkedAccount(linkAccountRequest, response.body());
                try {
                    TradeItAccountLinker.initKeyStore(context);
                    TradeItAccountLinker.saveLinkedAccount(context, linkedAccount, accountLabel);
                    callback.onSuccess(linkedAccount);
                } catch (TradeItSaveLinkedAccountException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                } catch (TradeItKeystoreServiceCreateKeyException e) {
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                }
            }
        });
    }
}
