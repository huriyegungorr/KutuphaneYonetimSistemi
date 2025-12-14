package com.example.kutuphaneyonetimsistemi.dao;

import com.example.kutuphaneyonetimsistemi.designpatterns.SingletonDBConnection;
import com.example.kutuphaneyonetimsistemi.model.OduncDetay;
import com.example.kutuphaneyonetimsistemi.model.Odunc;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class OduncDAO {

    private static final String TABLO_ADI = "odunc";
    private static final double VARSAYILAN_CEZA = 1.0;

    public boolean oduncKayitEkle(int kullaniciId, int kitapId, LocalDate oduncTarihi, LocalDate sonIadeTarihi) throws SQLException {
        Connection conn = SingletonDBConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {
            String insertSql =
                    "INSERT INTO " + TABLO_ADI + " (kullanici_id, kitap_id, odunc_tarihi, son_iade_tarihi) VALUES (?, ?, ?, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, kullaniciId);
                insertStmt.setInt(2, kitapId);
                insertStmt.setDate(3, Date.valueOf(oduncTarihi));
                insertStmt.setDate(4, Date.valueOf(sonIadeTarihi));

                if (insertStmt.executeUpdate() == 0) {
                    throw new SQLException("Ödünç kaydı eklenemedi.");
                }
            }

            String updateKitapSql = "UPDATE kitaplar SET adet = adet - 1 WHERE kitap_id = ? AND adet > 0";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateKitapSql)) {
                updateStmt.setInt(1, kitapId);
                if (updateStmt.executeUpdate() == 0) {
                    throw new SQLException("Kitap adeti güncellenemedi veya stok yetersiz.");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public boolean oduncKayitGuncelle(int oduncId, LocalDate gercekIadeTarihi) throws SQLException {
        Connection conn = SingletonDBConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {
            int kitapId = 0;
            LocalDate sonIadeTarihi;
            String selectSql = "SELECT kitap_id, son_iade_tarihi FROM " + TABLO_ADI + " WHERE odunc_id = ?";

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, oduncId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        kitapId = rs.getInt("kitap_id");
                        sonIadeTarihi = rs.getDate("son_iade_tarihi").toLocalDate();
                    } else {
                        throw new SQLException("Geçerli ödünç kaydı bulunamadı.");
                    }
                }
            }


            long gecikmeGun = ChronoUnit.DAYS.between(sonIadeTarihi, gercekIadeTarihi);
            gecikmeGun = Math.max(0, gecikmeGun);
            double cezaTutari = gecikmeGun * VARSAYILAN_CEZA;

            String updateOduncSql = "UPDATE " + TABLO_ADI + " SET iade_tarihi = ?, gecikme_gunu = ?, ceza_tutar = ? WHERE odunc_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateOduncSql)) {
                updateStmt.setDate(1, Date.valueOf(gercekIadeTarihi));
                updateStmt.setInt(2, (int) gecikmeGun);
                updateStmt.setDouble(3, cezaTutari);
                updateStmt.setInt(4, oduncId);

                if (updateStmt.executeUpdate() == 0) {
                    throw new SQLException("Ödünç kaydı güncellenemedi.");
                }
            }

            String updateKitapSql = "UPDATE kitaplar SET adet = adet + 1 WHERE kitap_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateKitapSql)) {
                updateStmt.setInt(1, kitapId);
                if (updateStmt.executeUpdate() == 0) {
                    throw new SQLException("Kitap adeti iade sonrası güncellenemedi.");
                }
            }
            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public List<OduncDetay> getGecikmisKitaplar(int kullaniciId) throws SQLException {
        List<OduncDetay> gecikmisList = new ArrayList<>();

        String sql =
                "SELECT o.odunc_id, o.son_iade_tarihi, k.kitap_adi, o.kitap_id " +
                        "FROM " + TABLO_ADI + " o " +
                        "JOIN kitaplar k ON o.kitap_id = k.kitap_id " +
                        "WHERE o.kullanici_id = ? AND o.iade_tarihi IS NULL AND o.son_iade_tarihi < CURRENT_DATE()";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    LocalDate sonIadeTarihi = rs.getDate("son_iade_tarihi").toLocalDate();
                    long gecikmeGunu = ChronoUnit.DAYS.between(sonIadeTarihi, LocalDate.now());
                    double cezaTutari = gecikmeGunu * VARSAYILAN_CEZA;

                    OduncDetay detay = new OduncDetay(
                            rs.getInt("odunc_id"),
                            rs.getString("kitap_adi"),
                            gecikmeGunu,
                            cezaTutari
                    );
                    gecikmisList.add(detay);
                }
            }
        }
        return gecikmisList;
    }

    public List<Odunc> getKullaniciAktifOduncleri(int kullaniciId) throws SQLException {
        List<Odunc> liste = new ArrayList<>();

        String sql = "SELECT o.*, k.kitap_adi FROM " + TABLO_ADI + " o " +
                "JOIN kitaplar k ON o.kitap_id = k.kitap_id " +
                "WHERE o.kullanici_id = ? AND o.iade_tarihi IS NULL " +
                "ORDER BY o.odunc_tarihi DESC";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Odunc o = new Odunc();
                    o.setOduncId(rs.getInt("odunc_id"));
                    o.setKitapId(rs.getInt("kitap_id"));
                    o.setKullaniciId(rs.getInt("kullanici_id"));
                    o.setOduncTarihi(rs.getDate("odunc_tarihi").toLocalDate());
                    o.setSonIadeTarihi(rs.getDate("son_iade_tarihi").toLocalDate());
                    // KRİTİK KISIM: Kitap Adı ataması doğru yapılıyor
                    o.setKitapAdi(rs.getString("kitap_adi"));

                    liste.add(o);
                }
            }
        }
        return liste;
    }

    public List<Odunc> aktifOduncleriGetir() throws SQLException {
        List<Odunc> liste = new ArrayList<>();
        String sql = "SELECT o.*, k.kitap_adi, u.ad, u.soyad, u.tc_kimlik " +
                "FROM " + TABLO_ADI + " o " +
                "JOIN kitaplar k ON o.kitap_id = k.kitap_id " +
                "JOIN kullanicilar u ON o.kullanici_id = u.kullanici_id " +
                "WHERE o.iade_tarihi IS NULL";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Odunc o = new Odunc();
                o.setOduncId(rs.getInt("odunc_id"));
                o.setKitapId(rs.getInt("kitap_id"));
                o.setKullaniciId(rs.getInt("kullanici_id"));
                o.setOduncTarihi(rs.getDate("odunc_tarihi").toLocalDate());
                o.setSonIadeTarihi(rs.getDate("son_iade_tarihi").toLocalDate());
                o.setKitapAdi(rs.getString("kitap_adi"));
                o.setUyeAdiSoyad(rs.getString("ad") + " " + rs.getString("soyad"));
                o.setUyeTc(rs.getString("tc_kimlik"));

                liste.add(o);
            }
        }
        return liste;
    }

    public List<Odunc> getKullaniciOduncGecmisi(int kullaniciId) throws SQLException {
        List<Odunc> liste = new ArrayList<>();
        String sql = "SELECT o.*, k.kitap_adi FROM " + TABLO_ADI + " o " +
                "JOIN kitaplar k ON o.kitap_id = k.kitap_id " +
                "WHERE o.kullanici_id = ? " +
                "ORDER BY o.odunc_tarihi DESC";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Odunc o = new Odunc();
                    o.setOduncId(rs.getInt("odunc_id"));
                    o.setKitapId(rs.getInt("kitap_id"));
                    o.setKullaniciId(rs.getInt("kullanici_id"));
                    o.setOduncTarihi(rs.getDate("odunc_tarihi").toLocalDate());
                    o.setSonIadeTarihi(rs.getDate("son_iade_tarihi").toLocalDate());

                    Date iadeDate = rs.getDate("iade_tarihi");
                    if (iadeDate != null) {
                        o.setIadeTarihi(iadeDate.toLocalDate());
                    }

                    o.setGecikmeGunu(rs.getInt("gecikme_gunu"));
                    o.setCezaTutar(rs.getDouble("ceza_tutar"));
                    o.setKitapAdi(rs.getString("kitap_adi"));
                    liste.add(o);
                }
            }
        }
        return liste;
    }

    public double[] sistemAyarlariniGetir() {

        double[] ayarlar = {15.0, VARSAYILAN_CEZA, 5.0};

        String sql = "SELECT odunc_suresi_gun, gunluk_gecikme_cezasi, max_odunc_sayisi FROM sistem_ayar LIMIT 1";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                ayarlar[0] = rs.getInt("odunc_suresi_gun");
                ayarlar[1] = rs.getDouble("gunluk_gecikme_cezasi");
                ayarlar[2] = rs.getInt("max_odunc_sayisi");
            }
        } catch (SQLException e) {
            System.err.println("Sistem ayarları yüklenirken hata oluştu: " + e.getMessage());
        }
        return ayarlar;
    }

    public int getAktifOduncSayisi(int kullaniciId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLO_ADI + " WHERE kullanici_id = ? AND iade_tarihi IS NULL";
        int count = 0;

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }

    public boolean isKitapZatenOduncAlinmis(int kullaniciId, int kitapId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLO_ADI +
                " WHERE kullanici_id = ? AND kitap_id = ? AND iade_tarihi IS NULL";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);
            stmt.setInt(2, kitapId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}