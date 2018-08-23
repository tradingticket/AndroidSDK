package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.TradeItSecurityQuestion

class TradeItSecurityQuestionParcelable : TradeItSecurityQuestion, Parcelable {

    constructor(securityQuestion: String, securityQuestionOptions: List<String>) : super(securityQuestion, securityQuestionOptions) {}


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.securityQuestion)
        dest.writeStringList(this.securityQuestionOptions)
    }

    protected constructor(`in`: Parcel) {
        this.securityQuestion = `in`.readString()
        this.securityQuestionOptions = `in`.createStringArrayList()
    }

    companion object {

        val CREATOR: Parcelable.Creator<TradeItSecurityQuestionParcelable> = object : Parcelable.Creator<TradeItSecurityQuestionParcelable> {
            override fun createFromParcel(source: Parcel): TradeItSecurityQuestionParcelable {
                return TradeItSecurityQuestionParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItSecurityQuestionParcelable> {
                return arrayOfNulls(size)
            }
        }
    }
}
