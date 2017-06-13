# Android Trade.it SDK 
Android SDK that wraps the Trade.it Java API (https://github.com/tradingticket/JavaApi).

Detailed Trade It API documentation can be found here: https://www.trade.it/api.

The JCenter repo can be found here: https://bintray.com/tradeit/maven/tradeit-android-sdk

For examples usage, see the example app and tests included with the SDK.  

# Quick Start

Add the following dependency to your project:
```
compile 'it.trade.tradeit:tradeit-android-sdk:1.0.0'
```

### Configuring the SDK

To use the SDK you will first need to call it's configuration method. You will also need to obtain an API key from https://trade.it or test with "tradeit-test-api-key".

Example in the onCreate method of your main application:

```Java
public class MainActivity extends AppCompatActivity {
    ...
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        TradeItSDK.configure(
            this.getApplicationContext(),
            "tradeit-test-api-key",
            TradeItEnvironment.QA
        );
        ....
    }
    ....
```

Interacting with the TradeIt API is done via `TradeItLinkedBroker` and `TradeItLinkedBrokerAccount`objects managed by the `TradeItLinkedBrokerManager` instance on the `TradeItSDK`: 

```Java
TradeItLinkedBrokerManager linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();
```

### Linking a user's broker

First the user must be prompted to choose which broker they wish to link. To retrieve the list of available brokers:

```Java
linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<TradeItAvailableBrokersResponse.Broker>>() {
    @Override
    public void onSuccess(List<Broker> brokerList) {
        // a list of broker is returned 
    }
    
    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }
});
```

Once a broker has been selected, link the user's broker account by sending the user to the OAuth login URL. 

```Java
// get the oauthURL
linkedBrokerManager.getOAuthLoginPopupUrl(
    "Dummy",
    "yourScheme://yourHost", // This URL is a deep link into your app that will be used to complete OAuth. More info below...
    new TradeItCallback<String>() {
        @Override
        public void onSuccess(String oAuthUrl) {
            // launch the OAuth page by loading the url in a webview 
        }

        @Override
        public void onError(TradeItErrorResult error) {
            // an error occured 
        }
    }
);
```

The second parameter is an Android deep link URL that should redirect back into your app once the user has completed their OAuth login.
Android custom URI schemes are handled in the `AndroidManifest.xml` file as intents. Here is an example activity:
```xml
<activity xmlns:android="http://schemas.android.com/apk/res/android"
          android:name="myapp.handleOAuth"
          android:label="MyApp">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="yourScheme"
              android:host="yourHost" />
    </intent-filter>
</activity>

```
For more info on configuring deep linking see the documentation: https://developer.android.com/training/app-indexing/deep-linking.html

If successful, the OAuthFlow will redirect to your deep link URL and append the `oAuthVerifier` token as a query parameter: `yourScheme://yourHost?oAuthVerifier=370676d4-f584-4d71-8c37-432aa8948059`

The `oAuthVerifier` can be parsed out like this:

```Java
String oAuthVerifier = intent.getData().getQueryParameter("oAuthVerifier");
```

The last step is to complete the broker linking by submitting the oAuthVerifier.

```Java
linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", oAuthVerifier, new TradeItCallback<TradeItLinkedBroker>() {
    @Override
    public void onSuccess(TradeItLinkedBroker linkedBroker) {
        // successfully linked broker
    }

    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }
});
```

This will result in the creation of a `TradeItLinkedBroker` object. The linked broker object is automatically persisted in the Android secure keystore and added to the list of linked brokers on the linked broker manager:

```Java
List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
```

When the user restarts the app, the list of linked brokers is automatically repopulated upon initialization and configuration of the `TradeItSDK`.

### Relinking

To update the credentials of a linked broker, follow the same steps as linking except use the `getOAuthLoginPopupForTokenUpdateUrl` method instead of the `getOAuthLoginPopupUrl` method:
```Java
linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl(
    linkedBroker,
    "yourScheme://yourHost",
    new TradeItCallback<String>() {
        @Override
        public void onSuccess(String oAuthUrl) {
            // display the url in a webview in order to the user complete his brokerage login.
        }

        @Override
        public void onError(TradeItErrorResult error) {
            // an error occured
        }
    }
);
```

### Unlinking

To unlink a broker:

```Java
linkedBrokerManager.unlinkBroker(
    linkedBroker,
    new TradeItCallback<TradeItResponse>() {
        @Override
        public void onSuccess(TradeItResponse response) {
            //successfully unlink the broker
        }

        @Override
        public void onError(TradeItErrorResult error) {
            // an error occured    
        }
    }
);
```

### Authenticating

Once a broker is linked via OAuth, it needs to be authenticated to perform actions on behalf of the user. Successfully authenticating will result in a session (that will time out after 15 minutes of inactivity). Obtain a session by calling `authenticate`:

```Java
linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
    @Override
    public void onSuccess(final List<TradeItLinkedBrokerAccountParcelable> accounts) {
        // The linked broker is successfully authenticated and the accounts associated with that broker login are populated on the linked broker object (and returned to the callback)
    });
    
    @Override
    public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
        // Sometimes there is a security question the user need to answer before being authenticated
        
        // Prompt the user for an answer and submit the answer like this
        this.submitSecurityAnswer("my answer"); // then one of the three callbacks will be called (onSuccess, onSecurityQuestion, onError)
        
        //or to cancel the security question
        this.cancelSecurityQuestion();
    }

        @Override
        public void onError(TradeItErrorResult error) {
            // an error occured
        }
    }
);
```

Use the linked broker accounts to perform actions on behalf of the user:

```Java
List<TradeItLinkedBrokerAccount> accounts = linkedBroker.getAccounts();
accounts.get(0).refreshPositions(...);
```

See below for more examples of actions that can be performed on a linked broker account.

### Balance info

To get the balance info of an account:

```Java
linkedBrokerAccount.refreshBalance(new TradeItCallback<TradeItBalance>() {
    @Override
    public void onSuccess(TradeItBalance balance) {
        // refreshes balance successful
    }
    @Override
    public void onError(TradeItErrorResult error) {
       // an error occured
    }
}
```

### Portfolio positions

To get the portfolio positions of an account:

```Java
linkedBrokerAccount.refreshPositions(new TradeItCallback<List<TradeItPositionParcelable>>() {
    @Override
    public void onSuccess(List<TradeItPositionParcelable> positions) {
        // successfully fetched positions
    }
    
    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }

```

### Trading

First create a `TradeItOrder` object to encapsulate the user's intended trade:

```Java
TradeItOrderParcelable order = new TradeItOrderParcelable(linkedBrokerAccount, "GE"); // by default it is a market order, quantity: 1, action: buy, expiration: good for day
order.setAction(TradeItOrderAction.SELL);
order.setPriceType(TradeItOrderPriceType.LIMIT);
order.setsetLimitPrice(20.0);
order.setQuantity(10);
order.setExpiration(TradeItOrderExpiration.GOOD_UNTIL_CANCELED);
```

Then preview the order:

```Java
order.previewOrder(
    new TradeItCallback<TradeItPreviewStockOrEtfOrderResponse>() {
        @Override
        public void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
            // Present the order preview info to the user and prompt them to submit the trade
        }

        @Override
        public void onError(TradeItErrorResult error) {
            //an error occured
        }
    }
);
```

Finally, submit the order:

```Java
String orderId = response.orderId; // get the orderId from the previewResponse
order.placeOrder(
    orderId,
    new TradeItCallback<TradeItPlaceStockOrEtfOrderResponse>() {
        @Override
        public void onSuccess(TradeItPlaceStockOrEtfOrderResponse placeOrderResponse) {
            // Successfully placed the order. Display returned order info to the user.
        }

        @Override
        public void onError(TradeItErrorResult error) {
            // an error occured
        }
    }
);
```

# Special Cases

### Custom server host and cookies

To point the SDK at a custom host and inject cookies on each request, implement the `RequestCookieProviderParcelable` and the `provideCookies` method (see `RequestCookieProviderParcelableImpl` for example), and then initialize the SDK like this:

```Java
TradeItSDK.configure(
    this.getApplicationContext(),
    "tradeit-test-api-key",
    TradeItEnvironment.QA,
    "https://mycustomhost.com/some/path/",
    new RequestCookieProviderParcelableImpl()
);
```
