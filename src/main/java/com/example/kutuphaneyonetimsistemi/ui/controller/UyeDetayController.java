package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.dao.OduncDAO;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.Odunc;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class UyeDetayController {

    @FXML private Label lblBaslik;
    @FXML private Label lblBilgi;
    @FXML private TableView<Odunc> tabloAktif;
    @FXML private TableColumn<Odunc, String> colAktifKitap;
    @FXML private TableColumn<Odunc, LocalDate> colAktifVerilis;
    @FXML private TableColumn<Odunc, LocalDate> colAktifSonTarih;
    @FXML private TableColumn<Odunc, String> colAktifDurum;
    @FXML private TableView<Odunc> tabloGecmis;
    @FXML private TableColumn<Odunc, String> colGecmisKitap;
    @FXML private TableColumn<Odunc, LocalDate> colGecmisVerilis;
    @FXML private TableColumn<Odunc, LocalDate> colGecmisIade;
    @FXML private TableColumn<Odunc, Integer> colGecmisGecikme;
    @FXML private TableColumn<Odunc, Double> colGecmisCeza;

    private final OduncDAO oduncDAO = new OduncDAO();
    private AbstractKullanici seciliUye;

    @FXML
    public void initialize() {

        colAktifKitap.setCellValueFactory(new PropertyValueFactory<>("kitapAdi"));
        colAktifVerilis.setCellValueFactory(new PropertyValueFactory<>("oduncTarihi"));
        colAktifSonTarih.setCellValueFactory(new PropertyValueFactory<>("sonIadeTarihi"));

        colAktifDurum.setCellValueFactory(cellData -> {
            Odunc o = cellData.getValue();

            if (o.getSonIadeTarihi() == null) return new SimpleStringProperty("Bilinmiyor");

            long gecikme = ChronoUnit.DAYS.between(o.getSonIadeTarihi(), LocalDate.now());
            if (gecikme > 0) {
                return new SimpleStringProperty("⚠️ " + gecikme + " gün GECİKTİ");
            } else {
                return new SimpleStringProperty("Zamanı Var");
            }
        });

        colGecmisKitap.setCellValueFactory(new PropertyValueFactory<>("kitapAdi"));
        colGecmisVerilis.setCellValueFactory(new PropertyValueFactory<>("oduncTarihi"));
        colGecmisIade.setCellValueFactory(new PropertyValueFactory<>("iadeTarihi"));
        colGecmisGecikme.setCellValueFactory(new PropertyValueFactory<>("gecikmeGunu"));
        colGecmisCeza.setCellValueFactory(new PropertyValueFactory<>("cezaTutar"));
    }

    public void setUyeBilgisi(AbstractKullanici uye) {
        this.seciliUye = uye;
        List<Odunc> tumGecmis = Collections.emptyList();

        try {

            tumGecmis = oduncDAO.getKullaniciOduncGecmisi(uye.getKullaniciId());
        } catch (SQLException e) {
            e.printStackTrace();
            mesajGoster(Alert.AlertType.ERROR, "Veritabanı Hatası",
                    "Ödünç geçmişi yüklenirken bir veritabanı hatası oluştu. Lütfen bağlantıyı kontrol edin.");
            tumGecmis = Collections.emptyList();
        }

        lblBaslik.setText("Üye Detayları: " + uye.getAd() + " " + uye.getSoyad());
        lblBilgi.setText("TC: " + uye.getTcKimlik() + " | Telefon: " + uye.getTelefon() + " | Email: " + uye.getEmail());

        ObservableList<Odunc> aktifListe = FXCollections.observableArrayList();
        ObservableList<Odunc> gecmisListe = FXCollections.observableArrayList();

        for (Odunc o : tumGecmis) {
            if (o.getIadeTarihi() == null) {
                aktifListe.add(o);
            } else {
                gecmisListe.add(o);
            }
        }

        tabloAktif.setItems(aktifListe);
        tabloGecmis.setItems(gecmisListe);

        renklendirmeAyarla();
    }

    private void renklendirmeAyarla() {

    }

    @FXML
    void handleKapat(ActionEvent event) {
        Stage stage = (Stage) lblBaslik.getScene().getWindow();
        stage.close();
    }

    private void mesajGoster(Alert.AlertType tip, String baslik, String mesaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }
}