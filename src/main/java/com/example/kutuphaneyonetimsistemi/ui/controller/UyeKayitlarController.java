package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.Rezervasyon;
import com.example.kutuphaneyonetimsistemi.model.Odunc;
import com.example.kutuphaneyonetimsistemi.model.OduncDetay;
import com.example.kutuphaneyonetimsistemi.service.RezervasyonService;
import com.example.kutuphaneyonetimsistemi.service.OduncService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UyeKayitlarController implements Initializable {

    private final RezervasyonService rezervasyonService = new RezervasyonService();
    private final OduncService oduncService = new OduncService();

    private AbstractKullanici mevcutKullanici;

    @FXML private Label baslikLabel;
    @FXML private TableView<Odunc> oduncTablosu;
    @FXML private TableColumn<Odunc, String> kitapAdOduncCol;
    @FXML private TableColumn<Odunc, LocalDate> oduncTarihiCol;
    @FXML private TableColumn<Odunc, LocalDate> sonIadeTarihiCol;
    @FXML private TableColumn<Odunc, String> oduncDurumCol;
    @FXML private TableView<Rezervasyon> rezervasyonTablosu;
    @FXML private TableColumn<Rezervasyon, String> kitapAdRezCol;
    @FXML private TableColumn<Rezervasyon, LocalDate> rezervasyonTarihiCol;
    @FXML private TableColumn<Rezervasyon, Integer> siraNumarasiCol;
    @FXML private TableColumn<Rezervasyon, String> durumRezCol;
    @FXML private TableColumn<Rezervasyon, Void> iptalCol;
    @FXML private TableView<OduncDetay> gecikmisTablosu;
    @FXML private TableColumn<OduncDetay, String> gecikmisKitapAdCol;
    @FXML private TableColumn<OduncDetay, Long> gecikmeGunuCol;
    @FXML private TableColumn<OduncDetay, Double> cezaCol;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        kitapAdRezCol.setCellValueFactory(new PropertyValueFactory<>("kitapAd"));
        rezervasyonTarihiCol.setCellValueFactory(new PropertyValueFactory<>("rezervasyonTarihi"));
        siraNumarasiCol.setCellValueFactory(new PropertyValueFactory<>("siraNumarasi"));

        durumRezCol.setCellValueFactory(new PropertyValueFactory<>("durum"));
        setupRezervasyonDurumColumn();

        kitapAdOduncCol.setCellValueFactory(new PropertyValueFactory<>("kitapAdi"));
        oduncTarihiCol.setCellValueFactory(new PropertyValueFactory<>("oduncTarihi"));
        sonIadeTarihiCol.setCellValueFactory(new PropertyValueFactory<>("sonIadeTarihi"));

        oduncDurumCol.setCellValueFactory(param -> {
            LocalDate sonTarih = param.getValue().getSonIadeTarihi();
            return new SimpleStringProperty("Personel Onayladı: İade Tarihi (" + (sonTarih != null ? sonTarih.toString() : "Tarih Belirsiz") + ")");
        });

        gecikmisKitapAdCol.setCellValueFactory(new PropertyValueFactory<>("kitapAd"));
        gecikmeGunuCol.setCellValueFactory(new PropertyValueFactory<>("gecikmeGunu"));
        cezaCol.setCellValueFactory(new PropertyValueFactory<>("cezaTutari"));

        setupIptalButtonColumn();

        rezervasyonTablosu.setPlaceholder(new Label("Aktif rezervasyon kaydınız bulunmamaktadır."));
        gecikmisTablosu.setPlaceholder(new Label("Gecikmiş kitap kaydınız bulunmamaktadır."));
        oduncTablosu.setPlaceholder(new Label("Aktif ödünç kaydınız bulunmamaktadır."));
    }

    public void initData(AbstractKullanici kullanici) {
        this.mevcutKullanici = kullanici;
        if (mevcutKullanici != null) {
            baslikLabel.setText("📜 " + mevcutKullanici.getAd() + " " + mevcutKullanici.getSoyad() + " Kayıtları");

            loadOduncKayitlari();
            loadRezervasyonKayitlari();
            loadGecikmisKitaplar();
        }
    }

    private void loadOduncKayitlari() {
        if (mevcutKullanici == null) return;

        try {

            List<Odunc> aktifOduncler = oduncService.getAktifOduncler(mevcutKullanici.getKullaniciId());

            oduncTablosu.setItems(FXCollections.observableArrayList(aktifOduncler));

        } catch (Exception e) {
            System.err.println("Aktif ödünçler yüklenirken hata oluştu: " + e.getMessage());
            oduncTablosu.setItems(FXCollections.emptyObservableList());
        }
    }

    private void loadGecikmisKitaplar() {
        if (mevcutKullanici == null) return;

        try {
            List<OduncDetay> gecikmisList = oduncService.getGecikmisKitaplar(mevcutKullanici.getKullaniciId());
            gecikmisTablosu.setItems(FXCollections.observableArrayList(gecikmisList));
        } catch (Exception e) {
            System.err.println("Gecikmiş kitaplar yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void loadRezervasyonKayitlari() {
        if (mevcutKullanici == null) return;

        try {
            List<Rezervasyon> rezervasyonList = rezervasyonService.getKullaniciRezervasyonlari(mevcutKullanici.getKullaniciId());
            rezervasyonTablosu.setItems(FXCollections.observableArrayList(rezervasyonList));
        } catch (Exception e) {
            System.err.println("Rezervasyonlar yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void setupRezervasyonDurumColumn() {
        durumRezCol.setCellFactory(column -> {
            return new TableCell<Rezervasyon, String>() {
                @Override
                protected void updateItem(String durum, boolean empty) {
                    super.updateItem(durum, empty);

                    if (durum == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        if (durum.equals("PERSONEL_IPTAL")) {
                            setText("❌ Personel Tarafından İptal Edildi");
                            setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                        } else if (durum.equals("IPTAL")) {
                            setText("İptal Edildi (Sizin Tarafınızdan)");
                            setStyle("-fx-text-fill: #e74c3c;");
                        } else if (durum.equals("BEKLEMEDE") || durum.equals("AKTIF")) {
                            setText(durum);
                            setStyle("-fx-text-fill: #2980b9;");
                        } else if (durum.equals("TAMAMLANDI") || durum.equals("ODUNC_VERILDI")) {
                            setText("Ödünç Verildi (Aktif Ödünçlerde Görünür)");
                            setStyle("-fx-text-fill: #27ae60;");
                        } else {
                            setText(durum);
                            setStyle("");
                        }
                    }
                }
            };
        });
    }

    private void setupIptalButtonColumn() {
        Callback<TableColumn<Rezervasyon, Void>, TableCell<Rezervasyon, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Rezervasyon, Void> call(final TableColumn<Rezervasyon, Void> param) {
                final TableCell<Rezervasyon, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("İptal Et");

                    {
                        btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        btn.setOnAction((ActionEvent event) -> {
                            Rezervasyon rezervasyon = getTableView().getItems().get(getIndex());
                            handleRezervasyonIptal(rezervasyon);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {

                            Rezervasyon rezervasyon = getTableView().getItems().get(getIndex());

                            if (rezervasyon.getDurum().equals("AKTIF") || rezervasyon.getDurum().equals("BEKLEMEDE")) {
                                setGraphic(btn);
                            } else {

                                setGraphic(null);
                            }
                        }
                    }
                };
                return cell;
            }
        };

        iptalCol.setCellFactory(cellFactory);
    }

    private void handleRezervasyonIptal(Rezervasyon rezervasyon) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("İptal Onayı");
        alert.setHeaderText(rezervasyon.getKitapAd() + " için rezervasyon iptali");
        alert.setContentText("Bu rezervasyonu iptal etmek istediğinizden emin misiniz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                if (rezervasyonService.rezervasyonIptalEt(rezervasyon.getRezervasyonId())) {

                    loadRezervasyonKayitlari();
                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Rezervasyon başarıyla iptal edildi.");
                    success.showAndWait();

                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR, "İptal işlemi başarısız oldu.");
                    error.showAndWait();
                }
            } catch (Exception e) {
                System.err.println("İptal sırasında hata: " + e.getMessage());
                Alert error = new Alert(Alert.AlertType.ERROR, "Beklenmedik bir hata oluştu: " + e.getMessage());
                error.showAndWait();
            }
        }
    }
}