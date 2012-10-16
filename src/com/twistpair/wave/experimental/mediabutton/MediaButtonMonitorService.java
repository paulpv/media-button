package com.twistpair.wave.experimental.mediabutton;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MediaButtonMonitorService //
                extends Service
{
    private static final String  TAG                  = MediaButtonMonitorService.class.getSimpleName();

    private static final int     NOTIFICATION_STARTED = 1;
    private static final int     NOTIFICATION_RUNNING = 2;
    private static final int     NOTIFICATION_STOPPED = 3;

    private NotificationManager  mNotificationManager;
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
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

        intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_STARTED, intent, 0);

        Notification notification = new NotificationCompat.Builder(this) //
        .setSmallIcon(R.drawable.ic_launcher) //
        .setContentTitle("Title: Started") //
        .setContentText("Text: Started") //
        .setContentInfo("Info: Started") //
        .setTicker("Ticker: Started") //
        .setContentIntent(pi) //
        .build();
        mNotificationManager.notify(NOTIFICATION_STARTED, notification);
        //startForeground(NOTIFICATION_STARTED, notification);

        notifyRunning();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");

        mNotificationManager.cancel(NOTIFICATION_RUNNING);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_STOPPED, intent, 0);

        Notification notification = new NotificationCompat.Builder(this) //
        .setSmallIcon(R.drawable.ic_launcher) //
        .setContentTitle("Title: Stopped") //
        .setContentText("Text: Stopped") //
        .setContentInfo("Info: Stopped") //
        .setTicker("Ticker: Stopped") //
        .setContentIntent(pi) //
        .build();
        mNotificationManager.notify(NOTIFICATION_STOPPED, notification);

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

    private void notifyRunning()
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_RUNNING, intent, 0);

        Notification notification = new NotificationCompat.Builder(this) //
        .setSmallIcon(R.drawable.ic_launcher) //
        .setContentTitle("Title: Running") //
        .setContentText("Text: Running") //
        .setContentInfo("Info: Running") //
        .setTicker("Ticker: Running") //
        .setContentIntent(pi) //
        .setOngoing(true) //
        .build();
        mNotificationManager.notify(NOTIFICATION_RUNNING, notification);
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