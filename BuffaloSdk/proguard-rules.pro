# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Dev/android-dev/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
#keep Signature
-keepattributes Signature
-dontpreverify

-ignorewarnings

# Preserve all public applications.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

#Generate Regular PacageName
-dontusemixedcaseclassnames

#keep Annotation
-keepattributes *Annotation*

#keep InnerClasses name
-keepattributes InnerClasses


-keepattributes Exceptions,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

#keep parameter name
-keepparameternames


-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

-keep class **.R$* {
    public static <fields>;
}



# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

###############################################################################
# Your application may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

# For mediation
-keepattributes *Annotation*

#baseapi
-keep interface com.buffalo.baseapi.ads.**{*;}
-keep interface com.buffalo.baseapi.ads.INativeAd{*;}
-keep interface com.buffalo.baseapi.ads.INativeAd$ImpressionListener{*;}
-keep interface com.buffalo.baseapi.ads.INativeAd$IAdOnClickListener{*;}
-keep interface com.buffalo.baseapi.ads.INativeAdLoaderListener{*;}

#native
-keep class com.buffalo.adsdk.nativead.NativeAdManager{*;}
-keep class com.buffalo.adsdk.nativead.NativeAdManagerEx{*;}
-keep class com.buffalo.adsdk.nativead.FeedListAdManager{*;}
-keep interface com.buffalo.adsdk.nativead.FeedListAdManager$*{*;}
-keep class com.buffalo.adsdk.nativead.NativeAdListManager{*;}

# init methods
-keep class com.buffalo.adsdk.AdManager {*;}
-keep class com.buffalo.adsdk.RequestParams {*;}
-keep class com.buffalo.adsdk.NativeAdManagerFactory {*;}
-keep class com.buffalo.adsdk.base.BaseNativeAd {*;}
-keep class com.buffalo.adsdk.nativead.NativeAd{*;}
-keep interface com.buffalo.adsdk.base.BaseNativeAd$* {*;}
-keep interface com.buffalo.adsdk.nativead.INativeAdListListener{*;}
-keep class com.buffalo.adsdk.Const {*;}
-keep class com.buffalo.adsdk.Const$* {*;}
-keep class com.buffalo.adsdk.NativeAdError {*;}
-keep class com.buffalo.adsdk.utils.ReportProxy{*;}
#-keep class com.buffalo.adsdk.utils.PreferenceUtil{*;}

#utils & don't need to keep
-keep class com.buffalo.utils.NetworkUtil{*;}
#-keep class com.buffalo.adsdk.config.PosBean{*;}
#-keep class com.buffalo.adsdk.config.ConfigResponse {*;}
#-keep class com.buffalo.adsdk.config.ConfigResponse$AdPosInfo {*;}

#Adapter
-keep class com.buffalo.adsdk.adapter.NativeloaderAdapter {*;}
-keep class * extends com.buffalo.adsdk.adapter.NativeloaderAdapter {*;}
-keep interface com.buffalo.adsdk.adapter.NativeloaderAdapter$NativeAdapterListener{*;}

#banner
-keep interface com.buffalo.adsdk.banner.BannerAdListener {*;}
-keep class com.buffalo.adsdk.banner.BannerAdView {*;}
-keep class com.buffalo.adsdk.banner.BannerAdListener {*;}
-keep enum com.buffalo.adsdk.banner.BannerAdSize{*;}
-keep class com.buffalo.adsdk.banner.BannerParams{*;}
-keep class com.buffalo.adsdk.banner.CMNativeBannerView {*;}

#interstitial
-keep interface com.buffalo.adsdk.interstitial.InterstitialAdCallBack {*;}
-keep class com.buffalo.adsdk.interstitial.InterstitialAdManager {*;}
-keep class com.buffalo.adsdk.adapter.FacebookInterstitialAdapter{ *; }

#unifiedreport
#-keep class com.buffalo.adsdk.unifiedreport.UnifiedReporter {*;}
-keep class com.buffalo.utils.offerreport.OfferReport{
        <fields>;
        <methods>;
}

#other listener
-keep interface com.buffalo.adsdk.BitmapListener {*;}
-keep interface com.buffalo.adsdk.ImageDownloadListener {*;}

#picks version
-keep class com.buffalo.picks.PicksMessageHelper{*;}


###############################################################################
#Picks Native Ad
#TODO:
-keep class  com.buffalo.utils.Commons{*;}

-keep class com.buffalo.picks.internal.loader.Ad{*;}
-keep class com.buffalo.picks.init.PicksMob{*;}
#-keep interface  com.buffalo.picks.init.IPicksBrowserCallBack{*;}
#-keep interface  com.buffalo.picks.init.ICallBack{*;}
-keep class com.buffalo.picks.market.MarketUtils{*;}
#-keep class com.buffalo.adsdk.report.ReportFactory{*;}


#-keep enum com.buffalo.adsdk.InternalAdError{*;}
#-keep class com.buffalo.utils.ViewShowReporter{*;}
#-keep class com.buffalo.utils.ViewShowReporter$Model{*;}
#-keep class com.buffalo.adsdk.config.RequestUFS{*;}

#CMAdView Render
-keep class com.buffalo.adsdk.view.NativeMediaView{*;}
-keep class com.buffalo.adsdk.NativeAdTemplate{*;}
-keep class com.buffalo.adsdk.NativeAdTemplate$Builder{*;}
-keep class com.buffalo.adsdk.NativeAdTemplate$ViewHolder{*;}
-keep interface com.buffalo.adsdk.NativeAdTemplate$INativeAdViewAdapter{*;}

# native splash ad
-keep class com.buffalo.adsdk.splashad.NativeSplashAd{*;}
-keep class com.buffalo.adsdk.splashad.NativeSplashAdView{*;}
-keep interface com.buffalo.adsdk.splashad.NativeSplashAd$SplashAdListener{*;}

#common class
-keep class com.buffalo.adsdk.config.PosBean{*;}
-keep class com.buffalo.utils.Commons{*;}