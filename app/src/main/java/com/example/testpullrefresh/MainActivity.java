package com.example.testpullrefresh;

import com.example.testpullrefresh.activity.GridViewActivity;
import com.example.testpullrefresh.activity.ListViewActivity;
import com.example.testpullrefresh.activity.RealGridViewActivity;
import com.example.testpullrefresh.activity.ScrollViewActivity;
import com.example.testpullrefresh.activity.WebViewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends FragmentActivity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		findViewById(R.id.main_test_webview_pull).setOnClickListener(this);
		findViewById(R.id.main_test_listview_pull).setOnClickListener(this);
		findViewById(R.id.main_test_scrollview_pull).setOnClickListener(this);
		findViewById(R.id.main_test_listview_couple_pull).setOnClickListener(this);
		findViewById(R.id.main_test_gridview_pull).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_test_webview_pull:
			startActivity(new Intent(this, WebViewActivity.class));
			break;
		case R.id.main_test_listview_pull:
			startActivity(new Intent(this, ListViewActivity.class));
			break;
		case R.id.main_test_scrollview_pull:
			startActivity(new Intent(this, ScrollViewActivity.class));
			break;
		case R.id.main_test_listview_couple_pull:
			startActivity(new Intent(this, GridViewActivity.class));
			break;
		case R.id.main_test_gridview_pull:
			startActivity(new Intent(this, RealGridViewActivity.class));
			break;

		default:
			break;
		}
		
	}

}
