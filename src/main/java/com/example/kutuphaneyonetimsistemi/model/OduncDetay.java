package com.example.kutuphaneyonetimsistemi.model;

public class OduncDetay {
    private int oduncId;
    private String kitapAd;
    private long gecikmeGunu;
    private double cezaTutari;

    public OduncDetay(int oduncId, String kitapAd, long gecikmeGunu, double cezaTutari) {
        this.oduncId = oduncId;
        this.kitapAd = kitapAd;
        this.gecikmeGunu = gecikmeGunu;
        this.cezaTutari = cezaTutari;
    }

    public String getKitapAd() { return kitapAd; }
    public long getGecikmeGunu() { return gecikmeGunu; }
    public double getCezaTutari() { return cezaTutari; }
    public int getOduncId() { return oduncId; }
}