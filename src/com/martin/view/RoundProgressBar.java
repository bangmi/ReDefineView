package com.martin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundProgressBar extends HorizontalProgressBar {

	private final static int default_radiu = 30;

	private int mRadiu = dp2px(default_radiu);
	private int mPaintWidth;

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		obtainStyle(attrs);
		// 描边
		mPaint.setStyle(Style.STROKE);
		// 抗锯齿
		mPaint.setAntiAlias(true);
		// 防抖动
		mPaint.setDither(true);
		// 连接交界处设置圆形弧度
		mPaint.setStrokeCap(Cap.ROUND);
		
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressBar(Context context) {
		this(context, null);
	}

	/**
	 * 获取自定义属性
	 */
	private void obtainStyle(AttributeSet attrs) {
		//
		mReachHeight = (int) (mUnReachHeight * 2.5f);

		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundPB);
		System.out.println(mRadiu);
		mRadiu = (int) ta.getDimension(R.styleable.RoundPB_radius, 10);
		System.out.println(mRadiu);
		ta.recycle();
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mPaintWidth = Math.max(mReachHeight, mUnReachHeight);
		// 默认四个方向的padding是一样的
		int expect = mPaintWidth + mRadiu * 2 + getPaddingLeft() + getPaddingRight();
		int width = resolveSize(expect, widthMeasureSpec);
		int height = resolveSize(expect, heightMeasureSpec);
		int mRealWidth = Math.min(width, height);
		mRadiu = (mRealWidth - getPaddingLeft() - getPaddingRight() - mPaintWidth) / 2;
		setMeasuredDimension(mRealWidth, mRealWidth);
		rectF = new RectF(0, 0, mRadiu * 2, mRadiu * 2);
	}

	private RectF rectF;

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// float radio = getProgress() * 1.0f / getMax();
		// String text = String.format("%.1f", radio * 100) + "%";
		String text = getProgress() + "%";
		float textWidth = mPaint.measureText(text);
		float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
		canvas.save();
		canvas.translate(getPaddingLeft() + mPaintWidth / 2, getPaddingTop() + mPaintWidth / 2);
		// unreach draw
		mPaint.setColor(mUnReachColor);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(mUnReachHeight);
		canvas.drawCircle(mRadiu, mRadiu, mRadiu, mPaint);
		// draw reach
		mPaint.setColor(mReachColor);
		mPaint.setStrokeWidth(mReachHeight);
		float angle = getProgress() * 1.0f / getMax() * 360f;
		canvas.drawArc(rectF, 0, angle, false, mPaint);
		//draw text
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);
		canvas.drawText(text, mRadiu-textWidth/2, mRadiu-textHeight, mPaint);

		canvas.restore();
	}
}
