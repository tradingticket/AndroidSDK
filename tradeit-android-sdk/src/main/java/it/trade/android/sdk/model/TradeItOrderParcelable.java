package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItPlaceStockOrEtfOrderResponse;
import it.trade.model.reponse.TradeItPreviewStockOrEtfOrderResponse;
import it.trade.model.request.TradeItPreviewStockOrEtfOrderRequest;


public class TradeItOrderParcelable implements Parcelable {

    private TradeItLinkedBrokerAccountParcelable linkedBrokerAccount;
    private String symbol;
    private int quantity = 1;
    private Double limitPrice;
    private Double stopPrice;
    private Double quoteLastPrice;
    private TradeItOrderAction action = TradeItOrderAction.BUY;
    private TradeItOrderPriceType priceType = TradeItOrderPriceType.MARKET;
    private TradeItOrderExpiration expiration = TradeItOrderExpiration.GOOD_FOR_DAY;

    public TradeItOrderParcelable(TradeItLinkedBrokerAccountParcelable linkedBrokerAccount, String symbol) {
        this.linkedBrokerAccount = linkedBrokerAccount;
        this.symbol = symbol;
    }

    public void previewOrder(final TradeItCallback<TradeItPreviewStockOrEtfOrderResponseParcelable> callback) {
        TradeItPreviewStockOrEtfOrderRequest previewRequest = new TradeItPreviewStockOrEtfOrderRequest(this.linkedBrokerAccount.getAccountNumber(),
                this.action.getActionValue(),
                String.valueOf(this.quantity),
                this.symbol,
                this.priceType.getPriceTypeValue(),
                (this.limitPrice != null ? this.limitPrice.toString() : null),
                (this.stopPrice != null ? this.stopPrice.toString() : null),
                this.expiration.getExpirationValue());
        final TradeItOrderParcelable order = this;
        this.linkedBrokerAccount.getTradeItApiClient().previewStockOrEtfOrder(previewRequest, new TradeItCallback<TradeItPreviewStockOrEtfOrderResponse>() {
            @Override
            public void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
                callback.onSuccess(new TradeItPreviewStockOrEtfOrderResponseParcelable(response));
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                order.linkedBrokerAccount.setErrorOnLinkedBroker(errorResultParcelable);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void placeOrder(String orderId, final TradeItCallback<TradeItPlaceStockOrEtfOrderResponseParcelable> callback) {
        this.linkedBrokerAccount.getTradeItApiClient().placeStockOrEtfOrder(orderId, new TradeItCallback<TradeItPlaceStockOrEtfOrderResponse>() {
            @Override
            public void onSuccess(TradeItPlaceStockOrEtfOrderResponse response) {
                callback.onSuccess(new TradeItPlaceStockOrEtfOrderResponseParcelable(response));
            }

            @Override
            public void onError(TradeItErrorResult error) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(error);
                callback.onError(errorResultParcelable);
            }
        });
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(Double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public Double getQuoteLastPrice() {
        return quoteLastPrice;
    }

    public void setQuoteLastPrice(Double quoteLastPrice) {
        this.quoteLastPrice = quoteLastPrice;
    }

    public TradeItOrderAction getAction() {
        return action;
    }

    public void setAction(TradeItOrderAction action) {
        this.action = action;
    }

    public TradeItOrderPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(TradeItOrderPriceType priceType) {
        this.priceType = priceType;
    }

    public TradeItOrderExpiration getExpiration() {
        return expiration;
    }

    public void setExpiration(TradeItOrderExpiration expiration) {
        this.expiration = expiration;
    }

    public TradeItLinkedBrokerAccountParcelable getLinkedBrokerAccount() {
        return linkedBrokerAccount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.linkedBrokerAccount, flags);
        dest.writeString(this.symbol);
        dest.writeInt(this.quantity);
        dest.writeValue(this.limitPrice);
        dest.writeValue(this.stopPrice);
        dest.writeValue(this.quoteLastPrice);
        dest.writeInt(this.action == null ? -1 : this.action.ordinal());
        dest.writeInt(this.priceType == null ? -1 : this.priceType.ordinal());
        dest.writeInt(this.expiration == null ? -1 : this.expiration.ordinal());
    }

    protected TradeItOrderParcelable(Parcel in) {
        this.linkedBrokerAccount = in.readParcelable(TradeItLinkedBrokerAccountParcelable.class.getClassLoader());
        this.symbol = in.readString();
        this.quantity = in.readInt();
        this.limitPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.stopPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.quoteLastPrice = (Double) in.readValue(Double.class.getClassLoader());
        int tmpAction = in.readInt();
        this.action = tmpAction == -1 ? null : TradeItOrderAction.values()[tmpAction];
        int tmpPriceType = in.readInt();
        this.priceType = tmpPriceType == -1 ? null : TradeItOrderPriceType.values()[tmpPriceType];
        int tmpExpiration = in.readInt();
        this.expiration = tmpExpiration == -1 ? null : TradeItOrderExpiration.values()[tmpExpiration];
    }

    public static final Parcelable.Creator<TradeItOrderParcelable> CREATOR = new Parcelable.Creator<TradeItOrderParcelable>() {
        @Override
        public TradeItOrderParcelable createFromParcel(Parcel source) {
            return new TradeItOrderParcelable(source);
        }

        @Override
        public TradeItOrderParcelable[] newArray(int size) {
            return new TradeItOrderParcelable[size];
        }
    };
}
