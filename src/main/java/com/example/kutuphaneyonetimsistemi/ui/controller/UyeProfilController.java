package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.service.KullaniciService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UyeProfilController {

    private final KullaniciService kullaniciService = new KullaniciService();

    private AbstractKullanici mevcutKullanici;

    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField tcKimlikField;
    @FXML private TextField kullaniciAdiField;
    @FXML private TextField emailField;
    @FXML private TextField telefonField;
    @FXML private PasswordField sifreField;
    @FXML private PasswordField sifreTekrarField;
    @FXML private Label profilMesajLabel;

    public void initData(AbstractKullanici kullanici) {
        this.mevcutKullanici = kullanici;
        if (mevcutKullanici != null) {
            adField.setText(mevcutKullanici.getAd());
            soyadField.setText(mevcutKullanici.getSoyad());
            tcKimlikField.setText(mevcutKullanici.getTcKimlik() != null ? mevcutKullanici.getTcKimlik() : "");
            kullaniciAdiField.setText(mevcutKullanici.getKullaniciAdi());
            emailField.setText(mevcutKullanici.getEmail());
            telefonField.setText(mevcutKullanici.getTelefon());
            tcKimlikField.setDisable(true);
            sifreField.clear();
            sifreTekrarField.clear();
            profilMesajLabel.setText("");
        }
    }

    @FXML
    public void handleProfilGuncelle(ActionEvent event) {
        if (mevcutKullanici == null) return;

        String yeniAd = adField.getText();
        String yeniSoyad = soyadField.getText();
        String yeniEmail = emailField.getText();
        String yeniTelefon = telefonField.getText();
        String yeniKullaniciAdi = kullaniciAdiField.getText();
        String yeniSifre = sifreField.getText();
        String yeniSifreTekrar = sifreTekrarField.getText();
        String tcKimlik = mevcutKullanici.getTcKimlik();

        if (yeniEmail.trim().isEmpty() || yeniAd.trim().isEmpty() || yeniSoyad.trim().isEmpty() || yeniKullaniciAdi.trim().isEmpty()) {
            profilMesajLabel.setText("❌ Ad, Soyad, Kullanıcı Adı ve E-posta alanları boş bırakılamaz.");
            profilMesajLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!yeniSifre.isEmpty() && !yeniSifre.equals(yeniSifreTekrar)) {
            profilMesajLabel.setText("❌ Yeni şifreler birbiriyle eşleşmiyor!");
            profilMesajLabel.setStyle("-fx-text-fill: red;");
            sifreField.clear();
            sifreTekrarField.clear();
            return;
        }

        mevcutKullanici.setAd(yeniAd.trim());
        mevcutKullanici.setSoyad(yeniSoyad.trim());
        mevcutKullanici.setEmail(yeniEmail.trim());
        mevcutKullanici.setTelefon(yeniTelefon.trim());
        mevcutKullanici.setKullaniciAdi(yeniKullaniciAdi.trim());

        String sifreToHash = yeniSifre.isEmpty() ? null : yeniSifre;

        boolean basarili = kullaniciService.profilGuncelle(mevcutKullanici, sifreToHash);

        if (basarili) {
            profilMesajLabel.setText("✅ Profil başarıyla güncellendi.");
            profilMesajLabel.setStyle("-fx-text-fill: green;");
            sifreField.clear();
            sifreTekrarField.clear();
        } else {

            profilMesajLabel.setText("❌ Güncelleme başarısız oldu. Girdiğiniz Kullanıcı Adı veya E-posta başka bir kullanıcı tarafından kullanılıyor olabilir.");
            profilMesajLabel.setStyle("-fx-text-fill: red;");
        }
    }
}