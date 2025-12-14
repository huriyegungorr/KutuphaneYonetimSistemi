package com.example.kutuphaneyonetimsistemi.designpatterns.observer;

public interface IBildirimObserver {
    void guncelle(int kullaniciId, String mesaj);

    void guncelle(String mesaj);
}