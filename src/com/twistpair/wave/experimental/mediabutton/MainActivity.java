package com.twistpair.wave.experimental.mediabutton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonServiceStart = (Button) findViewById(R.id.buttonServiceStart);
        buttonServiceStart.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, MediaButtonMonitorService.class);
                Log.i(TAG, "startService(" + intent + ")");
                startService(intent);
            }
        });

        Button buttonServiceStop = (Button) findViewById(R.id.buttonServiceStop);
        buttonServiceStop.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, MediaButtonMonitorService.class);
                Log.i(TAG, "stopService(" + intent + ")");
                stopService(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
