package com.martin.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 
 * @author Martin.Han
 * 
 */
public class Indicator extends LinearLayout {

	private int cellHeight;
	private int cellWidth;
	private int cellCount;
	private int traslatX;

	private Path mPath;
	private Paint mPaint;

	private int tabCount;
	private int DEFAULT_TAB_COUT = 3;

	private LinearLayout container;
	private List<View> views = new ArrayList<View>();

	public Indicator(Context context) {
		this(context, null);
	}

	public Indicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.view_indicator, this);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Style.FILL);
		// 获取自定义的属性值
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndicatorPro);
		tabCount = ta.getInt(R.styleable.IndicatorPro_tab_visible_count, -1);
		ta.recycle();
		if (tabCount < 0) {
			tabCount = DEFAULT_TAB_COUT;
		}
		intUI();
		setViewListener();
	}

	private void intUI() {
		container = (LinearLayout) findViewById(R.id.container);
		cellCount = container.getChildCount();
		for (int i = 0; i < cellCount; i++) {
			View v = container.getChildAt(i);
			v.setTag(i);
			views.add(v);
		}
	}

	int i;

	private void setViewListener() {
		for (i = 0; i < views.size(); i++) {
			views.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int flag = (Integer) v.getTag();
					scroll(flag, 0);
					if (onPagerChangeTo != null) {
						onPagerChangeTo.onPagerTo(0);
					}
				}
			});
		}

	}

	int viewWidth;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;

		cellHeight = h;
		cellWidth = w / tabCount;
		traslatX = 0;
		// 初始化下部的指示标记
		mPath = new Path();
		mPath.moveTo(0, 0);
		mPath.lineTo(0, 3);
		mPath.lineTo(cellWidth, 3);
		mPath.lineTo(cellWidth, 0);
		mPath.close();
		// 指示滚动条显示多少个tab

	}

	// 绘制本身，如果本身是ViewGroup,且没有背景则跳过此函数
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	// 绘制孩子View
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		canvas.save();
		canvas.translate(traslatX, cellHeight - 3);
		canvas.drawPath(mPath, mPaint);
		canvas.restore();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (cellCount <= 0)
			return;
		for (int i = 0; i < cellCount; i++) {
			View view = container.getChildAt(i);
			LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
			params.width = 1080 / tabCount;
			view.setLayoutParams(params);
		}

	}

	// 指示游标参数改变，数显Views
	public void scroll(int position, float offSet) {
		traslatX = (int) (cellWidth * position + cellWidth * offSet);
		// 整体左移x，游标右移x，看起来是没移动的
		if (position >= tabCount - 1) {
			this.scrollTo((int) ((position - (tabCount - 1)) * cellWidth + offSet * cellWidth), 0);
		}

		this.invalidate();
	}

	private OnPagerChangeTo onPagerChangeTo;

	public void setOnPagerChangeTo(OnPagerChangeTo onPagerChangeTo) {
		this.onPagerChangeTo = onPagerChangeTo;
	}

	public interface OnPagerChangeTo {
		public void onPagerTo(int position);
	}
}
