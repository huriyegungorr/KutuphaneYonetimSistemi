

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Veritabanı: `dbkutuphaneyonetimsistemi`
--

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `cezalar`
--

DROP TABLE IF EXISTS `cezalar`;
CREATE TABLE IF NOT EXISTS `cezalar` (
  `ceza_id` int NOT NULL AUTO_INCREMENT,
  `odunc_id` int NOT NULL,
  `kullanici_id` int NOT NULL,
  `gecikme_gunu` int NOT NULL,
  `ceza_tutar` decimal(10,2) NOT NULL,
  `odeme_durumu` enum('odendi','odenmedi') CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT 'odenmedi',
  PRIMARY KEY (`ceza_id`),
  KEY `odunc_id` (`odunc_id`),
  KEY `kullanici_id` (`kullanici_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kategoriler`
--

DROP TABLE IF EXISTS `kategoriler`;
CREATE TABLE IF NOT EXISTS `kategoriler` (
  `kategori_id` int NOT NULL AUTO_INCREMENT,
  `kategori_adi` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  PRIMARY KEY (`kategori_id`),
  UNIQUE KEY `kategori_adi` (`kategori_adi`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `kategoriler`
--

INSERT INTO `kategoriler` (`kategori_id`, `kategori_adi`) VALUES
(5, 'Otobiyografi'),
(4, 'Roman'),
(6, 'Distopik Roman');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kitaplar`
--

DROP TABLE IF EXISTS `kitaplar`;
CREATE TABLE IF NOT EXISTS `kitaplar` (
  `kitap_id` int NOT NULL AUTO_INCREMENT,
  `kitap_adi` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  `yayinevi` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `baski_yili` int DEFAULT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `adet` int NOT NULL DEFAULT '1',
  `raf_no` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `aciklama` text CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci,
  PRIMARY KEY (`kitap_id`),
  UNIQUE KEY `isbn` (`isbn`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `kitaplar`
--

INSERT INTO `kitaplar` (`kitap_id`, `kitap_adi`, `yayinevi`, `baski_yili`, `isbn`, `adet`, `raf_no`, `aciklama`) VALUES
(7, 'İçimizdeki Şeytan', 'Yapı Kredi Yayınları', 2010, '978975080299', 4, 'A4-3', 'Sabahattin Ali’nin bireyin iç çatışmalarını ele aldığı psikolojik roman.'),
(6, 'Sefiller', 'Can Yayınları', 2018, '9789750719387', 5, 'A-5', 'Victor Hugo’nun toplumsal adalet, vicdan ve insanlık üzerine yazdığı klasik roman.'),
(8, 'Martin Eden', 'İş Bankası Kültür Yayınları', 2011, '78678543', 10, 'A1-03', 'Jack London’ın bireycilik ve sınıf çatışmasını anlatan yarı otobiyografik eseri.'),
(9, 'Fareler ve İnsanlar', 'Sel Yayıncılık', 2013, '768', 1, 'Z-5', 'John Steinbeck’in dostluk ve hayaller üzerine yazdığı kısa roman.'),
(10, '1984', 'Can Yayınları', 2000, '758635', 3, 'B-1', 'George Orwell’ın totaliter rejimleri eleştiren distopik romanı.');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kitap_kategori`
--

DROP TABLE IF EXISTS `kitap_kategori`;
CREATE TABLE IF NOT EXISTS `kitap_kategori` (
  `kitap_id` int NOT NULL,
  `kategori_id` int NOT NULL,
  PRIMARY KEY (`kitap_id`,`kategori_id`),
  KEY `kategori_id` (`kategori_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `kitap_kategori`
--

INSERT INTO `kitap_kategori` (`kitap_id`, `kategori_id`) VALUES
(6, 4),
(7, 4),
(8, 5),
(9, 4),
(10, 6);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kitap_yazar`
--

DROP TABLE IF EXISTS `kitap_yazar`;
CREATE TABLE IF NOT EXISTS `kitap_yazar` (
  `kitap_id` int NOT NULL,
  `yazar_id` int NOT NULL,
  PRIMARY KEY (`kitap_id`,`yazar_id`),
  KEY `yazar_id` (`yazar_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `kitap_yazar`
--

INSERT INTO `kitap_yazar` (`kitap_id`, `yazar_id`) VALUES
(6, 5),
(7, 6),
(8, 7),
(9, 8),
(10, 9);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kullanicilar`
--

DROP TABLE IF EXISTS `kullanicilar`;
CREATE TABLE IF NOT EXISTS `kullanicilar` (
  `kullanici_id` int NOT NULL AUTO_INCREMENT,
  `rol_id` int NOT NULL,
  `ad` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  `soyad` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  `tc_kimlik` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `email` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `kullanici_adi` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `sifre` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  `telefon` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `adres` text CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci,
  `kayit_tarihi` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`kullanici_id`),
  UNIQUE KEY `tc_kimlik` (`tc_kimlik`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `kullanici_adi` (`kullanici_adi`),
  KEY `rol_id` (`rol_id`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `kullanicilar`
--

INSERT INTO `kullanicilar` (`kullanici_id`, `rol_id`, `ad`, `soyad`, `tc_kimlik`, `email`, `kullanici_adi`, `sifre`, `telefon`, `adres`, `kayit_tarihi`) VALUES
(18, 2, 'personel', 'p', '22222222222', 'personel@gmail.com', 'personel', '$SIM_V1$kuUX5cpUgT0DP4W1bzY2+A==$7FYsPlblv3TVCLDq5di9/nTftHl6T1EUATpQYNMTO3I=', '8456123', '', '2025-12-14 14:23:47'),
(17, 1, 'emel', 'adf', '55555555555', 'fubvdnjmk@gamil.com', 'emelcansuu', '$SIM_V1$63gDYv8jM438HAHQof0xBg==$4/D7AQUhLS3IEDtVIcZFADWLkSN9QHRh5qWdY1z/vYY=', '84761', '', '2025-12-13 11:18:27');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `odunc`
--

DROP TABLE IF EXISTS `odunc`;
CREATE TABLE IF NOT EXISTS `odunc` (
  `odunc_id` int NOT NULL AUTO_INCREMENT,
  `kitap_id` int NOT NULL,
  `kullanici_id` int NOT NULL,
  `odunc_tarihi` date NOT NULL,
  `iade_tarihi` date DEFAULT NULL,
  `son_iade_tarihi` date NOT NULL,
  `gecikme_gunu` int DEFAULT '0',
  `ceza_tutar` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`odunc_id`),
  KEY `kitap_id` (`kitap_id`),
  KEY `kullanici_id` (`kullanici_id`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `odunc`
--

INSERT INTO `odunc` (`odunc_id`, `kitap_id`, `kullanici_id`, `odunc_tarihi`, `iade_tarihi`, `son_iade_tarihi`, `gecikme_gunu`, `ceza_tutar`) VALUES
(25, 7, 17, '2025-12-14', NULL, '2025-12-29', 0, 0.00),
(24, 9, 17, '2025-12-14', '2025-12-14', '2025-12-29', 0, 0.00);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `rezervasyonlar`
--

DROP TABLE IF EXISTS `rezervasyonlar`;
CREATE TABLE IF NOT EXISTS `rezervasyonlar` (
  `rezervasyon_id` int NOT NULL AUTO_INCREMENT,
  `kitap_id` int NOT NULL,
  `kullanici_id` int NOT NULL,
  `rezervasyon_tarihi` date NOT NULL,
  `sira_numarasi` int NOT NULL DEFAULT '0',
  `bitis_tarihi` date DEFAULT NULL,
  `durum` varchar(20) COLLATE utf8mb3_turkish_ci NOT NULL DEFAULT 'AKTIF',
  PRIMARY KEY (`rezervasyon_id`),
  KEY `kitap_id` (`kitap_id`),
  KEY `kullanici_id` (`kullanici_id`)
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `rezervasyonlar`
--

INSERT INTO `rezervasyonlar` (`rezervasyon_id`, `kitap_id`, `kullanici_id`, `rezervasyon_tarihi`, `sira_numarasi`, `bitis_tarihi`, `durum`) VALUES
(40, 6, 17, '2025-12-14', 1, NULL, 'IPTAL'),
(26, 2, 3, '2025-12-08', 1, '2025-12-08', 'TAMAMLANDI'),
(39, 7, 17, '2025-12-14', 1, '2025-12-14', 'TAMAMLANDI'),
(27, 2, 3, '2025-12-10', 1, NULL, 'IPTAL'),
(28, 2, 10, '2025-12-11', 2, NULL, 'IPTAL'),
(29, 2, 3, '2025-12-11', 3, NULL, 'IPTAL'),
(30, 2, 10, '2025-12-11', 1, '2025-12-11', 'TAMAMLANDI'),
(31, 2, 11, '2025-12-11', 2, '2025-12-11', 'TAMAMLANDI'),
(32, 2, 10, '2025-12-11', 1, '2025-12-13', 'TAMAMLANDI'),
(33, 3, 3, '2025-12-13', 1, NULL, 'AKTIF'),
(34, 2, 3, '2025-12-13', 1, NULL, 'AKTIF'),
(35, 5, 3, '2025-12-13', 1, NULL, 'AKTIF'),
(36, 4, 3, '2025-12-13', 1, '2025-12-14', 'TAMAMLANDI'),
(37, 4, 10, '2025-12-13', 1, NULL, 'AKTIF'),
(38, 2, 10, '2025-12-13', 2, NULL, 'AKTIF');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `roller`
--

DROP TABLE IF EXISTS `roller`;
CREATE TABLE IF NOT EXISTS `roller` (
  `rol_id` int NOT NULL AUTO_INCREMENT,
  `rol_adi` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  PRIMARY KEY (`rol_id`),
  UNIQUE KEY `rol_adi` (`rol_adi`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `roller`
--

INSERT INTO `roller` (`rol_id`, `rol_adi`) VALUES
(1, 'uye'),
(2, 'personel');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `sistem_ayar`
--

DROP TABLE IF EXISTS `sistem_ayar`;
CREATE TABLE IF NOT EXISTS `sistem_ayar` (
  `ayar_id` int NOT NULL AUTO_INCREMENT,
  `max_odunc_sayisi` int DEFAULT '3',
  `odunc_suresi_gun` int DEFAULT '15',
  `gunluk_gecikme_cezasi` decimal(10,2) DEFAULT '1.00',
  PRIMARY KEY (`ayar_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `sistem_ayar`
--

INSERT INTO `sistem_ayar` (`ayar_id`, `max_odunc_sayisi`, `odunc_suresi_gun`, `gunluk_gecikme_cezasi`) VALUES
(1, 3, 15, 1.00);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `uye_bildirimleri`
--

DROP TABLE IF EXISTS `uye_bildirimleri`;
CREATE TABLE IF NOT EXISTS `uye_bildirimleri` (
  `bildirim_id` int NOT NULL AUTO_INCREMENT,
  `kullanici_id` int NOT NULL,
  `mesaj` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `tarih` date NOT NULL,
  `okundu_mu` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`bildirim_id`),
  KEY `kullanici_id` (`kullanici_id`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tablo döküm verisi `uye_bildirimleri`
--

INSERT INTO `uye_bildirimleri` (`bildirim_id`, `kullanici_id`, `mesaj`, `tarih`, `okundu_mu`) VALUES
(23, 17, '✅ Rezervasyonunuz onaylandı! \'İçimizdeki Şeytan\' adlı kitabı ödünç aldınız. Son iade tarihi: 2025-12-29. İyi okumalar dileriz.', '2025-12-14', 1),
(22, 17, '✅ \'Fareler ve İnsanlar\' adlı kitabın iade işlemi başarıyla onaylandı. Borç/Ceza: 0,00 TL. Teşekkür ederiz.', '2025-12-14', 1),
(21, 17, '✅ Personel tarafından işlem yapıldı. \'Fareler ve İnsanlar\' adlı kitabı ödünç aldınız. Son iade tarihi: 2025-12-29. İyi okumalar dileriz.', '2025-12-14', 1);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `yazarlar`
--

DROP TABLE IF EXISTS `yazarlar`;
CREATE TABLE IF NOT EXISTS `yazarlar` (
  `yazar_id` int NOT NULL AUTO_INCREMENT,
  `yazar_adi` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  PRIMARY KEY (`yazar_id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;

--
-- Tablo döküm verisi `yazarlar`
--

INSERT INTO `yazarlar` (`yazar_id`, `yazar_adi`) VALUES
(8, 'John Steinbeck'),
(7, 'Jack London'),
(6, 'Sabahattin Ali'),
(5, 'Viktor Hugo'),
(9, 'George Orwell');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `yetkiler`
--

DROP TABLE IF EXISTS `yetkiler`;
CREATE TABLE IF NOT EXISTS `yetkiler` (
  `yetki_id` int NOT NULL AUTO_INCREMENT,
  `rol_id` int NOT NULL,
  `yetki_adi` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_turkish_ci NOT NULL,
  PRIMARY KEY (`yetki_id`),
  KEY `rol_id` (`rol_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
