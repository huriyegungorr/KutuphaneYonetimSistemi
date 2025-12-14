package com.example.kutuphaneyonetimsistemi.designpatterns.observer;

public class KonsolBildirim implements IBildirimObserver {

    @Override
    public void guncelle(String mesaj) {
        System.out.println("📢 BİLDİRİM SİSTEMİ: " + mesaj);
    }

    @Override
    public void guncelle(int kullaniciId, String mesaj) {

        System.out.println("📢 BİLDİRİM SİSTEMİ (DB YAZMA): Kullanıcı ID: " + kullaniciId + " -> " + mesaj);

    }
}