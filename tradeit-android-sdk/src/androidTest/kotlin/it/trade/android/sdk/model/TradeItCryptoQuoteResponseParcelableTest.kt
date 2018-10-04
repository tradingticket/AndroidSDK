package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItCryptoQuoteResponseParcelableTest {
    private var cryptoQuoteResponseParcelable: TradeItCryptoQuoteResponseParcelable? = null

    @Before
    fun createTradeItCryptoQuote() {
        cryptoQuoteResponseParcelable = TradeItCryptoQuoteResponseParcelable()
    }

    @Test
    fun tradeItCryptoQuote_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        cryptoQuoteResponseParcelable!!.ask = BigDecimal(9000.0)
        cryptoQuoteResponseParcelable!!.bid = BigDecimal(7000.0)
        cryptoQuoteResponseParcelable!!.open = BigDecimal(6000.0)
        cryptoQuoteResponseParcelable!!.last = BigDecimal(10000.0)
        cryptoQuoteResponseParcelable!!.volume = BigDecimal(154000.0)
        cryptoQuoteResponseParcelable!!.dateTime = "MyDateTime"
        cryptoQuoteResponseParcelable!!.dayLow = BigDecimal(5000.0)
        cryptoQuoteResponseParcelable!!.dayHigh = BigDecimal(11000.0)

        // Write the data.
        val parcel = Parcel.obtain()
        cryptoQuoteResponseParcelable!!.writeToParcel(parcel, cryptoQuoteResponseParcelable!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItCryptoQuoteResponseParcelable.CREATOR.createFromParcel(parcel)

        // Verify that the received data is correct.
        assertThat(createdFromParcel.ask, `is`(cryptoQuoteResponseParcelable!!.ask))
        assertThat(createdFromParcel.bid, `is`(cryptoQuoteResponseParcelable!!.bid))
        assertThat(createdFromParcel.open, `is`(cryptoQuoteResponseParcelable!!.open))
        assertThat(createdFromParcel.last, `is`(cryptoQuoteResponseParcelable!!.last))
        assertThat(createdFromParcel.volume, `is`(cryptoQuoteResponseParcelable!!.volume))
        assertThat(createdFromParcel.dateTime, `is`(cryptoQuoteResponseParcelable!!.dateTime))
        assertThat(createdFromParcel.dayLow, `is`(cryptoQuoteResponseParcelable!!.dayLow))
        assertThat(createdFromParcel.dayHigh, `is`(cryptoQuoteResponseParcelable!!.dayHigh))
    }
}
