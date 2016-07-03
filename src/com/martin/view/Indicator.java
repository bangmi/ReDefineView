package com.martin.view;

import com.martin.view.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class Indicator extends LinearLayout {

	private int cellHeight;
	private int cellWidth;
	private int cellCount;
	private int traslatX;
	
	private Path mPath;
	private Paint mPaint;
	public Indicator(Context context) {
		this(context, null);
	}

	public Indicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.view_indicator,this);
		cellCount=((LinearLayout)getChildAt(0)).getChildCount();
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Style.FILL);
		
		setViewListener();
	}
	private void setViewListener() {
		LinearLayout views=(LinearLayout)getChildAt(0);
			views.getChildAt(0).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					scroll(0, 0);
					if (onPagerChangeTo!=null) {
						onPagerChangeTo.onPagerTo(0);
					}
				}
			});
			views.getChildAt(1).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					scroll(1, 0);
					if (onPagerChangeTo!=null) {
						onPagerChangeTo.onPagerTo(1);
					}
				}
			});
			views.getChildAt(2).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					scroll(2, 0);
					if (onPagerChangeTo!=null) {
						onPagerChangeTo.onPagerTo(2);
					}
				}
			});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		cellHeight=h;
		cellWidth=w/cellCount;
		traslatX=0;
		//初始化下部的指示标记
		mPath=new Path();
		mPath.moveTo(0, 0);
		mPath.lineTo(0, 3);
		mPath.lineTo(cellWidth, 3);
		mPath.lineTo(cellWidth, 0);
		mPath.close();
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		canvas.save();
		canvas.translate(traslatX, cellHeight-3);
		canvas.drawPath(mPath, mPaint);
		canvas.restore();
	}

	public void scroll(int position, float offSet) {
		traslatX=(int) (cellWidth*position+cellWidth*offSet);
		System.out.println(position);
		this.invalidate();
	}

	private OnPagerChangeTo onPagerChangeTo;
	
	public void  setOnPagerChangeTo(OnPagerChangeTo onPagerChangeTo){
		this.onPagerChangeTo=onPagerChangeTo;
	}
	
	public interface OnPagerChangeTo{
		public void onPagerTo(int position);
	}
}
