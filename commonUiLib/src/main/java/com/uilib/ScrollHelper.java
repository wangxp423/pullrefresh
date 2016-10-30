package com.uilib;

import java.util.ArrayList;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

public class ScrollHelper implements Runnable{
	
	public static final int DURATION	= 300;
	public static final int ORIENTATION_HORIZONTAL	= 1;
	public static final int ORIENTATION_VERTICAL	= 2;

	public static final int TYPE_VIEW_NORMAL	= 0;
	public static final int TYPE_VIEW_SCROLLVIEW	= 1;
	public static final int TYPE_VIEW_LISTVIEW	= 2;
	
    public static final int FUNCTION_DEFAULT    = 0;
    public static final int FUNCTION_ACCELERATE = 1;
    public static final int FUNCTION_DECELERATE = 2;
	
	private int mVelocityFunction = FUNCTION_DEFAULT;
	private float ACCELERATE;
	private float mVelocity;
    private int mDuration;
	private long mStartTime;
	private boolean mFinished = true;
    /**
     * Tracks the decay of a fling scroll
     */
	private Scroller mScroller;
    
//    private int mInitialVelocity;

    /**
     * X value reported by mScroller on the previous fling
     */
    private int mLastFlingX;
    private int mBounceX;
    private int mFinalX;
    private int mBounceTime;
    
    private int mViewType;
    private int mOrientation = -1;	//	移动方向
    
    /**
     * 如果是ListView的滚动，需要记录最顶部view的索引和坐标
     */
    private int mSelectionInListView;
    private int mOrginalTopInListView;
    /**
     * The view that is scrolled
     */
    private View mView;
    private ViewGroup mViewGroup;
    private ArrayList<View> mViews;
    
    private Callback mCallback;
    public interface Callback {
    	void onFinished (View v);
    }
    
    public void setCallback (Callback Callback) {
    	mCallback = Callback;
    }
    
    private OnScrollListener mOnScrollListener;
    public interface OnScrollListener {
        void onScroll (View v);
    }
    
    public void setOnScrollListener (OnScrollListener listener) {
        mOnScrollListener = listener;
    }
    
    public ScrollHelper (Context context) {
    	if (mScroller == null) {
            mScroller = new Scroller(context);
    	}
    }

    public ScrollHelper (View view) {
        setView(view);
    	if (mScroller == null) {
            mScroller = new Scroller(view.getContext());
    	}
    }

    public ScrollHelper (View view, int viewType) {
        setView(view, viewType);
    	if (mScroller == null) {
            mScroller = new Scroller(view.getContext());
    	}
    }
    
    public void setVelocityFunction (int functionType) {
        mVelocityFunction = functionType;
    }
    
    public void setView (View view) {
        mView = view;
        mViewGroup = (ViewGroup)view.getParent();
        if (view instanceof ScrollView) {
        	mViewType = TYPE_VIEW_SCROLLVIEW;
        } else if (view instanceof ListView) {
        	mViewType = TYPE_VIEW_LISTVIEW;
        	mOrientation = ORIENTATION_VERTICAL;
        } else {
        	mViewType = TYPE_VIEW_NORMAL;
        }
    }
    
    public void setView (ArrayList<View> views) {
        mViews = views;
        if (views != null && views.size() > 0) {
        	for (int i=0;i<views.size();i++) {
        		View view = views.get(i);
        		if (view != null) {
        	        mViewGroup = (ViewGroup)view.getParent();
        	        break;
        		}
        	}
        }
    }

    public void setView (View view, int viewType) {
        mView = view;
        mViewGroup = (ViewGroup)view.getParent();
        mViewType = viewType;
    }
    
    public boolean isFinished () {
        switch (mVelocityFunction) {
        case FUNCTION_ACCELERATE:
        case FUNCTION_DECELERATE:{
            return mFinished;
        }
        default:{
            if (mScroller != null) {
                return mScroller.isFinished() || mScroller.getCurrX() == mScroller.getFinalX();
            }
        }break;
        }
    	return false;
    }
    
//    public void startUsingVelocity(int initialVelocity) {
//        if (initialVelocity == 0) return;
//        
//        mViewGroup.removeCallbacks(this);
//        
//        mInitialVelocity = initialVelocity;
//        int initialX = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
//        mLastFlingX = initialX;
//        mScroller.fling(initialX, 0, initialVelocity, 0,
//                0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
//        mViewGroup.post(this);
//    }

    public void startUsingDistance(int distance) {
    	startUsingDistance(distance, mOrientation>0?mOrientation:ORIENTATION_HORIZONTAL);
    }

    public void startUsingDistance(int distance, int orientation) {
    	startUsingDistance(distance, orientation, DURATION);
    }

    /**
     * 
     * @param distance	目标点减去当前点的值
     * @param duration	动画所需的时间
     */
    public void startUsingDistance(int distance, int orientation, int duration) {
        if (distance == 0 || mViewGroup == null || mView == null) return;
        mOrientation = orientation;
        mViewGroup.removeCallbacks(this);

        mFinished = false;
        mBounceX = 0;
        mLastFlingX = 0;
        mFinalX = distance;
        mDuration = duration;
        switch (mVelocityFunction) {
        case FUNCTION_ACCELERATE:{
            final float MAX_VELOCITY = (float)distance * 2 / duration;
            mVelocity = 0;
            ACCELERATE = MAX_VELOCITY / duration;
            mStartTime = SystemClock.uptimeMillis();
        }break;
        case FUNCTION_DECELERATE:{
            final float MAX_VELOCITY = (float)distance * 2 / duration;
            mVelocity = MAX_VELOCITY;
            ACCELERATE = - MAX_VELOCITY / duration;
            mStartTime = SystemClock.uptimeMillis();
        }break;
        default:{
            mScroller.startScroll(0, 0, distance, 0, duration);
        }break;
        }
        mViewGroup.post(this);
        
        if (mView instanceof ListView) {
        	ListView listView = (ListView)mView;
        	if (listView.getChildCount() == 0) return;
            mSelectionInListView = listView.getFirstVisiblePosition();
            mOrginalTopInListView = listView.getChildAt(0).getTop();
        }
    }

    /**
     * 带回弹效果的滚动
     * @param distance
     * @param bounce 回弹的距离
     */
    public void startUsingDistanceWithBounce(int distance, int bounce, int orientation) {
    	startUsingDistanceWithBounce(distance, bounce, orientation, DURATION);
    }
    
    public void startUsingDistanceWithBounce(int distance, int bounce, int orientation, int duration) {
        if (distance == 0) return;
        bounce = Math.abs(bounce);
        bounce = distance>0?-bounce:bounce;
        
        startUsingDistance(distance - bounce, orientation, duration);
        mBounceX = bounce;
        if (mBounceTime == 0) mBounceTime = 6;
    }
    
    public void stop(boolean scrollIntoSlots) {
    	if (mViewGroup != null) {
        	mViewGroup.removeCallbacks(this);
        	if (!mScroller.isFinished()) endFling(scrollIntoSlots);
    	}
    }
    
    public void endFling(boolean force) {
        if (force) {
            final Scroller scroller = mScroller;
            scroller.forceFinished(true);
            mFinished = true;

            if (mBounceX != 0) {
            	if (mBounceTime <= 0) {
            		mBounceX = 0;
            		endFling(true);
            	} else {
                	final int distance = mBounceX;
                	int bounce = mBounceX / 3;
                	if (bounce == 0) {
                		bounce = mBounceX>0?2:-2;
                	}
            		
                	startUsingDistanceWithBounce(distance + bounce, bounce, mOrientation, DURATION >> 1);
                	mBounceTime --;
            	}
            } else {
            	final int x = mFinalX;
                final int delta = x - mLastFlingX;

            	if (delta != 0) {
                    switch (mOrientation) {
                    case ORIENTATION_HORIZONTAL:{
                		switch (mViewType) {
                		case TYPE_VIEW_SCROLLVIEW:{
                        	mView.scrollBy(delta, 0);
                            mView.invalidate();
                		}break;
                		case TYPE_VIEW_LISTVIEW:{
                		}break;
                		default:{
                        	if (mView != null) {
                                mView.offsetLeftAndRight(delta);
                        	} else if (mViews != null) {
                        		for (int i=mViews.size()-1;i>=0;i--) {
                        			View view = mViews.get(i);
                        			if (view != null) {
                        				view.offsetLeftAndRight(delta);
                        			}
                        		}
                        	}
                            mViewGroup.invalidate();
                		}break;
                		}
                    }break;
                    case ORIENTATION_VERTICAL:{
                		switch (mViewType) {
                		case TYPE_VIEW_SCROLLVIEW:{
                        	mView.scrollBy(0, delta);
                            mView.invalidate();
                		}break;
                		case TYPE_VIEW_LISTVIEW:{
                			final ListView listView = (ListView)mView;
                			mOrginalTopInListView += delta;
                			listView.setSelectionFromTop(mSelectionInListView, mOrginalTopInListView);
                		}break;
                		default:{
                        	if (mView != null) {
                                mView.offsetTopAndBottom(delta);
                        	} else if (mViews != null) {
                        		for (int i=mViews.size()-1;i>=0;i--) {
                        			View view = mViews.get(i);
                        			if (view != null) {
                        				view.offsetTopAndBottom(delta);
                        			}
                        		}
                        	}
                            mViewGroup.invalidate();
                		}break;
                		}
                    }break;
                    }
            	}
            	if (mOnScrollListener != null) {
            	    mOnScrollListener.onScroll(mView);
            	}
                if (mCallback != null) {
                	mCallback.onFinished(mView);
                }
            }
        }
    }
	
	@Override
	public void run () {

        boolean more = false;
        int x = 0;
        switch (mVelocityFunction) {
        case FUNCTION_ACCELERATE:{
            int timePassed = (int)(SystemClock.uptimeMillis() - mStartTime);
            final float detalV = ACCELERATE * timePassed;
            x = (int)(mVelocity * timePassed + detalV * timePassed / 2);
            more = timePassed < mDuration;
            if (!more) x = mFinalX;
        }break;
        case FUNCTION_DECELERATE:{
            int timePassed = (int)(SystemClock.uptimeMillis() - mStartTime);
            final float detalV = ACCELERATE * timePassed;
            x = (int)(mVelocity * timePassed + detalV * timePassed / 2);
            more = timePassed < mDuration;
            if (!more) x = mFinalX;
        }break;
        default:{
            final Scroller scroller = mScroller;
            more = scroller.computeScrollOffset();
            x = scroller.getCurrX();
        }break;
        }

        final int delta = x - mLastFlingX;

        switch (mOrientation) {
        case ORIENTATION_HORIZONTAL:{
    		switch (mViewType) {
    		case TYPE_VIEW_SCROLLVIEW:{
            	mView.scrollBy(delta, 0);
                mView.invalidate();
    		}break;
    		case TYPE_VIEW_LISTVIEW:{
    		}break;
    		default:{
            	if (mView != null) {
                    mView.offsetLeftAndRight(delta);
            	} else if (mViews != null) {
            		for (int i=mViews.size()-1;i>=0;i--) {
            			View view = mViews.get(i);
            			if (view != null) {
            				view.offsetLeftAndRight(delta);
            			}
            		}
            	}
                mViewGroup.invalidate();
    		}break;
    		}
        }break;
        case ORIENTATION_VERTICAL:{
    		switch (mViewType) {
    		case TYPE_VIEW_SCROLLVIEW:{
            	mView.scrollBy(0, delta);
                mView.invalidate();
    		}break;
    		case TYPE_VIEW_LISTVIEW:{
    			final ListView listView = (ListView)mView;
    			mOrginalTopInListView += delta;
    			listView.setSelectionFromTop(mSelectionInListView, mOrginalTopInListView);
    		}break;
    		default:{
            	if (mView != null) {
                    mView.offsetTopAndBottom(delta);
            	} else if (mViews != null) {
            		for (int i=mViews.size()-1;i>=0;i--) {
            			View view = mViews.get(i);
            			if (view != null) {
            				view.offsetTopAndBottom(delta);
            			}
            		}
            	}
                mViewGroup.invalidate();
    		}break;
    		}
        }break;
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(mView);
        }

        mLastFlingX = x;
        if (!more || (mBounceX != 0 && x == mFinalX)) {
            endFling(true);
        } else if (more) {
        	mViewGroup.post(this);
        }
	}
}
