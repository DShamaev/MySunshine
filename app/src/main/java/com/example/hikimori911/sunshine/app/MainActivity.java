package com.example.hikimori911.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    public static final boolean IS_DEBUG = true;
    public static final String TAG = "MySunshine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
        if(IS_DEBUG) {
            Log.d(TAG, "OnCreate");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(IS_DEBUG) {
            Log.d(TAG, "OnResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(IS_DEBUG) {
            Log.d(TAG, "OnPause");
        }
    }

    @Override
     protected void onDestroy() {
        super.onDestroy();
        if(IS_DEBUG) {
            Log.d(TAG, "OnDestroy");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(IS_DEBUG) {
            Log.d(TAG, "OnStart");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(IS_DEBUG) {
            Log.d(TAG, "OnStop");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_location) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String location = Utility.getPreferredLocation(this);
            Uri.Builder builder = Uri.parse("geo:0,0").buildUpon()
                    .appendQueryParameter("q",location);
            intent.setData(builder.build());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
