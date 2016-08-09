package com.martin.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.martin.view.R;

public class HomeActivity extends Activity implements OnClickListener {
	private Button indicator;
	private Button indexBar;
	private Button progressBar;
	private Button imageView;
	private Intent intent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		initUI();
	}

	private void initUI() {
		indicator = (Button) findViewById(R.id.indicator);
		indexBar = (Button) findViewById(R.id.indexbar);
		progressBar=(Button) findViewById(R.id.progressBar);
		imageView=(Button) findViewById(R.id.imageView);
		
		indicator.setOnClickListener(this);
		indexBar.setOnClickListener(this);
		progressBar.setOnClickListener(this);
		imageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.indexbar:
			intent.setClass(this, IndexBarActivity.class);
			startActivity(intent);
			break;

		case R.id.indicator:
			intent.setClass(this, IndicatorActivity.class);
			startActivity(intent);
			break;
		case R.id.progressBar:
			intent.setClass(this, ProgressBarActivity.class);
			startActivity(intent);
			break;
		case R.id.imageView:
			intent.setClass(this, ZoomImageActivity.class);
			startActivity(intent);
			break;
		}
	}
}
