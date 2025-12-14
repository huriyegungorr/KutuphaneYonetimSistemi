package com.example.kutuphaneyonetimsistemi.service;

import com.example.kutuphaneyonetimsistemi.dao.KullaniciDAO;
import com.example.kutuphaneyonetimsistemi.designpatterns.KullaniciFactory;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;

public class KullaniciService {

    private final KullaniciDAO kullaniciDAO = new KullaniciDAO();

    public String hashSifre(String sifre) {
        return SifreUtil.hashSifre(sifre);
    }

    public boolean checkSifre(String girilenSifre, String dbHash) {
        return SifreUtil.checkSifre(girilenSifre, dbHash);
    }

    public boolean kullaniciKaydet(String tip,
                                   String ad,
                                   String soyad,
                                   String email,
                                   String sifre,
                                   String telefon,
                                   String tcKimlik,
                                   String kullaniciAdi) {

        if (ad == null || ad.trim().isEmpty()) return false;
        if (soyad == null || soyad.trim().isEmpty()) return false;
        if (email == null || email.trim().isEmpty()) return false;
        if (sifre == null || sifre.isEmpty()) return false;

        tip = (tip == null) ? "üye" : tip.trim().toLowerCase();
        ad = ad.trim();
        soyad = soyad.trim();
        email = email.trim();
        if (telefon != null) telefon = telefon.trim();
        if (tcKimlik != null) tcKimlik = tcKimlik.trim();
        if (kullaniciAdi != null) {
            kullaniciAdi = kullaniciAdi.trim();
            if (kullaniciAdi.isEmpty()) kullaniciAdi = null;
        }

        String haslenmisSifre = SifreUtil.hashSifre(sifre);

        AbstractKullanici yeniKullanici = KullaniciFactory.createKullanici(
                tip,
                ad, soyad, email, haslenmisSifre, telefon, tcKimlik, kullaniciAdi
        );

        return kullaniciDAO.kullaniciEkle(yeniKullanici);
    }

    public AbstractKullanici authenticate(String kimlik, String girilenSifre) {
        if (kimlik == null || girilenSifre == null) return null;

        AbstractKullanici kullanici = kullaniciDAO.authenticate(kimlik.trim());

        if (kullanici == null) {
            return null;
        }

        String dbHash = kullanici.getSifre();

        if (dbHash != null && checkSifre(girilenSifre, dbHash)) {

            kullanici.setSifre(null);
            return kullanici;
        }

        return null;
    }

    public boolean profilGuncelle(AbstractKullanici guncelKullanici, String yeniSifreDuzMetin) {
        if (guncelKullanici == null || guncelKullanici.getKullaniciId() <= 0) return false;

        if (yeniSifreDuzMetin != null && !yeniSifreDuzMetin.isEmpty()) {
            String haslenmis = SifreUtil.hashSifre(yeniSifreDuzMetin);
            guncelKullanici.setSifre(haslenmis);
        }

        return kullaniciDAO.kullaniciGuncelle(guncelKullanici);
    }
}