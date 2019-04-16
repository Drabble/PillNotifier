package ch.drabble.pillnotification.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.drabble.pillnotification.R;
import ch.drabble.pillnotification.utils.Alarm;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.LONG;
import static java.util.Calendar.MONTH;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Alarm.set(this);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int days_with = Integer.parseInt(SP.getString("days_with", "24"));
        int days_without = Integer.parseInt(SP.getString("days_without", "3"));
        String use_date_string = SP.getString("use_date", "2019-04-01");
        String use_time_string = SP.getString("use_time", "11:00");

        Calendar current_cal = Calendar.getInstance();
        current_cal.setTime(new Date());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date use_date = new Date();
        try {
            use_date =  df.parse(use_date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar use_cal = Calendar.getInstance();
        use_cal.setTime(use_date);
        System.out.println(use_cal);
        System.out.println(current_cal);
        while((use_cal.get(Calendar.DAY_OF_YEAR) < current_cal.get(Calendar.DAY_OF_YEAR) &&
                use_cal.get(Calendar.YEAR) == current_cal.get(Calendar.YEAR)) || use_cal.get(Calendar.YEAR) < current_cal.get(Calendar.YEAR)){
            System.out.println(use_cal);
            use_cal.add(Calendar.DATE, days_with + days_without);
        }
        System.out.println(use_cal);
        use_cal.add(Calendar.DATE, -days_with - days_without);
        use_cal.set(Calendar.HOUR_OF_DAY, 0);
        use_cal.set(Calendar.MINUTE, 0);
        use_cal.set(Calendar.SECOND, 0);
        use_cal.set(Calendar.MILLISECOND, 0);
        Calendar cal = (Calendar)use_cal.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        GridLayout layout = (GridLayout)findViewById(R.id.grid);
        layout.removeAllViews();

        // Show 100 cells
        for(int i = 0; i < 100; i++){
            int year = cal.get(Calendar.YEAR);
            int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            String dayOfWeekText = cal.getDisplayName(DAY_OF_WEEK, LONG, Locale.getDefault());
            String monthText = cal.getDisplayName(MONTH, LONG, Locale.getDefault());

            View child = getLayoutInflater().inflate(R.layout.pill_cell, null);
            TextView dayTextView = (TextView)child.findViewById(R.id.day);
            dayTextView.setText(dayOfWeekText.substring(0,3));
            TextView dateTextView = (TextView)child.findViewById(R.id.date);
            dateTextView.setText(dayOfMonth + " " + monthText.substring(0,3));
            layout.addView(child);

            FrameLayout frame = (FrameLayout)child.findViewById(R.id.frame);
            long difference = Math.abs(use_cal.getTime().getTime() - cal.getTime().getTime());
            long days_between = difference / (24 * 60 * 60 * 1000);
            boolean use_today = (days_between % (days_with + days_without)) - days_with < 0;

            // Same day
            if(dayOfYear == current_cal.get(Calendar.DAY_OF_YEAR) &&
                    year == current_cal.get(Calendar.YEAR)){
                frame.setBackgroundColor(use_today ? Color.parseColor(getString(R.string.red)) : Color.parseColor(getString(R.string.light_red)));
            }
            // Before
            else if((dayOfYear < current_cal.get(Calendar.DAY_OF_YEAR) &&
                    year == current_cal.get(Calendar.YEAR)) || year < current_cal.get(Calendar.YEAR)){
                frame.setBackgroundColor(use_today ? Color.parseColor(getString(R.string.black)) : Color.parseColor(getString(R.string.grey)));
            }
            // After
            else{
                frame.setBackgroundColor(use_today ? Color.parseColor(getString(R.string.black)) : Color.parseColor(getString(R.string.grey)));
            }

            // Increase day by one
            cal.add(Calendar.DATE, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
