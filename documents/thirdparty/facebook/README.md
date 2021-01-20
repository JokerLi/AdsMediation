#接入说明：

混淆脚本

    -dontwarn com.facebook.ads.**
    -keep class com.facebook.ads.**{*;}


插屏需要添加Activity

    <activity android:name="com.facebook.ads.InterstitialAdActivity"
         android:configChanges="keyboardHidden|orientation|screenSize" />


开发者接入官网

    https://developers.facebook.com/docs/audience-network/android/native-api