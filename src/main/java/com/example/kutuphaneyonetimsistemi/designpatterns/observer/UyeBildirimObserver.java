package com.example.kutuphaneyonetimsistemi.designpatterns.observer;

import com.example.kutuphaneyonetimsistemi.dao.UyeBildirimDAO;
import java.sql.SQLException;

public class UyeBildirimObserver implements IBildirimObserver {

    private final UyeBildirimDAO bildirimDAO = new UyeBildirimDAO();

    @Override
    public void guncelle(int kullaniciId, String mesaj) {
        try {

            bildirimDAO.bildirimEkle(kullaniciId, mesaj);
            System.out.println("LOG: Kullanıcıya yeni bildirim kaydedildi (ID: " + kullaniciId + ")");
        } catch (SQLException e) {
            System.err.println("HATA: Bildirim veritabanına kaydedilemedi! " + e.getMessage());

        }
    }

    @Override
    public void guncelle(String mesaj) {

    }
}