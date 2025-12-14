package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.dao.KitapDAO;
import com.example.kutuphaneyonetimsistemi.dao.KullaniciDAO;
import com.example.kutuphaneyonetimsistemi.dao.OduncDAO;
import com.example.kutuphaneyonetimsistemi.designpatterns.observer.IBildirimObserver;
import com.example.kutuphaneyonetimsistemi.designpatterns.observer.UyeBildirimObserver;
import com.example.kutuphaneyonetimsistemi.model.Kitap;
import com.example.kutuphaneyonetimsistemi.model.Uye;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OduncVerManuelController implements Initializable {

    @FXML private TextField txtUyeKimlik;
    @FXML private TextField txtKitapISBN;
    @FXML private Button btnOduncVer;
    @FXML private Label lblDurumMesaji;

    private final KullaniciDAO kullaniciDAO = new KullaniciDAO();
    private final KitapDAO kitapDAO = new KitapDAO();
    private final OduncDAO oduncDAO = new OduncDAO();
    private final IBildirimObserver bildirimObserver = new UyeBildirimObserver();
    private int maxOduncSuresi = 15;
    private int maxOduncSayisi = 5;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSistemAyarlari();
        lblDurumMesaji.setText("");
    }

    private void loadSistemAyarlari() {
        double[] ayarlar = oduncDAO.sistemAyarlariniGetir();
        maxOduncSuresi = (int) ayarlar[0];
        maxOduncSayisi = (int) ayarlar[2];
    }

    @FXML
    void handleOduncVer(ActionEvent event) {
        lblDurumMesaji.setText("");
        String uyeKimlik = txtUyeKimlik.getText().trim();
        String kitapISBN = txtKitapISBN.getText().trim();

        if (uyeKimlik.isEmpty() || kitapISBN.isEmpty()) {
            lblDurumMesaji.setText("Lütfen Üye Kimliği ve Kitap ISBN'ini girin.");
            return;
        }

        try {
            Uye uye = kullaniciDAO.getUyeByKimlik(uyeKimlik);
            if (uye == null) {
                lblDurumMesaji.setText("Hata: Üye bulunamadı. Kimliği kontrol edin.");
                return;
            }
            int kullaniciId = uye.getKullaniciId();

            Kitap kitap = kitapDAO.getKitapByISBN(kitapISBN);
            if (kitap == null) {
                lblDurumMesaji.setText("Hata: Kitap bulunamadı. ISBN'i kontrol edin.");
                return;
            }
            int kitapId = kitap.getKitapId();

            int mevcutOduncSayisi = oduncDAO.getAktifOduncSayisi(kullaniciId);
            if (mevcutOduncSayisi >= maxOduncSayisi) {
                lblDurumMesaji.setText("Hata: Üye, maksimum ödünç limitine (" + maxOduncSayisi + ") ulaşmıştır.");
                return;
            }

            if (kitap.getMevcutAdet() <= 0) {
                lblDurumMesaji.setText("Hata: Kitabın kütüphane stoğu (mevcut adedi) yetersiz.");
                return;
            }

            if (oduncDAO.isKitapZatenOduncAlinmis(kullaniciId, kitapId)) {
                lblDurumMesaji.setText("Hata: Üyenin bu kitaptan aktif bir ödüncü bulunmaktadır.");
                return;
            }

            LocalDate oduncTarihi = LocalDate.now();
            LocalDate sonIadeTarihi = oduncTarihi.plusDays(maxOduncSuresi);

            boolean oduncBasarili = oduncDAO.oduncKayitEkle(kullaniciId, kitapId, oduncTarihi, sonIadeTarihi);

            if (oduncBasarili) {
                String mesaj = "✅ Personel tarafından işlem yapıldı. '" + kitap.getAd() +
                        "' adlı kitabı ödünç aldınız. Son iade tarihi: " + sonIadeTarihi + ". İyi okumalar dileriz.";

                bildirimObserver.guncelle(kullaniciId, mesaj);

                lblDurumMesaji.setText("✅ Ödünç verme işlemi başarıyla tamamlandı. (" + kitap.getAd() + ")");
                lblDurumMesaji.setStyle("-fx-text-fill: #27ae60;");

                txtUyeKimlik.clear();
                txtKitapISBN.clear();
            } else {
                lblDurumMesaji.setText("Kritik Hata: Ödünç kaydı oluşturulamadı (DB hatası).");
            }

        } catch (Exception e) {
            String hataMesaji;
            if (e instanceof SQLException) {
                hataMesaji = "Veritabanı erişim/işlem hatası: " + e.getMessage();
            } else {
                hataMesaji = "Beklenmedik bir hata oluştu: " + e.getMessage();
            }

            lblDurumMesaji.setText(hataMesaji);
            lblDurumMesaji.setStyle("-fx-text-fill: #c0392b;");
            System.err.println("Manuel ödünç verme sırasında hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
}