package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.TradeItCryptoQuoteResponse
import java.math.BigDecimal

class TradeItCryptoQuoteResponseParcelable : Parcelable {

    var ask: BigDecimal? = null
    var bid: BigDecimal? = null
    var last: BigDecimal? = null
    var open: BigDecimal? = null
    var dayHigh: BigDecimal? = null
    var dayLow: BigDecimal? = null
    var volume: BigDecimal? = null
    var dateTime: String? = null

    internal constructor() {}

    internal constructor(cryptoQuote: TradeItCryptoQuoteResponse) {
        this.ask = cryptoQuote.ask
        this.bid = cryptoQuote.bid
        this.last = cryptoQuote.last
        this.open = cryptoQuote.open
        this.dayHigh = cryptoQuote.dayHigh
        this.dayLow = cryptoQuote.dayLow
        this.volume = cryptoQuote.volume
        this.dateTime = cryptoQuote.dateTime
    }

    constructor(parcel: Parcel) {
        ask = parcel.readSerializable() as? BigDecimal
        bid = parcel.readSerializable() as? BigDecimal
        last = parcel.readSerializable() as? BigDecimal
        open = parcel.readSerializable() as? BigDecimal
        dayHigh = parcel.readSerializable() as? BigDecimal
        dayLow = parcel.readSerializable() as? BigDecimal
        volume = parcel.readSerializable() as? BigDecimal
        dateTime = parcel.readString()
    }

    override fun toString(): String {
        return "TradeItCryptoQuoteResponseParcelable(" +
            "ask=$ask, " +
            "bid=$bid, " +
            "last=$last, " +
            "open=$open, " +
            "dayHigh=$dayHigh, " +
            "dayLow=$dayLow, " +
            "volume=$volume, " +
            "dateTime=$dateTime" +
            ")"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(ask)
        parcel.writeSerializable(bid)
        parcel.writeSerializable(last)
        parcel.writeSerializable(open)
        parcel.writeSerializable(dayHigh)
        parcel.writeSerializable(dayLow)
        parcel.writeSerializable(volume)
        parcel.writeString(dateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItCryptoQuoteResponseParcelable> = object : Parcelable.Creator<TradeItCryptoQuoteResponseParcelable> {
            override fun createFromParcel(source: Parcel): TradeItCryptoQuoteResponseParcelable = TradeItCryptoQuoteResponseParcelable(source)
            override fun newArray(size: Int): Array<TradeItCryptoQuoteResponseParcelable?> = arrayOfNulls(size)
        }
    }


}