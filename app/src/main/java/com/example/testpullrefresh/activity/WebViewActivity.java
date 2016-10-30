package com.example.testpullrefresh.activity;

import com.example.testpullrefresh.R;
import com.example.testpullrefresh.R.drawable;
import com.example.testpullrefresh.R.id;
import com.example.testpullrefresh.R.layout;
import com.example.testpullrefresh.util.TabPartyPullRefreshCallback;
import com.uilib.widget.PullContainerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class WebViewActivity extends Activity implements OnClickListener{

	WebView mWebView;
	PullContainerView mPullContainerWebView;
	private ImageView mHeaderLeftImage, mHeaderRightImage, mHeaderTitleImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		initView();
	}
	
	private void initView(){
		mHeaderLeftImage = (ImageView) findViewById(R.id.pull_image_left);
        mHeaderRightImage = (ImageView) findViewById(R.id.pull_image_right);
        mHeaderTitleImage = (ImageView) findViewById(R.id.pull_image_title);
        initWebView();
        setHeaderImages(R.drawable.pull_tab_party_header_title_01);
        mPullContainerWebView = (PullContainerView)findViewById(R.id.webview_layout);
        mPullContainerWebView.setHeaderViewCallback(new TabPartyPullRefreshCallback(this,new TabPartyPullRefreshCallback.OnPushDownRefreshLisener(){
            @Override
            public void onPushDownRefresh() {
                mWebView.reload();
            }
        }));
	}
	
	private void initWebView(){
		mWebView = (WebView)findViewById(R.id.home_webview);
		mWebView.setOnClickListener(this);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 关闭缓存
		// LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
		// LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据。
		// LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
		// LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
		// LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.getSettings().setAppCacheEnabled(false);
		// //允许访问文件
		// webSettings.setAllowFileAccess(true);

		// 禁用外跳
		mWebView.setWebViewClient(new MyWebViewClient());

		mWebView.loadUrl("http://www.baidu.com");
	}
	
	public void setHeaderImages(int id){
        mHeaderTitleImage.setImageResource(id);
        mHeaderLeftImage.setImageResource(R.drawable.pull_tab_party_header_image_left);
        mHeaderRightImage.setImageResource(R.drawable.pull_tab_party_header_image_right);
    }
	
	/**
	 * WEBVIEW
	 */
	private class MyWebViewClient extends WebViewClient {
		private ProgressDialog progressDialog;

		public MyWebViewClient() {
			progressDialog = new ProgressDialog(WebViewActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage("努力加载中...");
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			String lowercase = url.toLowerCase();
			if (lowercase.startsWith("http://") || lowercase.startsWith("https://")) {
				return false;
			}
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			
		}

		@Override
		public void onPageStarted(WebView view, String url,
				android.graphics.Bitmap favicon) {
			progressDialog.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			progressDialog.dismiss();
			mPullContainerWebView.setEnabled(true);
	        mPullContainerWebView.notifyFinishLoad(true);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
