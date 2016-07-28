package com.martin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class HorizontalProgressBar extends ProgressBar {

	private final static int DEFAULT_UNREACH_HEIGHT = 2;// px
	private final static int DEFAULT_REACH_HEIGHT = 2;
	private final static int DEFAULT_UNREACH_COLOR = 0x44ff0000;
	private final static int DEFAULT_REACH_COLOR = 0xff0000;
	private final static int DEFAULT_TEXT_SIZE = 12;// sp
	private final static int DEFAULT_TEXT_COLOR = 0xff0000;
	private final static int DEFAULT_TEXT_OFFSET = 10;

	private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
	private int mTextColor = DEFAULT_TEXT_COLOR;
	private int mTextOffSet = dp2px(DEFAULT_TEXT_OFFSET);

	private int mReachColor = DEFAULT_REACH_COLOR;
	private int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
	private int mUnReachColor = DEFAULT_UNREACH_COLOR;
	private int mUnReachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);

	public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		obtainStyleValue(attrs);
	}

	public HorizontalProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBar(Context context) {
		this(context, null);
	}

	private void obtainStyleValue(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PB);
			mTextSize=(int) ta.getDimension(R.styleable.PB_progressBar_text_size, mTextSize);
			mTextOffSet=(int) ta.getDimension(R.styleable.PB_progressBar_text_offset, mTextOffSet);
			mTextColor=ta.getColor(R.styleable.PB_progressBar_text_color, mTextColor);
			mReachColor=ta.getColor(R.styleable.PB_progressBar_reach_color, mReachColor);
			mReachHeight=(int) ta.getDimension(R.styleable.PB_progressBar_reach_height, mReachHeight);
			mUnReachColor=ta.getColor(R.styleable.PB_progressBar_unreach_color, mUnReachColor);
			mUnReachHeight=(int) ta.getDimension(R.styleable.PB_progressBar_unreach_height, mUnReachHeight);
		ta.recycle();

	}

	private int dp2px(int dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources()
				.getDisplayMetrics());
	}

	private int sp2px(int spValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources()
				.getDisplayMetrics());
	}
}
