package com.tlemansec.navigationdrawerplus.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tlemansec.navigationdrawerplus.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Thibault on 26/06/15.
 */
public class SampleActivity extends Activity {

    //region Attributes

    @Bind(R.id.drawer_layout)
    DrawerPlus mDrawerPlus;

    @Bind(R.id.view_content_container)
    LinearLayout mContentContainer;

    @Bind(R.id.view_left_menu_container)
    FrameLayout mLeftMenuContainer;

    private final String TAG = SampleActivity.class.getSimpleName();

    //endregion


    //region Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_sample_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.open_left_menu_button)
    public void clickOnButton (View view) {
        //Do your job.
    }

    //endregion
}
