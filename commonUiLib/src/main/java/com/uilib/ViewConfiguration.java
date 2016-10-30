package com.uilib;

import android.content.Context;
import android.util.DisplayMetrics;

public class ViewConfiguration {

    /**
     * Defines the duration in milliseconds we will wait to see if a touch event
     * is a tap or a scroll. If the user does not move within this interval, it is
     * considered to be a tap.
     */
    private static final int TAP_TIMEOUT = 180;
    /**
     * Defines the duration in milliseconds between the first tap's up event and
     * the second tap's down event for an interaction to be considered a
     * double-tap.
     */
    private static final int DOUBLE_TAP_TIMEOUT = 250;  //  300
    /**
     * Defines the minimum duration in milliseconds between the first tap's up event and
     * the second tap's down event for an interaction to be considered a
     * double-tap.
     */
    private static final int DOUBLE_TAP_MIN_TIME = 40;
    
    public static int getTapTimeout () {return TAP_TIMEOUT;}
    public static int getDoubleTapTimeout () {return DOUBLE_TAP_TIMEOUT;}
    public static int getDoubleTapMinTime () {return DOUBLE_TAP_MIN_TIME;}
    
    /**
     * Inset in pixels to look for touchable content when the user touches the edge of the screen
     */
    private static final int EDGE_SLOP = 12;

    /**
     * Distance a touch can wander before we think the user is scrolling in pixels
     */
    private static final int TOUCH_SLOP = 7;	//	16;
    
    /**
     * Distance between the first touch and second touch to still be considered a double tap
     */
    private static final int DOUBLE_TAP_SLOP = 20;	//	100;
    
    /**
     * Minimum velocity to initiate a fling, as measured in pixels per second
     */
    private static final int MINIMUM_FLING_VELOCITY = 50;
    
    /**
     * Maximum velocity to initiate a fling, as measured in pixels per second
     */
    private static final int MAXIMUM_FLING_VELOCITY = 4000;
    private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
    private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
    private static final int VELOCITY_UNITS = 1000;

    private final int mEdgeSlop;
    private final int mTouchSlop;
    private final int mTouchSlopSquare;
    private final int mDoubleTapSlop;
    private final int mDoubleTapSlopSquare;
    private final int mMinimumFlingVelocity;
    private final int mMaximumFlingVelocity;
    private final int mMaximumMinorVelocity;
    private final int mMaximumMajorVelocity;
    private final int mVelocityUnits;
    
    private static ViewConfiguration Instance;
    /**
     * Creates a new configuration for the specified context. The configuration depends on
     * various parameters of the context, like the dimension of the display or the density
     * of the display.
     *
     * @param context The application context used to initialize this view configuration.
     *
     * @see #get(android.content.Context) 
     * @see android.util.DisplayMetrics
     */
    private ViewConfiguration (Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final float density = metrics.density;

        final int touchSlop, doubleTapSlop;
        mEdgeSlop = (int) (density * EDGE_SLOP + 0.5f);
        touchSlop = (int) (density * TOUCH_SLOP + 0.5f);
        doubleTapSlop = (int) (density * DOUBLE_TAP_SLOP + 0.5f);
        mTouchSlop = touchSlop;
        mTouchSlopSquare = touchSlop * touchSlop;
        mDoubleTapSlop = doubleTapSlop;
        mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
        mMinimumFlingVelocity = (int) (density * MINIMUM_FLING_VELOCITY + 0.5f);
        mMaximumFlingVelocity = (int) (density * MAXIMUM_FLING_VELOCITY + 0.5f);
        mMaximumMinorVelocity = (int) (MAXIMUM_MINOR_VELOCITY * density + 0.5f);
        mMaximumMajorVelocity = (int) (MAXIMUM_MAJOR_VELOCITY * density + 0.5f);
        mVelocityUnits = (int) (VELOCITY_UNITS * density + 0.5f);
    }

    /**
     * Returns a configuration for the specified context. The configuration depends on
     * various parameters of the context, like the dimension of the display or the
     * density of the display.
     *
     * @param context The application context used to initialize the view configuration.
     */
    public static ViewConfiguration get(Context context) {

        ViewConfiguration configuration = Instance;
        if (configuration == null) {
        	Instance = configuration = new ViewConfiguration(context);
        }

        return configuration;
    }

	public int getEdgeSlop() {
		return mEdgeSlop;
	}
	public int getTouchSlop() {
		return mTouchSlop;
	}
	public int getTouchSlopSquare() {
		return mTouchSlopSquare;
	}
	public int getDoubleTapSlop() {
		return mDoubleTapSlop;
	}
	public int getDoubleTapSlopSquare() {
		return mDoubleTapSlopSquare;
	}
	public int getMinimumFlingVelocity() {
		return mMinimumFlingVelocity;
	}
	public int getMaximumFlingVelocity() {
		return mMaximumFlingVelocity;
	}
	public int getMaximumMinorVelocity() {
		return mMaximumMinorVelocity;
	}
	public int getMaximumMajorVelocity() {
		return mMaximumMajorVelocity;
	}
	public int getVelocityUnits() {
		return mVelocityUnits;
	}
}
