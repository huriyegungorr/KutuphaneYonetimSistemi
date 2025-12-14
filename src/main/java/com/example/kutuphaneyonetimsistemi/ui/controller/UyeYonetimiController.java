package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.dao.KullaniciDAO;
import com.example.kutuphaneyonetimsistemi.designpatterns.KullaniciFactory;
import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.service.KullaniciService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class UyeYonetimiController implements Initializable {

    @FXML private TextField txtAd;
    @FXML private TextField txtSoyad;
    @FXML private TextField txtTC;
    @FXML private TextField txtKullaniciAdi;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefon;
    @FXML private TextField txtAdres;
    @FXML private TextField txtSifre;
    @FXML private TextField txtArama;
    @FXML private Button btnEkle;
    @FXML private Button btnGuncelle;
    @FXML private Button btnSil;
    @FXML private Button btnTemizle;
    @FXML private TableView<AbstractKullanici> tabloUyeler;
    @FXML private TableColumn<AbstractKullanici, Integer> colId;
    @FXML private TableColumn<AbstractKullanici, String> colAd;
    @FXML private TableColumn<AbstractKullanici, String> colSoyad;
    @FXML private TableColumn<AbstractKullanici, String> colTC;
    @FXML private TableColumn<AbstractKullanici, String> colKullaniciAdi;
    @FXML private TableColumn<AbstractKullanici, String> colEmail;
    @FXML private TableColumn<AbstractKullanici, String> colTelefon;
    @FXML private TableColumn<AbstractKullanici, String> colRol;
    @FXML private ComboBox<String> cmbRolSecim;

    private final KullaniciDAO kullaniciDAO = new KullaniciDAO();
    private final KullaniciService kullaniciService = new KullaniciService();
    private ObservableList<AbstractKullanici> uyeListesi;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(new PropertyValueFactory<>("kullaniciId"));
        colAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colSoyad.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        colTC.setCellValueFactory(new PropertyValueFactory<>("tcKimlik"));
        colKullaniciAdi.setCellValueFactory(new PropertyValueFactory<>("kullaniciAdi"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefon.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rolAdi"));

        ObservableList<String> roller = FXCollections.observableArrayList("Üye", "Personel");
        cmbRolSecim.setItems(roller);
        cmbRolSecim.getSelectionModel().selectFirst();

        tabloyuGuncelle();

        tabloUyeler.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                formuDoldur(yeniSecim);
            }
        });
    }

    @FXML
    void handleEkle(ActionEvent event) {
        String seciliTip = cmbRolSecim.getValue();

        if (alanlarBosMu() || seciliTip == null || seciliTip.isEmpty()) {
            mesajGoster(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen rol seçimi ve zorunlu alanları doldurunuz.");
            return;
        }

        String hamSifre = txtSifre.getText().isEmpty() ? "123456" : txtSifre.getText();
        String hashSifre = kullaniciService.hashSifre(hamSifre);

        String factoryTip = seciliTip.toLowerCase(Locale.ROOT).replace('ü', 'u');

        AbstractKullanici yeniKullanici = KullaniciFactory.createKullanici(
                factoryTip,
                txtAd.getText(),
                txtSoyad.getText(),
                txtEmail.getText(),
                hashSifre,
                txtTelefon.getText(),
                txtTC.getText(),
                txtKullaniciAdi.getText()
        );

        yeniKullanici.setAdres(txtAdres.getText());

        if (kullaniciDAO.kullaniciEkle(yeniKullanici)) {
            mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", yeniKullanici.getAdSoyad() + " başarıyla eklendi (Rol: " + seciliTip + ").");
            handleTemizle(null);
            tabloyuGuncelle();
        } else {
            mesajGoster(Alert.AlertType.ERROR, "Hata", "Kullanıcı eklenemedi. TC, Email veya Kullanıcı Adı dolu olabilir.");
        }
    }

    @FXML
    void handleGuncelle(ActionEvent event) {
        AbstractKullanici seciliKullanici = tabloUyeler.getSelectionModel().getSelectedItem();
        if (seciliKullanici == null) {
            mesajGoster(Alert.AlertType.WARNING, "Uyarı", "Lütfen güncellenecek kullanıcıyı seçiniz.");
            return;
        }

        String seciliTip = cmbRolSecim.getValue();
        if (alanlarBosMu() || seciliTip == null || seciliTip.isEmpty()) {
            mesajGoster(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen rol seçimi ve zorunlu alanları doldurunuz.");
            return;
        }

        String yeniFactoryTip = seciliTip.toLowerCase(Locale.ROOT).replace('ü', 'u');
        int yeniRolId = ("personel".equals(yeniFactoryTip)) ? 2 : 1;

        seciliKullanici.setAd(txtAd.getText());
        seciliKullanici.setSoyad(txtSoyad.getText());
        seciliKullanici.setTcKimlik(txtTC.getText());
        seciliKullanici.setKullaniciAdi(txtKullaniciAdi.getText());
        seciliKullanici.setEmail(txtEmail.getText());
        seciliKullanici.setTelefon(txtTelefon.getText());
        seciliKullanici.setAdres(txtAdres.getText());
        seciliKullanici.setRolId(yeniRolId);

        if (kullaniciDAO.kullaniciGuncelle(seciliKullanici)) {
            mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", "Kullanıcı bilgileri güncellendi.");
            handleTemizle(null);
            tabloyuGuncelle();
        } else {
            mesajGoster(Alert.AlertType.ERROR, "Hata", "Güncelleme başarısız (Çakışan veri olabilir).");
        }
    }

    @FXML
    void handleSil(ActionEvent event) {
        AbstractKullanici seciliKullanici = tabloUyeler.getSelectionModel().getSelectedItem();
        if (seciliKullanici == null) {
            mesajGoster(Alert.AlertType.WARNING, "Uyarı", "Lütfen silinecek kullanıcıyı seçiniz.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText(null);
        alert.setContentText(seciliKullanici.getAd() + " " + seciliKullanici.getSoyad() + " adlı kullanıcıyı silmek istiyor musunuz?");
        Optional<ButtonType> sonuc = alert.showAndWait();

        if (sonuc.isPresent() && sonuc.get() == ButtonType.OK) {
            if (kullaniciDAO.kullaniciSil(seciliKullanici.getKullaniciId())) {
                mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", "Kullanıcı silindi.");
                handleTemizle(null);
                tabloyuGuncelle();
            } else {
                mesajGoster(Alert.AlertType.ERROR, "Hata", "Kullanıcı silinemedi (İlişkili kayıtlar olabilir).");
            }
        }
    }

    @FXML
    void handleTemizle(ActionEvent event) {
        txtAd.clear();
        txtSoyad.clear();
        txtTC.clear();
        txtKullaniciAdi.clear();
        txtEmail.clear();
        txtTelefon.clear();
        txtAdres.clear();
        txtSifre.clear();
        cmbRolSecim.getSelectionModel().selectFirst();
        tabloUyeler.getSelectionModel().clearSelection();
    }

    @FXML
    void handleArama(KeyEvent event) {
        String aranan = txtArama.getText();
        if (aranan == null || aranan.trim().isEmpty()) {
            tabloyuGuncelle();
        } else {
            List<AbstractKullanici> aramaSonucu = kullaniciDAO.uyeAra(aranan);
            uyeListesi = FXCollections.observableArrayList(aramaSonucu);
            tabloUyeler.setItems(uyeListesi);
        }
    }

    private void tabloyuGuncelle() {
        List<AbstractKullanici> tumUyeler = kullaniciDAO.tumUyeleriGetir();
        uyeListesi = FXCollections.observableArrayList(tumUyeler);
        tabloUyeler.setItems(uyeListesi);
    }

    private void formuDoldur(AbstractKullanici k) {
        txtAd.setText(k.getAd());
        txtSoyad.setText(k.getSoyad());
        txtTC.setText(k.getTcKimlik());
        txtKullaniciAdi.setText(k.getKullaniciAdi());
        txtEmail.setText(k.getEmail());
        txtTelefon.setText(k.getTelefon());
        txtAdres.setText(k.getAdres());
        txtSifre.clear();

        String rolAdi = k.getRolAdi();
        if ("Üye".equalsIgnoreCase(rolAdi)) {
            cmbRolSecim.getSelectionModel().select("Üye");
        } else if ("Personel".equalsIgnoreCase(rolAdi)) {
            cmbRolSecim.getSelectionModel().select("Personel");
        } else {
            cmbRolSecim.getSelectionModel().clearSelection();
        }
    }

    private boolean alanlarBosMu() {
        if (txtAd.getText().isEmpty() || txtSoyad.getText().isEmpty() ||
                txtTC.getText().isEmpty() || txtKullaniciAdi.getText().isEmpty()) {
            mesajGoster(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen zorunlu alanları (Ad, Soyad, TC, Kullanıcı Adı) doldurunuz.");
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

    @FXML
    public void handleDetay() {
        AbstractKullanici secili = tabloUyeler.getSelectionModel().getSelectedItem();
        if (secili == null) {
            mesajGoster(Alert.AlertType.WARNING, "Uyarı", "Lütfen detaylarını görmek istediğiniz üyeyi seçin.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kutuphaneyonetimsistemi/uye-detay.fxml"));
            javafx.scene.Parent root = loader.load();

            UyeDetayController controller = loader.getController();
            controller.setUyeBilgisi(secili);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Kullanıcı Detay - " + secili.getAd());
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mesajGoster(Alert.AlertType.ERROR, "Hata", "Detay ekranı yüklenemedi.");
        }
    }
}