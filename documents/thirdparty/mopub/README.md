#接入说明：

权限

    <uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

设置 Activity

	<activity android:name="com.mopub.mobileads.MoPubActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
	<activity android:name="com.mopub.mobileads.MraidActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
	<activity android:name="com.mopub.common.MoPubBrowser"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
	<activity android:name="com.mopub.mobileads.MraidVideoPlayerActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
***

混淆脚本

	-keep class com.mopub.**{*;}
    -dontwarn com.mopub.**