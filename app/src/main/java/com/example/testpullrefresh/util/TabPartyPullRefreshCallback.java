package com.example.testpullrefresh.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testpullrefresh.R;
import com.uilib.widget.PullContainerView;

public class TabPartyPullRefreshCallback implements PullContainerView.HeaderAndFooterViewCallback {

    OnPushDownRefreshLisener mCallback;
    Animation mClockwiseAnim;
    Animation mAntiClockwiseAnim;
    int mSuggestedHeight;
    public TabPartyPullRefreshCallback (Context context,OnPushDownRefreshLisener callback) {
        mCallback = callback;
        mClockwiseAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise_half);
        mAntiClockwiseAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_anticlockwise_half);
        mSuggestedHeight = context.getResources().getDimensionPixelSize(R.dimen.homepage_pull_header_suggested_height);
    }
    @Override
    public void onStateChanged(View view, int newState, int oldState) {
//        System.out.println("onStateChanged: " + oldState + " => " + newState + "; " + view.getVisibility());
        switch (newState) {
        case PullContainerView.PULL_STATE_NONE:{ // 激活状态(下拉可以刷新)
            ImageView arrow = (ImageView)view.findViewById(R.id.pull_anim_arrow);
            arrow.clearAnimation();
            View loading = view.findViewById(R.id.pull_loading);
            loading.setVisibility(View.INVISIBLE);
            TextView text = (TextView)view.findViewById(R.id.message_txt);
            text.setText(R.string.label_refresh_pulldown);
            arrow.setVisibility(View.VISIBLE);
        }break;
        case PullContainerView.PULL_STATE_ENABLE:{ // 激活状态(松开可以刷新)
            ImageView arrow = (ImageView)view.findViewById(R.id.pull_anim_arrow);
            arrow.startAnimation(mClockwiseAnim);
            TextView text = (TextView)view.findViewById(R.id.message_txt);
            text.setText(R.string.tab_party_refresh_release);
        }break;
        case PullContainerView.PULL_STATE_PULLING:{
            if (oldState == PullContainerView.PULL_STATE_ENABLE) {
                ImageView arrow = (ImageView)view.findViewById(R.id.pull_anim_arrow);
                arrow.startAnimation(mAntiClockwiseAnim);
            }
            if (oldState == PullContainerView.PULL_STATE_ENABLE) {
                TextView text = (TextView)view.findViewById(R.id.message_txt);
                text.setText(R.string.label_refresh_pulldown);
            }
        }break;
        case PullContainerView.PULL_STATE_LOADING:{ // 刷新中
            View loading = view.findViewById(R.id.pull_loading);
            loading.setVisibility(View.VISIBLE);
            View arrow = view.findViewById(R.id.pull_anim_arrow);
            arrow.clearAnimation();
            arrow.setVisibility(View.GONE);
            if(mCallback != null) mCallback.onPushDownRefresh();
        }break;
        }
    }
    @Override
    public void onHeightChanged(View view, int height, int state) {
    }
    
    public interface OnPushDownRefreshLisener{
        void onPushDownRefresh();
    }
}
