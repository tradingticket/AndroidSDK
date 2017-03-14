package it.trade.android.sdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import it.trade.tradeitapi.model.TradeItErrorCode;

public class TradeItErrorResult implements Parcelable {


    private TradeItErrorCode errorCode = null;
    private String shortMessage = null;
    private List<String> longMessages = Arrays.asList("Trading is temporarily unavailable. Please try again in a few minutes.");
    private String systemMessage = "Unknown response sent from the server.";
    private int httpCode = 200;

     public TradeItErrorResult(TradeItErrorCode errorCode, String shortMessage, List<String> longMessages) {
        this.errorCode = errorCode;
        this.shortMessage = shortMessage;
        this.longMessages = longMessages;
        this.systemMessage = null;
    }

    public TradeItErrorResult(int httpCode) {
        this.httpCode = httpCode;
        this.systemMessage = "error sending request to ems server";
    }

    public TradeItErrorResult(String title, String message) {
        this.shortMessage = title;
        this.longMessages = Arrays.asList(message);
        this.systemMessage = message;
        this.errorCode = TradeItErrorCode.SYSTEM_ERROR;
    }

    public TradeItErrorResult() {
    }

    public boolean requiresAuthentication() {
        if (errorCode == TradeItErrorCode.SESSION_EXPIRED || errorCode == TradeItErrorCode.BROKER_ACCOUNT_ERROR) {
            return true;
        }
        return false;
    }

    public boolean requiresRelink() {
        if (errorCode == TradeItErrorCode.BROKER_AUTHENTICATION_ERROR || errorCode == TradeItErrorCode.TOKEN_INVALID_OR_EXPIRED) {
            return true;
        }
        return false;
    }

    public TradeItErrorCode getErrorCode() {
        return errorCode;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public List<String> getLongMessages() {
        return longMessages;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setErrorCode(TradeItErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public void setLongMessages(List<String> longMessages) {
        this.longMessages = longMessages;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    @Override
    public String toString() {
        return "TradeItErrorResult{" +
                "errorCode=" + errorCode +
                ", shortMessage='" + shortMessage + '\'' +
                ", longMessages=" + longMessages +
                ", systemMessage='" + systemMessage + '\'' +
                ", httpCode=" + httpCode +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorCode == null ? -1 : this.errorCode.ordinal());
        dest.writeString(this.shortMessage);
        dest.writeStringList(this.longMessages);
        dest.writeString(this.systemMessage);
        dest.writeInt(this.httpCode);
    }

    protected TradeItErrorResult(Parcel in) {
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : TradeItErrorCode.values()[tmpErrorCode];
        this.shortMessage = in.readString();
        this.longMessages = in.createStringArrayList();
        this.systemMessage = in.readString();
        this.httpCode = in.readInt();
    }

    public static final Parcelable.Creator<TradeItErrorResult> CREATOR = new Parcelable.Creator<TradeItErrorResult>() {
        @Override
        public TradeItErrorResult createFromParcel(Parcel source) {
            return new TradeItErrorResult(source);
        }

        @Override
        public TradeItErrorResult[] newArray(int size) {
            return new TradeItErrorResult[size];
        }
    };
}
