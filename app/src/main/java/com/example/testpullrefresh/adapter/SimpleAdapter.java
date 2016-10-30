package com.example.testpullrefresh.adapter;
import com.example.testpullrefresh.R;
import com.uilib.adapter.BaseCoupleListAdapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleAdapter extends BaseCoupleListAdapter<String>{
	
	private final int ROW_ITEM_COUNT = 2;
	private final int mSpace;
	private final int mScreenWidth, mWidth;
	private final int[] itemArray = {
			R.id.normal_text_left,
			R.id.normal_text_right
	};

	public SimpleAdapter(Context context) {
		super(context);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mSpace = 10;
		mWidth = (mScreenWidth - mSpace * (ROW_ITEM_COUNT - 1)) / ROW_ITEM_COUNT;
		Log.d("Test", "mScreenWidth = " + mScreenWidth + "   mWidth = " + mWidth);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			View root = LayoutInflater.from(getContext()).inflate(R.layout.adapter_normal, parent,false);
			holder = new ViewHolder();
			ViewGroup.LayoutParams lp = null;
			for(int i=0; i<ROW_ITEM_COUNT; i++){
				holder.name[i] = (TextView) root.findViewById(itemArray[i]);
				lp = holder.name[i].getLayoutParams();
				lp.height = mWidth;
				lp.width = mWidth;
			}
			convertView = root;
            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		for(int i=0; i< ROW_ITEM_COUNT; i++){
            final String item = getItem(position,i);
        	holder.name[i].setText(item);
        }
		return convertView;
	}
	
	class ViewHolder {
		TextView name[] = new TextView[ROW_ITEM_COUNT];
	}

}
