package it.trade.android.sdk.manager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.TradeItSdk2;
import it.trade.android.sdk.internal.DefaultCallbackWithErrorHandling;
import it.trade.android.sdk.model.TradeItCallback;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBroker;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.API.TradeItBrokerLinker;
import it.trade.tradeitapi.exception.TradeItDeleteLinkedLoginException;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedLoginException;
import it.trade.tradeitapi.exception.TradeItSaveLinkedLoginException;
import it.trade.tradeitapi.exception.TradeItUpdateLinkedLoginException;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItLinkLoginRequest;
import it.trade.tradeitapi.model.TradeItLinkLoginResponse;
import it.trade.tradeitapi.model.TradeItLinkedLogin;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenRequest;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenResponse;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForMobileRequest;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForMobileResponse;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForTokenUpdateRequest;
import it.trade.tradeitapi.model.TradeItOAuthLoginPopupUrlForTokenUpdateResponse;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.trade.tradeitapi.API.TradeItBrokerLinker.saveLinkedLogin;
import static it.trade.tradeitapi.API.TradeItBrokerLinker.updateLinkedLogin;

public class TradeItLinkedBrokerManager {

    protected TradeItBrokerLinker brokerLinker;

    private List<TradeItLinkedBroker> linkedBrokers = new ArrayList<>();
    private final Context context;
    private final String apiKey;
    private final TradeItEnvironment environment;

    public TradeItLinkedBrokerManager(Context context) throws TradeItKeystoreServiceCreateKeyException, TradeItRetrieveLinkedLoginException {
        this.context = context;
        this.apiKey =  TradeItSDK.getApiKey();
        this.environment = TradeItSDK.getEnvironment();
        this.brokerLinker = new TradeItBrokerLinker(apiKey, environment);
        TradeItBrokerLinker.initKeyStore(context);
        this.loadLinkedBrokersFromSharedPreferences();
    }

    public TradeItLinkedBrokerManager(Context context, TradeItSdk2 sdk) throws TradeItKeystoreServiceCreateKeyException, TradeItRetrieveLinkedLoginException {
        this.context = context;
        this.apiKey =  sdk.getApiKey();
        this.environment = sdk.getEnvironment();
        this.brokerLinker = new TradeItBrokerLinker(apiKey, environment);
        TradeItBrokerLinker.initKeyStore(context);
        this.loadLinkedBrokersFromSharedPreferences();
    }

    private void loadLinkedBrokersFromSharedPreferences() throws TradeItRetrieveLinkedLoginException {
        List<TradeItLinkedLogin> linkedLoginList = TradeItBrokerLinker.getLinkedLogins(context);
        for (TradeItLinkedLogin linkedLogin : linkedLoginList) {
            TradeItApiClient apiClient = new TradeItApiClient(linkedLogin, environment);
            //provides a default token, so if the user doesn't authenticate before an other call, it will pass an expired token in order to get the session expired error
            apiClient.setSessionToken("invalid-default-token");
            TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient);
            TradeItSDK.getLinkedBrokerCache().syncFromCache(linkedBroker);
            linkedBrokers.add(linkedBroker);
        }
    }

    public void getAvailableBrokers(final TradeItCallback<List<Broker>> callback) {
        brokerLinker.getAvailableBrokers(new Callback<TradeItAvailableBrokersResponse>() {
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

    public void getOAuthLoginPopupUrlForMobile(String broker, String deepLinkCallback, final TradeItCallback<String> callback) {
        TradeItOAuthLoginPopupUrlForMobileRequest request = new TradeItOAuthLoginPopupUrlForMobileRequest(broker, deepLinkCallback);
        brokerLinker.getOAuthLoginPopupUrlForMobile(request, new DefaultCallbackWithErrorHandling<TradeItOAuthLoginPopupUrlForMobileResponse, String>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthLoginPopupUrlForMobileResponse> response) {
                callback.onSuccess(response.body().oAuthURL);
            }
        });
    }

    public void getOAuthLoginPopupForTokenUpdateUrl(String broker, String userId, String deepLinkCallback, final TradeItCallback<String> callback) {
        TradeItOAuthLoginPopupUrlForTokenUpdateRequest request = new TradeItOAuthLoginPopupUrlForTokenUpdateRequest(broker, deepLinkCallback, userId);
        brokerLinker.getOAuthLoginPopupUrlForTokenUpdate(request, new DefaultCallbackWithErrorHandling<TradeItOAuthLoginPopupUrlForTokenUpdateResponse, String>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthLoginPopupUrlForTokenUpdateResponse> response) {
                callback.onSuccess(response.body().oAuthURL);
            }
        });
    }

    public void linkBrokerWithOauthVerifier(final String accountLabel, final String broker, String oAuthVerifier, final TradeItCallback<TradeItLinkedBroker> callback) {
        final TradeItOAuthAccessTokenRequest request = new TradeItOAuthAccessTokenRequest(oAuthVerifier);
        brokerLinker.getOAuthAccessToken(request, new DefaultCallbackWithErrorHandling<TradeItOAuthAccessTokenResponse, TradeItLinkedBroker>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItOAuthAccessTokenResponse> response) {
                TradeItLinkedLogin linkedLogin = new TradeItLinkedLogin(request, response.body());
                try {
                    TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(new TradeItApiClient(linkedLogin, environment));
                    int indexOfLinkedBroker = linkedBrokers.indexOf(linkedBroker);
                    if (indexOfLinkedBroker != -1) { //linked broker for this user id already exists, this is a token update
                        TradeItLinkedBroker linkedBrokerToUpdate = linkedBrokers.get(indexOfLinkedBroker);
                        linkedBrokerToUpdate.setLinkedLogin(linkedLogin);
                        linkedBroker = linkedBrokerToUpdate;
                        updateLinkedLogin(context, linkedLogin);
                    } else {
                        saveLinkedLogin(context, linkedLogin, accountLabel);
                        linkedBrokers.add(linkedBroker);
                    }
                    callback.onSuccess(linkedBroker);
                } catch (TradeItSaveLinkedLoginException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                } catch (TradeItUpdateLinkedLoginException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to update link broker", e.getMessage()));
                }
            }
        });
    }

    public void unlinkBroker(final TradeItLinkedBroker linkedBroker, TradeItCallback callback) {
        try {
            TradeItBrokerLinker.deleteLinkedLogin(context, linkedBroker.getLinkedLogin());
            linkedBrokers.remove(linkedBroker);
            TradeItSDK.getLinkedBrokerCache().removeFromCache(linkedBroker);
            this.brokerLinker.unlinkBrokerAccount(linkedBroker.getLinkedLogin(), new DefaultCallbackWithErrorHandling<TradeItResponse, TradeItResponse>(callback) {
                @Override
                public void onSuccessResponse(Response<TradeItResponse> response) {
                    callback.onSuccess(response.body());
                }
            });
        } catch (TradeItDeleteLinkedLoginException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
            callback.onError(new TradeItErrorResult("Unlink broker error", "An error occured while unlinking the broker, please try again later"));
        }
    }

    /**
     * @deprecated Use the new OAuth flow and the {@link #linkBrokerWithOauthVerifier(String, String, String, TradeItCallback)} method instead
     */
    @Deprecated
    public void linkBroker(final String accountLabel, String broker, String username, String password, final TradeItCallback<TradeItLinkedBroker> callback) {
        final TradeItLinkLoginRequest linkLoginRequest = new TradeItLinkLoginRequest(username, password, broker);
        brokerLinker.linkBrokerAccount(linkLoginRequest, new DefaultCallbackWithErrorHandling<TradeItLinkLoginResponse, TradeItLinkedBroker>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItLinkLoginResponse> response) {
                TradeItLinkedLogin linkedLogin = new TradeItLinkedLogin(linkLoginRequest, response.body());
                try {
                    saveLinkedLogin(context, linkedLogin, accountLabel);
                    TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(new TradeItApiClient(linkedLogin, environment));
                    linkedBrokers.add(linkedBroker);
                    callback.onSuccess(linkedBroker);
                } catch (TradeItSaveLinkedLoginException e) {
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
