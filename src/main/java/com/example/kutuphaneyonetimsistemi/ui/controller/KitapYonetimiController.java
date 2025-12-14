package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.dao.KitapDAO;
import com.example.kutuphaneyonetimsistemi.model.Kitap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class KitapYonetimiController implements Initializable {

    @FXML private TextField txtAd;
    @FXML private TextField txtYazar;
    @FXML private TextField txtYayinevi;
    @FXML private TextField txtYil;
    @FXML private TextField txtISBN;
    @FXML private TextField txtKategori;
    @FXML private TextField txtAdet;
    @FXML private TextField txtRaf;
    @FXML private TextField txtAciklama;
    @FXML private TableView<Kitap> tabloKitaplar;
    @FXML private TableColumn<Kitap, Integer> colId;
    @FXML private TableColumn<Kitap, String> colAd;
    @FXML private TableColumn<Kitap, String> colYazar;
    @FXML private TableColumn<Kitap, String> colYayinevi;
    @FXML private TableColumn<Kitap, String> colISBN;
    @FXML private TableColumn<Kitap, String> colDurum;
    @FXML private TableColumn<Kitap, Integer> colAdet;
    @FXML private TableColumn<Kitap, String> colRaf;
    @FXML private ComboBox<String> cmbAramaKriteri;
    @FXML private TextField txtAramaMetni;

    private KitapDAO kitapDAO = new KitapDAO();

    private ObservableList<Kitap> masterData;
    private FilteredList<Kitap> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colYazar.setCellValueFactory(new PropertyValueFactory<>("yazar"));
        colYayinevi.setCellValueFactory(new PropertyValueFactory<>("yayinEvi"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colDurum.setCellValueFactory(new PropertyValueFactory<>("durumString"));

        if (colAdet != null) colAdet.setCellValueFactory(new PropertyValueFactory<>("adet"));
        if (colRaf != null) colRaf.setCellValueFactory(new PropertyValueFactory<>("rafNo"));

        ObservableList<String> kriterler = FXCollections.observableArrayList(
                "Kitap Adı", "Yazar Adı", "ISBN", "Kategori/Tür"
        );
        cmbAramaKriteri.setItems(kriterler);
        cmbAramaKriteri.getSelectionModel().selectFirst();

        loadAndSetupFiltering();

        tabloKitaplar.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                formuDoldur(yeniSecim);
            }
        });
    }

    private void loadAndSetupFiltering() {

        try {
            masterData = FXCollections.observableArrayList(kitapDAO.getAllBooks());
        } catch (Exception e) {
            System.err.println("Kitap listesi yüklenirken hata: " + e.getMessage());
            masterData = FXCollections.observableArrayList();
        }

        filteredData = new FilteredList<>(masterData, p -> true);

        txtAramaMetni.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(createSearchPredicate(newValue));
        });

        cmbAramaKriteri.valueProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(createSearchPredicate(txtAramaMetni.getText()));
        });

        SortedList<Kitap> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabloKitaplar.comparatorProperty());

        tabloKitaplar.setItems(sortedData);
    }

    private Predicate<Kitap> createSearchPredicate(String searchText) {

        if (searchText == null || searchText.isEmpty()) {
            return kitap -> true;
        }

        final String lowerCaseFilter = searchText.toLowerCase(Locale.forLanguageTag("tr")).trim();
        final String selectedKriter = cmbAramaKriteri.getValue();

        return kitap -> {
            if (kitap == null) return false;

            String kriter = (selectedKriter == null || selectedKriter.isEmpty()) ? "Kitap Adı" : selectedKriter;

            String aranacakDeger = "";

            switch (kriter) {
                case "Kitap Adı":
                    aranacakDeger = kitap.getAd();
                    break;
                case "Yazar Adı":
                    aranacakDeger = kitap.getYazar();
                    break;
                case "ISBN":
                    aranacakDeger = kitap.getIsbn();
                    break;
                case "Kategori/Tür":
                    aranacakDeger = kitap.getKategori();
                    break;
                default:

                    aranacakDeger = kitap.getAd();
                    break;
            }

            if (aranacakDeger != null) {
                return aranacakDeger.toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter);
            }
            return false;
        };
    }

    @FXML
    void handleEkle(ActionEvent event) {
        if (alanlarBosMu()) return;

        try {
            String ad = txtAd.getText();
            String yazar = txtYazar.getText();
            String yayinevi = txtYayinevi.getText();
            int yil = txtYil.getText().isEmpty() ? 0 : Integer.parseInt(txtYil.getText());
            int adet = txtAdet.getText().isEmpty() ? 1 : Integer.parseInt(txtAdet.getText());
            String raf = txtRaf.getText();
            String aciklama = txtAciklama.getText();
            String isbn = txtISBN.getText();
            String kategori = txtKategori.getText();

            Kitap yeniKitap = new Kitap(ad, yazar, yayinevi, yil, isbn, kategori, adet, raf, aciklama);

            if (kitapDAO.kitapEkle(yeniKitap)) {
                mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", "Kitap ve ilişkili veriler başarıyla eklendi.");
                handleTemizle(null);

                masterData.add(yeniKitap);
            } else {
                mesajGoster(Alert.AlertType.ERROR, "Hata", "Kitap eklenirken veritabanı hatası oluştu.");
            }
        } catch (NumberFormatException e) {
            mesajGoster(Alert.AlertType.ERROR, "Format Hatası", "Lütfen Yıl ve Adet alanlarına sadece sayı giriniz.");
        }
    }

    @FXML
    void handleGuncelle(ActionEvent event) {
        Kitap seciliKitap = tabloKitaplar.getSelectionModel().getSelectedItem();
        if (seciliKitap == null) {
            mesajGoster(Alert.AlertType.WARNING, "Uyarı", "Lütfen güncellenecek kitabı seçiniz.");
            return;
        }
        if (alanlarBosMu()) return;

        try {
            int kitapId = seciliKitap.getId();

            String ad = txtAd.getText();
            String yazar = txtYazar.getText();
            String yayinevi = txtYayinevi.getText();
            int yil = txtYil.getText().isEmpty() ? 0 : Integer.parseInt(txtYil.getText());
            int adet = txtAdet.getText().isEmpty() ? 1 : Integer.parseInt(txtAdet.getText());
            String raf = txtRaf.getText();
            String aciklama = txtAciklama.getText();
            String isbn = txtISBN.getText();
            String kategori = txtKategori.getText();

            Kitap guncelKitap = new Kitap(ad, yazar, yayinevi, yil, isbn, kategori, adet, raf, aciklama);
            guncelKitap.setId(kitapId);

            if (kitapDAO.kitapGuncelle(guncelKitap)) {
                mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", guncelKitap.getAd() + " başarıyla güncellendi.");
                handleTemizle(null);

                loadAndSetupFiltering();
            } else {
                mesajGoster(Alert.AlertType.ERROR, "Hata", "Kitap güncellenirken veritabanı hatası oluştu.");
            }
        } catch (NumberFormatException e) {
            mesajGoster(Alert.AlertType.ERROR, "Format Hatası", "Lütfen Yıl ve Adet alanlarına sadece sayı giriniz.");
        }
    }

    @FXML
    void handleSil(ActionEvent event) {
        Kitap seciliKitap = tabloKitaplar.getSelectionModel().getSelectedItem();
        if (seciliKitap == null) {
            mesajGoster(Alert.AlertType.WARNING, "Uyarı", "Lütfen silinecek kitabı seçiniz.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setContentText(seciliKitap.getAd() + " kitabını silmek istediğinize emin misiniz?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean silindiMi = kitapDAO.kitapSil(seciliKitap.getId());

            if (silindiMi) {

                masterData.remove(seciliKitap);
                mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", "Kitap silindi.");
            } else {
                mesajGoster(Alert.AlertType.ERROR, "Hata", "Kitap silinemedi.");
            }
        }
    }

    private void tabloyuGuncelle() {

    }

    @FXML
    void handleListeYenile(ActionEvent event) {
        loadAndSetupFiltering();
        txtAramaMetni.clear();
        cmbAramaKriteri.getSelectionModel().selectFirst();
    }

    @FXML
    void handleTemizle(ActionEvent event) {
        txtAd.clear();
        txtYazar.clear();
        txtYayinevi.clear();
        txtYil.clear();
        txtISBN.clear();
        txtKategori.clear();

        if(txtAdet != null) txtAdet.clear();
        if(txtRaf != null) txtRaf.clear();
        if(txtAciklama != null) txtAciklama.clear();

        tabloKitaplar.getSelectionModel().clearSelection();
    }

    private void formuDoldur(Kitap k) {
        txtAd.setText(k.getAd());
        txtYazar.setText(k.getYazar());
        txtYayinevi.setText(k.getYayinEvi());
        txtYil.setText(String.valueOf(k.getBaskiYili() == 0 ? "" : k.getBaskiYili()));
        txtISBN.setText(k.getIsbn());
        txtKategori.setText(k.getKategori());

        if(txtAdet != null) txtAdet.setText(String.valueOf(k.getAdet()));
        if(txtRaf != null) txtRaf.setText(k.getRafNo());
        if(txtAciklama != null) txtAciklama.setText(k.getAciklama());
    }

    private boolean alanlarBosMu() {
        if (txtAd.getText().isEmpty() || txtISBN.getText().isEmpty()) {
            mesajGoster(Alert.AlertType.WARNING, "Uyarı", "Kitap Adı ve ISBN zorunludur!");
            return true;
        }
        return false;
    }

    private void mesajGoster(Alert.AlertType tip, String baslik, String mesaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }
}