package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.dao.OduncDAO;
import com.example.kutuphaneyonetimsistemi.dao.KullaniciDAO;
import com.example.kutuphaneyonetimsistemi.dao.KitapDAO;
import com.example.kutuphaneyonetimsistemi.dao.RezervasyonDAO;
import com.example.kutuphaneyonetimsistemi.designpatterns.observer.IBildirimObserver;
import com.example.kutuphaneyonetimsistemi.designpatterns.observer.UyeBildirimObserver;
import com.example.kutuphaneyonetimsistemi.designpatterns.strategy.ICezaHesaplama;
import com.example.kutuphaneyonetimsistemi.designpatterns.strategy.StandartCeza;
import com.example.kutuphaneyonetimsistemi.model.Odunc;
import com.example.kutuphaneyonetimsistemi.model.Rezervasyon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class OduncIslemleriController {

    @FXML private TableView<Rezervasyon> tabloRezervasyon;
    @FXML private TableColumn<Rezervasyon, String> colRezKitapAd;
    @FXML private TableColumn<Rezervasyon, String> colRezUyeAd;
    @FXML private TableColumn<Rezervasyon, Integer> colRezSiraNo;
    @FXML private TableColumn<Rezervasyon, Void> colRezAksiyon;
    @FXML private TableView<Odunc> tabloOdunc;
    @FXML private TableColumn<Odunc, Integer> colOduncId;
    @FXML private TableColumn<Odunc, String> colKitapAdi;
    @FXML private TableColumn<Odunc, String> colUyeAdi;
    @FXML private TableColumn<Odunc, LocalDate> colOduncTarihi;
    @FXML private TableColumn<Odunc, LocalDate> colSonTarih;
    @FXML private Label lblGecikme;
    @FXML private Label lblCeza;

    private final OduncDAO oduncDAO = new OduncDAO();
    private final KullaniciDAO kullaniciDAO = new KullaniciDAO();
    private final RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    private final KitapDAO kitapDAO = new KitapDAO();

    private final ICezaHesaplama cezaHesaplamaStratejisi = new StandartCeza();

    private final IBildirimObserver bildirimObserver = new UyeBildirimObserver();

    private double gunlukCezaMiktari = 1.0;
    private int maxOduncSuresi = 15;

    @FXML
    public void initialize() {
        double[] ayarlar = oduncDAO.sistemAyarlariniGetir();
        maxOduncSuresi = (int) ayarlar[0];
        gunlukCezaMiktari = ayarlar[1];

        colOduncId.setCellValueFactory(new PropertyValueFactory<>("oduncId"));
        colKitapAdi.setCellValueFactory(new PropertyValueFactory<>("kitapAdi"));
        colUyeAdi.setCellValueFactory(new PropertyValueFactory<>("uyeAdiSoyad"));
        colOduncTarihi.setCellValueFactory(new PropertyValueFactory<>("oduncTarihi"));
        colSonTarih.setCellValueFactory(new PropertyValueFactory<>("sonIadeTarihi"));
        handleTabloYenile(null);

        colRezKitapAd.setCellValueFactory(new PropertyValueFactory<>("kitapAd"));
        colRezSiraNo.setCellValueFactory(new PropertyValueFactory<>("siraNumarasi"));
        colRezUyeAd.setCellValueFactory(new PropertyValueFactory<>("uyeAdiSoyad"));

        setupRezervasyonAksiyonKolonu();
        loadRezervasyonKuyrugu();
    }

    private void loadRezervasyonKuyrugu() {
        try {
            List<Rezervasyon> kuyruk = rezervasyonDAO.getAktifRezervasyonKuyruguDetayli();
            tabloRezervasyon.setItems(FXCollections.observableArrayList(kuyruk));
        } catch (SQLException e) {
            System.err.println("Rezervasyon kuyruğu yüklenemedi: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Rezervasyon kuyruğu yüklenirken hata oluştu: " + e.getMessage());
            alert.show();
        }
    }

    private void setupRezervasyonAksiyonKolonu() {
        Callback<TableColumn<Rezervasyon, Void>, TableCell<Rezervasyon, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Rezervasyon, Void> call(final TableColumn<Rezervasyon, Void> param) {
                final TableCell<Rezervasyon, Void> cell = new TableCell<>() {

                    private final HBox hBox = new HBox(5);
                    private final Button btnVer = new Button("Ödünç Ver");
                    private final Button btnSil = new Button("Sil");

                    {
                        btnVer.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                        btnSil.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        hBox.getChildren().addAll(btnVer, btnSil);

                        btnVer.setOnAction((ActionEvent event) -> {
                            Rezervasyon r = getTableView().getItems().get(getIndex());
                            handleRezervasyonOduncVer(r);
                        });

                        btnSil.setOnAction((ActionEvent event) -> {
                            Rezervasyon r = getTableView().getItems().get(getIndex());
                            handleRezervasyonSil(r);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : hBox);
                    }
                };
                return cell;
            }
        };

        colRezAksiyon.setCellFactory(cellFactory);
    }

    private void handleRezervasyonOduncVer(Rezervasyon rezervasyon) {
        try {

            boolean oduncBasarili = oduncDAO.oduncKayitEkle(
                    rezervasyon.getKullaniciId(),
                    rezervasyon.getKitapId(),
                    LocalDate.now(),
                    LocalDate.now().plusDays(maxOduncSuresi)
            );

            if (oduncBasarili) {

                rezervasyonDAO.rezervasyonBitir(rezervasyon.getRezervasyonId());

                String sonIadeTarihi = LocalDate.now().plusDays(maxOduncSuresi).toString();
                String mesaj = "✅ Rezervasyonunuz onaylandı! '" + rezervasyon.getKitapAd() +
                        "' adlı kitabı ödünç aldınız. Son iade tarihi: " + sonIadeTarihi + ". İyi okumalar dileriz.";
                System.out.println("DEBUG MESAJ: Gonderilen içerik: " + mesaj);

                bildirimObserver.guncelle(rezervasyon.getKullaniciId(), mesaj);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Kitap başarıyla ödünç verildi ve rezervasyon tamamlandı!");
                alert.show();

                loadRezervasyonKuyrugu();
                handleTabloYenile(null);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: Ödünç verilemedi. Stok yetersiz olabilir.");
                alert.show();
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veritabanı hatası veya stok hatası: " + e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }

    private void handleRezervasyonSil(Rezervasyon rezervasyon) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rezervasyon İptali");
        alert.setHeaderText(rezervasyon.getKitapAd() + " rezervasyonu iptal edilsin mi?");
        alert.setContentText("Bu işlem geri alınamaz.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (rezervasyonDAO.rezervasyonIptalEt(rezervasyon.getRezervasyonId())) {

                    String mesaj = "❌ '" + rezervasyon.getKitapAd() + "' adlı kitabın rezervasyonu personel tarafından iptal edilmiştir.";
                    bildirimObserver.guncelle(rezervasyon.getKullaniciId(), mesaj);

                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Rezervasyon başarıyla iptal edildi.");
                    success.showAndWait();
                    loadRezervasyonKuyrugu();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR, "İptal işlemi başarısız oldu.");
                    error.showAndWait();
                }
            } catch (SQLException e) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Veritabanı hatası: " + e.getMessage());
                error.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleTabloYenile(ActionEvent event) {
        try {
            ObservableList<Odunc> liste = FXCollections.observableArrayList(oduncDAO.aktifOduncleriGetir());
            tabloOdunc.setItems(liste);
        } catch (Exception e) {
            System.err.println("Aktif ödünçler yüklenemedi: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aktif ödünçler yüklenirken hata oluştu.");
            alert.show();
        }
    }

    @FXML
    void handleCezaHesapla(ActionEvent event) {
        Odunc secili = tabloOdunc.getSelectionModel().getSelectedItem();
        if (secili == null) return;

        LocalDate bugun = LocalDate.now();

        long gecikmeGunLong = ChronoUnit.DAYS.between(secili.getSonIadeTarihi(), bugun);
        int gecikmeGun = (int) Math.max(0, gecikmeGunLong);

        double ceza = cezaHesaplamaStratejisi.cezaHesapla(gecikmeGun, gunlukCezaMiktari);

        lblGecikme.setText(gecikmeGun + " gün");
        lblCeza.setText(String.format("%.2f TL", ceza));
    }

    @FXML
    void handleIadeTamamla(ActionEvent event) {
        Odunc secili = tabloOdunc.getSelectionModel().getSelectedItem();
        if (secili == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Lütfen listeden bir kayıt seçiniz.");
            alert.show();
            return;
        }

        handleCezaHesapla(null);

        String cezaStr = lblCeza.getText().replace(" TL", "").replace(",", ".");
        double ceza = Double.parseDouble(cezaStr);

        try {
            boolean sonuc = oduncDAO.oduncKayitGuncelle(secili.getOduncId(), LocalDate.now());

            if (sonuc) {
                int kullaniciId = secili.getKullaniciId();
                String mesaj = "✅ '" + secili.getKitapAdi() + "' adlı kitabın iade işlemi başarıyla onaylandı. Borç/Ceza: " + String.format("%.2f TL", ceza) + ". Teşekkür ederiz.";

                bildirimObserver.guncelle(kullaniciId, mesaj);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kitap başarıyla iade alındı. Ceza: " + String.format("%.2f TL", ceza) + " TL");
                alert.show();

                handleTabloYenile(null);
                lblGecikme.setText("0 gün");
                lblCeza.setText("0.00 TL");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "İade işlemi sırasında hata oluştu.");
                alert.show();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kritik Hata: İade işlemi tamamlanamadı. " + e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }
}