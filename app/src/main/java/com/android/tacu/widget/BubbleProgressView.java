package com.android.tacu.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

public class BubbleProgressView extends View {

    private Paint mPaintProgress, mPaintBubble, mPaintProgressStr, mPaintIn, mPaintOut;
    private PathMeasure mPathMeasure;
    private Path mPathSrc, mPathDst, mPathBubble;

    private int mColorProgressBg = getResources().getColor(R.color.content_bg_color_grey);//进度条的背景颜色
    private int mColorProgress = getResources().getColor(R.color.color_default);//进度条的进度颜色
    private int mColorProgressStr;//进度条的进度文字的颜色
    private int mColorIn;
    private int mColorOut;

    private int inRadiu = UIUtils.dp2px(12);
    private int outRadiu = UIUtils.dp2px(14);

    private float mProgressHeight = UIUtils.dp2px(20);
    private float mBubbleTriangleHeight = UIUtils.dp2px(10);//气泡底部小三角高度
    private float mBubbleRectRound = UIUtils.dp2px(10);//气泡的圆角
    private float mTextSize = UIUtils.sp2px(16);//进度条文字大小
    private float mProgressStrMargin = UIUtils.dp2px(10);//气泡的边距

    private int mProgress = 10;//进度条的进度
    private int mMaxProgress = 100;
    private Paint.FontMetricsInt mFontMetricsInt;
    private float lineCirclePointY = 0;

    public BubbleProgressView(Context context) {
        this(context, null);
    }

    public BubbleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(mProgressHeight);

        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setStrokeCap(Paint.Cap.ROUND);//设置线头为圆角
        mPaintBubble.setStyle(Paint.Style.FILL);
        mPaintBubble.setStrokeJoin(Paint.Join.ROUND);//设置拐角为圆角
        mPaintBubble.setColor(getResources().getColor(R.color.color_default));//设置气泡的颜色

        mPaintProgressStr = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressStr.setStrokeWidth(1);
        mPaintProgressStr.setStyle(Paint.Style.FILL);
        mPaintProgressStr.setColor(mColorProgressStr);
        mPaintProgressStr.setTextSize(mTextSize);//设置字体大小
        mPaintProgressStr.setTextAlign(Paint.Align.CENTER);//将文字水平居中

        mPaintIn = new Paint();
        mPaintIn.setAntiAlias(true);
        mPaintIn.setStyle(Paint.Style.FILL);
        mPaintIn.setColor(mColorIn);

        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true);
        mPaintOut.setStyle(Paint.Style.FILL);
        mPaintOut.setColor(mColorOut);

        mPathSrc = new Path();
        mPathDst = new Path();
        mPathBubble = new Path();
        mPathMeasure = new PathMeasure();

        mColorProgressStr = getResources().getColor(R.color.text_color);
        mColorIn = getResources().getColor(R.color.color_default);
        mColorOut = getResources().getColor(R.color.color_otc_unhappy);

        mFontMetricsInt = mPaintProgressStr.getFontMetricsInt();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lineCirclePointY = h - mProgressHeight * 2;
        mPathSrc.moveTo(UIUtils.dp2px(15), h - mProgressHeight * 2);
        mPathSrc.lineTo(UIUtils.dp2px(15), h - mProgressHeight * 2);//进度条位置在控件整体底部，且距离控件左边和右边各20像素
        mPathMeasure.setPath(mPathSrc, false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawProgress(canvas);
        drawBubble(canvas);
    }

    /**
     * 画进度条
     */
    private void drawProgress(Canvas canvas) {
        mPathDst.reset();
        mPaintProgress.setColor(mColorProgressBg);
        canvas.drawPath(mPathSrc, mPaintProgress);//绘制进度背景（灰色部分）
        float stop = mPathMeasure.getLength() * mProgress / mMaxProgress;//计算进度条的进度
        mPathMeasure.getSegment(0, stop, mPathDst, true);//得到与进度对应的路径
        mPaintProgress.setColor(mColorProgress);
        canvas.drawPath(mPathDst, mPaintProgress);//绘制进度
    }

    private void drawBubble(Canvas canvas) {
        Rect rect = new Rect();
        mPaintProgressStr.getTextBounds(String.valueOf(mProgress), 0, String.valueOf(mProgress).length(), rect);//返回包围整个字符串的最小的一个Rect区域，以此计算出文字的高度和宽度
        int width = (int) (rect.width() + mProgressStrMargin);//计算字符串宽度(加上设置的边距)
        int height = (int) (rect.height() + mProgressStrMargin);//计算字符串高度(加上设置的边距)
        mPathBubble.reset();
        float p[] = new float[2];//用于存储点坐标的数组
        float t[] = new float[2];
        float stop = mPathMeasure.getLength() * mProgress;//计算进度条的进度
        mPathMeasure.getPosTan(stop, p, t);//获取进度所对应点的左边
        mPathBubble.moveTo(p[0], p[1] - mProgressHeight);
        mPathBubble.lineTo(p[0] + mBubbleTriangleHeight, p[1] - mBubbleTriangleHeight - mProgressHeight);//假设底部小三角为等腰直角三角形，那么三角形的高度就等于底边长度的1/2
        mPathBubble.lineTo(p[0] - mBubbleTriangleHeight, p[1] - mBubbleTriangleHeight - mProgressHeight);
        mPathBubble.close();//使路径闭合从而形成三角形
        //这里是计算文字所在矩形的位置及大小
        //left:因为设置的气泡底部三角形为等腰直角三角形，所以矩形的左边位置为，
        //      进度所在的横坐标 - 底部三角形高度 - 矩形圆角的半径(不减去圆角半径的话显得不够圆润)，
        //      而(mProgress*width)则是为了不断改变气泡底部的三角形与气泡顶部矩形的相对位置
        //      否则在进度条开始或结束位置可能为显示不全
        //top:进度所在的高度 - 底部三角形高度 - 进度条高度 - 矩形高度
        //right:矩形右边位置的计算原理与左边相同，同样((1-mProgress)*width)也是为了不断改变气泡底部的三角形与气泡顶部矩形的相对位置（与left相对应）
        //bottom:这个就简单了，与top相比小了一个矩形的高度
        RectF rectF = new RectF(p[0] - mBubbleTriangleHeight - mBubbleRectRound / 2 - (mProgress * width), p[1] - mBubbleTriangleHeight - mProgressHeight - height, p[0] + mBubbleTriangleHeight + mBubbleRectRound / 2 + (0.5F * width), p[1] - mBubbleTriangleHeight - mProgressHeight);
        mPathBubble.addRoundRect(rectF, mBubbleRectRound, mBubbleRectRound, Path.Direction.CW);//添加矩形路径
        canvas.drawPath(mPathBubble, mPaintBubble);//绘制气泡
        int i = (mFontMetricsInt.bottom - mFontMetricsInt.ascent) / 2 - mFontMetricsInt.bottom;//让文字垂直居中
        canvas.drawText(String.valueOf(mProgress), rectF.centerX(), rectF.centerY() + i, mPaintProgressStr);//绘制文字（将文字绘制在气泡矩形的中心点位置）

        canvas.drawCircle(p[0], lineCirclePointY, outRadiu, mPaintOut);
        canvas.drawCircle(p[0], lineCirclePointY, inRadiu, mPaintIn);
    }

    public void setMaxProgress(int max) {
        mMaxProgress = max;
        invalidate();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }
}
