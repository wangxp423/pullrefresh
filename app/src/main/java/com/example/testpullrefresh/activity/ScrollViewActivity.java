package com.example.testpullrefresh.activity;

import com.example.testpullrefresh.R;
import com.example.testpullrefresh.util.TabPartyPullRefreshCallback;
import com.uilib.widget.PullContainerView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollViewActivity extends FragmentActivity {
	

	ScrollView mScrollView;
	ViewGroup mContainerLayout;
	PullContainerView mPullContainerScrollView;
	private ImageView mHeaderLeftImage, mHeaderRightImage, mHeaderTitleImage;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_scrollview);
		initView();
		initData();
	}

	private void initView() {
		mScrollView = (ScrollView) findViewById(R.id.home_scroll_view);
		mContainerLayout = (ViewGroup) findViewById(R.id.home_scroll_linearlayout);
		mHeaderLeftImage = (ImageView) findViewById(R.id.pull_image_left);
		mHeaderRightImage = (ImageView) findViewById(R.id.pull_image_right);
		mHeaderTitleImage = (ImageView) findViewById(R.id.pull_image_title);
		setHeaderImages(R.drawable.pull_tab_party_header_title_01);
		mPullContainerScrollView = (PullContainerView) findViewById(R.id.native_scrollview_layout);
		mPullContainerScrollView.setPullUpEnable(true);
		mPullContainerScrollView.setPullDownEnable(true);
		mPullContainerScrollView.setHeaderViewCallback(
				new TabPartyPullRefreshCallback(this, new TabPartyPullRefreshCallback.OnPushDownRefreshLisener() {
					@Override
					public void onPushDownRefresh() {
						mPullContainerScrollView.postDelayed(new Runnable() {

							@Override
							public void run() {
								mPullContainerScrollView.notifyFinishLoad(true);
								Toast.makeText(ScrollViewActivity.this, "下拉刷新完成", Toast.LENGTH_SHORT).show();
							}
						}, 2000);
					}
				}));
//		mPullContainerListView.setFooterViewCallback(
//				new TabPartyPullRefreshCallback(this, new TabPartyPullRefreshCallback.OnPushDownRefreshLisener() {
//					@Override
//					public void onPushDownRefresh() {
//						mPullContainerListView.postDelayed(new Runnable() {
//							
//							@Override
//							public void run() {
//								mPullContainerListView.notifyFinishLoad(true);
//								Toast.makeText(ListViewActivity.this, "上拉刷新完成", Toast.LENGTH_SHORT).show();
//							}
//						}, 2000);
//					}
//				}));
		mPullContainerScrollView.setFooterViewCallback(new FooterRefreshCallback(this, new OnFooterRefreshLisener() {

			@Override
			public void onFooterRefresh() {
				mPullContainerScrollView.postDelayed(new Runnable() {

					@Override
					public void run() {
						initData();
						mPullContainerScrollView.notifyFinishLoad(true);
						Toast.makeText(ScrollViewActivity.this, "上拉刷新完成", Toast.LENGTH_SHORT).show();
					}
				}, 2000);
			}
		}));
	}

	public void setHeaderImages(int id) {
		mHeaderTitleImage.setImageResource(id);
		mHeaderLeftImage.setImageResource(R.drawable.pull_tab_party_header_image_left);
		mHeaderRightImage.setImageResource(R.drawable.pull_tab_party_header_image_right);
	}

	public void initData() {
		for (int i = 0; i < 15; i++) {
			TextView textView = (TextView) LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null, true);
			textView.setText("第" + i + "条数据");
			mContainerLayout.addView(textView);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

	}

	public class FooterRefreshCallback implements PullContainerView.HeaderAndFooterViewCallback {

		OnFooterRefreshLisener mCallback;
		Animation mClockwiseAnim;
		Animation mAntiClockwiseAnim;
		int mSuggestedHeight;

		public FooterRefreshCallback(Context context, OnFooterRefreshLisener callback) {
			mCallback = callback;
			mClockwiseAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise_half);
			mAntiClockwiseAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_anticlockwise_half);
			mSuggestedHeight = context.getResources()
					.getDimensionPixelSize(R.dimen.homepage_pull_header_suggested_height);
		}

		@Override
		public void onStateChanged(View view, int newState, int oldState) {
			switch (newState) {
			case PullContainerView.PULL_STATE_NONE: { // 激活状态(下拉可以刷新)
				Log.d("Test", "上拉 = PULL_STATE_NONE");
			}
				break;
			case PullContainerView.PULL_STATE_ENABLE: { // 激活状态(松开可以刷新)
				Log.d("Test", "上拉 = PULL_STATE_ENABLE");
			}
				break;
			case PullContainerView.PULL_STATE_PULLING: {
				Log.d("Test", "上拉 = PULL_STATE_PULLING");
			}
				break;
			case PullContainerView.PULL_STATE_LOADING: { // 刷新中
				Log.d("Test", "上拉 = PULL_STATE_LOADING");
				if (mCallback != null)
					mCallback.onFooterRefresh();
			}
				break;
			}
		}

		@Override
		public void onHeightChanged(View view, int height, int state) {
		}
	}

	public interface OnFooterRefreshLisener {
		void onFooterRefresh();
	}

}
