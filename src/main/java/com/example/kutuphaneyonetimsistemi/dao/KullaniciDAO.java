package com.example.kutuphaneyonetimsistemi.dao;

import com.example.kutuphaneyonetimsistemi.designpatterns.SingletonDBConnection;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.Uye;
import com.example.kutuphaneyonetimsistemi.model.Personel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KullaniciDAO {

    public KullaniciDAO() {

    }

    public boolean kullaniciEkle(AbstractKullanici kullanici) {

        String sql = "INSERT INTO kullanicilar (rol_id, ad, soyad, email, sifre, telefon, tc_kimlik, kullanici_adi, adres) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, kullanici.getRolId());
            stmt.setString(2, kullanici.getAd());
            stmt.setString(3, kullanici.getSoyad());
            stmt.setString(4, kullanici.getEmail());
            stmt.setString(5, kullanici.getSifre());
            stmt.setString(6, kullanici.getTelefon());
            stmt.setString(7, kullanici.getTcKimlik());
            stmt.setString(8, kullanici.getKullaniciAdi());
            stmt.setString(9, kullanici.getAdres());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Veritabanı Kayıt Hatası! Detaylar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public AbstractKullanici authenticate(String kimlik) {

        String sql = "SELECT * FROM kullanicilar WHERE (tc_kimlik = ? OR email = ? OR kullanici_adi = ?)";

        try (Connection connection = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, kimlik);
            stmt.setString(2, kimlik);
            stmt.setString(3, kimlik);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    return mapRowToKullanici(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Giriş sırasında veritabanı hatası!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean kullaniciGuncelle(AbstractKullanici kullanici) {

        try {
            if (kullaniciAdiVeEmailCakismaKontrol(
                    kullanici.getKullaniciId(),
                    kullanici.getKullaniciAdi(),
                    kullanici.getEmail())) {

                System.err.println("HATA: Kullanıcı adı veya E-posta başka bir kullanıcı tarafından kullanılıyor.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE kullanicilar SET ad=?, soyad=?, email=?, telefon=?, adres=?, tc_kimlik=?, kullanici_adi=?, sifre=? WHERE kullanici_id=?";

        try (Connection connection = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, kullanici.getAd());
            stmt.setString(2, kullanici.getSoyad());
            stmt.setString(3, kullanici.getEmail());
            stmt.setString(4, kullanici.getTelefon());
            stmt.setString(5, kullanici.getAdres());
            stmt.setString(6, kullanici.getTcKimlik());
            stmt.setString(7, kullanici.getKullaniciAdi());
            stmt.setString(8, kullanici.getSifre()); // Hashlenmiş şifre bekleniyor
            stmt.setInt(9, kullanici.getKullaniciId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Profil Güncelleme Hatası! Detaylar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean kullaniciSil(int id) {
        String sql = "DELETE FROM kullanicilar WHERE kullanici_id=?";
        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Silme Hatası! Detaylar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Uye getUyeByKimlik(String kimlik) throws SQLException {
        Uye uye = null;
        String sql = "SELECT k.*, r.rol_adi FROM kullanicilar k JOIN roller r ON k.rol_id = r.rol_id WHERE (k.tc_kimlik = ? OR k.kullanici_id = ?) AND r.rol_adi = 'uye'";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int id = -1;
            try {
                id = Integer.parseInt(kimlik);
            } catch (NumberFormatException ignored) {

            }

            stmt.setString(1, kimlik);
            stmt.setInt(2, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AbstractKullanici akullanici = mapRowToKullanici(rs);
                    if (akullanici instanceof Uye) {
                        uye = (Uye) akullanici;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kimlik ile üye getirilirken hata oluştu: " + e.getMessage());
            throw e;
        }
        return uye;
    }

    public List<AbstractKullanici> tumUyeleriGetir() {
        List<AbstractKullanici> uyeler = new ArrayList<>();
        String sql = "SELECT k.*, r.rol_adi FROM kullanicilar k JOIN roller r ON k.rol_id = r.rol_id WHERE r.rol_adi = 'uye'";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                uyeler.add(mapRowToKullanici(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uyeler;
    }

    public List<AbstractKullanici> uyeAra(String aramaKelimesi) {
        List<AbstractKullanici> uyeler = new ArrayList<>();
        String sql = "SELECT k.*, r.rol_adi FROM kullanicilar k JOIN roller r ON k.rol_id = r.rol_id WHERE r.rol_adi = 'uye' " +
                "AND (k.ad LIKE ? OR k.soyad LIKE ? OR k.tc_kimlik LIKE ?)";

        try (Connection conn = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + aramaKelimesi + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    uyeler.add(mapRowToKullanici(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uyeler;
    }

    public AbstractKullanici mapRowToKullanici(ResultSet rs) throws SQLException {

        int rolId = rs.getInt("rol_id");

        AbstractKullanici kullanici;
        if (rolId == 1) {
            kullanici = new Uye();
        } else if (rolId == 2) {
            kullanici = new Personel();
        } else {
            throw new SQLException("Bilinmeyen Rol ID'si: " + rolId);
        }

        kullanici.setKullaniciId(rs.getInt("kullanici_id"));
        kullanici.setRolId(rolId);
        kullanici.setAd(rs.getString("ad"));
        kullanici.setSoyad(rs.getString("soyad"));
        kullanici.setEmail(rs.getString("email"));
        kullanici.setTcKimlik(rs.getString("tc_kimlik"));
        kullanici.setKullaniciAdi(rs.getString("kullanici_adi"));
        kullanici.setTelefon(rs.getString("telefon"));

        try {
            kullanici.setAdres(rs.getString("adres"));
        } catch (SQLException ignored) {

        }

        try {
            kullanici.setSifre(rs.getString("sifre"));
        } catch (SQLException ignored) {

        }


        return kullanici;
    }

    private boolean kullaniciAdiVeEmailCakismaKontrol(int mevcutId, String kullaniciAdi, String email) throws SQLException {
        String sql;

        if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
            sql = "SELECT COUNT(*) FROM kullanicilar WHERE email = ? AND kullanici_id != ?";
        } else {
            sql = "SELECT COUNT(*) FROM kullanicilar WHERE (kullanici_adi = ? OR email = ?) AND kullanici_id != ?";
        }

        try (Connection connection = SingletonDBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                stmt.setString(1, email);
                stmt.setInt(2, mevcutId);
            } else {
                stmt.setString(1, kullaniciAdi);
                stmt.setString(2, email);
                stmt.setInt(3, mevcutId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}