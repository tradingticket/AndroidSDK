package it.trade.android.exampleapp;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.model.RequestCookieProviderParcelable;
import okhttp3.Cookie;

public class RequestCookieProviderParcelableImpl extends RequestCookieProviderParcelable {
        @Override
        public List<Cookie> provideCookies() {
            List<Cookie> cookies = new ArrayList<>();
            Cookie cookie1 = new Cookie.Builder().name("test1").value("value1").domain("mydomain").build();
            Cookie cookie2 = new Cookie.Builder().name("test2").value("value2").domain("mydomain").build();
            cookies.add(cookie1);
            cookies.add(cookie2);
            return cookies;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public RequestCookieProviderParcelableImpl() {
        }

        protected RequestCookieProviderParcelableImpl(Parcel in) {
        }

        public static final Creator<RequestCookieProviderParcelableImpl> CREATOR = new Creator<RequestCookieProviderParcelableImpl>() {
            @Override
            public RequestCookieProviderParcelableImpl createFromParcel(Parcel source) {
                return new RequestCookieProviderParcelableImpl(source);
            }

            @Override
            public RequestCookieProviderParcelableImpl[] newArray(int size) {
                return new RequestCookieProviderParcelableImpl[size];
            }
        };
    }