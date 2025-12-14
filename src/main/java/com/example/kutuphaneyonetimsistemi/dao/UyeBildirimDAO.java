package com.example.kutuphaneyonetimsistemi.dao;

import com.example.kutuphaneyonetimsistemi.designpatterns.SingletonDBConnection;
import com.example.kutuphaneyonetimsistemi.model.UyeBildirim;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class UyeBildirimDAO {

    private static final String TABLO_ADI = "uye_bildirimleri";

    public void bildirimEkle(int kullaniciId, String mesaj) throws SQLException {
        String sql =
                "INSERT INTO " + TABLO_ADI + " (kullanici_id, mesaj, tarih, okundu_mu) VALUES (?, ?, CURRENT_DATE(), 0)";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);
            stmt.setString(2, mesaj);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("HATA: Bildirim eklenirken veritabanı hatası oluştu. " + e.getMessage());
            throw e;
        }
    }

    public List<UyeBildirim> getBildirimler(int kullaniciId) throws SQLException {
        List<UyeBildirim> liste = new ArrayList<>();

        String sql =
                "SELECT bildirim_id, kullanici_id, mesaj, tarih, okundu_mu " +
                        "FROM " + TABLO_ADI + " WHERE kullanici_id = ? ORDER BY tarih DESC, bildirim_id DESC";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(extractBildirim(rs));
                }
            }
        }
        return liste;
    }

    public boolean bildirimOkunduIsaretle(int bildirimId) throws SQLException {
        String sql = "UPDATE " + TABLO_ADI + " SET okundu_mu = 1 WHERE bildirim_id = ?";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bildirimId);
            return stmt.executeUpdate() > 0;
        }
    }

    private UyeBildirim extractBildirim(ResultSet rs) throws SQLException {
        UyeBildirim bildirim = new UyeBildirim();

        bildirim.setBildirimId(rs.getInt("bildirim_id"));
        bildirim.setKullaniciId(rs.getInt("kullanici_id"));

        String cekilenMesaj = rs.getString("mesaj");
        bildirim.setMesaj(cekilenMesaj != null ? cekilenMesaj : "HATA: Mesaj içeriği çekilemedi.");

        bildirim.setTarih(rs.getDate("tarih").toLocalDate());
        bildirim.setOkunduMu(rs.getInt("okundu_mu") == 1);

        return bildirim;
    }
}