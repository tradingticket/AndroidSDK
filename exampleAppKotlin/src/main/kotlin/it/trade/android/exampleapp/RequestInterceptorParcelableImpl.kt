package it.trade.android.exampleapp

import android.os.Parcel
import android.os.Parcelable

import java.io.IOException

import it.trade.android.sdk.model.RequestInterceptorParcelable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RequestInterceptorParcelableImpl : RequestInterceptorParcelable {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val transformedRequest = originalRequest.newBuilder()
                .header("HeaderTestName", "HeaderTestValue")
                .header("HeaderTestName2", "HeaderTestValue2")
                .header("Cookie", "test1=value1; test2=value2")
                .method(originalRequest.method(), originalRequest.body())
                .build()
        return chain.proceed(transformedRequest)
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    constructor() {}

    protected constructor(`in`: Parcel) {}

    companion object {

        val CREATOR: Parcelable.Creator<RequestInterceptorParcelableImpl?> = object : Parcelable.Creator<RequestInterceptorParcelableImpl?> {
            override fun createFromParcel(source: Parcel): RequestInterceptorParcelableImpl {
                return RequestInterceptorParcelableImpl(source)
            }

            override fun newArray(size: Int): Array<RequestInterceptorParcelableImpl?> {
                return arrayOfNulls(size)
            }
        }
    }
}
