
package com.buffalo.adsdk;

public final class Const {
    public static final String TAG = "CMCMADSDK";
    public static final String VERSION = "4.1.0";
    public static final String HOST_NAME = "unconf.adkmob.com";
    public static final String HOST_NAME_CN = "unconf.mobad.ijinshan.com";
    public static final String CONFIG_URL = "https://" + HOST_NAME + "/b/";
    public static final String CONFIG_URL_CN = "https://" + HOST_NAME_CN + "/b/";
    public static final String HOST_NAME_UFS = "ufs.adkmob.com";
    public static final String CONFIG_URL_UFS = "https://" + HOST_NAME_UFS + "/p/";
    public static final int NET_TIMEOUT = 15 * 1000;
    public static final String KEY_JUHE = "ad";
    public static final String KEY_FB = "fb";
    public static final String KEY_CM = "cm";
    public static final String KEY_BD = "bd";
    public static final String KEY_GDT = "gdt";
    public static final String KEY_MV = "mv";
    public static final String KEY_OB = "ob";
    public static final String KEY_AC = "ac";

    //banner
    public static final String KEY_CM_BANNER = "cmb";
    public static final String KEY_MP_BANNER = "mpb";

    //for iclick
    public static final String KEY_ICLICK = "ic";
    public static final String KEY_ICLICK_VIDEO = "ic_video";
    //for fb hight ecpm
    public static final String KEY_FB_H = "fb_h";
    //for fb low ecpm
    public static final String KEY_FB_L = "fb_l";
    //for fb balance ecpm
    public static final String KEY_FB_B = "fb_b";

    //yahoo
    public static final String KEY_YH = "yh";
    //pubmatic
    public static final String KEY_PM = "pm";

    public static final String KEY_MP = "mp";
    //Chartboost
    public static final String KEY_CB = "cb";
    //Admob
    public static final String KEY_AB = "ab";
    //Inmobi
    public static final String KEY_IM = "im";


    public static final String KEY_LOOPME_VIDEO = "lpv";
    public static final String KEY_VUNGLE_VIDEO = "vgv";
    public static final String KEY_VAST_VIDEO = "vav";
    public static final String KEY_FACEBOOK_VIDEO = "fbv";
    public static final String KEY_MOPUB_VIDEO = "mpv";


    public static final String KEY_CM_INTERSTITIAL = "cmi";
    public static final String KEY_FB_INTERSTITIAL = "fbi";
    public static final String KEY_AB_INTERSTITIAL = "abi";

    public static final class res {

        public static final int cm = 80;
        public static final int baidu = 3004;
        public static final int gdt = 500;
        public static final int facebook = 3000;
        public static final int mopub = 3003;
        public static final int admob = 3002;
        public static final int yahoo = 3008;
        public static final int pubmatic = 3007;
        public static final int iclick = 3009;
        public static final int cmb = 3010;
        public static final int ac = 78;

        public static final int pega_fb_h = 6000;
        public static final int pega_fb_b = 6001;
        public static final int pega_fb_l = 6002;
        public static final int pega_fb_interstitial = 6003;

        public static final int pega_admob_h = 6010;
        public static final int pega_admob_b = 6011;
        public static final int pega_admob_interstitial = 6012;

        public static final int pega_mopub_h = 6020;
        public static final int pega_mopub_l = 6021;
        public static final int pega_mopub_banner = 6022;

        public static final int pega_picks_interstitial = 6037;

        public static final int inmobi = 6033;
        public static final int mv = 6042;
    }

    public static final class cacheTime {

        public static final long min_cache_time = 30 * 60 * 1000l;
        public static final long cm = 60 * 60 * 1000l;
        public static final long baidu = 30 * 60 * 1000l;
        public static final long gdt = 30 * 60 * 1000l;
        public static final long facebook = 3 * 60 * 60 * 1000l;
        public static final long mopub = 60 * 60 * 1000l;
        public static final long admob = 60 * 60 * 1000l;
        public static final long yahoo = 75 * 60 * 1000l;
        public static final long pubmatic = 60 * 60 * 1000l;
        public static final long ob = 60 * 60 * 1000l;
        public static final long ac = 20 * 60 * 1000l;

    }

    //上报的包名请参考：https://docs.google.com/a/conew.com/document/d/1cR4QvhPgV9qr-NiT2EefKv8e1gT-eh-_UU4GUKvfzr4/edit?usp=sharing_eid&ts=566796b3
    public static final class pkgName {

        public static final String cm = "com.cmcm.ad";
        public static final String baidu = "com.baidu.ad";
        public static final String gdt = "com.gdt.ad";
        public static final String facebook = "com.facebook.ad";
        public static final String mopub = "com.mopub.ad";
        public static final String admob = "com.admob.native";
        public static final String admob_interstitial = "com.admob.interstitial";
        public static final String yahoo = "com.yahoo.ad";
        public static final String iclick = "com.iclick.ad";
        public static final String pubmatic = "com.pubmatic.ad";
        public static final String cmb = "com.ad";
        public static final String mv = "com.mobvista.ad";
        public static final String imobi = "com.imobi.ad";
        public static final String ob = "com.cmcm.brandad";
        public static final String ac = "com.avocarrot.ad";
    }

    //开屏广告上报包名后缀，com.baidu.ad.splash
    public static String REPORT_SPLASH_SUFFIX = "splash";
    //插屏广告上报包名后缀
    public static String REPORT_INTERSTITIAL_SUFFIX = "interstitial";
    //banner广告上报包名后缀，com.mopub.ad.banner
    public static String REPORT_BANNER_SUFFIX = "banner";

    public static String CM_AD_DETAIL_URL = "http://ad.cmcm.com/";

    //offer report info
    //facebook
    public enum AdType {
        NATIVE, BANNER, VIDEO, INTERSTITIAL
    }

    public static int OFFERREPORT_FB = 1;
    //yahoo
    public static int OFFERREPORT_YH = 3;
    //mopub
    public static int OFFERREPORT_MP = 4;

    // impression  click
    public static int IMP_Report = 50;
    public static int CLK_Report = 60;


    public enum Event {
        CONFIG_START, CONFIG_SUCCESS, CONFIG_FAIL,
        LOAD_START_FAIL,

        GET_FEED_AD,                            //触发getAd
        GET_FEED_AD_SUCCESS,                    //getAd成功
        GET_FEED_AD_FAIL,                       //getAd失败
        FEED_AD_REQUEST_NUM,                    //主动发起请求(包括循环请求)
        FEED_AD_REQUEST_SUCCESS_NUM,            //请求成功
        FEED_AD_FAIL,                           //请求失败
        GET_FEED_AD_FAIL_FROM_CACHE,            //从第一个缓存获取广告失败
        GET_FEED_AD_FAIL_FROM_JUHE_CACHE,       //从聚合缓存池中获取广告失败
        GET_FEED_AD_FAIL_FROM_DUPLE_CACHE,      //从第三个缓存池中获取广告失败
        GET_FEED_AD_SUCCESS_FROM_CACHE,         //从第一个缓存获取广告成功
        GET_FEED_AD_SUCCESS_FROM_JUHE_CACHE,    //从聚合缓存池中获取广告成功
        GET_FEED_AD_SUCCESS_FROM_DUPLE_CACHE,   //从第三个缓存池中获取广告成功
        FEED_AD_REQUEST_2,                      //delay 2秒请求
        FEED_AD_REQUEST_4,                      //delay 4秒请求
        FEED_AD_REQUEST_8,                      //delay 8秒请求
        FEED_AD_REQUEST_16,                     //delay 16秒请求
        FEED_AD_PRELOAD_NUM,                    //preload触发请求(包括循环请求)
        FEED_AD_ONCE_GETAD_LOAD_NUM,            //getAd触发load
        FEED_AD_ONCE_LOAD_NUM,                  //主动触发load
        FEED_AD_ONCE_GETAD_LOAD_FAIL_NUM,       //getAd触发load，最终成功
        FEED_AD_ONCE_GETAD_LOAD_SUCCESS_NUM,    //getAd触发load，最终失败
        FEED_AD_ONCE_LOAD_FAIL_NUM,             //主动触发load，最终失败
        FEED_AD_ONCE_LOAD_SUCCESS_NUM,          //主动触发load，最终成功
        DELETE_AD_FROM_CACHE,                   //从第一个缓存池删除广告
        DELETE_AD_FROM_DUPL_AD_CACHE,           //从第三个缓存池删除广告
        DELETE_EXPIRED_AD,                      //删除过期广告
        GET_FEED_AD_NOT_REQUEST_COMPLETE        //触发getAd时，第一次请求没有返回成功
    }

    public static boolean isPicksAd(String adTypeName) {
        return KEY_CM.equals(adTypeName) || KEY_VAST_VIDEO.equals(adTypeName)
                || KEY_CM_BANNER.equals(adTypeName) || KEY_CM_INTERSTITIAL.equals(adTypeName) || KEY_OB.equals(adTypeName);
    }
}
