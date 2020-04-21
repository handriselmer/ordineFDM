package com.example.ordinefdm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            final EditTextPreference editText = findPreference("key_cod_cli");
            if(editText != null && sharedPref.getString("key_tipoOrd", "").equals("AF3")) {
                editText.setEnabled(true);
                editText.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
                    }
                });
            }
            sharedPref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(key.equals("key_tipoOrd") && sharedPreferences.getString("key_tipoOrd", "").equals("AF3") && editText != null) {
                        editText.setEnabled(true);
                        editText.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
                            }
                        });
                    } else {
                        if(editText != null)
                            editText.setEnabled(false);
                    }
                }
            });
        }
    }
}