#接入说明：

其他设置

        <!--admob ad request-->
        <meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />
        <!--Include the AdActivity configChanges and theme. -->
            <activity android:name="com.google.android.gms.ads.AdActivity"
                android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
                android:theme="@android:style/Theme.Translucent" />
***

混淆脚本

    -keep class * extends java.util.ListResourceBundle {
        protected Object[][] getContents();
    }
    -keep class com.google.ads.AdActivity{
        <fields>;
		<methods>;
    }
    -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
        public static final *** NULL;
    }
    -keepnames @com.google.android.gms.common.annotation.KeepName class *
    -keepclassmembernames class * {
        @com.google.android.gms.common.annotation.KeepName *;
    }
    -keepnames class * implements android.os.Parcelable {
        public static final ** CREATOR;
    }

For old ads classes

    -keep public class com.google.ads.**{
        public *;
    }

