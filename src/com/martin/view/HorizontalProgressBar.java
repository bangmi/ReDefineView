package com.martin.view;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class HorizontalProgressBar extends ProgressBar {

	private final static int DEFAULT_UNREACH_HEIGHT = 2;// px
	private final static int DEFAULT_REACH_HEIGHT = 2;
	private final static int DEFAULT_UNREACH_COLOR = 0x44ff0000;
	private final static int DEFAULT_REACH_COLOR = 0xBCBCBCBC;
	private final static int DEFAULT_TEXT_SIZE = 12;// sp
	private final static int DEFAULT_TEXT_COLOR = 0x44ff0000;
	private final static int DEFAULT_TEXT_OFFSET = 10;

	protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
	protected int mTextColor = DEFAULT_TEXT_COLOR;
	protected int mTextOffSet = dp2px(DEFAULT_TEXT_OFFSET);

	protected int mReachColor = DEFAULT_REACH_COLOR;
	protected int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
	protected int mUnReachColor = DEFAULT_UNREACH_COLOR;
	protected int mUnReachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);

	protected Paint mPaint;
	private int mRealWidth;

	public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeCap(Cap.ROUND);
		obtainStyleValue(attrs);
	}

	public HorizontalProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBar(Context context) {
		this(context, null);
	}

	/**
	 * 获取自定义属性
	 * 
	 */
	private void obtainStyleValue(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PB);
		mTextSize = (int) ta.getDimension(R.styleable.PB_progressBar_text_size, mTextSize);
		mTextOffSet = (int) ta.getDimension(R.styleable.PB_progressBar_text_offset, mTextOffSet);
		mTextColor = ta.getColor(R.styleable.PB_progressBar_text_color, mTextColor);
		mReachColor = ta.getColor(R.styleable.PB_progressBar_reach_color, mReachColor);
		mReachHeight = (int) ta.getDimension(R.styleable.PB_progressBar_reach_height, mReachHeight);
		mUnReachColor = ta.getColor(R.styleable.PB_progressBar_unreach_color, mUnReachColor);
		mUnReachHeight = (int) ta.getDimension(R.styleable.PB_progressBar_unreach_height,
				mUnReachHeight);
		ta.recycle();
		// 不要忘记了设置文字大小
		mPaint.setTextSize(mTextSize);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthVal = MeasureSpec.getSize(widthMeasureSpec);

		int heightVal = measureHeight(heightMeasureSpec);
		// 设置控件的值，调用过此方法后getMeasuredHeight()就能得到真正的View的参数了
		setMeasuredDimension(widthVal, heightVal);
		// 去掉填充内边距，得到实际的绘制区域
		mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();

	}

	/**
	 * 开始绘画
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(getPaddingLeft(), getMeasuredHeight() / 2);

		boolean isNeedUnReach = false;

		float radio = getProgress() * 1.0f / getMax();
		String text = String.format("%.1f", radio * 100) + "%";
		float textWidth = mPaint.measureText(text);
		float progressX = radio * (mRealWidth - textWidth - mTextOffSet * 2);
		if (progressX + textWidth + mTextOffSet * 2 > mRealWidth) {
			progressX = mRealWidth - textWidth - mTextOffSet * 2;
			isNeedUnReach = true;
		}
		// 绘制进度
		float endX = progressX;
		if (endX > 0) {
			mPaint.setColor(mReachColor);
			mPaint.setStrokeWidth(mReachHeight);
			canvas.drawLine(0, 0, endX, 0, mPaint);
		}
		// 绘制数字
		mPaint.setColor(mTextColor);
		float y = -(mPaint.ascent() + mPaint.descent()) / 2;
		canvas.drawText(text, progressX + mTextOffSet, y, mPaint);
		// 绘制未到达的进度
		if (!isNeedUnReach) {
			float startX = progressX + mTextOffSet * 2 + textWidth;
			mPaint.setColor(mUnReachColor);
			mPaint.setStrokeWidth(mUnReachHeight);
			canvas.drawLine(startX, 0, mRealWidth, 0, mPaint);
		}

		canvas.restore();
	}

	private int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int model = MeasureSpec.getMode(heightMeasureSpec);
		int size = MeasureSpec.getSize(heightMeasureSpec);

		if (model == MeasureSpec.EXACTLY) {
			// 用户已经指定了明确的值，除了数值外，match_parent也是明确的值
			result = size;
		} else {
			int textHeight = (int) (mPaint.descent() - mPaint.ascent());
			result = getPaddingTop() + getPaddingBottom()
					+ Math.max(Math.abs(textHeight), Math.max(mReachHeight, mUnReachHeight));

			if (model == MeasureSpec.AT_MOST) {
				// 这种模式下表示测量的值不能超过指定的值size
				result = Math.min(result, size);
			}

		}
		return result;
	}

	protected int dp2px(int dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources()
				.getDisplayMetrics());
	}

	protected int sp2px(int spValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources()
				.getDisplayMetrics());
	}
}
