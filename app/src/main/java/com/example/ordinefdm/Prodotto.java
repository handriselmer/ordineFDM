package com.example.ordinefdm;

public class Prodotto {
    private String cod_prod, barcode, descr, qta;

    public Prodotto (String cod_prod, String barcode, String descr, String qta) {
        this.cod_prod=cod_prod; this.barcode=barcode; this.descr=descr; this.qta=qta;
    }

    public String getCod_prod() {
        return cod_prod;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getDescr() {
        return descr;
    }

    public String getQta() {
        return qta;
    }

    public void setQta(String qta) {
        this.qta = qta;
    }
}
