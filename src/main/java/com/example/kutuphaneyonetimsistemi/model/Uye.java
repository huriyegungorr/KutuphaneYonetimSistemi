package com.example.kutuphaneyonetimsistemi.model;

public class Uye extends AbstractKullanici {

    public Uye() {
        super();
        this.rolId = 1;
    }

    public Uye(int kullaniciId, int rolId, String ad, String soyad, String tcKimlik, String email, String kullaniciAdi, String sifre, String telefon, String adres) {
        super(kullaniciId, rolId, ad, soyad, tcKimlik, email, kullaniciAdi, sifre, telefon, adres);
        this.rolId = 1;
    }

    public Uye(String ad, String soyad, String tcKimlik, String email, String kullaniciAdi, String sifre, String telefon, String adres) {
        super();
        this.ad = ad;
        this.soyad = soyad;
        this.tcKimlik = tcKimlik;
        this.email = email;
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.telefon = telefon;
        this.adres = adres;
        this.rolId = 1;
    }

    @Override
    public String getRolAdi() {
        return "Üye";
    }

    @Override
    public boolean yetkiKontrol(String islem) {
        return islem.equals("profil_guncelle") || islem.equals("kitap_arama") || islem.equals("rezervasyon");
    }
}