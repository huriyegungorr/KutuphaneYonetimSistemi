package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.designpatterns.facade.KutuphaneFacade;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField kimlikField;
    @FXML private PasswordField sifreField;
    @FXML private Label mesajLabel;

    private final KutuphaneFacade kutuphaneFacade = new KutuphaneFacade();

    @FXML
    private void handleLogin(ActionEvent event) {
        String kimlik = kimlikField.getText();
        String sifre = sifreField.getText();

        if (kimlik.isEmpty() || sifre.isEmpty()) {
            mesajLabel.setText("Lütfen kimlik ve şifre bilgilerini giriniz.");
            return;
        }

        AbstractKullanici kullanici = kutuphaneFacade.girisYap(kimlik, sifre);

        if (kullanici != null) {
            mesajLabel.setText("✅ Giriş Başarılı!");
            mesajLabel.setStyle("-fx-text-fill: green;");
            handleSuccessfulLogin(kullanici, event);
        } else {
            mesajLabel.setText("❌ Geçersiz kimlik veya şifre.");
            mesajLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void handleSuccessfulLogin(AbstractKullanici kullanici, ActionEvent event) {
        String hedefFxml;
        String title;
        int rolId = kullanici.getRolId();

        if (rolId == 1) {
            hedefFxml = "/com/example/kutuphaneyonetimsistemi/uye-panel.fxml";
            title = "Üye Ana Sayfa";
        } else if (rolId == 2) {
            hedefFxml = "/com/example/kutuphaneyonetimsistemi/personel-panel.fxml";
            title = "Personel Yönetim Paneli";
        } else {
            mesajLabel.setText("❌ Kullanıcı rolü tanımlanamadı. Yetkili ile görüşün.");
            mesajLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(hedefFxml));
            javafx.scene.Parent root = loader.load();

            if (rolId == 1) {
                UyePanelController controller = loader.getController();

                controller.initData(kullanici);
            }
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Ekran yüklenirken kritik hata oluştu! Yol: " + hedefFxml);
            e.printStackTrace();
            mesajLabel.setText("❌ Ana sayfa yüklenemedi! Detaylar için konsola bakınız.");
            mesajLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/kutuphaneyonetimsistemi/register-view.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Üye Kayıt");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            mesajLabel.setText("Kayıt sayfasına geçiş hatası.");
        }
    }
}