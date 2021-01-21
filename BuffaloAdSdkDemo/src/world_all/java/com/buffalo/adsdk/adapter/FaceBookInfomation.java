package com.buffalo.adsdk.adapter;

import android.net.Uri;
import android.text.TextUtils;

import com.buffalo.adsdk.Const;
import com.buffalo.utils.Commons;
import com.facebook.ads.Ad;
import com.facebook.ads.NativeAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by i on 2015/7/2.
 */
public class FaceBookInfomation {

    public static String getRawJson(int operation, NativeAd ad) {

        if (ad == null) {
            return "";
        }

        String iconUrl = null;
        String coverUrl = null;
        String title = null;
        String body = null;
        String socialcontext = null;
        String calltoaction = null;
        String fbad = null;

        try {
            if (!TextUtils.isEmpty(ad.getAdIcon().getUrl())) {
                iconUrl = ad.getAdIcon().getUrl();
            }
        } catch (Exception e) {
            iconUrl = null;
        }

        try {
            if (!TextUtils.isEmpty(ad.getAdCoverImage().getUrl())) {
                coverUrl = ad.getAdCoverImage().getUrl();
            }
        } catch (Exception e) {
            coverUrl = null;
        }

        try {
            if (!TextUtils.isEmpty(ad.getAdTitle())) {
                title = ad.getAdTitle();
            }
        } catch (Exception e) {
            title = null;
        }

        try {
            if (!TextUtils.isEmpty(ad.getAdBody())) {
                body = ad.getAdBody();
            }
        } catch (Exception e) {
            body = null;
        }

        try {
            if (!TextUtils.isEmpty(ad.getAdSocialContext())) {
                socialcontext = ad.getAdSocialContext();
            }
        } catch (Exception e) {
            socialcontext = null;
        }

        try {
            if (!TextUtils.isEmpty(ad.getAdCallToAction())) {
                calltoaction = ad.getAdCallToAction();
            }
        } catch (Exception e) {
            calltoaction = null;
        }

        try {
            String fbadString = getRawFBAdV4141(ad);
            if (!TextUtils.isEmpty(fbadString)) {
                fbad = fbadString;
            }
        } catch (Exception e) {
            fbad = null;
        }

        JSONObject jsonObj = new JSONObject();

        try {
            Commons.putValueIntoJson(jsonObj, "operation", operation + "");
            Commons.putValueIntoJson(jsonObj, "ad_id", ad.getId());
            Commons.putValueIntoJson(jsonObj, "icon_url", iconUrl);
            Commons.putValueIntoJson(jsonObj, "cover_url", coverUrl);
            Commons.putValueIntoJson(jsonObj, "title", title);
            Commons.putValueIntoJson(jsonObj, "body", body);
            Commons.putValueIntoJson(jsonObj, "social_context", socialcontext);
            Commons.putValueIntoJson(jsonObj, "call2action", calltoaction);
            Commons.putValueIntoJson(jsonObj, "fbad", fbad);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj.toString();
    }

    /*public static String getRawFBAd(NativeAd ad){
        String rawAdUriString = "";
        if(null == ad){
            return rawAdUriString;
        }
        try {
            Field adModel = ad.getClass().getDeclaredField("j");
            adModel.setAccessible(true);

            Field cField = adModel.get(ad).getClass().getDeclaredField("c");
            cField.setAccessible(true);

            Field bField = cField.get(adModel.get(ad)).getClass().getDeclaredField("b");
            bField.setAccessible(true);

            Object o = bField.get(cField.get(adModel.get(ad)));
            if (o instanceof Uri) {
                Uri fbadUri = (Uri) o;
                rawAdUriString = fbadUri.toString();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return rawAdUriString;
    }*/

    public static String getRawFBAd(NativeAd ad) {
        String rawAdUriString = "";
        try {
            Field adModel = ad.getClass().getSuperclass().getDeclaredField("adDataModel");
            adModel.setAccessible(true);

            Field bField = adModel.get(ad).getClass().getDeclaredField("b");
            bField.setAccessible(true);
            Object o = bField.get(adModel.get(ad));
            if (o instanceof Uri) {
                Uri fbadUri = (Uri) o;
                rawAdUriString = fbadUri.toString();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return rawAdUriString;
    }

    /**
     * for AudienceNetwork v4.7.0
     */
    public static String getRawFBAdV470(NativeAd ad) {
        String rawAdUriString = "";
        try {
            Field adAdapterField = ad.getClass().getDeclaredField("j");
            adAdapterField.setAccessible(true);
            Object adAdpterObject = adAdapterField.get(ad);
            if (adAdpterObject instanceof com.facebook.ads.internal.adapters.k) {
                com.facebook.ads.internal.adapters.k kAdapter = (com.facebook.ads.internal.adapters.k) adAdpterObject;
                Field adapterN = kAdapter.getClass().getDeclaredField("b");
                adapterN.setAccessible(true);
                Object nObject = adapterN.get(kAdapter);
                if (nObject instanceof com.facebook.ads.internal.adapters.n) {
                    com.facebook.ads.internal.adapters.n nAdapter = (com.facebook.ads.internal.adapters.n) nObject;
                    Field uriField = nAdapter.getClass().getDeclaredField("b");
                    uriField.setAccessible(true);
                    Object o = uriField.get(nAdapter);
                    if (o instanceof Uri) {
                        Uri fbadUri = (Uri) o;
                        rawAdUriString = fbadUri.toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return rawAdUriString;
    }


    /**
     * for AudienceNetwork v4.8.0
     */
    public static String getRawFBAdV480(NativeAd ad) {
        String rawAdUriString = "";
        try {
            Field adAdapterField = ad.getClass().getDeclaredField("k");
            adAdapterField.setAccessible(true);
            Object adAdpterObject = adAdapterField.get(ad);
            if (adAdpterObject instanceof com.facebook.ads.internal.adapters.j) {
                com.facebook.ads.internal.adapters.j kAdapter = (com.facebook.ads.internal.adapters.j) adAdpterObject;
                Field adapterN = kAdapter.getClass().getDeclaredField("c");
                adapterN.setAccessible(true);
//                Object nObject =  adapterN.get(kAdapter);
//                if(nObject instanceof   com.facebook.ads.internal.adapters.n ){
//                    com.facebook.ads.internal.adapters.n  nAdapter =  (com.facebook.ads.internal.adapters.n)nObject;
//                    Field uriField = nAdapter.getClass().getDeclaredField("b");
//                    uriField.setAccessible(true);
                Object o = adapterN.get(kAdapter);
                if (o instanceof Uri) {
                    Uri fbadUri = (Uri) o;
                    rawAdUriString = fbadUri.toString();
//                    }
                }
            }
        } catch (Exception e) {
        }
        return rawAdUriString;
    }

    /**
     * 获取facebook 插屏广告 offer信息
     *
     * @param ad
     * @return
     */
    public static String getFacebookInterstitialOfferStringV482(Ad ad) {
        try {
            Field hField = ad.getClass().getDeclaredField("d");
            hField.setAccessible(true);
            Object hObject = hField.get(ad);
            if (hObject instanceof com.facebook.ads.internal.h) {
                com.facebook.ads.internal.h object_h = (com.facebook.ads.internal.h) hObject;
                Field iField = object_h.getClass().getDeclaredField("l");
                iField.setAccessible(true);
                Object iObject = iField.get(object_h);
                if (iObject instanceof com.facebook.ads.internal.adapters.i) {
                    com.facebook.ads.internal.adapters.i object_i = (com.facebook.ads.internal.adapters.i) iObject;
                    Field kField = object_i.getClass().getDeclaredField("f");
                    kField.setAccessible(true);
                    Object kObject = kField.get(object_i);
                    if (kObject instanceof com.facebook.ads.internal.adapters.k) {
                        com.facebook.ads.internal.adapters.k object_k = (com.facebook.ads.internal.adapters.k) kObject;

                        Field strField = object_k.getClass().getDeclaredField("a");
                        strField.setAccessible(true);
                        Object strHtml = strField.get(object_k);
                        if (strHtml instanceof String) {
                            return strHtml.toString();
                        }
                    }
                }

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * for AudienceNetwork v4.14.1
     */
    public static String getRawFBAdV4141(NativeAd ad) {
        String rawAdUriString = "";
        try {
            Field adAdapterField = ad.getClass().getDeclaredField("m");
            adAdapterField.setAccessible(true);
            Object adAdpterObject = adAdapterField.get(ad);
            if (adAdpterObject instanceof com.facebook.ads.internal.adapters.l) {
                com.facebook.ads.internal.adapters.l lAdapter = (com.facebook.ads.internal.adapters.l) adAdpterObject;
                Field adapterN = lAdapter.getClass().getDeclaredField("d");
                adapterN.setAccessible(true);
                Object o = adapterN.get(lAdapter);
                if (o instanceof Uri) {
                    Uri fbadUri = (Uri) o;
                    rawAdUriString = fbadUri.toString();
                }
            }
        } catch (Exception e) {
        }
        return rawAdUriString;
    }

    public static String getFBReportPkg(String typeName) {
        String suffix = "";
        if (typeName.equals(Const.KEY_FB_L)) {
            suffix = "low";
        } else if (typeName.equals(Const.KEY_FB_B)) {
            suffix = "balance";
        } else if (typeName.equals(Const.KEY_FB_H)) {
            suffix = "hight";
        }
        return String.format("%s.%s", Const.pkgName.facebook, suffix);
    }

}
