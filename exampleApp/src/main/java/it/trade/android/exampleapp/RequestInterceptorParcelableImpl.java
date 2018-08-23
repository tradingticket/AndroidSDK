package it.trade.android.exampleapp;

import android.os.Parcel;

import java.io.IOException;

import it.trade.android.sdk.model.RequestInterceptorParcelable;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptorParcelableImpl extends RequestInterceptorParcelable {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request transformedRequest = originalRequest.newBuilder()
                .header("HeaderTestName", "HeaderTestValue")
                .header("HeaderTestName2", "HeaderTestValue2")
                .header("Cookie", "test1=value1; test2=value2")
                .method(originalRequest.method(), originalRequest.body())
                .build();
        return chain.proceed(transformedRequest);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public RequestInterceptorParcelableImpl() {
    }

    protected RequestInterceptorParcelableImpl(Parcel in) {
    }

    public static final Creator<RequestInterceptorParcelableImpl> CREATOR = new Creator<RequestInterceptorParcelableImpl>() {
        @Override
        public RequestInterceptorParcelableImpl createFromParcel(Parcel source) {
            return new RequestInterceptorParcelableImpl(source);
        }

        @Override
        public RequestInterceptorParcelableImpl[] newArray(int size) {
            return new RequestInterceptorParcelableImpl[size];
        }
    };
}
