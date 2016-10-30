package com.uilib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.uilib.R;
import com.uilib.ScrollHelper;
import com.uilib.UiUtil;
import com.uilib.ViewConfiguration;

public class PullContainerView extends FrameLayout implements ScrollHelper.Callback {

    public interface HeaderAndFooterViewCallback {
        public void onHeightChanged (View view, int height, int state);
        public void onStateChanged (View view, int newState, int oldState);
    }
    
    private int HEIGHT = -1;
    private int SCREEN_HEIGHT;
    private int mMainViewLayout, mCoupleViewLayout;
    private View mMainView, mMainContent;
    private View mCoupleView, mCoupleContent;
    private View mHeaderView, mFooterView;
    private View mMarginView;
    private int mHeaderSuggestedHeight, mFooterSuggestedHeight;
    private int mMainViewMarginTop, mMainViewMarginBottom;
    private int mCoupleViewMarginTop, mCoupleViewMarginBottom;
    private boolean mPullDownAble, mPullUpAble; //  是否开启了下拉上拉
    private HeaderAndFooterViewCallback mHeaderCallback, mFooterCallback;
    public static final int ORIENTATION_DOWN    = 1; //  拖动方向
    public static final int ORIENTATION_UP      = 2;
    public static final int PULL_STATE_NONE     = -1;
    public static final int PULL_STATE_NORMAL   = 0; // 正常状态
    public static final int PULL_STATE_PULLING  = 1; // 触发了下拉
    public static final int PULL_STATE_ENABLE   = 2; // 激活状态(松开可以刷新)
    public static final int PULL_STATE_LOADING  = 3; // 刷新中
    public static final int PULL_STATE_FINISH   = 4; // 加载成功
    private int mPullDownState = PULL_STATE_NONE;
    private int mPullUpState = PULL_STATE_NONE;
    private long mEnableTime;   //  激活刷新的时间
    private ScrollHelper mScrollHelper;
    
    public PullContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public PullContainerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGesture(context);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullContainerView);
            final int N = typedArray.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.PullContainerView_enable) {
                    boolean enable = typedArray.getBoolean(attr, true);
                    setEnabled(enable);
                } else if (attr == R.styleable.PullContainerView_headerSuggestedHeight) {
                    mHeaderSuggestedHeight = typedArray.getDimensionPixelSize(attr, 0);
                } else if (attr == R.styleable.PullContainerView_footerSuggestedHeight) {
                    mFooterSuggestedHeight = typedArray.getDimensionPixelSize(attr, 0);
                } else if (attr == R.styleable.PullContainerView_headerView) {
                    final int viewLayout = typedArray.getResourceId(attr, View.NO_ID);
                    if (viewLayout != View.NO_ID) mHeaderView = inflater.inflate(viewLayout, this, false);
                } else if (attr == R.styleable.PullContainerView_footerView) {
                    final int viewLayout = typedArray.getResourceId(attr, View.NO_ID);
                    if (viewLayout != View.NO_ID) mFooterView = inflater.inflate(viewLayout, this, false);
                } else if (attr == R.styleable.PullContainerView_mainView) {
                    mMainViewLayout = typedArray.getResourceId(attr, View.NO_ID);
                } else if (attr == R.styleable.PullContainerView_coupleView) {
                    mCoupleViewLayout = typedArray.getResourceId(attr, View.NO_ID);
                }
            }
        }
        if (mMainViewLayout > 0) {
            mMainView = inflater.inflate(mMainViewLayout, this, false);
            mMainContent = findContentView(mMainView);
            if (mMainContent != null) {
//              SDK Version: 9  Build.VERSION_CODES.GINGERBREAD (2.3)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                    mMainContent.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }
            }
        }
        if (mCoupleViewLayout > 0) {
            mCoupleView = inflater.inflate(mCoupleViewLayout, this, false);
            mCoupleContent = findContentView(mCoupleView);
            if (mCoupleContent != null) {
//              SDK Version: 9  Build.VERSION_CODES.GINGERBREAD (2.3)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                    mCoupleContent.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }
            }
        }
        init();
    }
    
    public void init () {
        removeAllViews();
        RelativeLayout child = new RelativeLayout(getContext());
        final int padding = SCREEN_HEIGHT = UiUtil.getScreenHeight(getContext());
        scrollTo(0, padding);
        addView(child);
        if (mMainView != null) {
            ViewGroup.LayoutParams lp = mMainView.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            mMainViewMarginTop = mlp.topMargin;
            mMainViewMarginBottom = mlp.bottomMargin;
            mlp.topMargin = 0;
            mlp.bottomMargin = 0;
            
            mMarginView = new View(getContext());
            lp = new ViewGroup.LayoutParams(-1, padding + mMainViewMarginTop);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lp);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            child.addView(mMarginView, params);
            mMarginView.setId(3);
            
            params = new RelativeLayout.LayoutParams(lp);
            params.addRule(RelativeLayout.BELOW, mMarginView.getId());
            child.addView(mMainView, params);
            if (mMainView.getId() == View.NO_ID) {
                mMainView.setId(1);
            }
            mMarginView = new View(getContext());
            lp = new ViewGroup.LayoutParams(-1, padding + mMainViewMarginBottom);
            params = new RelativeLayout.LayoutParams(lp);
            params.addRule(RelativeLayout.BELOW, mMainView.getId());
            child.addView(mMarginView, params);
            mMarginView.setId(4);
        }
        if (mCoupleView != null) {
            ViewGroup.LayoutParams lp = mCoupleView.getLayoutParams();
            mCoupleViewMarginTop = ((ViewGroup.MarginLayoutParams)lp).topMargin;
            mCoupleViewMarginBottom = ((ViewGroup.MarginLayoutParams)lp).bottomMargin;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lp);
            params.addRule(RelativeLayout.BELOW, mMarginView.getId());
            child.addView(mCoupleView, params);
            if (mCoupleView.getId() == View.NO_ID) {
                mCoupleView.setId(2);
            }
        }
        if (mHeaderView != null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mHeaderView.getLayoutParams());
            if (mCoupleView != null) {
                params.addRule(RelativeLayout.ABOVE, mCoupleView.getId());
            } else if (mMainView != null) {
                params.addRule(RelativeLayout.ABOVE, mMainView.getId());
            }
            child.addView(mHeaderView, params);
            mHeaderView.setVisibility(View.INVISIBLE);
            mPullDownAble = true;
        }
        if (mFooterView != null) {
            if (mMainView != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mFooterView.getLayoutParams());
                params.addRule(RelativeLayout.BELOW, mMainView.getId());
                child.addView(mFooterView, params);
                mFooterView.setVisibility(View.INVISIBLE);
                mPullUpAble = true;
            }
        }
        mScrollHelper = new ScrollHelper(getContext());
        mScrollHelper.setCallback(this);
    }

    public void setHeaderViewCallback (HeaderAndFooterViewCallback callback) {
        mHeaderCallback = callback;
    }
    public void setFooterViewCallback (HeaderAndFooterViewCallback callback) {
        mFooterCallback = callback;
    }
    
    public void setPullUpEnable (boolean enable) {
        mPullUpAble = enable;
        if (enable) setEnabled(true);
    }
    public void setPullDownEnable (boolean enable) {
        mPullDownAble = enable;
        if (enable) setEnabled(true);
    }
    
    private void updateViewHeight (final int height) {
        if (height > 0 && height != HEIGHT) {
            HEIGHT = height;
            if (mMainView != null) {
                ViewGroup.LayoutParams params = mMainView.getLayoutParams();
                params.height = height - mMainViewMarginTop - mMainViewMarginBottom;
                mMainView.setLayoutParams(params);
            }
            if (mCoupleView != null) {
                ViewGroup.LayoutParams params = mCoupleView.getLayoutParams();
                params.height = height - mCoupleViewMarginTop - mCoupleViewMarginBottom;
                mCoupleView.setLayoutParams(params);
            }
            mScrollHelper.setView(PullContainerView.this, ScrollHelper.TYPE_VIEW_SCROLLVIEW);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, final int top, int right, final int bottom) {
//        System.out.println("onLayout: " + changed + "; " + (right - left) + "; " + (bottom - top) + "; " + hashCode());
        if (changed) {
            post(new Runnable() {
                @Override
                public void run() {
                    
            updateViewHeight(bottom - top);
                }
            });
        }
        super.onLayout(changed, left, top, right, bottom);
    }
    
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        System.out.println("onMeasure: " + getMeasuredWidth() + "; " + getMeasuredHeight() + "; " + hashCode());
//        updateViewHeight(getMeasuredHeight());
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
    
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft()
                + getPaddingRight(), lp.width);

        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
    
    private int getMainViewScrollY () {
        final int top = UiUtil.getViewTopInParent(mMainView, getChildAt(0));
        return top - mMainViewMarginTop;
    }
    private int getCoupleViewScrollY () {
        if (mCoupleView != null) {
            final int top = UiUtil.getViewTopInParent(mCoupleView, getChildAt(0));
            return top - mCoupleViewMarginTop;
        } else {
            final int top = UiUtil.getViewTopInParent(mMainView, getChildAt(0));
            return top - mMainViewMarginTop;
        }
    }

    private void setPullState (int state, int orientation) {
        if ((orientation == ORIENTATION_DOWN && mPullDownState == state)
            || (orientation == ORIENTATION_UP && mPullUpState == state)) return;
//        System.out.println("setPullState: " + state + ";  orientation = " + orientation);
        switch (state) {
        case PULL_STATE_NONE:{
            if (orientation == ORIENTATION_DOWN) {
                mScrollHelper.startUsingDistance(getCoupleViewScrollY() - getScrollY(), ScrollHelper.ORIENTATION_VERTICAL);
            } else if (orientation == ORIENTATION_UP) {
                mScrollHelper.startUsingDistance(getMainViewScrollY() - getScrollY(), ScrollHelper.ORIENTATION_VERTICAL);
            }
            mEnableTime = 0;
        }break;
        case PULL_STATE_ENABLE:{ // 激活状态(松开可以刷新)
            mEnableTime = SystemClock.uptimeMillis();
        }break;
        case PULL_STATE_LOADING:{ // 刷新中
        }break;
        case PULL_STATE_FINISH:{
            if (orientation == ORIENTATION_DOWN) {
                mPullDownState = state;
            } else if (orientation == ORIENTATION_UP) {
                mPullUpState = state;
            }
            state = PULL_STATE_NONE;
        }break;
        }
        if (orientation == ORIENTATION_DOWN) {
            final int oldState = mPullDownState;
            mPullDownState = state;
            if (mHeaderCallback != null) mHeaderCallback.onStateChanged(mHeaderView, state, oldState);
            if (mHeaderView != null) {
                if (state == PULL_STATE_NONE) {
                    if (mHeaderView.getVisibility() != View.INVISIBLE) {
                        mHeaderView.setVisibility(View.INVISIBLE);
                    }
                } else if (state != PULL_STATE_FINISH) {
                    if (mHeaderView.getVisibility() != View.VISIBLE) {
                        mHeaderView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        if (orientation == ORIENTATION_UP) {
            final int oldState = mPullUpState;
            mPullUpState = state;
            if (mFooterCallback != null) mFooterCallback.onStateChanged(mFooterView, state, oldState);
            if (mFooterView != null) {
                if (state == PULL_STATE_NONE) {
                    if (mFooterView.getVisibility() != View.INVISIBLE) {
                        mFooterView.setVisibility(View.INVISIBLE);
                    }
                } else if (state != PULL_STATE_FINISH) {
                    if (mFooterView.getVisibility() != View.VISIBLE) {
                        mFooterView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
    
    public void notifyFinishLoad (boolean success) {
        if (mPullDownState == PULL_STATE_LOADING) {
            if (success) {
                if (mCoupleView != null) {
                    final int scrollY = getMainViewScrollY() + mMainView.getHeight();
                    scrollTo(getScrollX(), scrollY);
                    mScrollHelper.startUsingDistance(getMainViewScrollY() - scrollY, ScrollHelper.ORIENTATION_VERTICAL, 500);
                    if (mHeaderView.getVisibility() != View.INVISIBLE) {
                        mHeaderView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    scrollTo(getScrollX(), getMainViewScrollY());
                    setPullState(PULL_STATE_FINISH, ORIENTATION_DOWN);
                }
            } else {
                setPullState(PULL_STATE_NONE, ORIENTATION_DOWN);
            }
        } else if (mPullUpState == PULL_STATE_LOADING) {
            if (success) {
                if (mCoupleView != null) {
                    final int scrollY = getCoupleViewScrollY() - mCoupleView.getHeight();
                    scrollTo(getScrollX(), scrollY);
                    mScrollHelper.startUsingDistance(getCoupleViewScrollY() - scrollY, ScrollHelper.ORIENTATION_VERTICAL, 500);
                    if (mFooterView.getVisibility() != View.INVISIBLE) {
                        mFooterView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    scrollTo(getScrollX(), getCoupleViewScrollY());
                    setPullState(PULL_STATE_FINISH, ORIENTATION_UP);
                }
            } else {
                setPullState(PULL_STATE_NONE, ORIENTATION_UP);
            }
        }
    }
    
    @Override
    public void onFinished(View v) {
        if (mPullDownState == PULL_STATE_LOADING) {
            setPullState(PULL_STATE_FINISH, ORIENTATION_DOWN);
            if (mCoupleContent != null) {
                mPullDownAble = false;
                mPullUpAble = true;
            }
        } else if (mPullUpState == PULL_STATE_LOADING) {
            setPullState(PULL_STATE_FINISH, ORIENTATION_UP);
            if (mCoupleContent != null) {
                mPullDownAble = true;
                mPullUpAble = false;
            }
        }
    }
    
    private int mActivePointerId;
    private float mDownX, mDownY, mScrollingY, mDistance, mLastDistance;
    private int mTouchSlop;
    private void initGesture (Context context) {
        ViewConfiguration config = ViewConfiguration.get(getContext());
        mTouchSlop = config.getTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        final int action = ev.getActionMasked();
        final float y = ev.getY();//System.out.println(action + "; " + y + ", " + mDownY + ", " + mScrollingY + "; " + mPullDownState + "; " + mPullUpState + "; " + mScrollHelper.isFinished());
        if (!mScrollHelper.isFinished()) return true;
        if (!isEnabled()) return super.dispatchTouchEvent(ev);
        
        switch (action) {
        case MotionEvent.ACTION_DOWN:{
            mActivePointerId = ev.getPointerId(0);
            mDownX = ev.getX();
            mDownY = y;
            mScrollingY = y;
            mLastDistance = mDistance = -1;
        }break;
        case MotionEvent.ACTION_MOVE:{
            if (mPullDownState == PULL_STATE_LOADING || mPullUpState == PULL_STATE_LOADING) return true;
            final int pointerIndex = ev.findPointerIndex(mActivePointerId);
            if (pointerIndex < 0 || pointerIndex >= ev.getPointerCount()) return super.dispatchTouchEvent(ev);
            if (mPullDownState == PULL_STATE_NONE && mPullUpState == PULL_STATE_NONE) {
                final int detal = Math.abs((int)(ev.getX(pointerIndex) - mDownX));
                if (detal > mTouchSlop && super.dispatchTouchEvent(ev)) {
                    return true;
                }
            }
            final float py = ev.getY(pointerIndex);
            if (mPullUpAble && py < mDownY) {
                View view = mMainContent;
                if (view != null) {
                	if (view instanceof AbsListView) {
                        AbsListView topView = (AbsListView)view;
                        if (mPullUpState == PULL_STATE_NONE) {
                            final int last = topView.getLastVisiblePosition();
                            View bottomChild = null;
                            if (last == topView.getCount() - 1) bottomChild = topView.getChildAt(topView.getChildCount() - 1);
                            if (bottomChild == null || bottomChild.getBottom() > topView.getTop() + topView.getHeight()) {
                                mScrollingY = py;
                            } else {
                                setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                                return true;
                            }
                        }
                    } else if (view instanceof WebView) {
                        WebView topView = (WebView)view;
                        if (mPullUpState == PULL_STATE_NONE) {
                            final int distance = (int)(topView.getContentHeight()*topView.getScale() - topView.getScrollY() - topView.getHeight());
                            if (distance > 0) {
                                if (distance <= topView.getScale() && mScrollingY != py && mDistance == distance && mLastDistance == mDistance) {
                                    setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                                    return true;
                                }
                                mLastDistance = mDistance;
                                mDistance = distance;
                                mScrollingY = py;
                            } else {
                                setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                                return true;
                            }
                        }
                    } else if (view instanceof ScrollView) {
                    	ScrollView topView = (ScrollView)view;
                    	View containView = topView.getChildAt(0);
                        if (mPullUpState == PULL_STATE_NONE) {
                            final int distance = (int)(containView.getMeasuredHeight() - topView.getHeight() - topView.getScrollY());
                            if (distance > 0) {
                                mScrollingY = py;
                            } else {
                                setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                                return true;
                            }
                        }
					}
                }
            } else if (mPullDownAble && py > mDownY) {
                View view = mCoupleContent;
                if (view == null) view = mMainContent;
                if (view != null) {
                    if (view instanceof AbsListView) {
                        AbsListView bottomView = (AbsListView)view;
                        if (mPullDownState == PULL_STATE_NONE) {
                            final int first = bottomView.getFirstVisiblePosition();
                            View topChild = null;
                            if (first == 0) topChild = bottomView.getChildAt(0);
                            if (topChild == null || topChild.getTop() < bottomView.getPaddingTop()) {
                                mScrollingY = py;
                            } else {
                                setPullState(PULL_STATE_PULLING, ORIENTATION_DOWN);
                                return true;
                            }
                        }
                    } else if (view instanceof WebView) {
                        WebView topView = (WebView)view;
                        if (mPullDownState == PULL_STATE_NONE) {
                            final int distance = topView.getScrollY();
                            if (distance > 0) {
                                if (distance <= topView.getScale() && mScrollingY != py && mDistance == distance && mLastDistance == mDistance) {
                                    setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                                    return true;
                                }
                                mLastDistance = mDistance;
                                mDistance = distance;
                                mScrollingY = py;
                            } else {
                                setPullState(PULL_STATE_PULLING, ORIENTATION_DOWN);
                                return true;
                            }
                        }
                    } else if (view instanceof ScrollView) {
                        ScrollView topView = (ScrollView)view;
                        if (mPullDownState == PULL_STATE_NONE) {
                            final int distance = topView.getScrollY();
                            if (distance > 0) {
                                if (mScrollingY != py && mDistance == distance && mLastDistance == mDistance) {
                                    setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                                    return true;
                                }
                                mLastDistance = mDistance;
                                mDistance = distance;
                                mScrollingY = py;
                            } else {
                                setPullState(PULL_STATE_PULLING, ORIENTATION_DOWN);
                                return true;
                            }
                        }
                    }
                }
            }
            if (mPullUpState == PULL_STATE_PULLING || mPullUpState == PULL_STATE_ENABLE) {
                if (py <= mScrollingY) {
                    int detal = (int)(mScrollingY - py);
                    if (detal > mFooterSuggestedHeight) {
                        detal = convertDistance(detal, mFooterSuggestedHeight);
                        if (detal > SCREEN_HEIGHT) detal = SCREEN_HEIGHT;
                        setPullState(PULL_STATE_ENABLE, ORIENTATION_UP);
                    } else {
                        setPullState(PULL_STATE_PULLING, ORIENTATION_UP);
                    }
                    scrollTo(getScrollX(), getMainViewScrollY() + detal);
                    if (mFooterCallback != null) mFooterCallback.onHeightChanged(mFooterView, detal, mPullUpState);
                    return true;
                } else {
                    scrollTo(getScrollX(), getMainViewScrollY());
                    setPullState(PULL_STATE_NONE, ORIENTATION_UP);
                }
            } else if (mPullDownState == PULL_STATE_PULLING || mPullDownState == PULL_STATE_ENABLE) {
                if (py >= mScrollingY) {
                    int detal = (int)(py - mScrollingY);
                    if (detal > mHeaderSuggestedHeight) {
                        detal = convertDistance(detal, mHeaderSuggestedHeight);
                        if (detal > SCREEN_HEIGHT) detal = SCREEN_HEIGHT;
                        setPullState(PULL_STATE_ENABLE, ORIENTATION_DOWN);
                    } else {
                        setPullState(PULL_STATE_PULLING, ORIENTATION_DOWN);
                    }
                    scrollTo(getScrollX(), getCoupleViewScrollY() - detal);
                    if (mHeaderCallback != null) mHeaderCallback.onHeightChanged(mHeaderView, detal, mPullDownState);
                    return true;
                } else {
                    scrollTo(getScrollX(), getCoupleViewScrollY());
                    setPullState(PULL_STATE_NONE, ORIENTATION_DOWN);
                }
            }
        }break;
//        case MotionEvent.ACTION_POINTER_DOWN:{  //  id为按下时当时的索引值；
//            System.out.println("ACTION_POINTER_DOWN: " + ev.getActionIndex() + "; " + (ev.getPointerId(ev.getActionIndex())) + "; " + ev.getPointerCount());
//            if (ev.getPointerCount() != 2) break;
//        }break;
        case MotionEvent.ACTION_POINTER_UP:{    //  POINTER_UP事件后，如果后续没有新点击，index会前移；Count此时不变
//            System.out.println("ACTION_POINTER_UP: " + ev.getActionIndex() + "; " + ev.getPointerId(ev.getActionIndex()) + "; " + ev.getPointerCount());
            final int pIndex = ev.getActionIndex();
            final int pointerId = ev.getPointerId(pIndex);
            if (pointerId == mActivePointerId) {
                final int pointerIndex = (pIndex > 0) ? pIndex-1 : 1;
                mActivePointerId = ev.getPointerId(pointerIndex);
                final int pointerY = (int) ev.getY(pointerIndex);
                final float detal = pointerY - ev.getY(pIndex);
                mDownY += detal;
                mScrollingY += detal;
            }
        }break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:{
            final boolean timeEnough = SystemClock.uptimeMillis() > mEnableTime + 180;
            if (mPullDownState == PULL_STATE_ENABLE) {
                if (timeEnough) { //    避免误操作，限制需停留一定时间
                    final int detal = mHeaderSuggestedHeight;
                    scrollTo(getScrollX(), getCoupleViewScrollY() - detal);
                    if (mHeaderCallback != null) mHeaderCallback.onHeightChanged(mHeaderView, detal, mPullDownState);
                    setPullState(PULL_STATE_LOADING, ORIENTATION_DOWN);
                } else {
                    setPullState(PULL_STATE_NONE, ORIENTATION_DOWN);
                }
                return true;
            } else if (mPullUpState == PULL_STATE_ENABLE) {
                if (timeEnough) { //    避免误操作，限制需停留一定时间
                    final int detal = mFooterSuggestedHeight;
                    scrollTo(getScrollX(), getMainViewScrollY() + detal);
                    if (mFooterCallback != null) mFooterCallback.onHeightChanged(mFooterView, detal, mPullUpState);
                    setPullState(PULL_STATE_LOADING, ORIENTATION_UP);
                } else {
                    setPullState(PULL_STATE_NONE, ORIENTATION_UP);
                }
                return true;
            } else {
                if (mPullDownState != PULL_STATE_NONE && mPullDownState != PULL_STATE_LOADING) {
                    setPullState(PULL_STATE_NONE, ORIENTATION_DOWN);
                    return true;
                } else if (mPullUpState != PULL_STATE_NONE && mPullUpState != PULL_STATE_LOADING) {
                    setPullState(PULL_STATE_NONE, ORIENTATION_UP);
                    return true;
                }
            }
        }break;
        }

        if (mPullDownState != PULL_STATE_NONE || mPullUpState != PULL_STATE_NONE) return true;
        return super.dispatchTouchEvent(ev);
    }
    
    private int convertDistance (int detal, int critical) {
        if (detal > critical) {
            detal = critical + ((int)(0.6f * (detal - critical)));
            final int doubled = critical << 1;
            if (detal > doubled) {
                detal = doubled + ((int)(0.6f * (detal - doubled)));
            }
        }
        if (detal > SCREEN_HEIGHT) detal = SCREEN_HEIGHT;
        return detal;
    }
    
    private View findContentView (View group) {
        if (!(group instanceof ViewGroup)) {
            return null;       
        } else if (group instanceof WebView
                || group instanceof ScrollView
                || group instanceof AbsListView) {
            return group;
        }
        ViewGroup viewGroup = (ViewGroup)group;
        do {
            final int count = viewGroup.getChildCount();
            for (int i=count-1;i>=0;i--) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof WebView
                        || view instanceof ScrollView
                        || view instanceof AbsListView) {
                    return view;
                } else {
                    View find = findContentView(view);
                    if (view != find) return find;
                }
            }
        } while (viewGroup instanceof ViewGroup);
        
        return null;
    }
}
