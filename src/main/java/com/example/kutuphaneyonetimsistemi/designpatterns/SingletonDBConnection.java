package com.example.kutuphaneyonetimsistemi.designpatterns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonDBConnection {

    private static SingletonDBConnection instance;


    private SingletonDBConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Sürücüsü Başlatıldı.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Sürücüsü bulunamadı.", e);
        }
    }

    public static SingletonDBConnection getInstance() {
        if (instance == null) {
            instance = new SingletonDBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            final String URL = "jdbc:mysql://localhost:3306/dbkutuphaneyonetimsistemi?serverTimezone=UTC";
            final String USER = "";
            final String PASSWORD = "";

            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            return connection;
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantısı kurulamadı!");

            throw e;
        }
    }
}
