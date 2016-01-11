package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.service.FetchService;

public class ResultWidgetIntentService extends IntentService {
    private static final String LOG_TAG = ResultWidgetIntentService.class.getName();

    public ResultWidgetIntentService() {
        super(ResultWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "handling updateIntent " + intent);

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ResultWidgetProvider.class));

//        // Get today's data from the ContentProvider
//        String location = Utility.getPreferredLocation(this);
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                location, System.currentTimeMillis());
//        Cursor data = getContentResolver().query(weatherForLocationUri, FORECAST_COLUMNS, null,
//                null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
//        if (data == null) {
//            return;
//        }
//        if (!data.moveToFirst()) {
//            data.close();
//            return;
//        }
//
//        // Extract the weather data from the Cursor
//        int weatherId = data.getInt(INDEX_WEATHER_ID);
//        int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
//        String description = data.getString(INDEX_SHORT_DESC);
//        double maxTemp = data.getDouble(INDEX_MAX_TEMP);
//        double minTemp = data.getDouble(INDEX_MIN_TEMP);
//        String formattedMaxTemperature = Utility.formatTemperature(this, maxTemp);
//        String formattedMinTemperature = Utility.formatTemperature(this, minTemp);
//        data.close();
//
//        // Perform this loop procedure for each Today widget
//        for (int appWidgetId : appWidgetIds) {
//            // Find the correct layout based on the widget's width
//            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
//            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
//            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);
//            int layoutId;
//            if (widgetWidth >= largeWidth) {
//                layoutId = R.layout.widget_today_large;
//            } else if (widgetWidth >= defaultWidth) {
//                layoutId = R.layout.widget_today;
//            } else {
//                layoutId = R.layout.widget_today_small;
//            }
//            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
//
//            // Add the data to the RemoteViews
//            views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
//            // Content Descriptions for RemoteViews were only added in ICS MR1
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                setRemoteContentDescription(views, description);
//            }
//            views.setTextViewText(R.id.widget_description, description);
//            views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
//            views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);
//
//            // Create an Intent to launch MainActivity
//            Intent launchIntent = new Intent(this, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }

    }

    private void updateScores() {
        Intent fetchServiceIntent = new Intent(this, FetchService.class);
        startService(fetchServiceIntent);
    }

    private void updateWidget() {
        String orderBy = DatabaseContract.scores_table.DATE_COL + ", " + DatabaseContract.scores_table.TIME_COL + " desc";
        getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, null, "", null, orderBy);
//        String homeTeam = cursor.getString(COL_HOME);
//        String awayTeam = cursor.getString(COL_AWAY);
//        String matchTime = cursor.getString(COL_MATCHTIME);
//        int homeGoals = cursor.getInt(COL_HOME_GOALS);
//        int awayGoals = cursor.getInt(COL_AWAY_GOALS);
//
//        final ViewHolder mHolder = (ViewHolder) view.getTag();
//        mHolder.home_name.setText(homeTeam);
//        mHolder.away_name.setText(awayTeam);
//        mHolder.date.setText(matchTime);
//
//        mHolder.score.setText(Utilities.getScores(homeGoals, awayGoals));
//
//        mHolder.home_crest.setImageResource(Utilities.getTeamCrestByTeamName(homeTeam));
//        mHolder.away_crest.setImageResource(Utilities.getTeamCrestByTeamName(awayTeam));
    }
}
