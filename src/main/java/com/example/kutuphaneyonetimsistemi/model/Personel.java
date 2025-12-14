package com.example.kutuphaneyonetimsistemi.model;

public class Personel extends AbstractKullanici {

    public Personel() {
        super();
        this.rolId = 2;
    }

    public Personel(int kullaniciId, int rolId, String ad, String soyad, String tcKimlik, String email, String kullaniciAdi, String sifre, String telefon, String adres) {
        super(kullaniciId, rolId, ad, soyad, tcKimlik, email, kullaniciAdi, sifre, telefon, adres);
        this.rolId = 2;
    }

    public Personel(String ad, String soyad, String tcKimlik, String email, String kullaniciAdi, String sifre, String telefon, String adres) {
        super();
        this.ad = ad;
        this.soyad = soyad;
        this.tcKimlik = tcKimlik;
        this.email = email;
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.telefon = telefon;
        this.adres = adres;
        this.rolId = 2;
    }

    @Override
    public String getRolAdi() {
        return "Personel";
    }

    @Override
    public String getAdSoyad() {
        return getAd() + " " + getSoyad();
    }

    @Override
    public boolean yetkiKontrol(String islem) {
        return islem.equals("uye_yonetimi") || islem.equals("kitap_yonetimi") || islem.equals("odunc_ver") || islem.equals("iade_al");
    }
}