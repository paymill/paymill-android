![PAYMILL icon](https://static.paymill.com/r/335f99eb3914d517bf392beb1adaf7cccef786b6/img/logo-download_Light.png)
# PAYMILL Android SDK

The Android SDK provides a flexible and easy to integrate payment solution for your Android applications.

## Sample App


<a href="https://play.google.com/store/apps/details?id=com.paymill.android.samples.vouchermill">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

Our open source sample / demo app [VoucherMill](/samples/vouchermill) is available for download on Google Play. 
 
## Getting started

- Start with the [SDK guide](https://www.paymill.com/en-gb/documentation-3/reference/mobile-sdk/).
- Install the latest release.
- If you want to create transaction and preauthorizations directly from within your app, [install](https://paymill.com/mobile-app-install/) the PAYMILL mobile app.
- Check the sample / demo app [VoucherMill](/samples/vouchermill) for a showcase and stylable payment screens.
- Check the [full API documentation](http://paymill.github.io/paymill-android/docs/sdk/).

## Requirements

Android 2.2 (API Level 8).

## Installation

- Eclipse users add the `androi-sdk-1.2.0.jar` to their `libs/` folder.
- Maven users add this dependency to their `pom.xml`:

```xml
<dependency>
	<groupId>com.paymill.android</groupId>
	<artifactId>android-sdk</artifactId>
	<version>1.2.0</version>
</dependency>         
```

- Gradle / Android Studio / IntelliJ add following dependency to their `build.gradle`:

```
apply plugin: 'android'

repositories {
    mavenCentral()
}

dependencies {
   compile 'com.paymill.android:android-sdk:1.1'
}       
```

You will also have to add the following service definition inside the application tag in your `AndroidManifest.xml`:


```xml
 <!-- paymill sdk service -->
 <service android:name="com.paymill.android.service.PMService"
         android:enabled="true" android:exported="false">
 </service> 
```
If you haven't already, you will need to add the `INTERNET` permission  to your `AndroidManifest.xml`:

```
<uses-permission android:name="android.permission.INTERNET" />
```

## Working with the SDK


### Listeners

The class [PMManager](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/service/PMManager.html)exposes all the functionalities of the SDK trough static methods. The methods are asynchronous, return immediately and are thread-safe. Before calling a method, you should register a listener to receive the result. There are two types of listeners:

- **Foreground Listeners** can be added with the `addListener()` methods. Their callbacks are executed on the UI Thread and it is safe to modify UI elements from the callbacks.
- **A BackgroundListener** is set with the `setBackgroundListener()` method. The callbacks are executed on a separate Thread. Use if for long running and/or critical operations. The execution of the callback holds the PMService in foreground mode, so make sure you return from the callback method at some point.

A typical app may take advantage of both listeners, using a foreground listener to inform the user of sucessfull transaction, while using the background listener for network communication, database queries, etc.

**Important:** The SDK holds a strong references to both types of listeners. To not leak resources, make sure you remove listeners when you don't need them any longer. A good practice is to add your listener onCreate() and remove it onDestroy().


### PMMethod, PMParams and PMFactory


A [PMPaymentMethod](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/factory/PMPaymentMethod.html) object contains the credit card or bank account information of a customer. A [PMPaymentParams](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/factory/PMPaymentParams.html) object contains the parameters of a payment - amount, currency, description. Both must always be created with the [PMFactory](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/factory/PMFactory.html)class.

### Generate a token

Create [PMPaymentMethod](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/factory/PMPaymentMethod.html) and [PMPaymentParams](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/factory/PMPaymentParams.html), add listeners and call [PMManager.generateToken()](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/service/PMManager.html#generateToken%28android.content.Context,%20com.paymill.android.factory.PMPaymentMethod,%20com.paymill.android.factory.PMPaymentParams,%20com.paymill.android.service.PMService.ServiceMode,%20java.lang.String%29) with your PAYMILL public key and mode.

```java
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  PMManager.addListener(listener);
}

PMGenerateTokenListener listener = new PMGenerateTokenListener() {
  public void onGenerateTokenFailed(PMError error) {
    Log.e("PM", "Error:" + error.toString());
  }

  public void onGenerateToken(String token) {
    Log.d("PM", "Token:" + token);
  }
};

public void test() {
  // the payment method ( cc or dd data)
  PMPaymentMethod method = PMFactory.genCardPayment("Max Mustermann", "4111111111111111", "12", "2015", "1234");
  // the payment parameters (currency, amount, description)
  PMPaymentParams params = PMFactory.genPaymentParams("EUR", 100, null);
  PMManager.generateToken(getApplicationContext(), method, params, PMService.ServiceMode.TEST, "yourpublickey");
}

protected void onDestroy() {
  super.onDestroy();
  PMManager.removeListener(listener);
}
```
### Create a transaction

To create transactions and preauthorizations directly from the SDK you first need to install the Mobile App. In the code you will have to initialize the SDK, by calling [PMManager.init()](http://paymill.github.io/paymill-android/docs/sdk//reference/com/paymill/android/service/PMManager.html#init(android.content.Context, com.paymill.android.service.PMService.ServiceMode, java.lang.String, com.paymill.android.listener.PMBackgroundListener, java.lang.String) method with your PAYMILL public key and mode.

```java
// init the sdk as soon as possible
PMManager.init(getApplicationContext(), PMService.ServiceMode.TEST, "yourpublickey",null, null);
......
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  PMManager.addListener(listener);
}
PMTransListener listener= new PMTransListener() {
  public void onTransactionFailed(PMError error) {
    Log.e("PM", "Error:" + error.toString());
  }

  public void onTransaction(Transaction transaction) {
    Log.d("PM", "Transaction:" + transaction.getId());
  }
};
public void test() {
  // the payment method ( cc or dd data)
  PMPaymentMethod method = PMFactory.genCardPayment("Max Mustermann", "4111111111111111", "12", "2015", "1234");
  // the payment parameters (currency, amount, description)
  PMPaymentParams params = PMFactory.genPaymentParams("EUR", 100, null);
  // add the listener
  PMManager.addListener(listener);
  // trigger the transaction
  PMManager.transaction(getApplicationContext(), method, params, false);
}
protected void onDestroy() {
  super.onDestroy();
  PMManager.removeListener(listener);
}
```


## Release notes

### 1.2.0

+ Added new methods to create transactions and preauthorizations with a payment object.
+ Added a Safe Store to securely save payment objects with a user password.
+ Added the possibility to turn off or style the foreground notification.

### 1.1.1

* Mandatory changes in infrastructure

### 1.1
+ Added new method to generate Payments using IBAN and BIC in the [PMFactory](http://paymill.github.io/paymill-android/docs/sdk/reference/com/paymill/android/factory/PMFactory.html) .
+ Generating a token is now also possible without PMParams (using the new methods or just replacing with null).
* Improved error handling and added additional BRIDGE error type in PMError. You can use this to give the user conrecte information, why his card is rejected.

### 1.0
+ First live release.
+ Added the possiblity to generate tokens without initializing the SDK. The method can be used exactly like the JS-Bridge and does not require extra activation for mobile.
+ Added getVersion for the SDK.
* Bug fixes
