package com.example.kutuphaneyonetimsistemi.dao;

import com.example.kutuphaneyonetimsistemi.designpatterns.SingletonDBConnection;
import com.example.kutuphaneyonetimsistemi.model.Kitap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KitapDAO {

    private static final String TABLO_ADI = "kitaplar";

    public boolean kitapEkle(Kitap kitap) {
        Connection conn = null;
        PreparedStatement stmtKitap = null;
        PreparedStatement stmtRelYazar = null;
        PreparedStatement stmtRelKat = null;

        try {
            conn = SingletonDBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            int yazarId = findOrCreateId(conn, "yazarlar", "yazar_adi", "yazar_id", kitap.getYazar());
            int kategoriId = findOrCreateId(conn, "kategoriler", "kategori_adi", "kategori_id", kitap.getKategori());

            String sqlKitap = "INSERT INTO kitaplar (kitap_adi, yayinevi, baski_yili, isbn, adet, raf_no, aciklama) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtKitap = conn.prepareStatement(sqlKitap, Statement.RETURN_GENERATED_KEYS);
            stmtKitap.setString(1, kitap.getAd());
            stmtKitap.setString(2, kitap.getYayinEvi());
            if (kitap.getBaskiYili() > 0) {
                stmtKitap.setInt(3, kitap.getBaskiYili());
            } else {
                stmtKitap.setNull(3, Types.INTEGER);
            }
            stmtKitap.setString(4, kitap.getIsbn());
            stmtKitap.setInt(5, kitap.getAdet());
            stmtKitap.setString(6, kitap.getRafNo());
            stmtKitap.setString(7, kitap.getAciklama());

            int affectedRows = stmtKitap.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Kitap oluşturulamadı.");
            }

            int kitapId = 0;
            try (ResultSet generatedKeys = stmtKitap.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    kitapId = generatedKeys.getInt(1);
                    kitap.setId(kitapId);
                } else {
                    throw new SQLException("Kitap ID alınamadı.");
                }
            }

            String sqlRelYazar = "INSERT INTO kitap_yazar (kitap_id, yazar_id) VALUES (?, ?)";
            stmtRelYazar = conn.prepareStatement(sqlRelYazar);
            stmtRelYazar.setInt(1, kitapId);
            stmtRelYazar.setInt(2, yazarId);
            stmtRelYazar.executeUpdate();

            String sqlRelKat = "INSERT INTO kitap_kategori (kitap_id, kategori_id) VALUES (?, ?)";
            stmtRelKat = conn.prepareStatement(sqlRelKat);
            stmtRelKat.setInt(1, kitapId);
            stmtRelKat.setInt(2, kategoriId);
            stmtRelKat.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Transaction geri alınıyor...");
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (stmtKitap != null) stmtKitap.close();
                if (stmtRelYazar != null) stmtRelYazar.close();
                if (stmtRelKat != null) stmtRelKat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean kitapGuncelle(Kitap kitap) {
        if (kitap.getId() == 0) return false;

        Connection conn = null;
        PreparedStatement stmtKitap = null;

        try {
            conn = SingletonDBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String sqlKitap = "UPDATE kitaplar SET kitap_adi = ?, yayinevi = ?, baski_yili = ?, isbn = ?, adet = ?, raf_no = ?, aciklama = ? WHERE kitap_id = ?";
            stmtKitap = conn.prepareStatement(sqlKitap);
            stmtKitap.setString(1, kitap.getAd());
            stmtKitap.setString(2, kitap.getYayinEvi());
            if (kitap.getBaskiYili() > 0) {
                stmtKitap.setInt(3, kitap.getBaskiYili());
            } else {
                stmtKitap.setNull(3, Types.INTEGER);
            }
            stmtKitap.setString(4, kitap.getIsbn());
            stmtKitap.setInt(5, kitap.getAdet());
            stmtKitap.setString(6, kitap.getRafNo());
            stmtKitap.setString(7, kitap.getAciklama());
            stmtKitap.setInt(8, kitap.getId());
            stmtKitap.executeUpdate();

            silKitapIliskileri(conn, kitap.getId());

            int yazarId = findOrCreateId(conn, "yazarlar", "yazar_adi", "yazar_id", kitap.getYazar());
            int kategoriId = findOrCreateId(conn, "kategoriler", "kategori_adi", "kategori_id", kitap.getKategori());

            ekleKitapIliskileri(conn, kitap.getId(), yazarId, kategoriId);

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (stmtKitap != null) stmtKitap.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void silKitapIliskileri(Connection conn, int kitapId) throws SQLException {
        String sqlYazar = "DELETE FROM kitap_yazar WHERE kitap_id = ?";
        String sqlKategori = "DELETE FROM kitap_kategori WHERE kitap_id = ?";

        try (PreparedStatement stmtYazar = conn.prepareStatement(sqlYazar);
             PreparedStatement stmtKategori = conn.prepareStatement(sqlKategori)) {

            stmtYazar.setInt(1, kitapId);
            stmtYazar.executeUpdate();

            stmtKategori.setInt(1, kitapId);
            stmtKategori.executeUpdate();
        }
    }

    private void ekleKitapIliskileri(Connection conn, int kitapId, int yazarId, int kategoriId) throws SQLException {
        String sqlRelYazar = "INSERT INTO kitap_yazar (kitap_id, yazar_id) VALUES (?, ?)";
        String sqlRelKat = "INSERT INTO kitap_kategori (kitap_id, kategori_id) VALUES (?, ?)";

        try (PreparedStatement stmtRelYazar = conn.prepareStatement(sqlRelYazar);
             PreparedStatement stmtRelKat = conn.prepareStatement(sqlRelKat)) {

            stmtRelYazar.setInt(1, kitapId);
            stmtRelYazar.setInt(2, yazarId);
            stmtRelYazar.executeUpdate();

            stmtRelKat.setInt(1, kitapId);
            stmtRelKat.setInt(2, kategoriId);
            stmtRelKat.executeUpdate();
        }
    }

    private Kitap mapRowToKitap(ResultSet rs) throws SQLException {
        Kitap k = new Kitap();
        int kitapId = rs.getInt("kitap_id");

        k.setKitapId(kitapId);
        k.setId(kitapId);
        k.setAd(rs.getString("kitap_adi"));
        k.setYayinEvi(rs.getString("yayinevi"));
        k.setBaskiYili(rs.getInt("baski_yili"));
        k.setIsbn(rs.getString("isbn"));
        k.setAdet(rs.getInt("adet"));
        k.setMevcutAdet(rs.getInt("adet"));
        k.setRafNo(rs.getString("raf_no"));
        k.setAciklama(rs.getString("aciklama"));

        try {
            k.setYazar(rs.getString("yazar_adi"));
        } catch (SQLException ignored) {}

        try {
            k.setKategori(rs.getString("kategori_adi"));
        } catch (SQLException ignored) {}

        return k;
    }

    public Kitap getKitapByISBN(String isbn) throws SQLException {
        Kitap kitap = null;
        String sql = "SELECT kitap_id, kitap_adi, isbn, adet, yayinevi, baski_yili, raf_no, aciklama FROM " + TABLO_ADI + " WHERE isbn = ?";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    kitap = new Kitap();
                    int kitapId = rs.getInt("kitap_id");

                    kitap.setKitapId(kitapId);
                    kitap.setId(kitapId);

                    kitap.setAd(rs.getString("kitap_adi"));
                    kitap.setIsbn(rs.getString("isbn"));

                    int adet = rs.getInt("adet");
                    kitap.setAdet(adet);
                    kitap.setMevcutAdet(adet);

                    kitap.setYayinEvi(rs.getString("yayinevi"));
                    kitap.setBaskiYili(rs.getInt("baski_yili"));
                    kitap.setRafNo(rs.getString("raf_no"));
                    kitap.setAciklama(rs.getString("aciklama"));
                }
            }
        } catch (SQLException e) {
            System.err.println("ISBN ile kitap getirilirken hata oluştu: " + e.getMessage());
            throw e;
        }
        return kitap;
    }

    public List<Kitap> kitapAra(String kriter, String metin) {

        List<Kitap> sonuclar = new ArrayList<>();
        String kolon = "k.kitap_adi";

        String temizKriter = (kriter != null) ? kriter.toLowerCase(Locale.forLanguageTag("tr")).trim() : "";

        switch (temizKriter) {
            case "kitap adı":
                kolon = "k.kitap_adi";
                break;
            case "yazar adı":
                kolon = "y.yazar_adi";
                break;
            case "isbn":
                kolon = "k.isbn";
                break;
            case "kategori/tür":
                kolon = "cat.kategori_adi";
                break;
            default:
                kolon = "k.kitap_adi";
                break;
        }

        String sql = "SELECT k.*, y.yazar_adi, cat.kategori_adi " +
                "FROM kitaplar k " +
                "LEFT JOIN kitap_yazar ky ON k.kitap_id = ky.kitap_id " +
                "LEFT JOIN yazarlar y ON ky.yazar_id = y.yazar_id " +
                "LEFT JOIN kitap_kategori kc ON k.kitap_id = kc.kitap_id " +
                "LEFT JOIN kategoriler cat ON kc.kategori_id = cat.kategori_id " +
                "WHERE " + kolon + " LIKE ?";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + metin.toLowerCase(Locale.forLanguageTag("tr")).trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sonuclar.add(mapRowToKitap(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL ARAMA HATASI: " + e.getMessage());
            e.printStackTrace();
        }
        return sonuclar;
    }

    public List<Kitap> getAllBooks() {
        List<Kitap> kitaplar = new ArrayList<>();
        String sql = "SELECT k.*, y.yazar_adi, cat.kategori_adi " +
                "FROM kitaplar k " +
                "LEFT JOIN kitap_yazar ky ON k.kitap_id = ky.kitap_id " +
                "LEFT JOIN yazarlar y ON ky.yazar_id = y.yazar_id " +
                "LEFT JOIN kitap_kategori kc ON k.kitap_id = kc.kitap_id " +
                "LEFT JOIN kategoriler cat ON kc.kategori_id = cat.kategori_id";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                kitaplar.add(mapRowToKitap(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kitaplar;
    }

    public boolean kitapSil(int kitapId) {
        String sql = "DELETE FROM kitaplar WHERE kitap_id = ?";
        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kitapId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int findOrCreateId(Connection conn, String tableName, String nameCol, String idCol, String value) throws SQLException {

        String searchSQL = "SELECT " + idCol + " FROM " + tableName + " WHERE " + nameCol + " = ?";
        try (PreparedStatement searchStmt = conn.prepareStatement(searchSQL)) {
            searchStmt.setString(1, value);
            try (ResultSet rs = searchStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(idCol);
                }
            }
        }

        String insertSQL = "INSERT INTO " + tableName + " (" + nameCol + ") VALUES (?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, value);
            insertStmt.executeUpdate();
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException(tableName + " tablosuna kayıt eklenemedi: " + value);
    }
}