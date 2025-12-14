package com.example.kutuphaneyonetimsistemi.model;

import java.time.LocalDate;

public class Odunc {
    private int oduncId;
    private int kitapId;
    private int kullaniciId;
    private LocalDate oduncTarihi;
    private LocalDate sonIadeTarihi;
    private LocalDate iadeTarihi;
    private int gecikmeGunu;
    private double cezaTutar;
    private String kitapAdi;
    private String uyeAdiSoyad;
    private String uyeTc;

    public Odunc() {}

    public int getOduncId() { return oduncId; }
    public void setOduncId(int oduncId) { this.oduncId = oduncId; }

    public int getKitapId() { return kitapId; }
    public void setKitapId(int kitapId) { this.kitapId = kitapId; }

    public int getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(int kullaniciId) { this.kullaniciId = kullaniciId; }

    public LocalDate getOduncTarihi() { return oduncTarihi; }
    public void setOduncTarihi(LocalDate oduncTarihi) { this.oduncTarihi = oduncTarihi; }

    public LocalDate getSonIadeTarihi() { return sonIadeTarihi; }
    public void setSonIadeTarihi(LocalDate sonIadeTarihi) { this.sonIadeTarihi = sonIadeTarihi; }

    public LocalDate getIadeTarihi() { return iadeTarihi; }
    public void setIadeTarihi(LocalDate iadeTarihi) { this.iadeTarihi = iadeTarihi; }

    public int getGecikmeGunu() { return gecikmeGunu; }
    public void setGecikmeGunu(int gecikmeGunu) { this.gecikmeGunu = gecikmeGunu; }

    public double getCezaTutar() { return cezaTutar; }
    public void setCezaTutar(double cezaTutar) { this.cezaTutar = cezaTutar; }

    public String getKitapAdi() { return kitapAdi; }
    public void setKitapAdi(String kitapAdi) { this.kitapAdi = kitapAdi; }

    public String getUyeAdiSoyad() { return uyeAdiSoyad; }
    public void setUyeAdiSoyad(String uyeAdiSoyad) { this.uyeAdiSoyad = uyeAdiSoyad; }

    public String getUyeTc() { return uyeTc; }
    public void setUyeTc(String uyeTc) { this.uyeTc = uyeTc; }
}