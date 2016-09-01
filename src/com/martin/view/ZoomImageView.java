package com.martin.view;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,
		OnScaleGestureListener, OnTouchListener {

	private boolean isOnec = true;
	// 初始化时应该进行的缩放
	private float mInitScanle;
	// 缩小的最大值
	private float mMinScanle;
	// 放大的最大值
	private float mMaxScanle;
	// 双击放大的值
	private float mMidScanle;
	// 图像变化控制的矩阵
	private Matrix mMatrix;

	// 多指触控操作的控制图片的放大缩小
	private ScaleGestureDetector mScaleGestureDetector;
	// 双击触控
	private GestureDetector mGestureDetector;

	// ------自由移动---------

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mMatrix = new Matrix();
		this.setScaleType(ScaleType.MATRIX);
		this.setOnTouchListener(this);
		this.setBackgroundColor(context.getResources().getColor(R.color.black));
		mtouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mGestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);
	}

	private boolean isScanning=false;
	protected GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
		public boolean onDoubleTap(MotionEvent e) {
			if (isScanning) return true;
			int x = (int) e.getX();
			int y = (int) e.getY();
			if (getScale() < mMidScanle) {
				//没有动画的缩放
//				mMatrix.postScale(mMidScanle / getScale(), mMidScanle / getScale(), x, y);
//				checkBorder();
//				ZoomImageView.this.setImageMatrix(mMatrix);
				postDelayed(new AutoScaleRunnable(mMidScanle, x, y), 0);
				isScanning=true;
			} else {
//				mMatrix.postScale(mInitScanle / getScale(), mInitScanle / getScale(), x, y);
//				checkBorder();
//				ZoomImageView.this.setImageMatrix(mMatrix);
				postDelayed(new AutoScaleRunnable(mInitScanle, x, y), 0);
				isScanning=true;
			}
			return true;
		}
	};
	private float tmpScale;
	private float currentScale;
	private float BIGGER=1.05f;
	private float SMALL=0.95f;
	private class AutoScaleRunnable implements Runnable {
		private float mTargetScale;
		private float x;
		private float y;

		public AutoScaleRunnable(float mTargetScale, float x, float y) {
			super();
			this.mTargetScale = mTargetScale;
			this.x = x;
			this.y = y;
			if (getScale()<mTargetScale) {
				tmpScale=BIGGER;
			}else if(getScale()>mTargetScale){
				tmpScale=SMALL;
			}
		}

		@Override
		public void run() {
			mMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorder();
			setImageMatrix(mMatrix);
			
			currentScale=getScale();
			if ((currentScale<mTargetScale&&tmpScale>1.0f)||(currentScale>mTargetScale&&tmpScale<1.0f)) {
				postDelayed(this, 16);
			}else{
				mMatrix.postScale(mTargetScale/getScale(), mTargetScale/getScale(), x, y);
				checkBorder();
				setImageMatrix(mMatrix);
				isScanning=false;
			}

		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeOnGlobalLayoutListener(this);
	}

	// 布局layout结束后
	private int dw;
	private int dh;

	@Override
	public void onGlobalLayout() {
		if (isOnec) {
			isOnec = false;
			// view的尺寸
			int width = getWidth();
			int height = getHeight();
			// 图片的尺寸
			Drawable drawable = this.getDrawable();
			if (drawable == null) {
				return;
			}
			dw = drawable.getIntrinsicWidth();
			dh = drawable.getIntrinsicHeight();
			// 初始化时匹配屏幕,四种情况，宽度越界、高度越界、全部越界、全部没越界
			float scanle = 1.0f;
			if (dw >= width && dh <= height) {
				scanle = width * 1.0f / dw;
			}
			if (dw <= width && dh >= height) {
				scanle = height * 1.0f / dh;
			}
			if (dw >= width && dh >= height) {
				scanle = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}
			if (dw <= width && dh <= height) {
				scanle = Math.min(width * 1.0f / dw, height * 1.0f / width);
			}
			mInitScanle = scanle;
			mMinScanle = scanle / 2;
			mMidScanle = scanle * 2;
			mMaxScanle = scanle * 4;

			int dx = getWidth() / 2 - dw / 2;
			int dy = getHeight() / 2 - dh / 2;

			mMatrix.postTranslate(dx, dy);
			// 以某一点为中心点缩放图片
			mMatrix.postScale(mInitScanle, mInitScanle, getWidth() / 2, getHeight() / 2);
			setImageMatrix(mMatrix);
		}
	}

	/**
	 * 当前缩放的数值,这个值不是相对于初始化的图片缩放了多少，是指当前显示的图片相对于原始图片的缩放比例
	 */
	private float getScale() {
		float[] values = new float[9];
		mMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	float preScale = 1.0f;

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (getDrawable() == null) {
			return true;
		}
		// 当前图片已经缩放的比例
		float scale = getScale();
		// 期望的缩放比例
		float scaleFactor = detector.getScaleFactor();

		if ((scale < mMaxScanle && scaleFactor > 1.0f)
				|| (scale > mMinScanle && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mMinScanle) {
				scaleFactor = mMinScanle / scale;
			}
			if (scale * scaleFactor > mMaxScanle) {
				scaleFactor = mMaxScanle / scale;
			}
			// 此处容易造成缩放的积累，导致缩放过快，不是线性缩放
			mMatrix.postScale(scaleFactor / preScale, scaleFactor / preScale, detector.getFocusX(),
					detector.getFocusY());
			preScale = scaleFactor;
			// 检测图片是否越界，如果越界调整到正确的位置
			checkBorder();
			setImageMatrix(mMatrix);
		}
		return false;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		preScale = 1.0f;
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		if (getScale() < mInitScanle) {
			mMatrix.postScale(mInitScanle / getScale(), mInitScanle / getScale(), getWidth() / 2,
					getHeight() / 2);
			this.setImageMatrix(mMatrix);
		}
	}

	/**
	 * 缩放时进行边界控制，防止出现不合理的显示位置
	 */
	private void checkBorder() {
		RectF rectF = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;
		float width = getWidth();
		float height = getHeight();
		// 防止出现白边 宽度调整
		if (rectF.width() >= width) {
			if (rectF.left > 0) {
				deltaX = -rectF.left;
			} else if (rectF.right < width) {
				deltaX = width - rectF.right;
			}
		}
		// 高度调整
		if (rectF.height() >= height) {
			if (rectF.top > 0) {
				deltaY = -rectF.top;
			} else if (rectF.bottom < height) {
				deltaY = height - rectF.bottom;
			}
		}
		// 调整水平位置
		if (rectF.width() < width) {
			deltaX = width / 2f - rectF.width() / 2f - rectF.left;
		}
		// 调整竖直位置
		if (rectF.height() < height) {
			deltaY = height / 2f - rectF.height() / 2f - rectF.top;
		}
		mMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * mMatrix，改变矩阵，drawable图像获得改变的控件参数，宽高，l r b t。
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mMatrix;
		RectF rectF = new RectF();
		Drawable drawable = this.getDrawable();
		if (drawable != null) {
			rectF.set(0, 0, dw, dh);
			matrix.mapRect(rectF);
		}
		return rectF;
	}

	int mLastPointCount = 0;
	float mLastX;
	float mLastY;
	private boolean isCanMoveable;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}
		mScaleGestureDetector.onTouchEvent(event);
		float tx = 0;
		float ty = 0;
		// 手指的中心点位置
		int pointCount = event.getPointerCount();
		for (int i = 0; i < pointCount; i++) {
			tx += event.getX(i);
			ty += event.getY(i);
		}
		tx /= pointCount;
		ty /= pointCount;
		// 防止触控点数变化时造成图像瞬移
		if (pointCount != mLastPointCount) {
			mLastX = tx;
			mLastY = ty;
			mLastPointCount = pointCount;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = tx - mLastX;
			float dy = ty - mLastY;
			if (!isCanMoveable) {

				if (isMoveAction(dx, dy)) {
					isCanMoveable = true;
				}

			} else {
				RectF rectF = getMatrixRectF();
				if (rectF.width() < getWidth()) {
					dx = 0;
				}
				if (rectF.height() < getHeight()) {
					dy = 0;
				}

				mMatrix.postTranslate(dx, dy);
				// 移动的边界检测
				RectF moveRectF = getMatrixRectF();
				if (moveRectF.left > 0 || moveRectF.right < getWidth()) {
					mMatrix.postTranslate(-dx, 0);
				}
				if (moveRectF.top > 0 || moveRectF.bottom < getHeight()) {
					mMatrix.postTranslate(0, -dy);
				}
				setImageMatrix(mMatrix);
			}
			mLastX = tx;
			mLastY = ty;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLastPointCount = 0;
			break;

		default:
			break;
		}

		return true;
	}

	int mtouchSlop;

	private boolean isMoveAction(float dx, float dy) {

		return Math.sqrt(dx * dx + dy * dy) > mtouchSlop;
	}
}
