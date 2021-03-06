/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.systemui.R;
import com.android.systemui.ViewInvertHelper;
import com.android.systemui.statusbar.phone.NotificationPanelView;

/**
 * Container view for overflowing notification icons on Keyguard.
 */
public class NotificationOverflowContainer extends ActivatableNotificationView {

    private NotificationOverflowIconsView mIconsView;
    private ViewInvertHelper mViewInvertHelper;
    private boolean mDark;

    public NotificationOverflowContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIconsView = (NotificationOverflowIconsView) findViewById(R.id.overflow_icons_view);
        mIconsView.setMoreText((TextView) findViewById(R.id.more_text));
        mIconsView.setOverflowIndicator(findViewById(R.id.more_icon_overflow));
        mViewInvertHelper = new ViewInvertHelper(findViewById(R.id.content),
                NotificationPanelView.DOZE_ANIMATION_DURATION);
    }

    @Override
    public void setDark(boolean dark, boolean fade, long delay) {
        super.setDark(dark, fade, delay);
        if (mDark == dark) return;
        mDark = dark;
        if (fade) {
            mViewInvertHelper.fade(dark, delay);
        } else {
            mViewInvertHelper.update(dark);
        }
    }

    public NotificationOverflowIconsView getIconsView() {
        return mIconsView;
    }
}
