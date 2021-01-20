#接入说明：

权限

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_UPDATES"/>
    
插屏需要添加Activity

        <!-- 接入腾讯gdt必需 -->
        <service android:name="com.qq.e.comm.DownloadService" android:exported="false" />
        <activity android:name="com.qq.e.ads.ADActivity" android:theme="@android:style/Theme.Translucent" />
***

混淆脚本

    -keep class com.qq.e.** {
        public protected *;
    }
    -keep class android.support.v4.app.NotificationCompat**{
        public *;
    }