package com.example.ordinefdm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ProdottiListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Prodotto> listaProdotti;

    public ProdottiListAdapter (Context context, ArrayList<Prodotto> listaProdotti) {
        this.context=context; this.listaProdotti=listaProdotti;
    }

    public int getCount() {
        return listaProdotti.size();
    }

    public Object getItem(int pos) {
        return listaProdotti.get(pos);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row,null);
        }
        Prodotto prod = (Prodotto) getItem(pos);
        TextView cod_prod = convertView.findViewById(R.id.ordBarc);
        cod_prod.setText(prod.getCod_prod());
        TextView descr = convertView.findViewById(R.id.ordDescr);
        descr.setText(prod.getDescr());
        TextView qta = convertView.findViewById(R.id.ordColli);
        qta.setText(prod.getQta());
        return convertView;
    }
}
