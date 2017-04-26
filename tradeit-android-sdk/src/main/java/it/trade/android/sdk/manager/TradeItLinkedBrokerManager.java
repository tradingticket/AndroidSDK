package it.trade.android.sdk.manager;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.exceptions.TradeItDeleteLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItSaveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItUpdateLinkedLoginException;
import it.trade.android.sdk.internal.TradeItKeystoreService;
import it.trade.android.sdk.model.TradeItApiClientParcelable;
import it.trade.android.sdk.model.TradeItErrorResultParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.android.sdk.model.TradeItLinkedLoginParcelable;
import it.trade.api.TradeItApiClient;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallBackImpl;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItAvailableBrokersResponse;
import it.trade.model.reponse.TradeItResponse;
import it.trade.model.request.TradeItLinkedLogin;

public class TradeItLinkedBrokerManager {

    private List<TradeItLinkedBrokerParcelable> linkedBrokers = new ArrayList<>();
    private TradeItKeystoreService keystoreService;
    private TradeItLinkedBrokerCache linkedBrokerCache;
    private TradeItApiClient apiClient;

    public TradeItLinkedBrokerManager(TradeItApiClient apiClient, TradeItLinkedBrokerCache linkedBrokerCache, TradeItKeystoreService keystoreService) throws TradeItRetrieveLinkedLoginException {
        this.keystoreService = keystoreService;
        this.linkedBrokerCache = linkedBrokerCache;
        this.apiClient = apiClient;
        this.loadLinkedBrokersFromSharedPreferences();
    }

    private void loadLinkedBrokersFromSharedPreferences() throws TradeItRetrieveLinkedLoginException {
        List<TradeItLinkedLoginParcelable> linkedLoginList = keystoreService.getLinkedLogins();
        for (TradeItLinkedLoginParcelable linkedLogin : linkedLoginList) {
            TradeItApiClientParcelable
                    apiClient = new TradeItApiClientParcelable(this.apiClient);
            //provides a default token, so if the user doesn't authenticate before an other call, it will pass an expired token in order to get the session expired error
            apiClient.setSessionToken("invalid-default-token");

            TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, linkedBrokerCache);
            linkedBrokerCache.syncFromCache(linkedBroker);
            linkedBrokers.add(linkedBroker);
        }
    }

    public void getAvailableBrokers(final TradeItCallback<List<TradeItAvailableBrokersResponse.Broker>> callback) {
        apiClient.getAvailableBrokers(new TradeItCallBackImpl<List<TradeItAvailableBrokersResponse.Broker>>() {
            @Override
            public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerList) {
                callback.onSuccess(brokerList);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void getOAuthLoginPopupUrl(String broker, String deepLinkCallback, final TradeItCallback<String> callback) {
        apiClient.getOAuthLoginPopupUrlForMobile(broker, deepLinkCallback, new TradeItCallBackImpl<String>() {
            @Override
            public void onSuccess(String oAuthURL) {
                callback.onSuccess(oAuthURL);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void getOAuthLoginPopupForTokenUpdateUrl(TradeItLinkedBrokerParcelable linkedBroker, String deepLinkCallback, final TradeItCallback<String> callback) {
        apiClient.getOAuthLoginPopupUrlForTokenUpdate(linkedBroker.getBrokerName(), linkedBroker.getLinkedLogin().userId, deepLinkCallback, new TradeItCallBackImpl<String>() {
            @Override
            public void onSuccess(String oAuthUrl) {
                callback.onSuccess(oAuthUrl);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void linkBrokerWithOauthVerifier(final String accountLabel, String oAuthVerifier, final TradeItCallback<TradeItLinkedBrokerParcelable> callback) {
        apiClient.linkBrokerWithOauthVerifier(oAuthVerifier, new TradeItCallBackImpl<TradeItLinkedLogin>() {
            @Override
            public void onSuccess(TradeItLinkedLogin linkedLogin) {
                try {
                    TradeItLinkedLoginParcelable linkedLoginParcelable = new TradeItLinkedLoginParcelable(linkedLogin);
                    TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(new TradeItApiClientParcelable(apiClient), linkedLoginParcelable, linkedBrokerCache);
                    int indexOfLinkedBroker = linkedBrokers.indexOf(linkedBroker);
                    if (indexOfLinkedBroker != -1) { //linked broker for this user id already exists, this is a token update
                        TradeItLinkedBrokerParcelable linkedBrokerToUpdate = linkedBrokers.get(indexOfLinkedBroker);
                        linkedBrokerToUpdate.setLinkedLogin(linkedLoginParcelable);
                        linkedBroker = linkedBrokerToUpdate;
                        keystoreService.updateLinkedLogin(linkedLoginParcelable);
                    } else {
                        keystoreService.saveLinkedLogin(linkedLoginParcelable, accountLabel);
                        linkedBrokers.add(linkedBroker);
                    }
                    callback.onSuccess(linkedBroker);
                } catch (TradeItSaveLinkedLoginException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResultParcelable("Failed to link broker", e.getMessage()));
                } catch (TradeItUpdateLinkedLoginException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResultParcelable("Failed to update link broker", e.getMessage()));
                }
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void unlinkBroker(final TradeItLinkedBrokerParcelable linkedBroker, final TradeItCallback callback) {
        try {
            keystoreService.deleteLinkedLogin(linkedBroker.getLinkedLogin());
            linkedBrokers.remove(linkedBroker);
            linkedBrokerCache.removeFromCache(linkedBroker);
            apiClient.unlinkBrokerAccount(linkedBroker.getLinkedLogin(), new TradeItCallBackImpl<TradeItResponse>() {
                @Override
                public void onSuccess(TradeItResponse response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                    callback.onError(errorResultParcelable);
                }
            });
        } catch (TradeItDeleteLinkedLoginException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
            callback.onError(new TradeItErrorResultParcelable("Unlink broker error", "An error occured while unlinking the broker, please try again later"));
        }
    }

    /**
     * @deprecated Use the new OAuth flow and the #linkBrokerWithOauthVerifier(String, String, String, TradeItCallback) method instead
     */
    @Deprecated
    public void linkBroker(final String accountLabel, String broker, String username, String password, final TradeItCallback<TradeItLinkedBrokerParcelable> callback) {
        apiClient.linkBrokerAccount(username, password, broker, new TradeItCallBackImpl<TradeItLinkedLogin>() {
            @Override
            public void onSuccess(TradeItLinkedLogin linkedLogin) {
                TradeItLinkedLoginParcelable linkedLoginParcelable = new TradeItLinkedLoginParcelable(linkedLogin);
                try {
                    keystoreService.saveLinkedLogin(linkedLoginParcelable, accountLabel);
                    TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(new TradeItApiClientParcelable(apiClient), linkedLoginParcelable, linkedBrokerCache);
                    linkedBrokers.add(linkedBroker);
                    callback.onSuccess(linkedBroker);
                } catch (TradeItSaveLinkedLoginException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                    callback.onError(new TradeItErrorResult("Failed to link broker", e.getMessage()));
                }
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public List<TradeItLinkedBrokerParcelable> getLinkedBrokers() {
        return new ArrayList<>(linkedBrokers);
    }
}
