#Android Trade.it SDK 
Android SDK that wraps the Trade.it Android API (https://github.com/tradingticket/AndroidAPI).

Detailed Trade It API documentation can be found here: https://www.trade.it/api.

The JCenter repo can be found here: https://bintray.com/tradeit/maven/tradeit-android-sdk

For examples usage, see the example app and tests included with the SDK.  

#Quick Start
Add the following dependency in your project:
```
compile 'it.trade.tradeit:tradeit-android-sdk:0.0.6'
```
You have to configure once the sdk in your application.
In order to initialize the configuration, obtain an API key from Trade.it, or test with "tradeit-test-api-key"
Example in the onCreate method of your main application:
```Java
TradeItSDK.configure(this.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA);
```
Get an instance of the TradeItLinkedBrokerManager in your application: 
```Java
TradeItLinkedBrokerManager linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();
```
Query which brokers are available for your key:
```Java
linkedBrokerManager.getAvailableBrokers(new TradeItCallBackImpl<List<TradeItAvailableBrokersResponse.Broker>>() {
    @Override
    public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerList) {
        // a list of broker is returned 
    }
    
    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }
});
```
Link (authorize) a user's broker account: There are several steps to follow:
```Java
// get the oauthURL
linkedBrokerManager.getOAuthLoginPopupUrl("Dummy", "yourSpecificApp://yourSpecificHost", new TradeItCallBackImpl<String>() {
    @Override
    public void onSuccess(String oAuthUrl) {
        // display the url in a webview in order to the user complete his brokerage login. 
    }

    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured 
    }
});
```
Notice the second parameter is a deep link where to redirect back into your app once the user has completed his brokerage login.
Android custom URI schemes are handled in the AndroidManifest.xml file as intents. You’ll be adding an activity similar to this:
```xml
<activity xmlns:android="http://schemas.android.com/apk/res/android"
          android:name="myapp.handleOAuth"
          android:label="MyApp">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
         <data android:scheme="yourSpecificApp"
                            android:host="yourSpecificHost" />
    </intent-filter>
</activity>

```
Then the oAuthFlow will use your callback like: yourSpecificAppName://yourSpecificHost?oAuthVerifier=370676d4-f584-4d71-8c37-432aa8948059
When your deep link is called, you have to get the oAuthVerifier from your intent:
```Java
String oAuthVerifier = intent.getData().getQueryParameter("oAuthVerifier");
```
And the last step is to link the broker thanks to the oAuthVerifier. 
```Java
linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", "Dummy", oAuthVerifier, new TradeItCallBackImpl<TradeItLinkedBroker>() {
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
To get the linked brokers:
```Java
linkedBrokerManager.getLinkedBrokers();
```
To update the credentials of a linked broker, you have to follow the same steps as if it was a new link, but call the getOAuthLoginPopupForTokenUpdateUrl method in the first step with the linked broker to relink:
```Java
linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl(linkedBroker, "yourSpecificApp://yourSpecificHost", new TradeItCallBackImpl<String>() {
     @Override
     public void onSuccess(String oAuthUrl) {
        // display the url in a webview in order to the user complete his brokerage login.
     }
     
     @Override
     public void onError(TradeItErrorResult error) {
         // an error occured
     }
});
```
To unlink a broker:
```Java
linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallBackImpl<TradeItResponse>() {
    @Override
    public void onSuccess(TradeItResponse response) {
        //successfully unlink the broker
    }
    
    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured    
    }
});
```
To authenticate a linkedBroker:
```Java
linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
    @Override
    public void onSuccess(final List<TradeItLinkedBrokerAccount> accounts) {
        //the linked broker is successfully authenticated, his accounts are returned
    });
    
    @Override
    public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
        //there is a security question the user need to answer before being authenticated
        
        //to answer the security question call this method:
        this.submitSecurityAnswer("my answer"); // then one of the three callbacks will be called (onSuccess, onSecurityQuestion, onError)
    }

    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }
    
});
```
To get the accounts of a linked broker:
```Java
linkedBroker.getAccounts();
```
To get the balance of an account:
```Java
linkedBrokerAccount.refreshBalance(new TradeItCallBackImpl<TradeItGetAccountOverviewResponse>() {
    @Override
    public void onSuccess(TradeItGetAccountOverviewResponse balance) {
        // refreshes balance successful
    }
    @Override
    public void onError(TradeItErrorResult error) {
       // an error occured
    }
}
```
To get the positions of an account:
```Java
linkedBrokerAccount.refreshPositions(new TradeItCallBackImpl<List<TradeItGetPositionsResponse.Position>>() {
    @Override
    public void onSuccess(List<TradeItGetPositionsResponse.Position> positions) {
        // successfully got the positions 
    }
    
    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }

```
Create a new order:
```Java
TradeItOrder order = new TradeItOrder(linkedBrokerAccount, "GE"); // by default it is a market order, quantity: 1, action: buy, expiration: good for day
order.setAction(TradeItOrderAction.SELL);
order.setPriceType(TradeItOrderPriceType.LIMIT);
order.setsetLimitPrice(20.0);
order.setQuantity(10);
order.setExpiration(TradeItOrderExpiration.GOOD_UNTIL_CANCELED);
```
To preview an order:
```Java
order.previewOrder(new TradeItCallBackImpl<TradeItPreviewStockOrEtfOrderResponse>() {
    @Override
    public void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
        //successfully reviewed the order
    }
    
    @Override
    public void onError(TradeItErrorResult error) {
        //an error occured
    }
});
```
To place an order:
```Java
String orderId = response.orderId; // get the orderId from the previewResponse
order.placeOrder(orderId, new TradeItCallBackImpl<TradeItPlaceStockOrEtfOrderResponse>() {
    @Override
    public void onSuccess(TradeItPlaceStockOrEtfOrderResponse placeOrderResponse) {
        //Successfully placed the order
    }

    @Override
    public void onError(TradeItErrorResult error) {
        // an error occured
    }
});
```