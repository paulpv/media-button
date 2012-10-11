package com.twistpair.wave.experimental.mediabutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MediaHeadsetReceiver extends BroadcastReceiver
{
    private static final String TAG = MediaHeadsetReceiver.class.getSimpleName();

    public IntentFilter getFilter()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF); // API 1
        filter.addAction(Intent.ACTION_SCREEN_ON); // API 1
        filter.addAction(Intent.ACTION_HEADSET_PLUG); // API 1
        filter.addAction(Intent.ACTION_DOCK_EVENT); // API 5
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "onReceive: intent=" + intent);

        final String action = intent.getAction();
        Log.w(TAG, "action=" + action);
        final String extras = AppUtils.toString(intent.getExtras());
        Log.w(TAG, "extras=" + extras);
    }
}
