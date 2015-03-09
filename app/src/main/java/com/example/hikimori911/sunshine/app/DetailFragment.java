package com.example.hikimori911.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hikimori911.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAIL_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;

    public static final String HASHTAG_SUFFIX = " #SunshineApp";

    protected ShareActionProvider mShareActionProvider;
    public String mForecastStr;

    protected TextView mDayText;
    protected TextView mDateText;
    protected TextView mMaxTempText;
    protected TextView mMinTempText;
    protected ImageView mWeatherIcon;
    protected TextView mForecastText;
    protected TextView mHumidityText;
    protected TextView mWindText;
    protected TextView mPressureText;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDayText = (TextView)rootView.findViewById(R.id.list_item_day_textview);
        mDateText = (TextView)rootView.findViewById(R.id.list_item_date_textview);
        mMaxTempText = (TextView)rootView.findViewById(R.id.list_item_high_textview);
        mMinTempText = (TextView)rootView.findViewById(R.id.list_item_low_textview);
        mWeatherIcon = (ImageView)rootView.findViewById(R.id.list_item_icon);
        mForecastText = (TextView)rootView.findViewById(R.id.list_item_forecast_textview);
        mHumidityText = (TextView)rootView.findViewById(R.id.list_item_humidity_textview);
        mWindText = (TextView)rootView.findViewById(R.id.list_item_wind_textview);
        mPressureText = (TextView)rootView.findViewById(R.id.list_item_pressure_textview);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Get the menu item.
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mForecastStr!=null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    // Somewhere in the application.
    public void doShare(Intent shareIntent) {
        // When you want to share set the share intent.
        mShareActionProvider.setShareIntent(shareIntent);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(), intent.getData(),
                FORECAST_COLUMNS,null, null,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) { return; }
        long dateInMillis = data.getLong(COL_WEATHER_DATE);
        mDayText.setText(Utility.getDayName(getActivity(),dateInMillis));
        mDateText.setText(Utility.getFormattedMonthDay(getActivity(),dateInMillis));

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(getActivity());

        // Read high temperature from cursor
        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        mMaxTempText.setText(Utility.formatTemperature(getActivity(),high, isMetric));

        // Read low temperature from cursor
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        mMinTempText.setText(Utility.formatTemperature(getActivity(),low, isMetric));

        int weatherCondId = data.getInt(COL_WEATHER_CONDITION_ID);
        mWeatherIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherCondId));

        String desc = data.getString(COL_WEATHER_DESC);
        mForecastText.setText(desc);

        double humidity = data.getDouble(COL_WEATHER_HUMIDITY);
        mHumidityText.setText(getActivity().getString(R.string.format_humidity,humidity));

        float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float degrees = data.getFloat(COL_WEATHER_DEGREES);
        mWindText.setText(Utility.getFormattedWind(getActivity(),windSpeed,degrees));

        double pressure = data.getDouble(COL_WEATHER_PRESSURE);
        mPressureText.setText(getActivity().getString(R.string.format_pressure,pressure));

        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));
        mForecastStr = String.format("%s - %s - %s/%s", dateString, desc, high, low);
        if(mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + HASHTAG_SUFFIX);
        return shareIntent;
    }
}
