package it.trade.android.exampleapp;

import android.os.Parcel;

import java.util.HashSet;
import java.util.Set;

import it.trade.android.sdk.model.RequestCookieProviderParcelable;
import it.trade.model.Cookie;

public class RequestCookieProviderParcelableImpl extends RequestCookieProviderParcelable {
        @Override
        public Set<Cookie> provideCookies() {
            Set<Cookie> cookies = new HashSet<>();
            cookies.add(new Cookie("atest1", "cookie1"));
            cookies.add(new Cookie("atest2", "cookie2"));
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