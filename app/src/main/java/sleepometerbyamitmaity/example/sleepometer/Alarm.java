package sleepometerbyamitmaity.example.sleepometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import sleepometerbyamitmaity.example.sleepometer.alarmBroadcastReceiver.AlarmReceiver;

public class Alarm extends AppCompatActivity {
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    // OnToggleClicked() method is implemented the time functionality
    public void OnToggleClicked(View view) {
        long time;
        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(Alarm.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            // calendar is called to get current time in hour and minute
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            Intent intent = new Intent(this, AlarmReceiver.class);

            // we call broadcast using pendingIntent
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                // setting time as AM and PM
                if (calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            // Alarm rings continuously until toggle button is turned off
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
            // alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(Alarm.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }
}