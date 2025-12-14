package com.example.kutuphaneyonetimsistemi.service;

import com.example.kutuphaneyonetimsistemi.dao.OduncDAO;
import com.example.kutuphaneyonetimsistemi.dao.RezervasyonDAO;
import com.example.kutuphaneyonetimsistemi.model.Rezervasyon;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RezervasyonService {

    private final RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    private final OduncDAO oduncDAO = new OduncDAO();

    public boolean rezervasyonYap(int kitapId, int kullaniciId) {

        try {
            if (oduncDAO.isKitapZatenOduncAlinmis(kullaniciId, kitapId)) {
                System.err.println("HATA: Kullanıcı bu kitabı zaten ödünç almış. Rezervasyon yapılamaz.");
                return false;
            }

            if (rezervasyonDAO.kullaniciAyniKitabaAktifRezervasyonYaptiMi(kullaniciId, kitapId)) {
                System.err.println("HATA: Kullanıcının bu kitaba zaten aktif bir rezervasyonu bulunmaktadır. Rezervasyon yapılamaz.");
                return false;
            }

            Rezervasyon yeniRezervasyon = new Rezervasyon();
            yeniRezervasyon.setKitapId(kitapId);
            yeniRezervasyon.setKullaniciId(kullaniciId);
            yeniRezervasyon.setRezervasyonTarihi(LocalDate.now());

            return rezervasyonDAO.rezervasyonYap(yeniRezervasyon);

        } catch (SQLException e) {
            System.err.println("Rezervasyon yapılırken veritabanı hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean rezervasyonIptalEt(int rezervasyonId) {
        try {
            return rezervasyonDAO.rezervasyonIptalEt(rezervasyonId);
        } catch (SQLException e) {
            System.err.println("Rezervasyon iptal edilirken veritabanı hatası: " + e.getMessage());
            return false;
        }
    }

    public List<Rezervasyon> getKullaniciRezervasyonlari(int kullaniciId) {
        try {
            return rezervasyonDAO.getKullaniciRezervasyonlari(kullaniciId);
        } catch (SQLException e) {
            System.err.println("Kullanıcı rezervasyonları çekilirken veritabanı hatası: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}