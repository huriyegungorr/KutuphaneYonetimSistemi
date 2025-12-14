package com.example.kutuphaneyonetimsistemi.service;

import com.example.kutuphaneyonetimsistemi.dao.KitapDAO;
import com.example.kutuphaneyonetimsistemi.model.Kitap;
import java.util.List;

public class KitapService {

    private final KitapDAO kitapDAO = new KitapDAO();

    public List<Kitap> getAllBooks() {
        return kitapDAO.getAllBooks();
    }

    public List<Kitap> kitapAra(String kriter, String metin) {
        return kitapDAO.kitapAra(kriter, metin);
    }

    public boolean kitapEkle(Kitap kitap) {

        return kitapDAO.kitapEkle(kitap);
    }

    public boolean kitapGuncelle(Kitap kitap) {
        return kitapDAO.kitapGuncelle(kitap);
    }

    public boolean kitapSil(int kitapId) {
        return kitapDAO.kitapSil(kitapId);
    }
}