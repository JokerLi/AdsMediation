#接入说明：

权限

    <!-- baidu需要加的权限 六个必需权限  -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    
插屏需要添加Activity

        <!--  接入Baidu必需：将下一行的android:value设置为您在百度union网站申请的AppSid-->
        <meta-data  android:name="BaiduMobAd_APP_ID" android:value="ec65005f" />
            <activity
                android:name="com.baidu.mobads.AppActivity"
                android:configChanges="keyboard|keyboardHidden|orientation"
                android:theme="@android:style/Theme.Translucent.NoTitleBar"	/>
***

混淆脚本

    -keep class com.baidu.** {
            public protected *;
    }