package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.dao.UyeBildirimDAO;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.UyeBildirim;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class UyeBildirimController implements Initializable {

    @FXML private VBox mainVBox;
    @FXML private TableView<UyeBildirim> bildirimTablosu;
    @FXML private TableColumn<UyeBildirim, LocalDate> colTarih;
    @FXML private TableColumn<UyeBildirim, String> colMesaj;
    @FXML private TableColumn<UyeBildirim, Boolean> colOkundu;
    @FXML private Label lblBaslik;

    private final UyeBildirimDAO bildirimDAO = new UyeBildirimDAO();
    private AbstractKullanici mevcutKullanici;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colTarih.setCellValueFactory(new PropertyValueFactory<>("tarih"));

        colMesaj.setCellValueFactory(new PropertyValueFactory<>("mesaj"));

        colMesaj.setCellFactory(column -> new TableCell<UyeBildirim, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setWrapText(true);
                }
            }
        });

        colOkundu.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isOkunduMu()));
        colOkundu.setCellFactory(column -> new TableCell<UyeBildirim, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("✅ Okundu");
                        setStyle("-fx-text-fill: #27ae60;");
                    } else {
                        setText("🔔 Yeni");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        bildirimTablosu.setPlaceholder(new Label("Henüz bir bildiriminiz bulunmamaktadır."));

        bildirimTablosu.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null && !yeniSecim.isOkunduMu()) {
                markAsRead(yeniSecim);
            }
        });
    }

    public void initData(AbstractKullanici kullanici) {
        this.mevcutKullanici = kullanici;
        if (mevcutKullanici != null) {
            lblBaslik.setText("🔔 Bildirimleriniz");
            loadBildirimler();
        }
    }

    private void loadBildirimler() {
        if (mevcutKullanici == null) return;

        try {
            List<UyeBildirim> bildirimList = bildirimDAO.getBildirimler(mevcutKullanici.getKullaniciId());
            bildirimTablosu.setItems(FXCollections.observableArrayList(bildirimList));

        } catch (SQLException e) {
            System.err.println("Bildirimler yüklenirken hata oluştu: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bildirimler yüklenemedi: Veritabanı hatası.");
            alert.show();
            bildirimTablosu.setItems(FXCollections.emptyObservableList());
        }
    }

    private void markAsRead(UyeBildirim bildirim) {
        if (bildirim.isOkunduMu()) return;

        try {
            if (bildirimDAO.bildirimOkunduIsaretle(bildirim.getBildirimId())) {
                bildirim.setOkunduMu(true);
                bildirimTablosu.refresh();
            }
        } catch (SQLException e) {
            System.err.println("Bildirim okundu olarak işaretlenirken hata oluştu: " + e.getMessage());
        }
    }
}