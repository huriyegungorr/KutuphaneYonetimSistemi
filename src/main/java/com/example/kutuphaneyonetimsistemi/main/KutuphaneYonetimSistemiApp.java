package com.example.kutuphaneyonetimsistemi.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class KutuphaneYonetimSistemiApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                KutuphaneYonetimSistemiApp.class.getResource("/com/example/kutuphaneyonetimsistemi/login-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 450, 450);
        stage.setTitle("Giriş Yap");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}