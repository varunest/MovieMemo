package com.varunest.moviememo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class about extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		    //Remove notification bar
		setContentView(R.layout.about);
		WebView wv = (WebView)findViewById(R.id.about_webview);
		wv.loadUrl("file:///android_asset/test.html");
	}
}
