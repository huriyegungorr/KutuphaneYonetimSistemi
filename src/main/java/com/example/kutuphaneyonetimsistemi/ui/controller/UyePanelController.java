package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UyePanelController implements Initializable {

    @FXML private BorderPane uyeMainPane;
    @FXML private VBox icerikVBox;
    @FXML private Label hosGeldinizLabel;
    @FXML private Button btnBildirimler;

    private AbstractKullanici mevcutKullanici;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initData(AbstractKullanici kullanici) {
        this.mevcutKullanici = kullanici;
        if (mevcutKullanici != null) {
            hosGeldinizLabel.setText("Hoş Geldiniz, " + mevcutKullanici.getAd() + " " + mevcutKullanici.getSoyad());

            handleBildirimler(null);
        } else {

            new Alert(Alert.AlertType.ERROR, "Kritik Hata: Kullanıcı verisi yüklenemedi.").show();
        }
    }


    @FXML
    public void handleBildirimler(ActionEvent event) {
        sayfaYukle("uye-bildirim.fxml");
    }

    @FXML
    public void handleKitapArama(ActionEvent event) {
        sayfaYukle("uye-kitap-arama.fxml");
    }

    @FXML
    public void handleKayıtlarım(ActionEvent event) {
        sayfaYukle("uye-kayitlar.fxml");
    }

    @FXML
    public void handleProfilim(ActionEvent event) {
        sayfaYukle("uye-profil.fxml");
    }

    private void sayfaYukle(String fxmlDosyaAdi) {

        if (mevcutKullanici == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Oturum verisi eksik. Lütfen tekrar giriş yapın.");
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("/com/example/kutuphaneyonetimsistemi/" + fxmlDosyaAdi);

            if (fxmlUrl == null) {
                System.err.println("HATA: FXML dosyası bulunamadı: " + fxmlDosyaAdi);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof UyeBildirimController) {
                ((UyeBildirimController) controller).initData(mevcutKullanici);
            } else if (controller instanceof UyeProfilController) {
                ((UyeProfilController) controller).initData(mevcutKullanici);
            } else if (controller instanceof UyeKitapAramaController) {
                UyeKitapAramaController kitapAramaController = (UyeKitapAramaController) controller;
                kitapAramaController.setMevcutKullanici(mevcutKullanici);
                kitapAramaController.setParentController(this);
            } else if (controller instanceof UyeKayitlarController) {
                ((UyeKayitlarController) controller).initData(mevcutKullanici);
            }

            uyeMainPane.setCenter(view);

        } catch (IOException e) {
            System.err.println("Sayfa yüklenirken hata oluştu: " + fxmlDosyaAdi);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCikis(ActionEvent event) {
        System.out.println("Çıkış yapıldı.");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/kutuphaneyonetimsistemi/login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}