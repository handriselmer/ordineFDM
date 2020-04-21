package com.example.ordinefdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.io.File;

public class Main2Activity extends AppCompatActivity {

    private TextView text;
    private File fileOrd;
    private SharedPreferences sharedPref;
    private String nomeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getString("key_tipoOrd", "").equals("AF3"))
            nomeFile = "/SCARICOTRE.TXT";
        else
            nomeFile = "/TMWII.DAT";
        fileOrd = new File(getExternalFilesDir(null) + nomeFile);
        text = findViewById(R.id.textView_del);
        if (fileOrd.exists()) {
            text.setText(R.string.ordine_presente);
        } else {
            text.setText(R.string.inizia_ordine);
        }
    }

    public void start(View v) {
        if(!(sharedPref.getString("key_tipoOrd", "").equals("AF3")) || !(sharedPref.getString("key_cod_cli", "").equals(""))) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void delete(View v) {
        if (fileOrd.delete()) {
            text.setText(R.string.inizia_ordine);
        }
    }

    public void startSettings(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPref.getString("key_tipoOrd", "").equals("AF3"))
            nomeFile = "/SCARICOTRE.TXT";
        else
            nomeFile = "/TMWII.DAT";
        fileOrd = new File(getExternalFilesDir(null) + nomeFile);
        if (fileOrd.exists()) {
            text.setText(R.string.ordine_presente);
        } else {
            text.setText(R.string.inizia_ordine);
        }
    }
}
