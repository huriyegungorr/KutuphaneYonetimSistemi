package com.example.kutuphaneyonetimsistemi.model;

import java.time.LocalDate;

public class UyeBildirim {

    private int bildirimId;
    private int kullaniciId;
    private String mesaj;
    private LocalDate tarih;
    private boolean okunduMu;
    public UyeBildirim() {}
    public int getBildirimId() {
        return bildirimId;
    }

    public int getKullaniciId() {
        return kullaniciId;
    }

    public String getMesaj() {
        return mesaj;
    }

    public LocalDate getTarih() {
        return tarih;
    }

    public boolean isOkunduMu() {
        return okunduMu;
    }

    public void setBildirimId(int bildirimId) {
        this.bildirimId = bildirimId;
    }

    public void setKullaniciId(int kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public void setTarih(LocalDate tarih) {
        this.tarih = tarih;
    }

    public void setOkunduMu(boolean okunduMu) {
        this.okunduMu = okunduMu;
    }
}