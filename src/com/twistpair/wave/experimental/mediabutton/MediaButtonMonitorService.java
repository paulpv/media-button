package com.twistpair.wave.experimental.mediabutton;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class MediaButtonMonitorService //
                extends Service
{
    private static final String  TAG = MediaButtonMonitorService.class.getSimpleName();

    private SettingsObserver     mSettingsObserver;
    private ComponentName        mComponentName;
    private AudioManager         mAudioManager;
    private MediaHeadsetReceiver mMediaHeadsetReceiver;

    private static class SettingsObserver //
                    extends ContentObserver
    {
        private static final String       TAG                   = SettingsObserver.class.getSimpleName();

        private static final String       MEDIA_BUTTON_RECEIVER = "media_button_receiver";

        private ContentResolver           mContentResolver;
        private MediaButtonMonitorService mMonitorService;

        SettingsObserver(MediaButtonMonitorService monitorService)
        {
            super(new Handler());
            mMonitorService = monitorService;
            mContentResolver = mMonitorService.getContentResolver();
        }

        public void start()
        {
            Log.i(TAG, "start()");
            Uri uri = Settings.System.getUriFor(MEDIA_BUTTON_RECEIVER);
            mContentResolver.registerContentObserver(uri, false, this);
        }

        public void stop()
        {
            Log.i(TAG, "stop()");
            mContentResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange)
        {
            Log.d(TAG, "onChange(selfChange=" + selfChange + ")");

            String receiverName = Settings.System.getString(mContentResolver, MEDIA_BUTTON_RECEIVER);
            Log.d(TAG, "MEDIA_BUTTON_RECEIVER changed to " + receiverName);
            Log.d(TAG, "'" + receiverName + "' == '" + mMonitorService.mComponentName.flattenToString() + "'");

            if (!selfChange //
                            && !receiverName.equals(mMonitorService.mComponentName.flattenToString())
            //&& !receiverName.equals("com.harleensahni.android.mbr/com.harleensahni.android.mbr.ReceiverSelector$1")
            )
            {
                //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mMonitorService.getApplicationContext());
                //preferences.edit().putString(Constants.LAST_MEDIA_BUTTON_RECEIVER, receiverName).commit();
                //Log.d(TAG, "Set LAST_MEDIA_BUTTON_RECEIVER to " + receiverName);
                mMonitorService.registerMediaButtonEventReceiver();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate()");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mComponentName = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
        mSettingsObserver = new SettingsObserver(this);
        mMediaHeadsetReceiver = new MediaHeadsetReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand(intent=" + intent + ", flags=" + flags + ", startId=" + startId + ")");
        mSettingsObserver.start();
        registerReceiver(mMediaHeadsetReceiver, mMediaHeadsetReceiver.getFilter());
        registerMediaButtonEventReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");

        if (mMediaHeadsetReceiver != null)
        {
            unregisterReceiver(mMediaHeadsetReceiver);
            mMediaHeadsetReceiver = null;
        }

        if (mSettingsObserver != null)
        {
            mSettingsObserver.stop();
            mSettingsObserver = null;
        }

        unregisterMediaButtonEventReceiver();
    }

    public void registerMediaButtonEventReceiver()
    {
        Log.d(TAG, "registerMediaButtonReceiver()");
        if (mAudioManager != null)
        {
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        }
    }

    public void unregisterMediaButtonEventReceiver()
    {
        Log.d(TAG, "unregisterMediaButtonEventReceiver()");
        if (mAudioManager != null)
        {
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        }
    }
}