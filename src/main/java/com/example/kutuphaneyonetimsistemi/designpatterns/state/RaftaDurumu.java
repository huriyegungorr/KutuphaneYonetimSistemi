package com.example.kutuphaneyonetimsistemi.designpatterns.state;

public class RaftaDurumu implements KitapDurumu {
    @Override
    public void durumBilgisiVer() {
        System.out.println("Kitap şu an rafta, ödünç verilebilir.");
    }

    @Override
    public boolean oduncVerilebilirMi() {
        return true;
    }

    @Override
    public String getDurumAdi() {
        return "Rafta";
    }
}