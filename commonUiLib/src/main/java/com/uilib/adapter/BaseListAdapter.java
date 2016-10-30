package com.uilib.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.widget.BaseAdapter;


public abstract class BaseListAdapter<T> extends BaseAdapter {
	Context mContext;
    ArrayList<T> mList = null;

    public BaseListAdapter(Context context) {
    	mContext = context;
    }
    
    public final Context getContext(){
    	return mContext;
    }

    @Override
    public final int getCount() {
        return (mList == null) ? 0 : mList.size();
    }

    @Override
    public final T getItem(int position) {
    	if (position < 0 || mList == null || mList.size() == 0) return null;
        return mList.get(position);
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
        mList = group;
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
    
    public void addItem (int index, T item) {
        if(mList != null) {
            mList.add(index, item);
            notifyDataSetChanged();
        }
    }
    
    public final ArrayList<T> getList() {
    	return mList;
    }
    
    public void removeAt (int index) {
        if(mList != null) {
            mList.remove(index);
            notifyDataSetChanged();
        }
    }
    
    public void remove (T item) {
        if(mList != null) {
            mList.remove(item);
            notifyDataSetChanged();
        }
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
