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

#base cmadsdk
# TODO:

#baseapi
-keep interface com.cmcm.baseapi.ads.**{*;}
-keep interface com.cmcm.baseapi.ads.INativeAd{*;}
-keep interface com.cmcm.baseapi.ads.INativeAd$ImpressionListener{*;}
-keep interface com.cmcm.baseapi.ads.INativeAd$IAdOnClickListener{*;}
-keep interface com.cmcm.baseapi.ads.INativeAdLoaderListener{*;}

#native
-keep class com.cmcm.adsdk.nativead.NativeAdManager{*;}
-keep class com.cmcm.adsdk.nativead.NativeAdManagerEx{*;}
-keep class com.cmcm.adsdk.nativead.FeedListAdManager{*;}
-keep interface com.cmcm.adsdk.nativead.FeedListAdManager$*{*;}
-keep class com.cmcm.adsdk.nativead.NativeAdListManager{*;}

# init methods
-keep class com.cmcm.adsdk.CMAdManager {*;}
-keep class com.cmcm.adsdk.CMRequestParams {*;}
-keep class com.cmcm.adsdk.CMAdManagerFactory {*;}
-keep class com.cmcm.adsdk.base.CMBaseNativeAd {*;}
-keep class com.cmcm.adsdk.nativead.CMNativeAd{*;}
-keep interface com.cmcm.adsdk.base.CMBaseNativeAd$* {*;}
-keep interface com.cmcm.adsdk.nativead.INativeAdListListener{*;}
-keep class com.cmcm.adsdk.Const {*;}
-keep class com.cmcm.adsdk.Const$* {*;}
-keep class com.cmcm.adsdk.CMAdError {*;}
-keep class com.cmcm.adsdk.utils.ReportProxy{*;}
#-keep class com.cmcm.adsdk.utils.PerferenceUtil{*;}

#utils & don't need to keep
-keep class com.cmcm.utils.NetworkUtil{*;}
#-keep class com.cmcm.adsdk.config.PosBean{*;}
#-keep class com.cmcm.adsdk.config.ConfigResponse {*;}
#-keep class com.cmcm.adsdk.config.ConfigResponse$AdPosInfo {*;}

#Adapter
-keep class com.cmcm.adsdk.adapter.NativeloaderAdapter {*;}
-keep class * extends com.cmcm.adsdk.adapter.NativeloaderAdapter {*;}
-keep interface com.cmcm.adsdk.adapter.NativeloaderAdapter$NativeAdapterListener{*;}

#banner
-keep interface com.cmcm.adsdk.banner.CMBannerAdListener {*;}
-keep class com.cmcm.adsdk.banner.CMAdView {*;}
-keep class com.cmcm.adsdk.banner.CMBannerAdListener {*;}
-keep enum com.cmcm.adsdk.banner.CMBannerAdSize{*;}
-keep class com.cmcm.adsdk.banner.CMBannerParams{*;}
-keep class com.cmcm.adsdk.banner.CMNativeBannerView {*;}

#interstitial
-keep interface com.cmcm.adsdk.interstitial.InterstitialAdCallBack {*;}
-keep class com.cmcm.adsdk.interstitial.InterstitialAdManager {*;}
-keep class com.cmcm.adsdk.adapter.FacebookInterstitialAdapter{ *; }

#unifiedreport
#-keep class com.cmcm.adsdk.unifiedreport.UnifiedReporter {*;}
-keep class com.cmcm.utils.offerreport.OfferReport{
        <fields>;
        <methods>;
}

#other listener
-keep interface com.cmcm.adsdk.BitmapListener {*;}
-keep interface com.cmcm.adsdk.ImageDownloadListener {*;}

#picks version
-keep class com.cmcm.picks.PicksMessageHelper{*;}


###############################################################################
#Picks Native Ad
#TODO:
-keep class  com.cmcm.utils.Commons{*;}

-keep class com.cmcm.picks.internal.loader.Ad{*;}
-keep class com.cmcm.picks.init.PicksMob{*;}
#-keep interface  com.cmcm.picks.init.IPicksBrowserCallBack{*;}
#-keep interface  com.cmcm.picks.init.ICallBack{*;}
-keep class com.cmcm.picks.market.MarketUtils{*;}
#-keep class com.cmcm.adsdk.report.ReportFactory{*;}


#-keep enum com.cmcm.adsdk.InternalAdError{*;}
#-keep class com.cmcm.utils.ViewShowReporter{*;}
#-keep class com.cmcm.utils.ViewShowReporter$Model{*;}
#-keep class com.cmcm.adsdk.config.RequestUFS{*;}

#CMAdView Render
-keep class com.cmcm.adsdk.view.CMMediaView{*;}
-keep class com.cmcm.adsdk.CMNativeAdTemplate{*;}
-keep class com.cmcm.adsdk.CMNativeAdTemplate$Builder{*;}
-keep class com.cmcm.adsdk.CMNativeAdTemplate$ViewHolder{*;}
-keep interface com.cmcm.adsdk.CMNativeAdTemplate$ICMNativeAdViewAdapter{*;}

# native splash ad
-keep class com.cmcm.adsdk.splashad.NativeSplashAd{*;}
-keep class com.cmcm.adsdk.splashad.NativeSplashAdView{*;}
-keep interface com.cmcm.adsdk.splashad.NativeSplashAd$SplashAdListener{*;}

#common class
-keep class com.cmcm.adsdk.config.PosBean{*;}
-keep class com.cmcm.utils.Commons{*;}

-keep class com.cmcm.utils.internal.report.Reporter{*;}