package com.example.ordinefdm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NewQtaDialog.NewQtaDialogListener {

    private ArrayList<Prodotto> ord = new ArrayList<Prodotto>();
    private ProdottiListAdapter ord_adapter;
    private Bundle prod;
    private ListView list_ord;
    private String barcode;
    private TextView descrizione;
    private TextView pzxcrt;
    private EditText textBarc;
    private EditText colli;
    private Prodotto p;
    private ProgressBar bar;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textBarc = findViewById(R.id.editText_barc);
        colli = findViewById(R.id.editText_colli);
        ord_adapter = new ProdottiListAdapter(this,ord);
        list_ord = findViewById(R.id.list_ord);
        list_ord.setAdapter(ord_adapter);
        new BackgroundTask().execute();
        textBarc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    findViewById(R.id.button_ok).callOnClick();
                    handled = true;
                }
                return handled;
            }
        });
        textBarc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    findViewById(R.id.button_ok).callOnClick();
                    return true;
                }
                return false;
            }
        });
        list_ord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                write(((Prodotto)parent.getItemAtPosition(position)).getCod_prod(), ((Prodotto)parent.getItemAtPosition(position)).getBarcode(), "0");
                ord.remove(position);
                ord_adapter.notifyDataSetChanged();
                return true;
            }
        });

        list_ord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p = (Prodotto) parent.getItemAtPosition(position);
                openDialog();
            }
        });
    }

    public void onClickOk(View v) throws FileNotFoundException {
        barcode = textBarc.getText().toString();
        if (!(barcode.compareTo("") == 0)) {
        descrizione = findViewById(R.id.textView_descr);
        pzxcrt = findViewById(R.id.textView_pz);
        for (int i = 0; i < ord.size(); i++) {
            if (ord.get(i).getCod_prod().compareTo(barcode) == 0 || ord.get(i).getBarcode().compareTo(barcode) == 0) {
                p = ord.get(i);
                openDialog();
                textBarc.requestFocus();
                return;
            }
        }
        prod = trovaBarcode(barcode);
        if(!prod.getBoolean("trovato")) {
            Toast.makeText(getApplicationContext(), "Barcode non trovato", Toast.LENGTH_LONG).show();
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle("Barcode non trovato");
            alert.setMessage("Vuoi aggiungere comunque?");
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    prod.putString("cod_prod", barcode);
                    prod.putString("barcode", barcode);
                    prod.putString("descrizione", "NON RICONOSCIUTO");
                    descrizione.setText(prod.getString("descrizione"));
                    pzxcrt.setText("???");
                    colli.setText("1");
                    colli.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(colli, InputMethodManager.SHOW_IMPLICIT);
                    }
                    colli.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_SEND) {
                                findViewById(R.id.button_ok_colli).callOnClick();
                                handled = true;
                            }
                            return handled;
                        }
                    });
                }
            });
            alert.show();
        } else {
            descrizione.setText(prod.getString("descrizione"));
            pzxcrt.setText(prod.getString("pz_crt"));
            colli.setText("1");
            colli.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(colli, InputMethodManager.SHOW_IMPLICIT);
            }
            colli.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        findViewById(R.id.button_ok_colli).callOnClick();
                        handled = true;
                    }
                    return handled;
                }
            });
        }}
    }

    public void onClickOkColli(View v) throws FileNotFoundException {
        if (colli.getText().toString().compareTo("") == 0 || prod.isEmpty()) {
            Toast.makeText(getApplicationContext(), "INSERIRE BARC/QTA", Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < ord.size(); i++) {
                if (ord.get(i).getCod_prod().compareTo(barcode) == 0 || ord.get(i).getBarcode().compareTo(barcode) == 0) {
                    p = ord.get(i);
                    openDialog();
                    textBarc.requestFocus();
                    return;
                }
            }
            Prodotto prodotto = new Prodotto(prod.getString("cod_prod"), prod.getString("barcode"), prod.getString("descrizione"), colli.getText().toString());
            ord.add(prodotto);
            ord_adapter.notifyDataSetChanged();
            textBarc.requestFocus();
            write(prod.getString("cod_prod"), prod.getString("barcode"), colli.getText().toString());
        }
    }

    private Bundle trovaBarcode(String barcode) {
        boolean trovato = false;
        Bundle prod = new Bundle();
        try {
            String nomeFile = getExternalFilesDir(null) + "/magan.txt";
            FileReader leggi = new FileReader(nomeFile);
            BufferedReader buf = new BufferedReader(leggi);
            String riga;
            String barc, cod_prod;
            try {
                while ((riga = buf.readLine()) != null && !trovato) {
                    barc = riga.substring(0,20).trim();
                    cod_prod = riga.substring(21,36).trim();
                    if (barc.compareTo(barcode) == 0 || cod_prod.compareTo(barcode) == 0) {
                        prod.putString("barcode", barc);
                        prod.putString("cod_prod", cod_prod);
                        prod.putString("descrizione", riga.substring(37,67).trim());
                        prod.putString("pz_crt", riga.substring(80,84).trim());
                        trovato = true;
                        prod.putBoolean("trovato", trovato);
                    }
                }
                buf.close();
                leggi.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return prod;
    }

    public void openDialog() {
        NewQtaDialog newQtaDialog = new NewQtaDialog(p.getDescr(), p.getQta());
        newQtaDialog.show(getSupportFragmentManager(), "newQtaDialog");
    }

    @Override
    public void applyNewQta(String newQta) {
        if (!(newQta.compareTo("") == 0)) {
            p.setQta(newQta);
            write(p.getCod_prod(), p.getBarcode(), "0");
            write(p.getCod_prod(), p.getBarcode(), newQta);
        }
        ord_adapter.notifyDataSetChanged();
    }

    public void read() {
        try {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String nomeFile; int inizio_cod ,fine_cod, inizio_qta, fine_qta; String regex;
            if(sharedPref.getString("key_tipoOrd", "").equals("AF3")) {
                nomeFile = "/SCARICOTRE.TXT"; inizio_cod = 5; fine_cod = 18; inizio_qta = 18; fine_qta = 22; regex = " +$";
            } else {nomeFile = "/TMWII.DAT"; inizio_cod = 0; fine_cod = 13; inizio_qta = 14; fine_qta = 18; regex = "^0+";}
            FileReader fileOrd = new FileReader(getExternalFilesDir(null) + nomeFile);
            BufferedReader leggi = new BufferedReader(fileOrd);
            String line, cod, qta;
            try {
                while ((line = leggi.readLine()) != null) {
                    cod = line.substring(inizio_cod,fine_cod).replaceAll(regex , "");
                    qta = line.substring(inizio_qta,fine_qta).replaceAll("^0+", "");
                    prod = trovaBarcode(cod);
                    if (!prod.getBoolean("trovato")) {
                        Prodotto prodotto = new Prodotto(cod,cod,"NON RICONOSCIUTO",qta);
                        ord.add(prodotto);
                    } else {
                        Prodotto prodotto = new Prodotto(prod.getString("cod_prod"), prod.getString("barcode"), prod.getString("descrizione"), qta);
                        ord.add(prodotto);
                    }
                }
                leggi.close();
                fileOrd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void write(String cod, String barc, String qta) {
        try {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String nomeFile, cod_cli, regex, format, cod_cli_format, riga; int inizio_cod ,fine_cod;
            String quantita = String.format(Locale.ITALY, "%04d", Integer.parseInt(qta));
            if(sharedPref.getString("key_tipoOrd", "").equals("AF3")) {
                cod_cli = sharedPref.getString("key_cod_cli", ""); regex = " +$"; format = "%-13d"; cod_cli_format = "%05d";
                nomeFile = "/SCARICOTRE.TXT"; inizio_cod = 5; fine_cod = 18;
                riga = String.format(Locale.ITALY, cod_cli_format, Integer.parseInt(cod_cli)) + String.format(Locale.ITALY, format, Long.parseLong(cod)) + quantita + "\n";
            } else {
                nomeFile = "/TMWII.DAT"; inizio_cod = 0; fine_cod = 13; regex = "^0+"; format = "%013d ";
                riga = String.format(Locale.ITALY, format, Long.parseLong(cod)) + quantita + "\n";
            }
            if (!(qta.compareTo("0") == 0)) {
                File fileOrd = new File(getExternalFilesDir(null) + nomeFile);
                BufferedWriter scrivi = new BufferedWriter(new FileWriter(fileOrd, true));
                scrivi.write(riga);
                scrivi.close();
            } else {
                File fileTmp = new File(getExternalFilesDir(null) + "/TMP");
                BufferedWriter scriviTmp = new BufferedWriter(new FileWriter(fileTmp));
                File fileOrd = new File(getExternalFilesDir(null) + nomeFile);
                BufferedReader leggi = new BufferedReader(new FileReader(fileOrd));
                String line, codice;
                while ((line = leggi.readLine()) != null) {
                    codice = line.substring(inizio_cod,fine_cod).replaceAll(regex, "");
                    if (codice.compareTo(cod) == 0 || codice.compareTo(barc) == 0) continue;
                    scriviTmp.write(line + "\n");
                }
                leggi.close();
                scriviTmp.close();
                fileOrd.delete();
                fileTmp.renameTo(fileOrd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCamera(View v) {
        Intent intent = new Intent(this, Main3Activity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            ((EditText)findViewById(R.id.editText_barc)).setText(data.getStringExtra("barcode"));
            findViewById(R.id.button_ok).callOnClick();
        }
    }

    private class BackgroundTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = findViewById(R.id.progressBar);
            bar.setIndeterminate(true);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            read();
            return "PRONTO";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.GONE);
            ord_adapter.notifyDataSetChanged();
        }
    }
}
