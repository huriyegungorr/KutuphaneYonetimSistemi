module com.example.kutuphaneyonetimsistemi {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;

    opens com.example.kutuphaneyonetimsistemi to javafx.graphics, javafx.fxml;
    opens com.example.kutuphaneyonetimsistemi.ui.controller to javafx.fxml;
    opens com.example.kutuphaneyonetimsistemi.model to javafx.base;
    opens com.example.kutuphaneyonetimsistemi.dao to javafx.fxml, java.sql;
    opens com.example.kutuphaneyonetimsistemi.designpatterns to java.sql;
    opens com.example.kutuphaneyonetimsistemi.designpatterns.state to javafx.base;

    exports com.example.kutuphaneyonetimsistemi.main;
}