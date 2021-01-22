package com.buffalo.ads;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buffalo.ads.ui.AdClickListener;
import com.buffalo.ads.ui.ShowCasePagerAdapter;
import com.buffalo.baseapi.ads.INativeAd;

public class WindowManagerSimpleActivity extends FragmentActivity implements AdClickListener {
    private WindowManager mWindowManager;
    private View mWindowRootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windowmanager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showWindowManager();
    }

    private void showWindowManager() {
        mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                mWindowManager.getDefaultDisplay().getWidth(),
                mWindowManager.getDefaultDisplay().getHeight(),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.RGBA_8888);
        params.gravity = (Gravity.CENTER);
        RelativeLayout relativeLayout = new RelativeLayout(WindowManagerSimpleActivity.this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(mWindowManager.getDefaultDisplay().getWidth(),
                mWindowManager.getDefaultDisplay().getHeight()));
        relativeLayout.setBackgroundResource(R.drawable.screensave_bg);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ViewPager viewPager = new ViewPager(this);
        ShowCasePagerAdapter showCasePagerAdapter = new ShowCasePagerAdapter(this);
        showCasePagerAdapter.setAdClickLisener(this);
        viewPager.setAdapter(showCasePagerAdapter);

        relativeLayout.addView(viewPager, layoutParams);
        relativeLayout.addView(createCloseTextView());
        relativeLayout.setVisibility(View.VISIBLE);
        mWindowRootView = relativeLayout;
        mWindowManager.addView(relativeLayout, params);
    }

    private View createCloseTextView() {
        TextView textView = new TextView(this);
        textView.setText("点击关闭");
        textView.setTextSize(14);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setPadding(10, 10, 10, 10);
        textView.setBackgroundColor(Color.GRAY);
        textViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_BOTTOM);
        textViewParams.setMargins(0, 0, 0, 0);
        textView.setLayoutParams(textViewParams);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeWindowManger();
                finish();
            }
        });
        return textView;
    }

    private void closeWindowManger() {
        try {
            if (mWindowManager != null && mWindowRootView != null && mWindowRootView.getParent() != null) {
                mWindowManager.removeView(mWindowRootView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWindowRootView = null;
            mWindowManager = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowRootView = null;
        mWindowManager = null;
    }


    @Override
    public void onAdClicked(INativeAd ad) {
        closeWindowManger();
        finish();
    }
}
