package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
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

        updateScores();
        for (int appWidgetId : appWidgetIds) {
            Log.i(LOG_TAG, "updating widget with id " + appWidgetId);
            updateWidget(appWidgetManager, appWidgetId);
        }
    }

    private void updateScores() {
        Intent fetchServiceIntent = new Intent(this, FetchService.class);
        startService(fetchServiceIntent);
    }

    private void updateWidget(AppWidgetManager appWidgetManager, int appWidgetId) {
        String[] columns = new String[] {
                DatabaseContract.scores_table._ID, DatabaseContract.scores_table.TIME_COL,
                DatabaseContract.scores_table.HOME_COL, DatabaseContract.scores_table.AWAY_COL,
                DatabaseContract.scores_table.HOME_GOALS_COL, DatabaseContract.scores_table.AWAY_GOALS_COL
        };
        String selection = DatabaseContract.scores_table.HOME_GOALS_COL + " >= 0 and " + DatabaseContract.scores_table.AWAY_GOALS_COL + " >= 0";
        String orderBy = DatabaseContract.scores_table._ID + " desc";
        Cursor cursor = getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, columns, selection, null, orderBy);

        if (!cursor.moveToFirst()) {
            return;
        }
        int _id = cursor.getInt(0);
        String time = cursor.getString(1);
        String homeTeam = cursor.getString(2);
        String awayTeam = cursor.getString(3);
        int homeGoals = cursor.getInt(4);
        int awayGoals = cursor.getInt(5);
        cursor.close();

        String score = Utilities.getScores(homeGoals, awayGoals);
        String spokenMatchResult = Utilities.getSpokenMatchResult(homeTeam, awayTeam, homeGoals, awayGoals, time, this);
        Log.i(LOG_TAG, "data for widget: " + homeTeam + " against " + awayTeam + ", _id " + _id + ", " + time + ", " + score);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_result);

        views.setTextViewText(R.id.home_name, homeTeam);
        views.setTextViewText(R.id.away_name, awayTeam);
        views.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(homeTeam));
        views.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(awayTeam));

        views.setTextViewText(R.id.data_textview, time);
        views.setTextViewText(R.id.score_textview, Utilities.getScores(homeGoals, awayGoals));
        // Content Descriptions for RemoteViews were only added in ICS MR1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            views.setContentDescription(R.id.widget_result, spokenMatchResult);
        }

        // Create an Intent to launch MainActivity
        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_result, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
