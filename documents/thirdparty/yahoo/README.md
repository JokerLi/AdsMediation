#接入说明：

权限

    <uses-permission android:name="android.permission.INTERNET" /> 
    <!--optional permission - highly recommended-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> 
    <!--optional permission -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
***

混淆脚本

	-keep class com.cmcm.adsdk.nativead.FlurryNativeLoader{
        <fields>;
        <methods>;
     }
	-keep class com.flurry.** { *; }
	-dontwarn com.flurry.**
	-keepattributes *Annotation*,EnclosingMethod
	-keep class com.google.android.gms.ads.** { *;}
	-dontwarn com.google.android.gms.ads.**
	-keep class com.google.android.gms.**