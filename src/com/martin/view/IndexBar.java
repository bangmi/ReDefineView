package com.martin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Android中有两个坐标系，View中的原点在左上角，但是绘制时从左下角开始绘制
 * 
 * @author Martin.Han
 * 
 */
public class IndexBar extends View {
	private int cellHeight = 0;
	private int cellWidth = 0;

	private String[] indexs = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "G", "K", "L", "M", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private Paint mPaint;
	private Paint cPaint;
	private Paint tPaint;
	private Paint rPaint;
	private Rect rect;

	// 只提供布局文件中初始化的方式
	public IndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.setBackgroundResource(R.drawable.selector_indexbar_press);
		init();
	}

	private void init() {
		rect = new Rect();
		// 背景字母的画笔
		mPaint = new Paint();
		mPaint.setColor(Color.parseColor("#565656"));
		mPaint.setTextSize(35);
		// mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setAntiAlias(true);// 抗锯齿
		// 橘色圆形选中区域的画笔
		cPaint = new Paint();
		cPaint.setColor(Color.parseColor("#EE5B1A"));
		cPaint.setAntiAlias(true);// 抗锯齿
		// 选中的白色字体的画笔
		tPaint = new Paint();
		tPaint.setColor(Color.parseColor("#FFFFFF"));
		tPaint.setTextSize(40);
		tPaint.setTypeface(Typeface.DEFAULT_BOLD);
		tPaint.setAntiAlias(true);// 抗锯齿
		// View触发时间背景的画笔
		rPaint = new Paint();
		rPaint.setColor(Color.parseColor("#BFBFBF"));
		rPaint.setAntiAlias(true);// 抗锯齿
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		cellWidth = w;
		cellHeight = h / indexs.length;
	}

	/**
	 * 绘画时是在左下角为原点开始绘制的，注意计算好正确的绘制坐标，整个View的坐标是左上角为原点，需要转换坐标 绘画时不要有耗时操作，提高绘制效率
	 * paint 提供了测量文字宽度的方法，但是没有测量高度的，只能使用矩形间接得到
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// System.out.println("onDraw..");
		if (cellHeight == 0)
			cellHeight = getMeasuredHeight();
		if (cellWidth == 0)
			cellWidth = getMeasuredWidth();
		float x = 0;
		float y = 0;
		// 按压下去时画背景，否则不用绘制背景色
		if (isTouched) {
			canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), rPaint);
		}
		// 分别绘画字母条目，棕色的原型选中原，白色的选中字体
		for (int i = 0; i < indexs.length; i++) {
			x = cellWidth / 2 - mPaint.measureText(indexs[i]) / 2;
			mPaint.getTextBounds(indexs[i], 0, indexs[i].length(), rect);
			y = cellHeight * i + cellHeight / 2 + rect.height() / 2;
			canvas.drawText(indexs[i], x, y, mPaint);
		}
		if (isTouched) {
			canvas.drawCircle(cellWidth / 2, touchY, 30, cPaint);
			int position = (int) (touchY / cellHeight);
			// 避免indexs[]越界
			if (position < indexs.length && position >= 0) {
				x = cellWidth / 2 - mPaint.measureText(indexs[position]) / 2;
				y = cellHeight * position + cellHeight / 2 + rect.height() / 2;
				tPaint.getTextBounds(indexs[position], 0, 1, rect);
				canvas.drawText(indexs[position], x, y, tPaint);
			}
		}
	}

	float touchY;
	int touchIndex;
	int flag = -1;
	boolean isTouched = false;

	/**
	 * getY()View坐标系，getRawY()屏幕坐标系;
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			isTouched = true;
			touchY = event.getY();
			touchIndex = (int) (touchY / cellHeight);
			// 避免在一个格子滑动时重复输出
			if (touchIndex != flag) {
				flag = touchIndex;
				if (touchIndex >= 0 && touchIndex < indexs.length) {
					if (listener != null) {
						listener.index(indexs[touchIndex]);
					}
				}
				this.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (listener != null) {
				listener.cancle();
			}
			isTouched = false;
			flag = -1;
			this.invalidate();
			break;

		}
		// 只要触发onTouchEvent就重绘控件,这个地方调用会增加绘制的次数
		// this.invalidate();
		return true;
	}

	private OnIndexListener listener;

	public void setOnIndexListener(OnIndexListener listener) {
		this.listener = listener;
	}

	public interface OnIndexListener {
		public void index(String index);

		public void cancle();
	}
}
