package com.icechn.library.showphoto.Progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 圆形进度：支持外环与内圆同时画进度
 */

public class CircleProgress extends View {
    private static final int MAX_PROGRESS = 100;
    private static final int MIN_PROGRESS = 0;
    private static final int DEFAULT_GAP = 8;
    // 画实心圆的画笔【背景】
    private Paint mInnerCircleBgPaint;
    // 画实心圆的画笔【前景】
    private Paint mInnerCicleFgPaint;
    // 画圆环的画笔【前景】
    private Paint mOuterRingFgPaint;
    // 画圆环的画笔【背景】
    private Paint mOuterRingBgPaint;
    // 圆形颜色[背景]
    private int mInnerCircleBgColor = Color.TRANSPARENT;
    // 圆形颜色[前景]
    private int mInnerCircleFgColor = Color.TRANSPARENT;
    // 圆环颜色[前景]
    private int mOuterRingFgColor = 0xffff6400;
    // 圆环颜色[背景]
    private int mOuterRingBgColor = Color.TRANSPARENT;
    private boolean mInnerRefreshEnable = true;
    private boolean mOuterRefreshEnable = true;
    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    //内圆与外环之间的间隙，默认是8 px
    private int mGap = DEFAULT_GAP;
    //是否顺时针方向，默认为true
    private boolean mIsClockWise = true;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 总进度
    private int mTotalProgress = MAX_PROGRESS;
    // 当前进度
    private int mProgress;

    public CircleProgress(Context context) {
        super(context);
        initVariable();
    }
    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVariable();
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVariable();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVariable();
    }

    private void initVariable() {
        initInnerVar();
        initOuterVar();
    }
    private void initInnerVar() {
        mInnerRefreshEnable = mInnerCircleBgColor != mInnerCircleFgColor;
        mInnerCircleBgPaint = new Paint();
        mInnerCircleBgPaint.setAntiAlias(true);
        mInnerCircleBgPaint.setColor(mInnerCircleBgColor);
        mInnerCircleBgPaint.setStyle(Paint.Style.FILL);
        mInnerCicleFgPaint = new Paint();
        mInnerCicleFgPaint.setAntiAlias(true);
        mInnerCicleFgPaint.setColor(mInnerCircleFgColor);
        mInnerCicleFgPaint.setStyle(Paint.Style.FILL);
    }
    private void initOuterVar() {
        mOuterRefreshEnable = mOuterRingBgColor != mOuterRingFgColor;
        mOuterRingFgPaint = new Paint();
        mOuterRingFgPaint.setAntiAlias(true);
        mOuterRingFgPaint.setColor(mOuterRingFgColor);
        mOuterRingFgPaint.setStyle(Paint.Style.STROKE);
        mOuterRingFgPaint.setStrokeWidth(mStrokeWidth);
        mOuterRingBgPaint = new Paint();
        mOuterRingBgPaint.setAntiAlias(true);
        mOuterRingBgPaint.setColor(mOuterRingBgColor);
        mOuterRingBgPaint.setStyle(Paint.Style.STROKE);
        mOuterRingBgPaint.setStrokeWidth(mStrokeWidth);
    }

    /**
     * 设置半径与宽度
     * @param innerRadius :    内环半径
     * @param outerStrokeWidth :    外环宽度
     * @param middleGap : 内圆与外环之间的间隙，当<0时使用默认值
     * @param isClockWise : 是否顺时针方向
     */
    public void setRadius(int innerRadius, int outerStrokeWidth, int middleGap, boolean isClockWise) {
        mRadius = innerRadius;
        mStrokeWidth = outerStrokeWidth;
        mGap = middleGap;
        mIsClockWise = isClockWise;
        if (mGap < 0) {
            mGap = DEFAULT_GAP;
        }
        mRingRadius = mRadius + mStrokeWidth / 2 + mGap;
        mOuterRingFgPaint.setStrokeWidth(mStrokeWidth);
        mOuterRingBgPaint.setStrokeWidth(mStrokeWidth);
    }

    /**
     * 设置内环前景与背景颜色
     * @param fgColor
     * @param bgColor
     */
    public void setInnerCircleColor(int fgColor, int bgColor) {
        mInnerCircleFgColor = fgColor;
        mInnerCircleBgColor = bgColor;
        initInnerVar();
    }
    /**
     * 设置外环前景与背景颜色
     * @param fgColor
     * @param bgColor
     */
    public void setOuterRingColor(int fgColor, int bgColor) {
        mOuterRingFgColor = fgColor;
        mOuterRingBgColor = bgColor;
        initOuterVar();
    }


    /**
     *
     * @param innerFgColor 内环填充颜色
     * @param innerBgColor 内环填充颜色
     * @param outerFgColor 外环填充【前景色】
     * @param outerBgColor 外环填充【背景色】
     * @param radius     内环半径
     * @param strokeWidth 外环宽度
     * @param middleGap : 内圆与外环之间的间隙，当<0时使用默认值
     * @param isClockWise : 是否顺时针方向
     */
    public void setParameter(int innerFgColor, int innerBgColor,
                             int outerFgColor, int outerBgColor,
                             int radius, int strokeWidth, int middleGap, boolean isClockWise) {
        setRadius(radius, strokeWidth, middleGap, isClockWise);
        setInnerCircleColor(innerFgColor, innerBgColor);
        setOuterRingColor(outerFgColor, outerBgColor);
    }

    /**
     * 设置最大的进度值，默认为100
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress) {
        if (maxProgress <= 0) {
            maxProgress = MAX_PROGRESS;
        }
        this.mTotalProgress = maxProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initVariable();

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;

        //画实心背景
        canvas.drawCircle(mXCenter, mYCenter, mRadius, mInnerCircleBgPaint);

        //画圆环背景
        RectF outerRect = new RectF();
        outerRect.left = mXCenter - mRingRadius;
        outerRect.right = mXCenter + mRingRadius;
        outerRect.top = mYCenter - mRingRadius;
        outerRect.bottom = mYCenter + mRingRadius;
        canvas.drawArc(outerRect, -90, 360, false, mOuterRingBgPaint);

        //根据进度画圆环前景与实心前景
        if (mProgress > 0 ) {
            float startAngel = -90;
            if (mIsClockWise) {//顺时针方向
            } else {//逆时针方向
                startAngel += (1 - (float) mProgress / mTotalProgress) * 360;
            }
            float sweepAngel = ((float) mProgress / mTotalProgress) * 360;
            if (mOuterRefreshEnable) {
                canvas.drawArc(outerRect, startAngel, sweepAngel, false, mOuterRingFgPaint);
            }
            if (mInnerRefreshEnable) {
                RectF innerRect = new RectF();
                innerRect.left = mXCenter - mRadius;
                innerRect.right = mXCenter + mRadius;
                innerRect.top = mYCenter - mRadius;
                innerRect.bottom = mYCenter + mRadius;
                canvas.drawArc(innerRect, startAngel, sweepAngel, true, mInnerCicleFgPaint);
            }
        }
    }

    /**
     *
     * @param progress [0 , 100]
     */
    public void setProgress(int progress) {
        if (progress > mTotalProgress) {
            progress = mTotalProgress;
        } else if (progress < MIN_PROGRESS) {
            progress = MIN_PROGRESS;
        }
        if (progress == mProgress) {
            Log.i("", "The same value");
            return ;
        }
        mProgress = progress;
        postInvalidate();
    }

    /**
     * 获取当前进度 [0 , 100]
     * @return
     */
    public int getProgress() {
        return mProgress;
    }
}
