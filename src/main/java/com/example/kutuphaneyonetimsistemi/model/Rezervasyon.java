package com.example.kutuphaneyonetimsistemi.model;

import java.time.LocalDate;

public class Rezervasyon {
    private int rezervasyonId;
    private int kullaniciId;
    private int kitapId;
    private LocalDate rezervasyonTarihi;
    private int siraNumarasi;
    private LocalDate bitisTarihi;
    private String durum;

    private String kitapAd;
    private String uyeAdiSoyad;

    public Rezervasyon(int kullaniciId, int kitapId, LocalDate rezervasyonTarihi) {
        this.kullaniciId = kullaniciId;
        this.kitapId = kitapId;
        this.rezervasyonTarihi = rezervasyonTarihi;
        this.siraNumarasi = 0;
    }

    public Rezervasyon() {}

    public int getRezervasyonId() { return rezervasyonId; }
    public int getKullaniciId() { return kullaniciId; }
    public int getKitapId() { return kitapId; }
    public LocalDate getRezervasyonTarihi() { return rezervasyonTarihi; }
    public int getSiraNumarasi() { return siraNumarasi; }
    public String getKitapAd() { return kitapAd; }
    public LocalDate getBitisTarihi() { return bitisTarihi; }
    public String getDurum() { return durum; }
    public String getUyeAdiSoyad() { return uyeAdiSoyad; }

    public void setRezervasyonId(int rezervasyonId) { this.rezervasyonId = rezervasyonId; }
    public void setKullaniciId(int kullaniciId) { this.kullaniciId = kullaniciId; }
    public void setKitapId(int kitapId) { this.kitapId = kitapId; }
    public void setRezervasyonTarihi(LocalDate rezervasyonTarihi) { this.rezervasyonTarihi = rezervasyonTarihi; }
    public void setSiraNumarasi(int siraNumarasi) { this.siraNumarasi = siraNumarasi; }
    public void setKitapAd(String kitapAd) { this.kitapAd = kitapAd; }
    public void setBitisTarihi(LocalDate bitisTarihi) { this.bitisTarihi = bitisTarihi; }
    public void setDurum(String durum) { this.durum = durum; }
    public void setUyeAdiSoyad(String uyeAdiSoyad) { this.uyeAdiSoyad = uyeAdiSoyad; }
}