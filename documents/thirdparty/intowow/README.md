#接入说明：

import library

	需要V4的jar包支持需要加入：android-support-v4.jar
***

权限

    <uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.SET_ORIENTATION" />

设置 Activity

	<activity
    	android:name="com.intowow.sdk.WebViewActivity"
        android:configChanges="orientation|screenSize"
        android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >
	</activity>

设置 Receiver

	<receiver android:name="com.intowow.sdk.ScheduleReceiver">
    	<intent-filter >
    	<action android:name="com.intowow.sdk.prefetch"/>
    	</intent-filter>
	</receiver>

设置 meta-data

	<meta-data android:name="CRYSTAL_ID" android:value="申請的CRYSTAL_ID" />
***

混淆脚本

	-keep class com.intowow.sdk.*{ *; }
	-dontwarn com.intowow.sdk.**
	-dontwarn com.intowow.sdk.InterstitialAdActivity
	-keep class com.intowow.sdk.InterstitialAdActivity{ *; }
	-keep interface android.support.v4.app.** { *; }
	-keep class android.support.v4.** { *; }