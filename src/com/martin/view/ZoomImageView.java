package com.martin.view;

import android.R.integer;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

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
		super.setScaleType(ScaleType.MATRIX);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		this.setOnTouchListener(this);
		mtouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
	@Override
	public void onGlobalLayout() {
		if (isOnec) {
			isOnec = false;
			// view的尺寸
			int width = getWidth();
			int height = getHeight();
			// 图片的尺寸
			Drawable drawable = getDrawable();
			if (drawable == null) {
				return;
			}
			int dw = drawable.getIntrinsicWidth();
			int dh = drawable.getIntrinsicHeight();
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
			mMinScanle = mInitScanle / 2;
			mMidScanle = mInitScanle * 2;
			mMaxScanle = mInitScanle * 4;

			int dx = getWidth() / 2 - dw / 2;
			int dy = getHeight() / 2 - dh / 2;

			mMatrix.postTranslate(dx, dy);
			mMatrix.postScale(mInitScanle, mInitScanle, getWidth() / 2, getHeight() / 2);
			setImageMatrix(mMatrix);
		}
	}

	/**
	 * 当前缩放的数值
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
		// 宽度调整
		if (rectF.width() >= width) {
			if (rectF.left > 0) {
				deltaX = -rectF.left;
			} else if (rectF.right < width) {
				deltaX = width - rectF.right;
			}
		}
		// 高度调整
		if (rectF.height() > height) {
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
		Drawable drawable = getDrawable();
		if (drawable != null) {
			rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
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
				//移动的边界检测
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
