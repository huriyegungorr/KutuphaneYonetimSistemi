package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.service.KullaniciService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {

    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField emailField;
    @FXML private TextField kullaniciAdiField;
    @FXML private TextField tcKimlikField;
    @FXML private PasswordField sifreField;
    @FXML private TextField telefonField;
    @FXML private Label mesajLabel;

    private final KullaniciService kullaniciService = new KullaniciService();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SIFRE_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mesajLabel.setStyle("-fx-text-fill: red;");
    }

    @FXML
    protected void handleRegister(ActionEvent event) {

        String rolTipi = "üye";

        String ad = adField.getText().trim();
        String soyad = soyadField.getText().trim();
        String email = emailField.getText().trim();
        String sifre = sifreField.getText();
        String telefon = telefonField.getText().trim();
        String tcKimlik = tcKimlikField.getText().trim();
        String kullaniciAdi = kullaniciAdiField.getText().trim();

        mesajLabel.setText("");
        mesajLabel.setStyle("-fx-text-fill: red;");

        if (ad.isEmpty() || soyad.isEmpty() || sifre.isEmpty() || kullaniciAdi.isEmpty()) {

            mesajLabel.setText("Lütfen Ad, Soyad, Kullanıcı Adı ve Şifre alanlarını doldurunuz.");
            return;
        }

        if (!SIFRE_PATTERN.matcher(sifre).matches()) {
            mesajLabel.setText("Şifre en az 8 karakter, bir büyük harf ve bir rakam içermelidir.");
            return;
        }

        if (email.isEmpty()) {
            mesajLabel.setText("E-posta adresi zorunludur.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            mesajLabel.setText("Lütfen geçerli bir e-posta formatı giriniz (örn: ornek@mail.com).");
            return;
        }

        if (tcKimlik.isEmpty()) {
            mesajLabel.setText("TC Kimlik numarası zorunludur.");
            return;
        }
        if (tcKimlik.length() != 11 || !tcKimlik.matches("\\d+")) {
            mesajLabel.setText("TC Kimlik numarası 11 haneli sayı olmalıdır.");
            return;
        }

        if (telefon.isEmpty()) {
            mesajLabel.setText("Telefon numarası zorunludur.");
            return;
        }

        boolean basarili = kullaniciService.kullaniciKaydet(
                rolTipi,
                ad, soyad, email, sifre, telefon, tcKimlik, kullaniciAdi
        );

        if (basarili) {
            mesajLabel.setText("✅ Kayıt Başarılı! Giriş yapabilirsiniz.");
            mesajLabel.setStyle("-fx-text-fill: green;");
        } else {
            mesajLabel.setText("❌ Kayıt başarısız oldu. Girdiğiniz TC Kimlik, E-posta veya Kullanıcı Adı zaten kullanılıyor olabilir.");
            mesajLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/kutuphaneyonetimsistemi/login-view.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Giriş Yap");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            mesajLabel.setText("Giriş sayfasına geçiş hatası.");
        }
    }
}