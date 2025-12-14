# Kütüphane Yönetim Sistemi (Library Management System) 

Bu proje, bir kütüphanenin günlük operasyonlarını (kitap takibi, üye yönetimi, ödünç/iade işlemleri) dijitalleştirmek amacıyla geliştirilmiş kapsamlı bir masaüstü uygulamasıdır.  
Java ve JavaFX teknolojileri kullanılarak geliştirilmiş olup, arka planda MySQL veritabanı ile çalışmaktadır.  
Proje, Yazılım Mühendisliği prensiplerine uygun olarak, Tasarım Desenleri (Design Patterns) merkeze alınarak mimarilendirilmiştir.  

## Özellikler 
Üye Paneli Kitap Arama: Kitap adı, yazar veya ISBN'e göre detaylı arama.  
Durum Kontrolü: Kitabın rafta mı yoksa ödünçte mi olduğunu görüntüleme (State Pattern).  
Kişisel İşlemler: Ödünç alınan kitapları ve iade tarihlerini görüntüleme.  
Rezervasyon: Müsait olmayan kitaplar için rezervasyon oluşturma.  
Personel (Yönetici) PaneliCRUD İşlemleri: Kitap ve Üye ekleme, silme, güncelleme işlemleri.  
Ödünç/İade Yönetimi: Üyeler adına kitap verme ve iade alma işlemleri.  
Ceza Hesaplama: Gecikmiş kitaplar için otomatik ceza hesaplama (Strategy Pattern).  
Raporlama: Tüm üyeleri ve kitap envanterini listeleme.  

## Mimari ve Tasarım Desenleri  
Bu projede kodun sürdürülebilirliğini ve esnekliğini artırmak için aşağıdaki tasarım desenleri kullanılmıştır:  
### Tasarım Deseni Kullanım Amacı ve Yeri  
Singleton veritabanı bağlantısının (SingletonDBConnection) tek bir nesne üzerinden yönetilmesi ve kaynak tasarrufu sağlanması.  
Factory Method farklı kullanıcı tiplerinin (Uye, Personel) oluşturulma sürecinin soyutlanması (KullaniciFactory).  
Observer  kitap iade edildiğinde veya rezervasyon sırası geldiğinde ilgili üyeye bildirim gönderilmesi.  
State kitapların durumunun (Rafta, Ödünçte, Kayıp) dinamik olarak yönetilmesi.  
Strategy farklı ceza hesaplama algoritmalarının (Standart üye, VIP üye vb.) çalışma zamanında seçilebilmesi.  
Facade karmaşık alt sistemlerin (Kitap arama, ceza sorgulama, ödünç verme) tek bir arayüz arkasında basitleştirilmesi.  
DAO (Data Access Object)Veritabanı işlemlerinin iş mantığından (Service katmanı) ayrılması.  

## Kullanılan Teknolojiler  
Dil: Java (JDK 25)   
Arayüz (UI): JavaFX & FXML  
Veritabanı: MySQL  
IDE: IntelliJ IDEA  
Yapı Aracı: Maven   
Diyagramlar: Use Case, Class ,Sequence, ER Diyagramları    

<img width="2044" height="2014" alt="ERDiyagramı drawio" src="https://github.com/user-attachments/assets/c2572c27-0cf0-404a-9604-c08ecdd97bab" />  
<img width="1929" height="2099" alt="SequenceDiyagramı drawio" src="https://github.com/user-attachments/assets/8aeaf1f9-b21a-4813-8851-eff938e63b5c" />  
<img width="979" height="924" alt="Class Diyagramı" src="https://github.com/user-attachments/assets/890fa807-22da-45e5-9136-d1c30c199e76" />   
<img width="979" height="924" alt="Class Diyagramı" src="https://github.com/user-attachments/assets/67301170-8c65-4547-b6b9-a6562d6e59bc" />  



## Veritabanı Şeması (ER Diyagramı)   
Proje ilişkisel veritabanı yapısı üzerine kuruludur.  
Temel tablolar:  
Kullanicilar (Abstract yapıda Üye ve Personel verileri)  
Kitaplar (Yazar ve Kategori ilişkileriyle)  
Odunc (İşlem kayıtları)  
Cezalar ve Rezervasyonlar   

## Kurulum ve Çalıştırma  
Projeyi Klonlayın: git clone https://github.com/huriyegungorr/KutuphaneYonetimSistemi.git
Veritabanını içe Aktarın:MySQL'de dbkutuphaneyonetimsistemi adında bir veritabanı oluşturun.  
Proje dosyasındaki database.sql dosyasını bu veritabanına import edin.    
Veritabanı Ayarları:src/main/resources (veya ilgili dizin) altındaki SingletonDBConnection sınıfında yer alan kullanıcı adı ve şifreyi kendi yerel veritabanı bilgilerinizle güncelleyin.  
Çalıştırın:IntelliJ IDEA üzerinden HelloApplication.java veya Main.java dosyasını çalıştırın.  

## Ekran Görüntüleri    
![LoginEkranı](https://github.com/user-attachments/assets/3e1d9670-a380-43f8-bb6d-2c2d5a8ea476)
![KayıtEkranı](https://github.com/user-attachments/assets/c48fd497-06b9-4854-9ead-e69345d64ac2)
![UyePanel](https://github.com/user-attachments/assets/74b9fca0-3018-49f6-9f63-07ec719c1ec1)
![PersonelPanel](https://github.com/user-attachments/assets/7e895e00-c9c9-43da-8fbd-69b89e8d4c55)


Not: Bu proje, Yazılım Mühendisliği / Yazılım Mimarisi ve Tasarımı dersi kapsamında Nesne Yönelimli Programlama ve Tasarım Desenleri yetkinliklerini göstermek amacıyla geliştirilmiştir.   
Kişiler:    
Huriye Güngör : https://github.com/huriyegungorr/KutuphaneYonetimSistemi  
Emel Cansu : https://github.com/emelcansu/LibraryManagementSystem
