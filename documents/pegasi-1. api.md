# Pegasi-SDK for Android接入使用文档

- [概述][1]
- [接入前的准备][2]
- [初始化SDK][3]
- [原生广告接入][4]
- [插屏广告接入][5]
- [API接口][6]
- [常见问题][7]
- [错误码][8]
- [联系方式][9]





## <div id ='introduction'>1 概述</div>
本文档描述了Android开发者如何集成Pegasi 产品。 

Pegasi 给开发者提供了管理多个广告平台的Mediation 方案，完成优先级调整和流量分配。目前支持Native(原生广告)、Interstitial （插屏广告）两种广告形式。

Pegasi SDK 包含如下内容

| 文件              | 说明                                   |
| --------------- | ------------------------------------ |
| pegasi_base.aar | 基础天马SDK 版本, 不内置猎户SDK                 |
| pegasi_cn.aar   | 内嵌猎户中文版本的天马SDK                       |
| pegasi_ww.aar   | 内嵌猎户全球版的天马SDK                        |
| thirdparty 目录   | 每个目录包含验证过的第三方广告网络SDK, Adapter 和 对应说明 |
| pegasi.pdf      | 接入说明文档                               |



目前支持的广告网络如下

| 广告源          | KEY  | Const常量             | 方法说明                                     |
| ------------ | ---- | ------------------- | ---------------------------------------- |
| 猎户原生广告       | cm   | KEY_CM              | com.cmcm.adsdk.adapter.PicksNativeAdapter |
| 猎户插屏广告       | cmi  | KEY_CM_INTERSTITIAL | com.cmcm.adsdk.adapter.PicksInterstatialAdapter |
| Facebook原生广告 | fb   | KEY_FB              | com.cmcm.adsdk.adapter.FacebookNativeAdapter |
| Facebook插屏广告 | fbi  | KEY_FB_INTERSTITIAL | com.cmcm.adsdk.adapter.FacebookInterstitialAdapter |
| Admob原生广告    | ab   | KEY_AB              | com.cmcm.adsdk.adapter.AdmobNativeAdapter |
| Admob插屏广告    | abi  | KEY_AB_INTERSTITIAL | com.cmcm.adsdk.adapter.AdmobInterstitialAdapter |
| Mopub原生广告    | mp   | KEY_MP              | com.cmcm.adsdk.adapter.MopubNativeAdapter |
| Yahoo原生广告    | yh   | KEY_YH              | com.cmcm.adsdk.adapter.YahooNativeAdapter |
| 百度原生广告       | bd   | KEY_BD              | com.cmcm.adsdk.adapter.BaiduNativeAdapter |
| 广点通原生广告      | gdt  | KEY_GDT             | com.cmcm.adsdk.adapter.GDTNativeAdapter  |



整个接入过程包括四部分

* 集成准备
* 广告位样式设计 & 研发接入
* 接入测试： 在后台正确配置各广告网络的请求参数和优先级后，确认客户端可以正常展现，点击即可。
* Pegasi 后台维护

整个操作部分将由运营人员张玉静（zhangyujing@cmcm.com）引导完成。如有问题请直接联系运营人员。

NOTE: **如果App 要接入admob 的Native 广告, 由于admob Native 广告的特殊显示要求，要在App 的每个广告位都支持admob **



## <div id ='prepare'>2 集成准备</div>
要接入Pegasi， 需要在Pegasi 后台注册账户， 一个账户可以创建并且管理多个应用。

* 登录Pegasi系统后台（如果没有Pegasi 账户，请联系运营人员申请账号、密码）
* 创建要接入的应用， 获取系统生成的APPID
* 在对应的应用下，创建广告位，获取系统生成的的广告单元ID

开发者可以在创建完广告位中，按照要求配置需要聚合的广告网络信息，并且通过给定eCPM来控制该广告网络的优先级。



## <div id ='init'>3 初始化SDK</div>
**3.1 将Pegasi SDK 添加至您的项目**
将Pegasi sdk 的 zip包中的 cmcmadsdk.aar 复制到项目的 libs 文件夹中，然后按照下面修改 build.gradle：
```gradle
repositories {
  flatDir {
    dirs 'libs'
  }
}
dependencies {
  ...
  compile(name: 'cmcmadsdk', ext: 'aar')
}
```

**3.2 添加相应的权限**

打开 AndroidManifest.xml，配置以下内容：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

如果不添加 INTERNET ， ACCESS_NETWORK_STATE 会导致广告请求失败。

**3.3 SDK 初始化**
请在每个要用到广告的进程的Application 类的onCreate 中初始化SDK。

```java
// 参数 context: application context
// 参数 appid: 从Pegasi-SSP平台分配的应用id
// 参数 isCnVersion: true:国内版 false:海外版(中国为国内，非中国为海外)
CMAdManager.applicationInit(Context context, String appid, boolean isCnVersion);
```
注：
如果第三方广告网络的SDK需要初始化，请在调用Pegasi SDK初始化之前初始化该SDK。

**3.4 添加第三方adaptor**
集成第三方sdk时，拷贝相应的Adapter(zip包adapter目录) 到工程的包com.cmcm.adsdk.adapter 中, 然后通过下面方法给每个广告网络SDK 注册对应adapter。如需要支持Facebook（其他广告网络） 的广告SDK 时，调用下面方法注册adapter。
```java
CMAdManager.addLoaderClass(Const.KEY_FB, "com.cmcm.adsdk.adapter.FacebookNativeAdapter");
```
注: 

1. adapter不能被混淆

2. 如果广告网络不在支持列表, 请与运营人员联系. 


**3.5 代码混淆**

如果程序使用了混淆，请添加以下混淆脚本
```proguard
-dontwarn com.cmcm.**
-keep class  com.cmcm.** { *;}
```

注：请务必按照第三方广告网络的要求添加相应混淆脚本
<br>
## <div id ='Native'>4 原生广告接入</div>
集成后效果如下:
![NativeAd][image-native]

**4.1原生广告接入流程**

#### 4.1.1 确保SDK已经在Application中初始化成功，参考初始化部分

#### 4.1.2 广告加载

```java
NativeAdManager mNativeAdManager;
public void loadAd(View view){
	mNativeAdManager = new NativeAdManager(this, AD_UNIT);
	mNativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
	    @Override
	    public void adLoaded() {
	        showAd();
	    }
	
	    @Override
	    public void adFailedToLoad(int error) {
	        //failed
	        Log.i("nativead","ad failed, code : " + error);
	    }
	
	    @Override
	    public void adClicked(INativeAd iNativeAd) {
	        //adClick
	    }
	});
	
	mNativeAdManager.loadAd();
}
```
​	


#### 4.1.3 广告展现
将显示的View和广告进行绑定，用户点击该View可实现广告的跳转。
1、自定义好自己的广告展现view，并与广告数据绑定。
2、调用ad的registerViewForInteraction。
自定义布局文件


```xml
<RelativeLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	android:id="@+id/native_outer_view"
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:background="@android:color/white">
	<ImageView android:id="@+id/native_icon_image"
	    android:layout_width="50dp"
	    android:layout_height="50dp"
	    android:background="@null"
	    android:layout_alignParentLeft="true"
	    android:layout_alignParentTop="true"
	    android:layout_marginTop="10dp"
	    android:layout_marginLeft="10dp"
	    android:scaleType="fitXY"
	    />
	
	<TextView android:id="@+id/native_title"
	    android:layout_width="match_parent"
	    android:layout_marginLeft="84dp"
	    android:layout_marginTop="32dp"
	    android:layout_height="wrap_content"
	    android:layout_alignParentTop="true"
	    android:layout_alignParentLeft="true"
	    android:textColor="@android:color/darker_gray"
	    android:textStyle="bold" />
	
	<TextView android:id="@+id/native_text"
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:layout_below="@+id/native_icon_image"
	    android:layout_alignParentLeft="true"
	    android:layout_marginLeft="10dp"
	    android:layout_marginTop="10dp"
	    android:textColor="@android:color/darker_gray" />


	<ImageView android:id="@+id/native_main_image"
	    android:layout_width="match_parent"
	    android:layout_height="180dp"
	    android:background="@null"
	    android:layout_marginTop="10dp"
	    android:layout_marginLeft="10dp"
	    android:layout_marginRight="10dp"
	    android:layout_below="@+id/native_text"
	    android:layout_alignParentLeft="true"
	    android:contentDescription="native_main_image"
	    android:scaleType="fitXY"/>
	
	<TextView
	    android:id="@+id/native_cta"
	    android:gravity="center"
	    android:layout_width="match_parent"
	    android:layout_height="35dp"
	    android:text="learn_more"
	    android:textColor="@android:color/black"
	    android:textStyle="bold"
	    android:layout_marginLeft="10dp"
	    android:layout_marginRight="10dp"
	    android:layout_marginTop="10dp"
	    android:layout_marginBottom="10dp"
	    android:layout_below="@+id/native_main_image"
	    android:textSize="14sp"
	    android:layout_alignParentRight="true"
	    android:clickable="true"
	    android:background="#aa0000ff"
	    android:paddingBottom="10dp" />
</RelativeLayout>
```

创建并渲染ad view有两种方式

1. 采用自定义布局方式。将广告内容显示在上述的xml布局，并且绑定

```java
private void showAd() {

    INativeAd ad = mNativeAdManager.getAd();
    View adView = View.inflate(this.getApplicationContext(), R.layout.native_ad_layout, null);
    String iconUrl = ad.getAdIconUrl();
    ImageView iconImageView = (ImageView) adView.findViewById(R.id.native_icon_image);
    VolleyUtil.loadImage(iconImageView, iconUrl);
    
    String mainImageUrl = ad.getAdCoverImageUrl();
    ImageView imageViewMain = (ImageView) adView.findViewById(R.id.native_main_image);
    imageViewMain.setVisibility(View.VISIBLE);
    VolleyUtil.loadImage(imageViewMain, mainImageUrl);
    
    TextView titleTextView = (TextView) adView.findViewById(R.id.native_title);
    TextView bigButton = (TextView) adView.findViewById(R.id.native_cta);
    TextView bodyTextView = (TextView) adView.findViewById(R.id.native_text);
    
    titleTextView.setText(ad.getAdTitle());
    bigButton.setText(ad.getAdCallToAction());
    bodyTextView.setText(ad.getAdBody());
    mAdContainer.removeAllViews();
    mAdContainer.addView(adView);
    ad.registerViewForInteraction(adView);
}
```

2. 采用sdk提供接口。将自定义的xml布局中的id传入sdk，并返回一个渲染好的view

```java
private void showAd() {
    View adView = null;
  	INativeAd ad = mNativeAdManager.getAd();
    CMNativeAdTemplate binder = new CMNativeAdTemplate.Builder(R.layout.native_ad_layout)
                .iconImageId(R.id.native_icon_image)
                .mainImageId(R.id.native_main_image)
                .titleId(R.id.native_title)
                .callToActionId(R.id.native_cta)
                .textId(R.id.native_text)
                .build();
    adView = binder.getBindedView(ad);
  	if(adView == null){
      return;
  	}
    ad.registerViewForInteraction(adView);
}
```

SDK 将自动记录展示并处理点击。请记住，您必须使用 INativeAd 实例注册广告视图才能启用此功能。要使整个视图可以点击，请使用以下命令注册视图：

```java
registerViewForInteraction(View view)
```

在不需要该广告的时候，将广告与之前绑定的View进行解绑,跟据自己页面的生命周期，在合适的时机反注册广告的View。
```java
unregisterView();
```

<br>

**4.2 信息流接入原生广告流程**
信息流中使用原生广告时，建议使用FeedListAdManager，专门针对信息流场景进行了各种优化以提升填充率和广告展现效果，如优化了重复广告的展现、缓存的自动补全等逻辑。
集成后效果如下:
![Native Ad in listview][image-native-list]

#### 4.2.1 确保SDK已经在Application中初始化成功，参考初始化部分
#### 4.2.2 加载广告
```java
private FeedListAdManager mLoader;
private void loadAd() {
	mLoader = new FeedListAdManager(this.getApplicationContext(), "天马广告位id");
	mLoader.loadAds();
}
```
#### 4.2.3 广告展现
参考Native接入部分，更多内容请参考demo中的NativeAdListActivity。
```java
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
    	CMNativeAdTemplate template = null;
    	if(convertView == null || converView.getTag() == null 
    			||!(convertView.getTag() instanceof CMNativeAdTemplate)){
			template = createViewTemplate();
    	}else{
          	template = (CMNativeAdTemplate)convertView.getTag();
    	}   
    	INativeAd ad = getAd();
		convertView = template.getBindedView(ad);
		ad.registerViewForInteraction(convertView);
		convertView.setTag(template);
    	return convertView;
    }
    
	private CMNativeAdTemplate createViewTemplate() {
		return new CMNativeAdTemplate.Builder(R.layout.native_ad_layout)
                .iconImageId(R.id.native_icon_image)
                .mainImageId(R.id.native_main_image)
                .titleId(R.id.native_title)
                .callToActionId(R.id.native_cta)
                .textId(R.id.native_text)
                .build();
    }
```
<br>

**4.3 开屏接入原生广告流程**
开屏中使用原生广告时，使用NativeSplashAd接口。集成后效果如下:
![Native Ad in splash][image-native-splash]

#### 4.3.1 确保SDK已经在Application中初始化成功，参考初始化部分

#### 4.3.2 加载广告

​```java
private NativeSplashAd mNativeSplashAd;
private void loadAd() {
	mNativeSplashAd = new NativeSplashAd(this, "广告位id", new NativeSplashAd.SplashAdListener() {
	@Override
	public void onLoadSuccess() {
	    Log.i("NativeSplashAd", "native splash onLoadSuccess");
	}
	
	@Override
	public void onAdImpression() {
	    Log.i("NativeSplashAd", "native splash onAdImpression");
	}
	
	@Override
	public void onEndAdImpression() {
	    Log.i("NativeSplashAd", "native splash onEndAdImpression");
	}
	
	@Override
	public void onClick() {
	    Log.i("NativeSplashAd", "native splash onClick");
	}
	
	@Override
	public void onSkipClick() {
	    Log.i("NativeSplashAd", "native splash onSkipClick");
	}
	
	@Override
	public void onFailed(int errorCode) {
	    Log.i("NativeSplashAd", "native splash onFailed errorCode = " + errorCode);
	}
});
	mNativeSplashAd.load();
}
```

#### 4.3.3 广告展现

当开屏广告加载成功回调后，用户可以展示开屏广告。
1、判断开屏广告对象是否可用isValid()。
2、可用的话调用ad的createNativeSplashView。
自定义布局文件

​```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
	<RelativeLayout
		android:id="@+id/native_splash_container"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:gravity="center">
	</RelativeLayout>
	
</RelativeLayout>
```

将广告内容显示在上述的xml布局中

```java
private void showAd() {
	mContainer = (RelativeLayout) findViewById(R.id.native_splash_container);
    if (mNativeSplashAd.isValid()) {
            NativeSplashAdView splashView = mNativeSplashAd.createNativeSplashView();
            mContainer.removeAllViews();
            mContainer.addView(splashView);
    }
}
```

<br>

## <div id ='Interstitial'>5 插屏广告接入</div>

集成后效果如下:
![Interstitial Ad][image-interstitial]
**5.1插屏广告接入流程**
#### 5.1.1 初始化
确保SDK已经在Application中初始化成功，参考初始化部分
#### 5.1.2 加载广告
```java
public void load(){
	interstitialAdManager = new InterstitialAdManager(activity, “天马的广告位id”);
	interstitialAdManager.setInterstitialCallBack(new InterstitialAdCallBack() {
	    @Override
	    public void onAdLoadFailed(int errorCode) {
	    }
	    @Override
	    public void onAdLoaded() {
	    }
	    @Override
	    public void onAdClicked() {
	    }
	    @Override
	    public void onAdDisplayed() {
	    }
	    @Override
	    public void onAdDismissed() {
	    }
	});
	interstitialAdManager.loadAd();
}
```
#### 5.1.3 展现插屏广告
```java
if(interstitialAdManager.isReady()) {
	interstitialAdManager.showAd();
}
```

<br>
## <div id ='API'>6 API接口</div>
#### 6.1 原生相关接口
1. com.cmcm.adsdk.nativead.NativeAdManager

| 方法名                                      | 方法说明       |
| ---------------------------------------- | ---------- |
| NativeAdManager(Context context, String AD_UNIT) | 构造方法       |
| setNativeAdListener(INativeAdLoaderListener listener) | 设置加载广告时的回调 |
| loadAd( )                                | 加载广告，并发请求  |
| getAd()                                  | 获取广告内容     |

2. com.cmcm.baseapi.ads.INativeAdLoaderListener

| 方法名                       | 方法说明                |
| ------------------------- | ------------------- |
| onAdLoaded()              | 广告加载成功              |
| adFailedToLoad(int error) | 广告加载失败，error为具体的错误码 |
| adClicked(INativeAd ad)   | 广告点击的回调             |

3. 信息流相关接口FeedListAdManager

| 方法名                                      | 方法说明                          |
| ---------------------------------------- | ----------------------------- |
| loadAds()                                | 触发广告拉取                        |
| getAd()                                  | 获取广告内容                        |
| setFeedListener(FeedListListener feedListener) | 设置广告可用的回调接口（注意：这个接口会有多次回调的情况） |
4. com.cmcm.adsdk.nativead.FeedListAdManager.FeedListListener

| 方法名                     | 方法说明     |
| ----------------------- | -------- |
| onAdsAvailable()        | 缓存池有可用广告 |
| onAdClick(INativeAd ad) | 广告点击的回调  |

5. 原生开屏相关接口NativeSplashAd

| 方法名                                      | 方法说明         |
| :--------------------------------------- | ------------ |
| load()                                   | 触发广告拉取       |
| isValid()                                | 判断当前广告是否可用   |
| createNativeSplashView()                 | 创建广告View     |
| setIsShowCountDownTime(boolean isShowCountDown) | 是否展示广告倒计时和跳过 |
| setShowSpreadSign(boolean isShowSpreadSign) | 是否展示广告标识     |
| setLoadTimeOutMilliSecond(int timeMilliSecond) | 设置广告拉取超时时间   |
| setAdShowTimeSecond(int timeSecond)      | 设置广告展示时间     |

6. com.cmcm.adsdk.splashad.SplashAdListener

| 方法名                 | 方法说明                |
| ------------------- | ------------------- |
| onLoadSuccess()     | 广告加载成功              |
| onAdImpression()    | 广告展示开始              |
| onEndAdImpression() | 广告展示结束              |
| onClick()           | 广告点击的回调             |
| onSkipClick()       | 广告点击跳过的回调           |
| onFailed(int error) | 广告加载失败，error为具体的错误码 |

7. com.cmcm.baseapi.ads.INativeAd

| 方法名                                      | 方法说明                       |
| ---------------------------------------- | -------------------------- |
| getAdtitle()                             | 广告title                    |
| getAdBody()                              | 广告描述                       |
| getAdIconUrl()                           | icon的url                   |
| getAdCoverImageUrl()                     | 大图的url                     |
| getAdCallToAction()                      | button的文案                  |
| getAdStarRating()                        | 广告的评分信息（可能为空）              |
| getAdSocialContext()                     | 广告的下载数或者是网站（可能为空）          |
| hasExpired()                             | 是否过期的判断（true：过期 false：不过期） |
| isDownLoadApp()                          | 是否是下载类型广告（true：是 false：不是） |
| setImpressionListener(ImpressionListener listener) | 设置广告展现的回调接口                |
| registerViewForInteraction(View view)    | 绑定广告内容和广告展现的view           |
| unregisterView()                         | 解绑广告内容和广告展现的view           |

<br>

#### 6.2 插屏相关接口

1. com.cmcm.adsdk.interstitial.InterstitialAdManager


| 方法名                                      | 方法说明   |
| ---------------------------------------- | ------ |
| loadAd()                                 | 加载广告   |
| showAd()                                 | 展示广告   |
| setInterstitialCallBack(InterstitialAdCallBack listener) | 设置回调接口 |

2. com.cmcm.adsdk.interstitial.InterstitialAdCallBack

| 方法名                          | 方法说明          |
| ---------------------------- | ------------- |
| onAdLoaded( )                | 广告请求成功        |
| onAdLoadFailed( )            | 广告请求失败        |
| onAdClicked( )               | 广告点击          |
| onAdDisplayed( )             | 广告展示          |
| onAdDismissed(int errorcode) | 点击close按钮广告消失 |


## <div id ='Questions'>7 常见问题</div>
#### 7.1 集成问题
1、Pegasi-SDK目前支持哪些广告样式？ 
答：目前支持原生广告和插屏广告，同时我们还提供了信息流专属的原生广告。

2、Pegasi-SDK的广告的缓存时间最多多长时间？
答：各个渠道的广告有效其存在差异，具体缓存时间为个渠道自定义。 其中主要的广告源有效期时间如下：
Admob，猎户，Mopub均为60分钟，Facebook为180分钟，百度和广点通为30分钟，Yahoo为75分钟。

3、Pegasi-SDK聚合了几家Network平台？
答：目前已经聚合了猎户，百度、广点通，FB、Admob、Yahoo、对于未对接Network可通过Adaptor进行对接。


#### 7.2 广告相关问题
1、为什么有时候拉取不到广告？
答：请查看应用ID、广告单元ID等信息是否填写正确并通过审核，之后查看后台单子是否已经投放、投放状态是否正确。

2.为什么拉取不到Facebook广告？
答:FB广告的广告展示有以下必要条件： 
a.手机安装FB客户端
b.FB客户端是否登录成功且账号需是活跃用户



<br>
## <div id ='Errorcode'>8 错误码</div>
| 错误码   | 错误码说明        | 解决方案                         |
| ----- | ------------ | ---------------------------- |
| 10001 | 聚合配置没有加载成功   | 检查聚合的配置是否在ssp配置成功            |
| 10002 | 没有广告数据       | 检查各广告平台的id是否生效，或抓包查看有无广告数据返回 |
| 10005 | 没有合法的adaptor | 检查自己设置的adaptor是否合法           |
| 10009 | 参数错误         | /                            |
| 1202  | 参数错误         | 原生开屏广告超时                     |
| 1206  | 参数错误         | 原生开屏广告回调接口为空                 |
| 1208  | 参数错误         | 原生开屏广告只可拉取广告一次               |

<br>
## <div id ='Contract'>9 联系方式</div>
- 如有问题，请联系<a href="Mailto:cm_bs_dpt@conew.com">cm_bs_dpt@conew.com</a>
- 猎豹移动其他产品 [https://www.cmcm.com/zh-cn/][10]


<br>
<br>
<br>




[1]:	#introduction
[2]:	#prepare
[3]:	#init
[4]:	#Native
[5]:	#Interstitial
[6]:	#API
[7]:	#Questions
[8]:	#Errorcode
[9]:	#Contract
[10]:	https://www.cmcm.com/zh-cn/

[image-native]:	images/native.jpg "GitHub,Social Coding"
[image-native-list]:	images/native_list.jpg "GitHub,Social Coding"
[image-interstitial]:	images/Interstitial.jpg "Intersitial"
[image-native-splash]:	images/native_splash.jpg "native_splash"