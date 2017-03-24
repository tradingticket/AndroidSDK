package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TradeItSecurityQuestion implements Parcelable {

    private String securityQuestion = "";
    private List<String>  securityQuestionOptions = new ArrayList<>();

    public TradeItSecurityQuestion(String securityQuestion, List<String> securityQuestionOptions) {
        this.securityQuestion = securityQuestion;
        this.securityQuestionOptions = (securityQuestionOptions != null ? securityQuestionOptions : this.securityQuestionOptions);
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public List<String> getSecurityQuestionOptions() {
        return securityQuestionOptions;
    }

    @Override
    public String toString() {
        return "TradeItSecurityQuestion{" +
                "securityQuestion='" + securityQuestion + '\'' +
                ", securityQuestionOptions=" + securityQuestionOptions +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.securityQuestion);
        dest.writeStringList(this.securityQuestionOptions);
    }

    protected TradeItSecurityQuestion(Parcel in) {
        this.securityQuestion = in.readString();
        this.securityQuestionOptions = in.createStringArrayList();
    }

    public static final Parcelable.Creator<TradeItSecurityQuestion> CREATOR = new Parcelable.Creator<TradeItSecurityQuestion>() {
        @Override
        public TradeItSecurityQuestion createFromParcel(Parcel source) {
            return new TradeItSecurityQuestion(source);
        }

        @Override
        public TradeItSecurityQuestion[] newArray(int size) {
            return new TradeItSecurityQuestion[size];
        }
    };
}
