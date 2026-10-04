extends Node

var heroes: Dictionary = {}

func _ready() -> void:
	_register_all_heroes()

func _register_all_heroes() -> void:
	# 1. Okçu Elf
	register_hero(HeroData.new(
		"okcu_elf", "Okçu Elf", "Zümrüt Ormanı Nişancısı",
		HeroData.HeroRole.RANGED, 680, 115, 16, 1.0, HeroData.AttackRange.RANGED,
		"Ok Yağmuru", "Tüm düşmanlara 180 hasar verir ve saldırı hızlarını 2.5 sn %25 yavaşlatır.",
		Color("#10B981"), "🏹", "Kadim ormanların koruyucusu keskin nişancı."
	))

	# 2. Siyah Ork
	register_hero(HeroData.new(
		"siyah_ork", "Siyah Ork", "Karanlık Dağlar Reisi",
		HeroData.HeroRole.WARRIOR, 1250, 130, 32, 1.3, HeroData.AttackRange.MELEE,
		"Ork Gazabı & Kükreme", "Öndeki hedefe 270 hasar vurur ve verilen hasarın %45'i kadar can yeniler.",
		Color("#DC2626"), "👹", "Yıkıcı baltasıyla savaş saflarını darmadağın eden dev ork."
	))

	# 3. Zırhlı İnsan
	register_hero(HeroData.new(
		"zirhli_insan", "Zırhlı İnsan", "Kraliyet Başşövalyesi",
		HeroData.HeroRole.WARRIOR, 1150, 100, 48, 1.2, HeroData.AttackRange.MELEE,
		"Kutsal Kılıç Darbesi", "Hedefe 230 kutsal hasar vurur ve zırhını 4 sn boyunca 25 puan kırar.",
		Color("#F59E0B"), "⚔️", "Çelik zırhlı kraliyet şövalyesi."
	))

	# 4. Muhafız
	register_hero(HeroData.new(
		"muhafiz", "Muhafız", "Aegis Kalkanı Koruyucusu",
		HeroData.HeroRole.TANK, 1400, 72, 58, 1.5, HeroData.AttackRange.MELEE,
		"Aegis Kalkan Duvarı", "Tüm takım arkadaşlarına 260 puanlık koruyucu kalkan açar ve Taunt uygular.",
		Color("#3B82F6"), "🛡️", "Koca kalkanıyla müttefiklerini ölümden koruyan paladin."
	))

	# 5. Robot Tank
	register_hero(HeroData.new(
		"robot_tank", "Robot Tank", "Siber Muharebe Mk-IV",
		HeroData.HeroRole.TANK, 1550, 88, 52, 1.6, HeroData.AttackRange.RANGED,
		"Roket Yaylımı", "Omuz bataryalarından 3 güdümlü roket atar, 300 patlayıcı hasar ve yanma verir.",
		Color("#06B6D4"), "🤖", "Gelişmiş reaktörle çalışan sibernetik savaş tankı."
	))

	# 6. Taş Golemi
	register_hero(HeroData.new(
		"tas_golemi", "Taş Golemi", "Kadim Zirve Devi",
		HeroData.HeroRole.TANK, 1750, 65, 65, 1.8, HeroData.AttackRange.MELEE,
		"Sismik Deprem", "Yere devasa yumruk vurup tüm düşmanlara 150 hasar ve öndekilere 2 sn sersemletme verir.",
		Color("#78716C"), "🗿", "Granit kayalardan oluşmuş yıkılmaz kadim toprak devi."
	))

	# 7. Alev Büyücüsü
	register_hero(HeroData.new(
		"alev_buyucusu", "Alev Büyücüsü", "Lav Vadisi Büyücüsü",
		HeroData.HeroRole.MAGE, 630, 135, 12, 1.3, HeroData.AttackRange.RANGED,
		"Kıyamet Ateşi", "En yüksek canlı düşmanın üzerine dev alev patlaması indirir: 330 hasar + yanma.",
		Color("#F97316"), "🔥", "Alevleri kontrol eden korkusuz ateş büyücüsü."
	))

	# 8. Gölge Suikastçı
	register_hero(HeroData.new(
		"golge_suikastci", "Gölge Suikastçı", "Gece Bıçakları Lideri",
		HeroData.HeroRole.ASSASSIN, 720, 145, 18, 0.9, HeroData.AttackRange.MELEE,
		"Gölge Arkası Suikast", "Düşman arka saflarına sıçrayarak en zayıf hedefe %250 kritik hasar vurur.",
		Color("#8B5CF6"), "🗡️", "Gölgelerden saldırıp göz açıp kapayıncaya dek yok eden suikastçı."
	))

	# 9. Şifacı Peri
	register_hero(HeroData.new(
		"sifaci_peri", "Şifacı Peri", "Işık Çeşmesi Muhafızı",
		HeroData.HeroRole.SUPPORT, 700, 62, 20, 1.2, HeroData.AttackRange.RANGED,
		"Hayat Baharı", "En yaralı 2 takım arkadaşına anında 290 can kazandırır.",
		Color("#14B8A6"), "🧚", "Işıltılı kanatlarıyla dostlarının yaralarını saran sevimli peri."
	))

	# 10. Buz Cadısı
	register_hero(HeroData.new(
		"buz_cadisi", "Buz Cadısı", "Kuzey Ayazı Hükümdarı",
		HeroData.HeroRole.MAGE, 660, 110, 15, 1.2, HeroData.AttackRange.RANGED,
		"Buzul Fırtınası", "Düşman saflarına tipi estirir; tüm düşmanlara 170 hasar vurup dondurur.",
		Color("#0284C7"), "❄️", "Sonsuz kışın dondurucu buz rüzgarlarını yöneten büyücü."
	))

func register_hero(hero: HeroData) -> void:
	heroes[hero.id] = hero

func get_hero(hero_id: String) -> HeroData:
	if heroes.has(hero_id):
		return heroes[hero_id]
	return heroes.values()[0]

func get_all_heroes() -> Array:
	return heroes.values()
