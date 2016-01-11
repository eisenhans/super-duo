package barqsoft.footballscores.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ResultWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = ResultWidgetProvider.class.getName();

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onEnabled(Context context) {
        Log.i(LOG_TAG, "setting up widget");

        Intent intent = new Intent(context, getClass());
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, 0, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
    }

    @Override
    public void onDisabled(Context context) {
        if (alarmManager != null && alarmIntent != null) {
            Log.i(LOG_TAG, "cancelling alarm for widget upate because all widgets have been deleted, intent: " + alarmIntent);
            alarmManager.cancel(alarmIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.i(LOG_TAG, "updating widget");
        context.startService(new Intent(context, ResultWidgetIntentService.class));
    }
}
