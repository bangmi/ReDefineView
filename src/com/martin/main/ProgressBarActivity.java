package com.martin.main;

import com.martin.view.HorizontalProgressBar;
import com.martin.view.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class ProgressBarActivity extends Activity {

	private HorizontalProgressBar progressBar;

	private final static int MSG_UPDATE = 0x001;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int progress = progressBar.getProgress();
			progressBar.setProgress(++progress);
			if (progress < 100) {
				mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 100);
			} else {
				mHandler.removeMessages(MSG_UPDATE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_progressbar);
		progressBar = (HorizontalProgressBar) findViewById(R.id.progressBar);
		mHandler.sendEmptyMessage(MSG_UPDATE);
	}

}
