package com.example.kutuphaneyonetimsistemi.dao;

import com.example.kutuphaneyonetimsistemi.designpatterns.SingletonDBConnection;
import com.example.kutuphaneyonetimsistemi.model.Rezervasyon;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RezervasyonDAO {

    public boolean rezervasyonYap(Rezervasyon rezervasyon) throws SQLException {
        Connection conn = SingletonDBConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {

            int siraNumarasi = getNextSiraNumarasi(conn, rezervasyon.getKitapId());
            rezervasyon.setSiraNumarasi(siraNumarasi);

            String sql =
                    "INSERT INTO rezervasyonlar " +
                            "(kitap_id, kullanici_id, rezervasyon_tarihi, sira_numarasi, bitis_tarihi, durum) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, rezervasyon.getKitapId());
                stmt.setInt(2, rezervasyon.getKullaniciId());
                stmt.setDate(3, Date.valueOf(rezervasyon.getRezervasyonTarihi()));
                stmt.setInt(4, rezervasyon.getSiraNumarasi());
                stmt.setNull(5, Types.DATE);
                stmt.setString(6, "AKTIF");

                if (stmt.executeUpdate() == 0)
                    throw new SQLException("Rezervasyon eklenemedi!");
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

    public boolean kullaniciAyniKitabaAktifRezervasyonYaptiMi(int kullaniciId, int kitapId) throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM rezervasyonlar " +
                        "WHERE kitap_id = ? AND kullanici_id = ? AND durum IN ('AKTIF', 'BEKLEMEDE')";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kitapId);
            stmt.setInt(2, kullaniciId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getNextSiraNumarasi(Connection conn, int kitapId) throws SQLException {

        String sql =
                "SELECT COALESCE(MAX(sira_numarasi), 0) AS max_sira " +
                        "FROM rezervasyonlar WHERE kitap_id = ? AND durum IN ('AKTIF', 'BEKLEMEDE')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kitapId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("max_sira") + 1;
                }
            }
        }
        return 1;
    }

    public boolean rezervasyonIptalEt(int rezervasyonId) throws SQLException {
        Connection conn = SingletonDBConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {

            int kitapId = getKitapIdForRezervasyon(conn, rezervasyonId);

            String sql = "UPDATE rezervasyonlar SET durum = 'IPTAL' WHERE rezervasyon_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, rezervasyonId);
                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            reorderQueue(conn, kitapId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public boolean rezervasyonBitir(int rezervasyonId) throws SQLException {
        Connection conn = SingletonDBConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {

            int kitapId = getKitapIdForRezervasyon(conn, rezervasyonId);

            String sql = "UPDATE rezervasyonlar SET durum = 'TAMAMLANDI', bitis_tarihi = CURRENT_DATE " +
                    "WHERE rezervasyon_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, rezervasyonId);
                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            reorderQueue(conn, kitapId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    private int getKitapIdForRezervasyon(Connection conn, int rezervasyonId) throws SQLException {
        String sql = "SELECT kitap_id FROM rezervasyonlar WHERE rezervasyon_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rezervasyonId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("kitap_id");
                }
            }
        }

        throw new SQLException("Rezervasyon ID bulunamadı: " + rezervasyonId);
    }

    private void reorderQueue(Connection conn, int kitapId) throws SQLException {

        String selectSQL = "SELECT rezervasyon_id FROM rezervasyonlar " +
                "WHERE kitap_id = ? AND durum IN ('AKTIF', 'BEKLEMEDE') " +
                "ORDER BY rezervasyon_tarihi ASC, sira_numarasi ASC"; // Kuyruk önceliği

        String updateSQL = "UPDATE rezervasyonlar SET sira_numarasi = ? WHERE rezervasyon_id = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {

            selectStmt.setInt(1, kitapId);

            try (ResultSet rs = selectStmt.executeQuery()) {
                int newSiraNumarasi = 1;
                List<Integer> idsToUpdate = new ArrayList<>();
                while (rs.next()) {
                    idsToUpdate.add(rs.getInt("rezervasyon_id"));
                }

                for (int id : idsToUpdate) {
                    updateStmt.setInt(1, newSiraNumarasi++);
                    updateStmt.setInt(2, id);
                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
            }
        }
    }

    public List<Rezervasyon> getAktifRezervasyonKuyruguDetayli() throws SQLException {

        List<Rezervasyon> liste = new ArrayList<>();

        String sql = "SELECT r.*, k.kitap_adi, u.ad, u.soyad " +
                "FROM rezervasyonlar r " +
                "JOIN kitaplar k ON r.kitap_id = k.kitap_id " +
                "JOIN kullanicilar u ON r.kullanici_id = u.kullanici_id " +
                "WHERE r.durum IN ('AKTIF', 'BEKLEMEDE') " +
                "ORDER BY r.sira_numarasi ASC";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Rezervasyon r = extractRezervasyon(rs);

                try {
                    r.setKitapAd(rs.getString("kitap_adi"));
                    r.setUyeAdiSoyad(rs.getString("ad") + " " + rs.getString("soyad"));
                } catch (SQLException ignored) {
                }

                liste.add(r);
            }
        }
        return liste;
    }

    public List<Rezervasyon> getKullaniciRezervasyonlari(int kullaniciId) throws SQLException {

        List<Rezervasyon> list = new ArrayList<>();

        String sql =
                "SELECT r.*, k.kitap_adi FROM rezervasyonlar r " +
                        "JOIN kitaplar k ON r.kitap_id = k.kitap_id " +
                        "WHERE r.kullanici_id = ? ORDER BY r.rezervasyon_tarihi DESC";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kullaniciId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rezervasyon r = extractRezervasyon(rs);
                    try { r.setKitapAd(rs.getString("kitap_adi")); } catch (SQLException ignored) {}
                    list.add(r);
                }
            }
        }

        return list;
    }

    public List<Rezervasyon> getKitapRezervasyonKuyrugu(int kitapId) throws SQLException {

        List<Rezervasyon> list = new ArrayList<>();

        String sql =
                "SELECT * FROM rezervasyonlar " +
                        "WHERE kitap_id = ? AND durum IN ('AKTIF', 'BEKLEMEDE') " +
                        "ORDER BY sira_numarasi ASC";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kitapId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRezervasyon(rs));
                }
            }
        }

        return list;
    }

    private Rezervasyon extractRezervasyon(ResultSet rs) throws SQLException {
        Rezervasyon r = new Rezervasyon();

        r.setRezervasyonId(rs.getInt("rezervasyon_id"));
        r.setKitapId(rs.getInt("kitap_id"));
        r.setKullaniciId(rs.getInt("kullanici_id"));
        r.setRezervasyonTarihi(rs.getDate("rezervasyon_tarihi").toLocalDate());
        r.setSiraNumarasi(rs.getInt("sira_numarasi"));

        Date bitis = rs.getDate("bitis_tarihi");
        r.setBitisTarihi(bitis != null ? bitis.toLocalDate() : null);

        r.setDurum(rs.getString("durum"));

        return r;
    }
}