package com.example.kutuphaneyonetimsistemi.service;

import com.example.kutuphaneyonetimsistemi.dao.OduncDAO;
import com.example.kutuphaneyonetimsistemi.designpatterns.SingletonDBConnection;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.Kitap;
import com.example.kutuphaneyonetimsistemi.model.Odunc;
import com.example.kutuphaneyonetimsistemi.model.OduncDetay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OduncService {

    private final OduncDAO oduncDAO = new OduncDAO();

    private static final int ODUNC_SURESI_GUN = 14;
    private static final double GUNLUK_CEZA_TL = 1.0;

    public boolean kitapOduncAl(AbstractKullanici kullanici, Kitap kitap) {

        if (kitap.getAdet() <= 0) {
            System.err.println("Ödünç Hatası: Kitap stokta mevcut değil.");
            return false;
        }

        LocalDate oduncTarihi = LocalDate.now();
        LocalDate iadeTarihi = oduncTarihi.plusDays(ODUNC_SURESI_GUN);

        try {

            return oduncDAO.oduncKayitEkle(kullanici.getKullaniciId(), kitap.getId(), oduncTarihi, iadeTarihi);

        } catch (SQLException e) {
            System.err.println("Ödünç alınırken veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean kitapIadeEt(int oduncId) {
        try {
            return oduncDAO.oduncKayitGuncelle(oduncId, LocalDate.now());
        } catch (SQLException e) {
            System.err.println("Kitap iade edilirken veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Odunc> getAktifOduncler(int kullaniciId) {
        try {
            return oduncDAO.getKullaniciAktifOduncleri(kullaniciId);
        } catch (SQLException e) {
            System.err.println("Aktif ödünçler çekilirken veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<OduncDetay> getGecikmisKitaplar(int kullaniciId) {
        try {
            return oduncDAO.getGecikmisKitaplar(kullaniciId);
        } catch (SQLException e) {
            System.err.println("Gecikmiş kitaplar çekilirken veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}