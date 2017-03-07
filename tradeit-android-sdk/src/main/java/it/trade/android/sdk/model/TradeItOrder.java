package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.android.sdk.internal.DefaultCallbackWithErrorHandling;
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderRequest;
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse;
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderRequest;
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderResponse;
import retrofit2.Response;
import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.internal.PreviewTradeCallbackWithErrorHandling;

public class TradeItOrder implements Parcelable {

    private TradeItLinkedBrokerAccount linkedBrokerAccount;
    private String symbol;
    private int quantity = 1;
    private Double limitPrice;
    private Double stopPrice;
    private Double quoteLastPrice;
    private TradeItOrderAction action = TradeItOrderAction.BUY;
    private TradeItOrderPriceType priceType = TradeItOrderPriceType.MARKET;
    private TradeItOrderExpiration expiration = TradeItOrderExpiration.GOOD_FOR_DAY;

    public TradeItOrder(TradeItLinkedBrokerAccount linkedBrokerAccount, String symbol) {
        this.linkedBrokerAccount = linkedBrokerAccount;
        this.symbol = symbol;
    }

    public void previewOrder(final TradeItCallback<TradeItPreviewStockOrEtfOrderResponse> callback) {
        TradeItPreviewStockOrEtfOrderRequest previewRequest = new TradeItPreviewStockOrEtfOrderRequest(this.linkedBrokerAccount.getAccountNumber(),
                this.action.getActionValue(),
                String.valueOf(this.quantity),
                this.symbol,
                this.priceType.getPriceTypeValue(),
                (this.limitPrice != null ? this.limitPrice.toString() : null),
                (this.stopPrice != null ? this.stopPrice.toString() : null),
                this.expiration.getExpirationValue());

        this.linkedBrokerAccount.getTradeItApiClient().previewStockOrEtfOrder(previewRequest, new PreviewTradeCallbackWithErrorHandling<TradeItPreviewStockOrEtfOrderResponse, TradeItPreviewStockOrEtfOrderResponse>(callback) {

            @Override
            public void onSuccessResponse(Response<TradeItPreviewStockOrEtfOrderResponse> response) {
                callback.onSuccess(response.body());
            }

        });
    }

    public void placeOrder(String orderId, final TradeItCallback<TradeItPlaceStockOrEtfOrderResponse> callback) {
        TradeItPlaceStockOrEtfOrderRequest placeStockOrEtfOrderRequest = new TradeItPlaceStockOrEtfOrderRequest(orderId);
        this.linkedBrokerAccount.getTradeItApiClient().placeStockOrEtfOrder(placeStockOrEtfOrderRequest, new DefaultCallbackWithErrorHandling<TradeItPlaceStockOrEtfOrderResponse, TradeItPlaceStockOrEtfOrderResponse>(callback) {

            @Override
            public void onSuccessResponse(Response<TradeItPlaceStockOrEtfOrderResponse> response) {
                callback.onSuccess(response.body());
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

    protected TradeItOrder(Parcel in) {
        this.linkedBrokerAccount = in.readParcelable(TradeItLinkedBrokerAccount.class.getClassLoader());
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

    public static final Parcelable.Creator<TradeItOrder> CREATOR = new Parcelable.Creator<TradeItOrder>() {
        @Override
        public TradeItOrder createFromParcel(Parcel source) {
            return new TradeItOrder(source);
        }

        @Override
        public TradeItOrder[] newArray(int size) {
            return new TradeItOrder[size];
        }
    };
}
