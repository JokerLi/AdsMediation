package com.buffalo.adsdk.splashad;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.R;
import com.buffalo.utils.Commons;
import com.buffalo.utils.Logger;

/**
 * Created by chenhao on 16/11/3.
 */

public class CountdownView extends View {

    private int mCircleColor;
    private float mCircleWidth;
    private int mTextColor;
    private float mTextSize;
    private Paint mPaint;
    private OnCountdownListener mOnCountdownListener;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private static final int DEFAULT_COUNT = 5;
    private int mCount = DEFAULT_COUNT;
    private int mCurrentCount = mCount;
    private Handler mHandler;
    private static final int COUNTDOWN_MSG = 8888;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountdownView, 0, 0);
        try {
            mCircleColor = typedArray.getColor(R.styleable.CountdownView_circle_color, Color.parseColor("#656869"));
            mCircleWidth = typedArray.getDimension(R.styleable.CountdownView_circle_width, Commons.dip2px(context, 2));
            mTextColor = typedArray.getColor(R.styleable.CountdownView_text_color,Color.parseColor("#656869"));
            mTextSize = typedArray.getDimension(R.styleable.CountdownView_text_size, Commons.sp2px(context, 12));
        }catch (Exception e){

        }finally {
            typedArray.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - mCircleWidth /2); //圆环的半径
        //开始画圆弧
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mCircleColor);
        mPaint.setStrokeWidth(mCircleWidth);
        //计算弧度
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
        int angle =(int)(360* (1 - mCurrentCount*1.0/mCount));
        int start = -90 + angle;   //开始的角度
        int sweepAngle = 360 - angle; //扫过的角度
        canvas.drawArc(oval, start, sweepAngle, false, mPaint);  //根据进度画圆弧
        //开始画倒计时文字
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        //计算数字
        String text = "" + mCurrentCount;
        float textWidth = mPaint.measureText(text);
        canvas.drawText(text, centre- textWidth/ 2, centre + textWidth/2,  mPaint); //画出进度百分比
    }

    public void setCountNum(int num){
        if(num > 0) {
            this.mCount = num;
        }
        mCurrentCount = mCount;
    }


    public void start(){
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(myCountDownRunnable, COUNT_DOWN_INTERVAL);
        Logger.i(Const.TAG, "native splash CountDownTimer start.");
    }

    private Runnable myCountDownRunnable = new Runnable(){
        @Override
        public void run() {
            CountdownView.this.postInvalidate();
            mCurrentCount--;
            Logger.i(Const.TAG, "native splash CountDownTimer current count: " + mCurrentCount);
            if (mCurrentCount == 0 && mOnCountdownListener != null) {
                Logger.i(Const.TAG, "native splash CountDownTimer finish.");
                mOnCountdownListener.onCountdownFinish();
            }
            if (mCurrentCount > 0) {
               mHandler.postDelayed(this, COUNT_DOWN_INTERVAL);
            }

        }
    };


    public void stop(){
        if (mHandler != null) {
            mHandler.removeMessages(COUNTDOWN_MSG);
            mHandler.removeCallbacks(myCountDownRunnable);
        }
    }

    public void setCircleColor(int color) {
        this.mCircleColor = color;
    }

    public void setCircleWidth(int width) {
        this.mCircleWidth = width;
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
    }

    public void setTextSize(int size) {
        this.mTextSize = size;
    }

    public void setOnCountdownListener(OnCountdownListener listener){
        this.mOnCountdownListener = listener;
    }

    public interface OnCountdownListener{
        public abstract void onCountdownFinish();
    }
}
