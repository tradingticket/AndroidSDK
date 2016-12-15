package trade.it.android.sdk.manager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.trade.tradeitapi.API.TradeItAccountLinker;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.exception.TradeItDeleteLinkedAccountException;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedAccountException;
import it.trade.tradeitapi.exception.TradeItSaveLinkedAccountException;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker;
import it.trade.tradeitapi.model.TradeItLinkAccountRequest;
import it.trade.tradeitapi.model.TradeItLinkAccountResponse;
import it.trade.tradeitapi.model.TradeItLinkedAccount;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenRequest;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenResponse;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForMobileRequest;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForMobileResponse;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import trade.it.android.sdk.internal.DefaultCallbackWithErrorHandling;
import trade.it.android.sdk.model.TradeItCallback;
import trade.it.android.sdk.model.TradeItErrorResult;
import trade.it.android.sdk.model.TradeItLinkedBroker;
import trade.it.android.sdk.model.TradeItLinkedBrokerCache;

public class TradeItLinkedBrokerManager {

    protected TradeItAccountLinker accountLinker;
    private Context context = null;
    private TradeItLinkedBrokerCache linkedBrokerCache = new TradeItLinkedBrokerCache();

    private List<TradeItLinkedBroker> linkedBrokers = new ArrayList<>();

    public TradeItLinkedBrokerManager(Context context, TradeItAccountLinker accountLinker) throws TradeItKeystoreServiceCreateKeyException, TradeItRetrieveLinkedAccountException {
        this.accountLinker = accountLinker;
        this.context = context;
        TradeItAccountLinker.initKeyStore(context);
        this.loadLinkedBrokersFromSharedPreferences();
    }

    private void loadLinkedBrokersFromSharedPreferences() throws TradeItRetrieveLinkedAccountException {
        List<TradeItLinkedAccount> linkedAccountList = TradeItAccountLinker.getLinkedAccounts(this.context);
        for (TradeItLinkedAccount linkedAccount : linkedAccountList) {
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(context, new TradeItApiClient(linkedAccount));
            this.linkedBrokerCache.syncFromCache(context, linkedBroker);
            linkedBrokers.add(linkedBroker);
        }
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
        accountLinker.getOAuthLoginPopupUrlForMobile(request, new DefaultCallbackWithErrorHandling<TradeItOAuthLoginPopupUrlForMobileResponse, String>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthLoginPopupUrlForMobileResponse> response) {
                callback.onSuccess(response.body().oAuthURL);
            }
        });
    }

    public void linkBrokerWithOauthVerifier(final String accountLabel, final String broker, String oAuthVerifier, final TradeItCallback<TradeItLinkedBroker> callback) {
        final TradeItOAuthAccessTokenRequest request = new TradeItOAuthAccessTokenRequest(oAuthVerifier);
        request.environment = accountLinker.getTradeItEnvironment();
        accountLinker.getOAuthAccessToken(request, new DefaultCallbackWithErrorHandling<TradeItOAuthAccessTokenResponse, TradeItLinkedBroker>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthAccessTokenResponse> response) {
                TradeItLinkedAccount linkedAccount = new TradeItLinkedAccount(broker, request, response.body());
                try {
                    TradeItAccountLinker.saveLinkedAccount(context, linkedAccount, accountLabel);
                    TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(context, new TradeItApiClient(linkedAccount));
                    linkedBrokers.add(linkedBroker);
                    callback.onSuccess(linkedBroker);
                } catch (TradeItSaveLinkedAccountException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                }
            }
        });
    }

    public void unlinkBroker(final TradeItLinkedBroker linkedBroker, TradeItCallback callback) {
        try {
            TradeItAccountLinker.deleteLinkedAccount(context, linkedBroker.getLinkedAccount());
            linkedBrokers.remove(linkedBroker);
            this.accountLinker.unlinkBrokerAccount(linkedBroker.getLinkedAccount(), new DefaultCallbackWithErrorHandling<TradeItResponse, TradeItResponse>(callback) {
                @Override
                public void onSuccessResponse(Response<TradeItResponse> response) {
                    callback.onSuccess(response.body());
                }
            });
        } catch (TradeItDeleteLinkedAccountException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
            callback.onError(new TradeItErrorResult("Unlink broker error", "An error occured while unlinking the broker, please try again later"));
        }
    }

    /**
     * @deprecated Use the new OAuth flow and the {@link #linkBrokerWithOauthVerifier(String, String, String, TradeItCallback)} method instead
     */
    @Deprecated
    public void linkBroker(final String accountLabel, String broker, String username, String password, final TradeItCallback<TradeItLinkedBroker> callback) {
        final TradeItLinkAccountRequest linkAccountRequest = new TradeItLinkAccountRequest(username, password, broker);
        linkAccountRequest.environment = accountLinker.getTradeItEnvironment();
        accountLinker.linkBrokerAccount(linkAccountRequest, new DefaultCallbackWithErrorHandling<TradeItLinkAccountResponse, TradeItLinkedBroker>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItLinkAccountResponse> response) {
                TradeItLinkedAccount linkedAccount = new TradeItLinkedAccount(linkAccountRequest, response.body());
                try {
                    TradeItAccountLinker.saveLinkedAccount(context, linkedAccount, accountLabel);
                    TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(context, new TradeItApiClient(linkedAccount));
                    linkedBrokers.add(linkedBroker);
                    callback.onSuccess(linkedBroker);
                } catch (TradeItSaveLinkedAccountException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                }
            }
        });
    }

    public List<TradeItLinkedBroker> getLinkedBrokers() {
        return new ArrayList<>(linkedBrokers);
    }
}
