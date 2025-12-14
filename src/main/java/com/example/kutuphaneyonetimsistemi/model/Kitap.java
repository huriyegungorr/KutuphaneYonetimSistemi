package com.example.kutuphaneyonetimsistemi.model;

import com.example.kutuphaneyonetimsistemi.designpatterns.state.KitapDurumu;
import com.example.kutuphaneyonetimsistemi.designpatterns.state.RaftaDurumu;

public class Kitap extends Yayin {

    private int kitapId;
    protected String yazar;
    protected String kategori;
    protected String isbn;
    protected int baskiYili;
    protected int mevcutAdet;
    protected String rafNo;
    protected String aciklama;
    protected KitapDurumu durum;

    public Kitap(String ad, String yazar, String yayinEvi, int baskiYili, String isbn, String kategori, int mevcutAdet, String rafNo, String aciklama) {
        this.ad = ad;
        this.yazar = yazar;
        this.yayinEvi = yayinEvi;
        this.baskiYili = baskiYili;
        this.isbn = isbn;
        this.kategori = kategori;
        this.mevcutAdet = mevcutAdet;
        this.rafNo = rafNo;
        this.aciklama = aciklama;
        this.durum = new RaftaDurumu();
    }

    public Kitap() { this.durum = new RaftaDurumu(); }

    @Override
    public String getDetay() { return ad + " - " + isbn; }

    public String getDurumString() { return (durum != null) ? durum.getDurumAdi() : "Bilinmiyor"; }

    public int getKitapId() {
        return kitapId;
    }

    public void setKitapId(int kitapId) {
        this.kitapId = kitapId;
    }

    public int getMevcutAdet() {
        return mevcutAdet;
    }

    public void setMevcutAdet(int mevcutAdet) {
        this.mevcutAdet = mevcutAdet;
    }

    public int getAdet() {
        return mevcutAdet;
    }

    public void setAdet(int adet) {
        this.mevcutAdet = adet;
    }

    public String getYazar() { return yazar; }
    public void setYazar(String yazar) { this.yazar = yazar; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getBaskiYili() { return baskiYili; }
    public void setBaskiYili(int baskiYili) { this.baskiYili = baskiYili; }
    public String getRafNo() { return rafNo; }
    public void setRafNo(String rafNo) { this.rafNo = rafNo; }
    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getYayinEvi() { return yayinEvi; }
    public void setYayinEvi(String yayinEvi) { this.yayinEvi = yayinEvi; }
}