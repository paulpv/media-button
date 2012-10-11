package com.twistpair.wave.experimental.mediabutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MediaButtonReceiver //
                extends BroadcastReceiver
{
    private static final String TAG = MediaButtonReceiver.class.getSimpleName();

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
