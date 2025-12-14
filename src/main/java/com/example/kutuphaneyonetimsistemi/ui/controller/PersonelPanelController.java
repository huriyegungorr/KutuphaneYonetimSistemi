package com.example.kutuphaneyonetimsistemi.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PersonelPanelController {

    @FXML
    private BorderPane mainPane;

    @FXML
    public void handleKitapIslemleri(ActionEvent event) {

        sayfaYukle("kitap-yonetimi.fxml");
    }

    @FXML
    public void handleUyeIslemleri(ActionEvent event) {
        sayfaYukle("uye-yonetimi.fxml");
    }

    @FXML
    public void handleOduncIslemleri(ActionEvent event) {
        sayfaYukle("odunc-islemleri.fxml");
    }

    @FXML
    public void handleRaporlar(ActionEvent event) {

        System.out.println("İstatistikler henüz hazırlanmadı.");
    }

    @FXML
    public void handleCikis(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/com/example/kutuphaneyonetimsistemi/login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sayfaYukle(String fxmlDosyaAdi) {
        try {
            URL fxmlUrl = getClass().getResource("/com/example/kutuphaneyonetimsistemi/" + fxmlDosyaAdi);
            if (fxmlUrl == null) {
                System.err.println("HATA: FXML dosyası bulunamadı: " + fxmlDosyaAdi);
                return;
            }
            Parent view = FXMLLoader.load(fxmlUrl);
            mainPane.setCenter(view);
        } catch (IOException e) {
            System.err.println("Sayfa yüklenirken hata oluştu: " + fxmlDosyaAdi);
            e.printStackTrace();
        }
    }
}