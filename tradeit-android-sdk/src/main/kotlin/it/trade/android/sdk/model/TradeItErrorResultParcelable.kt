package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.TradeItErrorResult
import it.trade.model.reponse.TradeItErrorCode

class TradeItErrorResultParcelable : TradeItErrorResult, Parcelable {

    constructor(errorResult: TradeItErrorResult) {
        this.shortMessage = errorResult.shortMessage
        this.longMessages = errorResult.longMessages
        this.systemMessage = errorResult.systemMessage
        this.errorCode = errorResult.errorCode
        this.httpCode = errorResult.httpCode
    }

    constructor(throwable: Throwable) {
        this.systemMessage = throwable.message
    }

    constructor(
        errorCode: TradeItErrorCode,
        shortMessage: String,
        longMessages: List<String>
    ) : super(errorCode, shortMessage, longMessages) {}

    constructor(title: String, message: String) : super(title, message) {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (this.errorCode == null) -1 else this.errorCode.ordinal)
        dest.writeString(this.shortMessage)
        dest.writeStringList(this.longMessages)
        dest.writeString(this.systemMessage)
        dest.writeInt(this.httpCode)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        val tmpErrorCode = `in`.readInt()
        this.errorCode = if (tmpErrorCode == -1) null else TradeItErrorCode.values()[tmpErrorCode]
        this.shortMessage = `in`.readString()
        this.longMessages = `in`.createStringArrayList()
        this.systemMessage = `in`.readString()
        this.httpCode = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItErrorResultParcelable> = object : Parcelable.Creator<TradeItErrorResultParcelable> {
            override fun createFromParcel(source: Parcel): TradeItErrorResultParcelable {
                return TradeItErrorResultParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItErrorResultParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
