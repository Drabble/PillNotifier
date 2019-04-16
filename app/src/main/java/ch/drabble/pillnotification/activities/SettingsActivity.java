package ch.drabble.pillnotification.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import ch.drabble.pillnotification.R;
import ch.drabble.pillnotification.utils.Alarm;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference saveButton = (Preference) findPreference("save_button");
        saveButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });
        Preference use_time = (Preference) findPreference("use_time");
        use_time.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Alarm.set(SettingsActivity.this);
                return true;
            }
        });
    }
}