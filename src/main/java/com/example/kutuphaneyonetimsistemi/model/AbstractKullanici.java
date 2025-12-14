package com.example.kutuphaneyonetimsistemi.model;

public abstract class AbstractKullanici {

    protected int kullaniciId;
    protected int rolId;
    protected String ad;
    protected String soyad;
    protected String tcKimlik;
    protected String email;
    protected String kullaniciAdi;
    protected String sifre;
    protected String telefon;
    protected String adres;

    public AbstractKullanici() {
    }

    public AbstractKullanici(int kullaniciId, int rolId, String ad, String soyad, String tcKimlik, String email, String kullaniciAdi, String sifre, String telefon, String adres) {
        this.kullaniciId = kullaniciId;
        this.rolId = rolId;
        this.ad = ad;
        this.soyad = soyad;
        this.tcKimlik = tcKimlik;
        this.email = email;
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.telefon = telefon;
        this.adres = adres;
    }
    public String getAdSoyad() {
        return this.ad + " " + this.soyad;
    }

    public abstract String getRolAdi();
    public abstract boolean yetkiKontrol(String islem);


    public int getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(int kullaniciId) { this.kullaniciId = kullaniciId; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(String tcKimlik) { this.tcKimlik = tcKimlik; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }
}