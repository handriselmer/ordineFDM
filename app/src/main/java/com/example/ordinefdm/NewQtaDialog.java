package com.example.ordinefdm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NewQtaDialog extends AppCompatDialogFragment {
    private EditText newQta;
    private NewQtaDialogListener listener;
    private String descr, qta;
    NewQtaDialog(String descr, String qta) {
        this.descr = descr; this.qta = qta;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_layout, null);
        b.setView(v)
                .setTitle(descr + " Qta " + qta)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nQta = newQta.getText().toString();
                        listener.applyNewQta(nQta);
                    }
                });
        newQta = v.findViewById(R.id.editTextNewQta);
        return b.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NewQtaDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "deve implementare NewQtaDialogListener");
        }
    }

    public interface NewQtaDialogListener {
        void applyNewQta(String newQta);
    }
}
