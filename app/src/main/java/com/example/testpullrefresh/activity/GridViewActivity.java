package com.example.testpullrefresh.activity;

import java.util.ArrayList;

import com.example.testpullrefresh.R;
import com.example.testpullrefresh.adapter.SimpleAdapter;
import com.example.testpullrefresh.util.TabPartyPullRefreshCallback;
import com.uilib.widget.PullContainerView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 用ListView代替GridView
 * @author wangxp
 * @Date
 * @Function
 */
public class GridViewActivity extends FragmentActivity {
	
	private ArrayList<String> listData = new ArrayList<String>();
	private SimpleAdapter mAdapter;

	ListView mListView;
	PullContainerView mPullContainerListView;
	private ImageView mHeaderLeftImage, mHeaderRightImage, mHeaderTitleImage;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_listview);
		initView();
		initData();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.home_list_view);
		mHeaderLeftImage = (ImageView) findViewById(R.id.pull_image_left);
		mHeaderRightImage = (ImageView) findViewById(R.id.pull_image_right);
		mHeaderTitleImage = (ImageView) findViewById(R.id.pull_image_title);
		setHeaderImages(R.drawable.pull_tab_party_header_title_01);
		mPullContainerListView = (PullContainerView) findViewById(R.id.native_listview_layout);
		mPullContainerListView.setPullUpEnable(true);
		mPullContainerListView.setPullDownEnable(true);
		mPullContainerListView.setHeaderViewCallback(
				new TabPartyPullRefreshCallback(this, new TabPartyPullRefreshCallback.OnPushDownRefreshLisener() {
					@Override
					public void onPushDownRefresh() {
						mPullContainerListView.postDelayed(new Runnable() {

							@Override
							public void run() {
								mPullContainerListView.notifyFinishLoad(true);
								Toast.makeText(GridViewActivity.this, "下拉刷新完成", Toast.LENGTH_SHORT).show();
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
		mPullContainerListView.setFooterViewCallback(new FooterRefreshCallback(this, new OnFooterRefreshLisener() {

			@Override
			public void onFooterRefresh() {
				mPullContainerListView.postDelayed(new Runnable() {

					@Override
					public void run() {
						mPullContainerListView.notifyFinishLoad(true);
						mAdapter.addList(listData);
						Toast.makeText(GridViewActivity.this, "上拉刷新完成", Toast.LENGTH_SHORT).show();
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
		listData.add("航美集团");
		listData.add("航美在线");
		listData.add("航美移动");
		listData.add("航美传媒");
		listData.add("航美恒隆");
		listData.add("航美新干线");
		listData.add("往返");
		listData.add("工程管理");
		listData.add("航美集团");
		listData.add("航美在线");
		listData.add("航美移动");
		listData.add("航美传媒");
		listData.add("航美恒隆");
		listData.add("航美新干线");
		listData.add("往返");
		listData.add("工程管理");
		mAdapter = new com.example.testpullrefresh.adapter.SimpleAdapter(this);
		mListView.setAdapter(mAdapter);
		mAdapter.addList(listData);
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
