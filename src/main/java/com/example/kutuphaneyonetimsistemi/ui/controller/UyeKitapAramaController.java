package com.example.kutuphaneyonetimsistemi.ui.controller;

import com.example.kutuphaneyonetimsistemi.model.AbstractKullanici;
import com.example.kutuphaneyonetimsistemi.model.Kitap;
import com.example.kutuphaneyonetimsistemi.service.KitapService;
import com.example.kutuphaneyonetimsistemi.service.RezervasyonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class UyeKitapAramaController implements Initializable {

    private final KitapService kitapService = new KitapService();
    private final RezervasyonService rezervasyonService = new RezervasyonService();

    private AbstractKullanici mevcutKullanici;

    private UyePanelController parentController;

    @FXML private ComboBox<String> aramaKriteri;
    @FXML private TextField aramaMetni;
    @FXML private TableView<Kitap> kitapTablosu;
    @FXML private TableColumn<Kitap, String> adCol;
    @FXML private TableColumn<Kitap, String> yazarCol;
    @FXML private TableColumn<Kitap, String> isbnCol;
    @FXML private TableColumn<Kitap, String> mevcutDurumCol;
    @FXML private TableColumn<Kitap, Integer> adetCol;
    @FXML private TableColumn<Kitap, String> konumCol;
    @FXML private TableColumn<Kitap, String> kategoriCol;
    @FXML private TableColumn<Kitap, Void> rezerveCol;

    private ObservableList<Kitap> masterData;
    private FilteredList<Kitap> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        aramaKriteri.setItems(FXCollections.observableArrayList(
                "Tüm Alanlarda Ara", "Kitap Adı", "Yazar Adı", "ISBN", "Kategori/Tür"
        ));
        aramaKriteri.getSelectionModel().selectFirst();

        adCol.setCellValueFactory(new PropertyValueFactory<>("ad"));
        yazarCol.setCellValueFactory(new PropertyValueFactory<>("yazar"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        mevcutDurumCol.setCellValueFactory(new PropertyValueFactory<>("durumString"));
        adetCol.setCellValueFactory(new PropertyValueFactory<>("adet"));
        konumCol.setCellValueFactory(new PropertyValueFactory<>("rafNo"));
        kategoriCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));

        setupRezervasyonButtonColumn();

        loadAndSetupFiltering();
    }

    public void setMevcutKullanici(AbstractKullanici kullanici) {
        this.mevcutKullanici = kullanici;
    }

    public void setParentController(UyePanelController controller) {
        this.parentController = controller;
    }

    private void setupRezervasyonButtonColumn() {

        Callback<TableColumn<Kitap, Void>, TableCell<Kitap, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Kitap, Void> call(final TableColumn<Kitap, Void> param) {
                final TableCell<Kitap, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Rezerve Et");

                    {
                        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        btn.setOnAction((ActionEvent event) -> {
                            Kitap kitap = getTableView().getItems().get(getIndex());
                            handleRezervasyonYap(kitap);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {

                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        rezerveCol.setCellFactory(cellFactory);
    }

    private void handleRezervasyonYap(Kitap kitap) {
        if (mevcutKullanici == null || kitap == null) {
            mesajGoster(Alert.AlertType.ERROR, "Hata", "Oturum verisi eksik. Lütfen tekrar giriş yapın.");
            return;
        }

        boolean basarili = rezervasyonService.rezervasyonYap(kitap.getId(), mevcutKullanici.getKullaniciId());

        if (basarili) {
            mesajGoster(Alert.AlertType.INFORMATION, "Başarılı", kitap.getAd() + " kitabı için rezervasyonunuz yapıldı.");

            if (parentController != null) {
                parentController.handleKayıtlarım(null);
            }
        } else {

            mesajGoster(Alert.AlertType.ERROR, "Hata", "Rezervasyon yapılamadı. Aynı kitap için birden fazla kaydınız olabilir.");
        }
    }

    private void mesajGoster(Alert.AlertType tip, String baslik, String mesaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    private void loadAndSetupFiltering() {
        masterData = FXCollections.observableArrayList(kitapService.getAllBooks());

        filteredData = new FilteredList<>(masterData, p -> true);

        aramaMetni.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createSearchPredicate(newValue));
        });

        aramaKriteri.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createSearchPredicate(aramaMetni.getText()));
        });

        SortedList<Kitap> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(kitapTablosu.comparatorProperty());

        kitapTablosu.setItems(sortedData);
    }

    private Predicate<Kitap> createSearchPredicate(String searchText) {

        if (searchText == null || searchText.isEmpty()) {
            return kitap -> true;
        }
        String lowerCaseFilter = searchText.toLowerCase(Locale.forLanguageTag("tr"));
        String selectedKriter = aramaKriteri.getValue();

        return kitap -> {
            boolean matches = false;

            switch (selectedKriter) {
                case "Kitap Adı":
                    matches = kitap.getAd().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter);
                    break;
                case "Yazar Adı":
                    matches = kitap.getYazar().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter);
                    break;
                case "ISBN":
                    matches = kitap.getIsbn().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter);
                    break;
                case "Kategori/Tür":
                    matches = kitap.getKategori().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter);
                    break;
                case "Tüm Alanlarda Ara":
                default:
                    if (kitap.getAd().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter)) {
                        matches = true;
                    } else if (kitap.getYazar().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter)) {
                        matches = true;
                    } else if (kitap.getIsbn().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter)) {
                        matches = true;
                    } else if (kitap.getKategori().toLowerCase(Locale.forLanguageTag("tr")).contains(lowerCaseFilter)) {
                        matches = true;
                    }
                    break;
            }
            return matches;
        };
    }

    @FXML
    public void handleReset(javafx.event.ActionEvent event) {
        aramaMetni.clear();
        aramaKriteri.getSelectionModel().selectFirst();
    }
}