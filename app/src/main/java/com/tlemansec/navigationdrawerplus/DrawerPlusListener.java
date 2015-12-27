package com.tlemansec.navigationdrawerplus;

import android.view.View;


/**
 * Created by Thibault on 16/08/2015.
 */
public interface DrawerPlusListener {

    void onDrawerClosed(View drawer);
    void onDrawerOpened(View drawer);
    void onDrawerSliding(View drawer, float slidePosition, float lastDrawerPosition);

}
