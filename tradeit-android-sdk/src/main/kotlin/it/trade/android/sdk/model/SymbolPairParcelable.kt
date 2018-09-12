package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

class SymbolPairParcelable(val baseSymbol: String, val quoteSymbol: String) : Parcelable {
    override fun toString(): String {
        return "SymbolPairParcelable(baseSymbol='$baseSymbol', quoteSymbol='$quoteSymbol')"
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(baseSymbol)
        writeString(quoteSymbol)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SymbolPairParcelable> = object : Parcelable.Creator<SymbolPairParcelable> {
            override fun createFromParcel(source: Parcel): SymbolPairParcelable = SymbolPairParcelable(source)
            override fun newArray(size: Int): Array<SymbolPairParcelable?> = arrayOfNulls(size)
        }
    }
}