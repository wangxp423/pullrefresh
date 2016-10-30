package com.uilib.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * 用户每行多列的样式
 * @author wangwenguan
 *
 * @param <T>
 */
public abstract class BaseCoupleListAdapter<T> extends BaseAdapter {
	Context mContext;
    ArrayList<T> mList = null;
    int mNumColumns = 2;

    public BaseCoupleListAdapter(Context context) {
    	mContext = context;
    }
    
    public final Context getContext(){
    	return mContext;
    }
    
    public void setNumColumns (int numColumns) {
        mNumColumns = numColumns;
    }
    
    @Override
    public final int getCount() {
        return (mList == null) ? 0 : ((mList.size() + (mNumColumns - 1)) / mNumColumns);
    }

    @Override
    public final T getItem(int position) {
        return null;
    }
    public final T getItem(int position, int itemIndex) {
    	if (mList == null) return null;
    	final int index = (position * mNumColumns) + itemIndex;
    	if (index < 0 || index >= mList.size()) return null;
        return mList.get(index);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final boolean hasStableIds() {
        return true;
    }

    @Override
    public final boolean isEmpty() {
        return (mList == null) ? true : mList.isEmpty();
    }

    /**
     * 用于设置List的方法，也可以不用这个方法，进行设置值，可以在子类的构造方法里直接进行设置
     * @param group
     */
    public void setList(ArrayList<T> group) {
        if (mList == null) {
            mList = group;
        } else {
            mList.clear();
            if (group != null) mList.addAll(group);
        }
        notifyDataSetChanged(); // 如果改变了group，则显示控件则要更新显示数据
    }
    
    public final void addList(ArrayList<T> group) {
    	if (group == null) return;
    	
    	if(mList == null) {
    		mList = group;
    	} else {
    	    if (group != null) mList.addAll(group);
    	}

    	notifyDataSetChanged(); // 如果改变了group，则显示控件则要更新显示数据
    }
    
    public final void addListFromHeader(ArrayList<T> group) {
    	if (group == null) return;
    	
    	if(mList == null) {
    		mList = group;
    	} else {
    	    if (group != null) mList.addAll(0, group);
    	}
    	
    	notifyDataSetChanged(); // 如果改变了group，则显示控件则要更新显示数据
    }
    
    public final ArrayList<T> getList() {
    	return mList;
    }
    
    public void removeAll () {
        if(mList != null) {
        	mList.clear();
            notifyDataSetChanged();
        }
    }
	
	public void destroy () {
		removeAll();
		mList = null;
	}
}
