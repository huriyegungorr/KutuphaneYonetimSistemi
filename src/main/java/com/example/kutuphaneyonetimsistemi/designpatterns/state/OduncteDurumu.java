package com.example.kutuphaneyonetimsistemi.designpatterns.state;

public class OduncteDurumu implements KitapDurumu {
    @Override
    public void durumBilgisiVer() {
        System.out.println("Kitap başkasına ödünç verilmiş.");
    }

    @Override
    public boolean oduncVerilebilirMi() {
        return false;
    }

    @Override
    public String getDurumAdi() {
        return "Oduncte";
    }
}