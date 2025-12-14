package com.example.kutuphaneyonetimsistemi.designpatterns.strategy;

public class StandartCeza implements ICezaHesaplama {
    @Override
    public double cezaHesapla(int gecikmeGunSayisi, double gunlukCezaMiktari) {
        if (gecikmeGunSayisi <= 0) return 0.0;
        return gecikmeGunSayisi * gunlukCezaMiktari;
    }
}