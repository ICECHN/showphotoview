package com.icechn.library.showphoto.Progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.icechn.library.showphoto.Utils.BaseUtils;

/**
 * Created by ICE on 2016/12/29.
 */

public class PhotoLoadingProgress extends Drawable {

    public PhotoLoadingProgress(Context context) {
        mStrokeWidth = BaseUtils.dp2px(context, 2);
        mRadius = BaseUtils.dp2px(context, 20);
        mRingRadius = mRadius + mStrokeWidth / 2 + mGap;

        mOuterRingBgPaint = new Paint();
        mOuterRingBgPaint.setAntiAlias(true);
        mOuterRingBgPaint.setColor(mOuterRingBgColor);
        mOuterRingBgPaint.setStyle(Paint.Style.STROKE);
        mOuterRingBgPaint.setStrokeWidth(mStrokeWidth);

        mInnerCircleBgPaint = new Paint();
        mInnerCircleBgPaint.setAntiAlias(true);
        mInnerCircleBgPaint.setColor(mInnerCircleBgColor);
        mInnerCircleBgPaint.setStyle(Paint.Style.FILL);
        mInnerCicleFgPaint = new Paint();
        mInnerCicleFgPaint.setAntiAlias(true);
        mInnerCicleFgPaint.setColor(mInnerCircleFgColor);
        mInnerCicleFgPaint.setStyle(Paint.Style.FILL);
    }
    @Override
    public void draw(Canvas canvas) {
        Rect size = getBounds();
        mXCenter = size.width() / 2;
        mYCenter = size.height() / 2;

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
            float sweepAngel = ((float) mProgress / mTotalProgress) * 360;
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

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    protected boolean onLevelChange(int level) {
        mProgress = level;
        return super.onLevelChange(level);
    }

    private static final int DEFAULT_GAP = 8;
    // 画实心圆的画笔【背景】
    private Paint mInnerCircleBgPaint;
    // 画实心圆的画笔【前景】
    private Paint mInnerCicleFgPaint;
    // 画圆环的画笔【背景】
    private Paint mOuterRingBgPaint;
    // 圆形颜色[背景]
    private int mInnerCircleBgColor = Color.TRANSPARENT;
    // 圆形颜色[前景]
    private int mInnerCircleFgColor = Color.LTGRAY;
    // 圆环颜色[背景]
    private int mOuterRingBgColor = Color.LTGRAY;
    private boolean mInnerRefreshEnable = true;
    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    //内圆与外环之间的间隙，默认是8 px
    private int mGap = DEFAULT_GAP;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 总进度
    private int mTotalProgress = 10000;
    // 当前进度
    private int mProgress;
}
