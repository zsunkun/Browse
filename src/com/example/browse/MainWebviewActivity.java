package com.example.browse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Android 4.0以后WebView的重定向返回没有问题 key:setting webViewClient
 * 
 * @author sunkun
 *
 */
public class MainWebviewActivity extends Activity {

	private WebView mWebView;
	private long mLastBackClickTime = 0;
	private int mScreenWidth;

	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_webview);
		mWebView = (WebView) findViewById(R.id.my_webview);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		mScreenWidth = getScreenWidth();
		String url = "http://www.baidu.com";
		// client.shouldOverrideUrlLoading(mWebView, url);
		mWebView.loadUrl(url);
		mWebView.setWebViewClient(client);
		mWebView.setOnTouchListener(mOnTouchListener);
		mWebView.addJavascriptInterface(new JavascriptInterface(this),
				"imagelistner");// 添加js交互接口类，并起别名 imagelistner
	}

	private int getScreenWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		float startX = 0;
		float endX = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				endX = event.getX();
				if (startX != 0 && endX != 0) {
					if (mWebView.canGoBack()
							&& ((startX - endX) < -mScreenWidth / 3)) {
						mWebView.goBack();
					}
					if (mWebView.canGoForward()
							&& ((startX - endX) > mScreenWidth / 3)) {
						mWebView.goForward();
					}
				}
			}
			return false;
		}
	};

	// js通信接口
	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void opentouch() {
			System.out.println("opentouch--");
		}

		public void closetouch() {
			System.out.println("closetouch--");
		}
	}

	// 注入js函数监听
	private void addimageSlidingListener() {
		mWebView.loadUrl("javascript:(function(){"
				+ "var objs = $('.swiper-container'); " // 轮播图片的div容器，
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].ontouchmove=function()  "
				+ "    {  "
				+ "        window.imagelistner.closetouch();  "
				+ "   return false; }  "
				+ "}"

				+ "var objs = $('body'); " // 点击任何地方 ， 系统开始可以接受滚动
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].ontouchstart=function()  " + "    {  "
				+ "        window.imagelistner.opentouch();  "
				+ "   return false; }  " + "}"

				+ "})()");
	}

	private WebViewClient client = new WebViewClient() {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// view.loadUrl(url);
			return false;
		}

		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			addimageSlidingListener();//not work
		};
	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		long now = System.currentTimeMillis();
		if (now - mLastBackClickTime < 1000) {
			super.onBackPressed();
			return;
		}
		mLastBackClickTime = now;
		Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
	}
}
