package com.example.kutuphaneyonetimsistemi.designpatterns.facade;

import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.service.KullaniciService;

public class KutuphaneFacade {

    private final KullaniciService kullaniciService = new KullaniciService();

    public AbstractKullanici girisYap(String kimlik, String sifre) {

        AbstractKullanici kullanici = kullaniciService.authenticate(kimlik, sifre);

        if (kullanici != null) {
            System.out.println("Giriş başarılı. Oturum başlatılıyor: " + kullanici.getRolAdi());
        }
        return kullanici;
    }
}