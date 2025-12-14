package com.example.kutuphaneyonetimsistemi.designpatterns;

import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.Uye;
import com.example.kutuphaneyonetimsistemi.model.Personel;

public class KullaniciFactory {

    public static AbstractKullanici createKullanici(String tip, String ad, String soyad, String email, String sifre, String telefon, String tcKimlik, String kullaniciAdi) {

        String adres = "";

        AbstractKullanici kullanici;
        int rolId;

        if ("uye".equalsIgnoreCase(tip)) {
            rolId = 1;

            kullanici = new Uye(ad, soyad, tcKimlik, email, kullaniciAdi, sifre, telefon, adres);

        } else if ("personel".equalsIgnoreCase(tip)) {
            rolId = 2;

            kullanici = new Personel(ad, soyad, tcKimlik, email, kullaniciAdi, sifre, telefon, adres);

        } else if ("admin".equalsIgnoreCase(tip)) {
            rolId = 3;

            kullanici = new Personel(ad, soyad, tcKimlik, email, kullaniciAdi, sifre, telefon, adres);

        } else {

            System.err.println("HATA: Tanımsız kullanıcı rolü, varsayılan (Uye) olarak ayarlandı: " + tip);
            rolId = 1;
            kullanici = new Uye(ad, soyad, tcKimlik, email, kullaniciAdi, sifre, telefon, adres);
        }

        kullanici.setRolId(rolId);

        return kullanici;
    }
}