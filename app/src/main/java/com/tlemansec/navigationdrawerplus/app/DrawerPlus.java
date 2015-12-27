package com.tlemansec.navigationdrawerplus.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.tlemansec.navigationdrawerplus.app.R;


/**
 * Created by Thibault on 02/11/15.
 */
public class DrawerPlus extends DrawerLayout {

    //region Attributes

    protected DrawerPlusListener mDrawerPlusListener;
    protected View mDrawerContentViewContainer;
    protected View mDrawerLeftMenuViewContainer;
    protected View mDrawerRightMenuViewContainer;
    protected View mDrawerLeftMenuChildViewContainer;
    protected View mDrawerRightMenuChildViewContainer;

    protected float mLastMenuPosition = 0.0f;
    protected boolean mSlideOverContentView;

    private int mContentViewContainerId;
    private int mLeftMenuViewContainerId;
    private int mRightMenuViewContainerId;
    private int mLeftMenuChildViewContainerId;
    private int mRightMenuChildViewContainerId;

    private final String TAG = DrawerPlus.class.getSimpleName();

    //endregion


    //region Constructors

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in.
     * @param attrs A collection of attributes, as found associated with a tag in an XML document.
     */
    public DrawerPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViewWithAttributes(context, attrs);
    }

    //endregion


    //region Life Cycle

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDrawerContentViewContainer = findViewById(mContentViewContainerId);
        mDrawerLeftMenuViewContainer = findViewById(mLeftMenuViewContainerId);
        mDrawerRightMenuViewContainer = findViewById(mRightMenuViewContainerId);
        mDrawerLeftMenuChildViewContainer = findViewById(mLeftMenuChildViewContainerId);
        mDrawerRightMenuChildViewContainer = findViewById(mRightMenuChildViewContainerId);

        mDrawerLeftMenuViewContainer.post(new Runnable() {
            @Override
            public void run() {
                mDrawerLeftMenuChildViewContainer.setTop(mDrawerLeftMenuViewContainer.getMeasuredHeight());
            }
        });

        mDrawerRightMenuViewContainer.post(new Runnable() {
            @Override
            public void run() {
                mDrawerRightMenuChildViewContainer.setTop(mDrawerRightMenuViewContainer.getMeasuredHeight());
            }
        });
    }

    //endregion


    //region Utils

    private void initializeViewWithAttributes(Context context, AttributeSet attrs) {
        if(attrs == null || context == null)
            return;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.drawerPlus);
        mContentViewContainerId = typedArray.getResourceId(R.styleable.drawerPlus_layout_contentView, -1);
        mLeftMenuViewContainerId = typedArray.getResourceId(R.styleable.drawerPlus_layout_leftMenuView, -1);
        mRightMenuViewContainerId = typedArray.getResourceId(R.styleable.drawerPlus_layout_rightMenuView, -1);
        mLeftMenuChildViewContainerId = typedArray.getResourceId(R.styleable.drawerPlus_layout_leftMenuChildView, -1);
        mRightMenuChildViewContainerId = typedArray.getResourceId(R.styleable.drawerPlus_layout_rightMenuChildView, -1);

        if (mContentViewContainerId == -1 || (mLeftMenuViewContainerId == -1 && mRightMenuViewContainerId == -1))
            throw new IllegalArgumentException("ContentView and all Menu Views cannot be null!");

        if (isInEditMode())
            return;

        mSlideOverContentView = typedArray.getBoolean(R.styleable.drawerPlus_layout_slideOverContentView,
                true);

        int overlayColor = typedArray.getColor(R.styleable.drawerPlus_layout_overlayColor, -1);
        if(overlayColor != -1)
            setScrimColor(overlayColor);

        setDrawerListener(new SimpleDrawerPlusListener());

        typedArray.recycle();
    }

    /**
     * Called when a drawer's position changes.
     * When we are using a tablet, the drawer opens above the main page.
     * When we are using a smartphone, the drawer pushes to the right the main page.
     *
     * @param slideOffset The new offset of this drawer within its range, from 0-1.
     */
    private void handleOnDrawerSlide(View drawerView, float slideOffset) {
        if(drawerView == null || mDrawerContentViewContainer == null) {
            Log.e(TAG, "handleOnDrawerSlide missing parameters");
            return;
        }

        boolean isLeftDrawer = drawerView.getId() == mLeftMenuViewContainerId ? true : false;

        float moveFactor = drawerView.getWidth() * slideOffset;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(!mSlideOverContentView) {
                //Push the main page to the right or left with the drawer layout menu.
                mDrawerContentViewContainer.setTranslationX(isLeftDrawer ? moveFactor : -moveFactor);
            }

        } else {
            TranslateAnimation anim = new TranslateAnimation(mLastMenuPosition, moveFactor, 0.0f, 0.0f);
            anim.setDuration(0);
            anim.setFillAfter(true);

            if(!mSlideOverContentView) {
                //Push the main page to the right or left with the drawer layout menu.
                mDrawerContentViewContainer.startAnimation(anim);
            }

            mLastMenuPosition = isLeftDrawer ? moveFactor : -moveFactor;
        }

        if(mDrawerPlusListener != null) {
            String drawerId = String.valueOf(drawerView.getId());
            Log.d(TAG, "onDrawerSliding " + drawerId);
            mDrawerPlusListener.onDrawerSliding(drawerView, moveFactor, mLastMenuPosition);
        }

        //(isLeftDrawer ? mDrawerLeftMenuChildViewContainer : mDrawerRightMenuChildViewContainer).setAlpha(slideOffset);

        if(isLeftDrawer) {
            mDrawerLeftMenuChildViewContainer.setTop((int)(-slideOffset * mDrawerLeftMenuViewContainer.getHeight()));
        } else {
            mDrawerRightMenuChildViewContainer.setTop((int)(-slideOffset * mDrawerRightMenuViewContainer.getHeight()));
        }
    }

    /**
     * The drawer is locked closed. The user may not open it, though
     * the app may open it programmatically. The lock is done on all gravities.
     */
    public void lockDrawerOnSwipe() {
        setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * The drawer is unlocked. The lock is undone on all gravities.
     */
    public void unlockDrawer() {
        setDrawerLockMode(LOCK_MODE_UNLOCKED);
    }

    //endregion


    //region Getters Setters

    /**
     * Define a reference using a DrawerPlusListener, to follow the opening and closing state of the menu.
     *
     * @param drawerPlusListener a reference to a DrawerPlusListener.
     */
    public void setDrawerPlusListener(DrawerPlusListener drawerPlusListener) {
        this.mDrawerPlusListener = drawerPlusListener;
    }

    //endregion


    //region Local Class

    public class SimpleDrawerPlusListener extends DrawerLayout.SimpleDrawerListener {
        /**
         * Called when a drawer's position changes.
         * @param drawerView The child view that was moved.
         * @param slideOffset The new offset of this drawer within its range, from 0-1.
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            handleOnDrawerSlide(drawerView, slideOffset);
        }

        /**
         * Called when a drawer has settled in a completely open state.
         * The drawer is interactive at this point.
         *
         * @param drawerView Drawer view that is now open.
         */
        @Override
        public void onDrawerOpened(View drawerView) {
            if(drawerView != null) {
                if(drawerView.getId() == mLeftMenuViewContainerId) {
                    setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED, mDrawerRightMenuViewContainer);

                } else {
                    setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED, mDrawerLeftMenuViewContainer);
                }
            }

            if(mDrawerPlusListener != null) {
                String drawerId = drawerView != null ? String.valueOf(drawerView.getId()) : "";
                Log.d(TAG, "onDrawerOpened " + drawerId);
                mDrawerPlusListener.onDrawerOpened(drawerView);
            }
        }

        /**
         * Called when a drawer has settled in a completely closed state.
         *
         * @param drawerView Drawer view that is now closed.
         */
        @Override
        public void onDrawerClosed(View drawerView) {
            if(drawerView != null) {
                if(drawerView.getId() == mLeftMenuViewContainerId) {
                    setDrawerLockMode(LOCK_MODE_UNLOCKED, mDrawerRightMenuViewContainer);

                } else {
                    setDrawerLockMode(LOCK_MODE_UNLOCKED, mDrawerLeftMenuViewContainer);
                }
            }

            if (mDrawerPlusListener != null) {
                String drawerId = drawerView != null ? String.valueOf(drawerView.getId()) : "";
                Log.d(TAG, "onDrawerClosed " + drawerId);
                mDrawerPlusListener.onDrawerClosed(drawerView);
            }
        }
    }

    //endregion
}
