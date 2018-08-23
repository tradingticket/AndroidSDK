package it.trade.android.sdk.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

import it.trade.api.TradeItApi
import it.trade.api.TradeItApiClient
import it.trade.model.request.TradeItEnvironment
import it.trade.model.request.TradeItRequestWithKey

class TradeItApiClientParcelable : TradeItApiClient, Parcelable {

    var requestInterceptorParcelable: RequestInterceptorParcelable? = null
        private set

    @JvmOverloads constructor(apiKey: String, environment: TradeItEnvironment, requestInterceptorParcelable: RequestInterceptorParcelable? = null) : super(apiKey, environment, requestInterceptorParcelable, forceTLS12()) {
        this.requestInterceptorParcelable = requestInterceptorParcelable
    }

    protected constructor(tradeItApi: TradeItApi) : super(tradeItApi) {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(this.requestInterceptorParcelable, flags)
        dest.writeString(this.serverUuid)
        dest.writeString(this.sessionToken)
        dest.writeInt(if (this.environment == null) -1 else this.environment.ordinal)
        dest.writeString(this.apiKey)
    }

    protected constructor(`in`: Parcel) {
        this.requestInterceptorParcelable = `in`.readParcelable(RequestInterceptorParcelable::class.java!!.getClassLoader())
        this.serverUuid = `in`.readString()
        this.sessionToken = `in`.readString()
        val tmpEnvironment = `in`.readInt()
        this.environment = if (tmpEnvironment == -1) null else TradeItEnvironment.values()[tmpEnvironment]
        this.apiKey = `in`.readString()
        TradeItRequestWithKey.API_KEY = apiKey
        this.tradeItApi = this.createTradeItApi(environment, requestInterceptorParcelable, forceTLS12())
    }

    companion object {

        val CREATOR: Parcelable.Creator<TradeItApiClientParcelable> = object : Parcelable.Creator<TradeItApiClientParcelable> {
            override fun createFromParcel(source: Parcel): TradeItApiClientParcelable {
                return TradeItApiClientParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItApiClientParcelable> {
                return arrayOfNulls(size)
            }
        }

        private fun forceTLS12(): Boolean {
            return Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22
        }
    }
}
