# Hero Arena 3D - Godot Engine 4.7 Projesi

Bu klasör (`godot_project`), Hero Arena oyununun **Godot Engine 4.7** (ve 4.x serisi) için hazırlanmış, tüm ayrıştırma (parsing) hataları giderilmiş, çalışmaya hazır tam proje kaynak kodlarını içerir.

---

## 🎮 Proje İçeriği ve Yapısı

```
godot_project/
├── project.godot                     # Godot 4 proje ana yapılandırma dosyası
├── icon.svg                          # Proje ikonu
├── scenes/
│   ├── main_menu.tscn                # Ana Menü sahnesi
│   ├── hero_viewer_3d.tscn           # 360° 3D Kahraman İnceleme sahnesi
│   └── battle_arena.tscn             # 3D Otomatik Savaş Arenası sahnesi
└── scripts/
    ├── hero_data.gd                  # Kahraman veri modeli (Resource)
    ├── hero_registry.gd              # 10 kahramanın tüm istatistik ve yetenekleri (Autoload)
    ├── combat_hero.gd                # Arenadaki 3D savaşçı birimleri
    ├── battle_engine.gd              # Otomatik savaş motoru mantığı
    ├── hero_3d_mesh_builder.gd       # 10 kahramanın 3D modellerini oluşturan sınıf
    ├── main_menu.gd                  # Ana menü kontrolleri
    ├── hero_viewer_3d.gd             # 3D döndürme & inceleme kontrolleri
    └── battle_arena.gd               # Savaş kontrolcüsü ve hız ayarları
```

---

## 🚀 Godot Engine'de Nasıl Açılır?

1. **Godot Engine 4'ü İndirin:**
   - Resmi siteden [Godot Engine 4.x Standard](https://godotengine.org/) sürümünü indirin (Windows, macOS veya Linux).
2. **Projeyi İçe Aktarın (Import):**
   - Godot'yu açın.
   - **"Import" (İçe Aktar)** butonuna tıklayın.
   - Proje klasöründeki `godot_project/project.godot` dosyasını seçin.
   - **"Import & Edit"** butonuna basın.
3. **Çalıştırma:**
   - Klavyeden **F5** tuşuna basarak veya sağ üstteki **Play (Oynat)** butonuna tıklayarak oyunu başlatın.
   - Başlangıç sahnesi `res://scenes/main_menu.tscn` olarak ayarlanmıştır.

---

## 💎 10 Kahraman Listesi

1. **Okçu Elf** (Menzilli, Yay & Işıltılı Ok, *Ok Yağmuru*)
2. **Siyah Ork** (Savaşçı, Çift Ağızlı Dev Balta, *Ork Gazabı & Kükreme*)
3. **Zırhlı İnsan** (Şövalye, Kutsal Çelik Kılıç, *Kutsal Kılıç Darbesi*)
4. **Muhafız** (Paladin, Işık Haresi & Kule Kalkanı, *Aegis Kalkan Duvarı*)
5. **Robot Tank** (Siber Tank, Paletler & Omuz Roketleri, *Roket Yaylımı*)
6. **Taş Golemi** (Dev, Granit Yumruklar & Rün Çatlakları, *Sismik Deprem*)
7. **Alev Büyücüsü** (Büyücü, Cübbe & Alev Topu Asası, *Kıyamet Ateşi*)
8. **Gölge Suikastçı** (Suikastçı, Gece Pelerini & Çift Hançer, *Gölge Arkası Suikast*)
9. **Şifacı Peri** (Destek, Çırpınan Kanatlar & Çiçek Asası, *Hayat Baharı*)
10. **Buz Cadısı** (Büyücü, Buzdan Taç & Glasiyel Asa, *Buzul Fırtınası*)

---

## 📱 Dışa Aktarma (Export) Seçenekleri

Godot içerisinden **Project > Export** menüsünü kullanarak:
- **Android APK / AAB** (Mobil)
- **Windows / Linux / macOS** (.exe / binary)
- **Web (HTML5)** tarayıcı oyunu
olarak tek tıkla derleyebilirsiniz!
