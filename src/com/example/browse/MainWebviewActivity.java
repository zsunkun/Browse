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
 * Android 4.0�Ժ�WebView���ض��򷵻�û������ key:setting webViewClient
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
				"imagelistner");// ���js�����ӿ��࣬������� imagelistner
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

	// jsͨ�Žӿ�
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

	// ע��js��������
	private void addimageSlidingListener() {
		mWebView.loadUrl("javascript:(function(){"
				+ "var objs = $('.swiper-container'); " // �ֲ�ͼƬ��div������
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].ontouchmove=function()  "
				+ "    {  "
				+ "        window.imagelistner.closetouch();  "
				+ "   return false; }  "
				+ "}"

				+ "var objs = $('body'); " // ����κεط� �� ϵͳ��ʼ���Խ��ܹ���
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
			// html�������֮����Ӽ���ͼƬ�ĵ��js����
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
		Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
	}
}
