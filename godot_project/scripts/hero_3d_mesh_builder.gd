class_name Hero3DMeshBuilder
extends RefCounted

static func build_hero_3d(hero_id: String) -> Node3D:
	var root = Node3D.new()
	root.name = "Hero3D_" + hero_id

	# Ground Pedestal
	var pedestal = _create_cylinder(Vector3(0, -0.6, 0), 0.9, 0.2, Color("#1E2638"))
	root.add_child(pedestal)
	var glow_ring = _create_cylinder(Vector3(0, -0.48, 0), 0.75, 0.05, _get_hero_color(hero_id), true)
	root.add_child(glow_ring)

	match hero_id:
		"okcu_elf":
			_build_elf_archer(root)
		"siyah_ork":
			_build_black_orc(root)
		"zirhli_insan":
			_build_knight(root)
		"muhafiz":
			_build_paladin(root)
		"robot_tank":
			_build_robot_tank(root)
		"tas_golemi":
			_build_stone_golem(root)
		"alev_buyucusu":
			_build_fire_mage(root)
		"golge_suikastci":
			_build_shadow_assassin(root)
		"sifaci_peri":
			_build_fairy(root)
		"buz_cadisi":
			_build_frost_witch(root)
		_:
			_build_knight(root)

	return root

static func _build_elf_archer(root: Node3D) -> void:
	var green = Color("#10B981")
	var dark_green = Color("#047857")
	var wood = Color("#92400E")
	var skin = Color("#FDE68A")

	# Legs & Boots
	root.add_child(_create_box(Vector3(-0.2, -0.2, 0), Vector3(0.18, 0.6, 0.2), dark_green))
	root.add_child(_create_box(Vector3(0.2, -0.2, 0), Vector3(0.18, 0.6, 0.2), dark_green))
	# Tunic
	root.add_child(_create_box(Vector3(0, 0.3, 0), Vector3(0.5, 0.6, 0.35), green))
	# Head
	root.add_child(_create_box(Vector3(0, 0.8, 0), Vector3(0.35, 0.38, 0.35), skin))
	# Hood top
	root.add_child(_create_prism(Vector3(0, 1.05, 0), Vector3(0.3, 0.25, 0.3), dark_green))
	# Bow in hand
	root.add_child(_create_box(Vector3(-0.45, 0.3, 0.2), Vector3(0.06, 1.1, 0.06), wood))
	root.add_child(_create_box(Vector3(-0.1, 0.3, 0.2), Vector3(0.7, 0.04, 0.04), green, true))

static func _build_black_orc(root: Node3D) -> void:
	var dark_green = Color("#1E3A1E")
	var iron = Color("#1F242D")
	var red = Color("#DC2626")

	# Bulky Legs
	root.add_child(_create_box(Vector3(-0.3, -0.2, 0), Vector3(0.28, 0.6, 0.3), iron))
	root.add_child(_create_box(Vector3(0.3, -0.2, 0), Vector3(0.28, 0.6, 0.3), iron))
	# Heavy Torso
	root.add_child(_create_box(Vector3(0, 0.35, 0), Vector3(0.85, 0.7, 0.5), dark_green))
	root.add_child(_create_box(Vector3(0, 0.35, 0.1), Vector3(0.75, 0.55, 0.4), iron))
	# Spiked Shoulders
	root.add_child(_create_box(Vector3(-0.55, 0.65, 0), Vector3(0.3, 0.3, 0.35), iron))
	root.add_child(_create_box(Vector3(0.55, 0.65, 0), Vector3(0.3, 0.3, 0.35), iron))
	# Horned Head
	root.add_child(_create_box(Vector3(0, 0.85, 0.1), Vector3(0.45, 0.4, 0.4), dark_green))
	root.add_child(_create_box(Vector3(0, 0.85, 0.32), Vector3(0.25, 0.06, 0.04), red, true))
	# Giant Battleaxe
	root.add_child(_create_box(Vector3(0.7, 0.3, 0.2), Vector3(0.08, 1.5, 0.08), iron))
	root.add_child(_create_box(Vector3(0.95, 0.8, 0.2), Vector3(0.4, 0.45, 0.05), iron))
	root.add_child(_create_box(Vector3(1.16, 0.8, 0.2), Vector3(0.05, 0.5, 0.06), red, true))

static func _build_knight(root: Node3D) -> void:
	var steel = Color("#94A3B8")
	var gold = Color("#F59E0B")
	var blue = Color("#2563EB")

	# Armor legs
	root.add_child(_create_box(Vector3(-0.22, -0.2, 0), Vector3(0.22, 0.6, 0.25), steel))
	root.add_child(_create_box(Vector3(0.22, -0.2, 0), Vector3(0.22, 0.6, 0.25), steel))
	# Breastplate
	root.add_child(_create_box(Vector3(0, 0.35, 0), Vector3(0.65, 0.65, 0.4), steel))
	root.add_child(_create_box(Vector3(0, 0.35, 0.22), Vector3(0.1, 0.4, 0.04), gold, true))
	root.add_child(_create_box(Vector3(0, 0.45, 0.22), Vector3(0.35, 0.1, 0.04), gold, true))
	# Visor Helmet & Plume
	root.add_child(_create_box(Vector3(0, 0.85, 0.05), Vector3(0.38, 0.4, 0.38), steel))
	root.add_child(_create_box(Vector3(0, 1.1, -0.05), Vector3(0.08, 0.2, 0.3), blue))
	# Steel Broadsword
	root.add_child(_create_box(Vector3(0.55, 0.2, 0.2), Vector3(0.08, 1.3, 0.04), steel))
	root.add_child(_create_box(Vector3(0.55, 0.55, 0.2), Vector3(0.35, 0.06, 0.06), gold))

static func _build_paladin(root: Node3D) -> void:
	var white = Color("#F8FAFC")
	var gold = Color("#FBBF24")
	var blue = Color("#0284C7")

	root.add_child(_create_cylinder(Vector3(0, 1.25, 0), 0.35, 0.04, gold, true)) # Halo
	root.add_child(_create_box(Vector3(0, 0.35, 0), Vector3(0.75, 0.7, 0.45), white))
	root.add_child(_create_box(Vector3(0, 0.85, 0.05), Vector3(0.42, 0.4, 0.42), gold))
	# Tower Shield
	root.add_child(_create_box(Vector3(-0.55, 0.3, 0.3), Vector3(0.55, 1.1, 0.08), blue))
	root.add_child(_create_box(Vector3(-0.55, 0.3, 0.35), Vector3(0.1, 0.7, 0.04), gold, true))
	# Warhammer
	root.add_child(_create_box(Vector3(0.55, 0.3, 0.2), Vector3(0.08, 1.1, 0.08), gold))
	root.add_child(_create_box(Vector3(0.55, 0.75, 0.2), Vector3(0.38, 0.28, 0.3), white))

static func _build_robot_tank(root: Node3D) -> void:
	var mech = Color("#334155")
	var cyan = Color("#06B6D4")
	var dark = Color("#0F172A")

	# Caterpillar Treads
	root.add_child(_create_box(Vector3(-0.45, -0.2, 0), Vector3(0.28, 0.4, 0.9), dark))
	root.add_child(_create_box(Vector3(0.45, -0.2, 0), Vector3(0.28, 0.4, 0.9), dark))
	# Hull & Turret
	root.add_child(_create_box(Vector3(0, 0.25, 0), Vector3(0.7, 0.5, 0.6), mech))
	root.add_child(_create_box(Vector3(0, 0.28, 0.32), Vector3(0.5, 0.08, 0.04), cyan, true)) # Visor
	# Shoulder Missile Pods
	root.add_child(_create_box(Vector3(-0.5, 0.55, 0), Vector3(0.3, 0.28, 0.45), mech))
	root.add_child(_create_box(Vector3(0.5, 0.55, 0), Vector3(0.3, 0.28, 0.45), mech))
	# Cannon
	root.add_child(_create_cylinder(Vector3(0, 0.15, 0.5), 0.1, 0.5, dark))

static func _build_stone_golem(root: Node3D) -> void:
	var rock = Color("#57534E")
	var dark_rock = Color("#292524")
	var gold = Color("#FBBF24")

	root.add_child(_create_box(Vector3(-0.35, -0.2, 0), Vector3(0.35, 0.55, 0.38), dark_rock))
	root.add_child(_create_box(Vector3(0.35, -0.2, 0), Vector3(0.35, 0.55, 0.38), dark_rock))
	root.add_child(_create_box(Vector3(0, 0.4, 0), Vector3(1.05, 0.8, 0.7), rock))
	root.add_child(_create_box(Vector3(0, 0.45, 0.36), Vector3(0.45, 0.1, 0.04), gold, true)) # Rune crack
	# Giant Fists
	root.add_child(_create_box(Vector3(-0.75, 0.2, 0.15), Vector3(0.4, 0.4, 0.4), dark_rock))
	root.add_child(_create_box(Vector3(0.75, 0.2, 0.15), Vector3(0.4, 0.4, 0.4), dark_rock))
	# Head
	root.add_child(_create_box(Vector3(0, 0.9, 0.1), Vector3(0.48, 0.35, 0.4), rock))
	root.add_child(_create_box(Vector3(0, 0.92, 0.32), Vector3(0.3, 0.06, 0.04), gold, true)) # Eyes

static func _build_fire_mage(root: Node3D) -> void:
	var red = Color("#DC2626")
	var orange = Color("#F97316")
	var gold = Color("#FBBF24")

	root.add_child(_create_cylinder(Vector3(0, 0.05, 0), 0.48, 0.8, red)) # Robe
	root.add_child(_create_box(Vector3(0, 0.7, 0.05), Vector3(0.38, 0.38, 0.36), orange))
	root.add_child(_create_prism(Vector3(0, 1.05, -0.05), Vector3(0.2, 0.4, 0.2), red)) # Hat
	# Staff & Fireball
	root.add_child(_create_cylinder(Vector3(0.5, 0.4, 0.15), 0.05, 1.4, Color("#78350F")))
	root.add_child(_create_box(Vector3(0.5, 1.15, 0.15), Vector3(0.25, 0.25, 0.25), gold, true))

static func _build_shadow_assassin(root: Node3D) -> void:
	var dark = Color("#1E1B4B")
	var purple = Color("#6B21A8")
	var violet = Color("#A855F7")

	root.add_child(_create_box(Vector3(0, 0.3, 0), Vector3(0.52, 0.6, 0.32), dark))
	root.add_child(_create_box(Vector3(0, 0.2, -0.18), Vector3(0.6, 0.85, 0.06), purple)) # Cape
	root.add_child(_create_box(Vector3(0, 0.75, 0.05), Vector3(0.35, 0.35, 0.35), dark))
	root.add_child(_create_box(Vector3(0, 0.78, 0.24), Vector3(0.22, 0.05, 0.04), violet, true)) # Visor
	# Dual Daggers
	root.add_child(_create_box(Vector3(-0.45, 0.1, 0.15), Vector3(0.05, 0.55, 0.1), violet, true))
	root.add_child(_create_box(Vector3(0.45, 0.1, 0.15), Vector3(0.05, 0.55, 0.1), violet, true))

static func _build_fairy(root: Node3D) -> void:
	var teal = Color("#14B8A6")
	var pale = Color("#CCFBF1")
	var wing = Color("#2DD4BF")
	var gold = Color("#FDE047")

	root.add_child(_create_cylinder(Vector3(0, 0.2, 0), 0.28, 0.65, pale)) # Dress
	root.add_child(_create_box(Vector3(0, 0.65, 0.05), Vector3(0.28, 0.3, 0.28), pale))
	root.add_child(_create_box(Vector3(0, 0.78, 0.05), Vector3(0.32, 0.05, 0.32), gold, true)) # Circlet
	# Wings
	root.add_child(_create_prism(Vector3(-0.45, 0.6, -0.15), Vector3(0.65, 0.65, 0.02), wing, true))
	root.add_child(_create_prism(Vector3(0.45, 0.6, -0.15), Vector3(0.65, 0.65, 0.02), wing, true))
	# Healing Staff
	root.add_child(_create_box(Vector3(0.4, 0.4, 0.15), Vector3(0.05, 1.1, 0.05), gold))
	root.add_child(_create_box(Vector3(0.4, 0.98, 0.15), Vector3(0.2, 0.2, 0.2), teal, true))

static func _build_frost_witch(root: Node3D) -> void:
	var navy = Color("#0369A1")
	var cyan = Color("#38BDF8")
	var ice = Color("#E0F2FE")

	root.add_child(_create_cylinder(Vector3(0, 0.1, 0), 0.46, 0.8, navy)) # Robe
	root.add_child(_create_box(Vector3(0, 0.68, 0.05), Vector3(0.34, 0.35, 0.34), ice))
	# Ice Tiara
	root.add_child(_create_prism(Vector3(0, 0.96, 0.1), Vector3(0.08, 0.28, 0.08), ice, true))
	root.add_child(_create_prism(Vector3(-0.14, 0.92, 0.1), Vector3(0.06, 0.2, 0.06), cyan, true))
	root.add_child(_create_prism(Vector3(0.14, 0.92, 0.1), Vector3(0.06, 0.2, 0.06), cyan, true))
	# Glacial Staff
	root.add_child(_create_box(Vector3(0.48, 0.4, 0.15), Vector3(0.05, 1.3, 0.05), navy))
	root.add_child(_create_box(Vector3(0.48, 1.1, 0.15), Vector3(0.25, 0.25, 0.25), ice, true))

# Mesh & Material Helper Functions
static func _create_box(pos: Vector3, sz: Vector3, col: Color, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var box = BoxMesh.new()
	box.size = sz
	mi.mesh = box
	mi.position = pos
	var mat = StandardMaterial3D.new()
	mat.albedo_color = col
	mat.roughness = 0.4
	if emissive:
		mat.emission_enabled = true
		mat.emission = col
		mat.emission_energy_multiplier = 1.4
	mi.material_override = mat
	return mi

static func _create_cylinder(pos: Vector3, radius: float, height: float, col: Color, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var cyl = CylinderMesh.new()
	cyl.top_radius = radius
	cyl.bottom_radius = radius
	cyl.height = height
	cyl.radial_segments = 12
	mi.mesh = cyl
	mi.position = pos
	var mat = StandardMaterial3D.new()
	mat.albedo_color = col
	mat.roughness = 0.4
	if emissive:
		mat.emission_enabled = true
		mat.emission = col
		mat.emission_energy_multiplier = 1.4
	mi.material_override = mat
	return mi

static func _create_prism(pos: Vector3, sz: Vector3, col: Color, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var prism = PrismMesh.new()
	prism.size = sz
	mi.mesh = prism
	mi.position = pos
	var mat = StandardMaterial3D.new()
	mat.albedo_color = col
	if emissive:
		mat.emission_enabled = true
		mat.emission = col
		mat.emission_energy_multiplier = 1.4
	mi.material_override = mat
	return mi

static func _get_hero_color(hero_id: String) -> Color:
	match hero_id:
		"okcu_elf": return Color("#10B981")
		"siyah_ork": return Color("#DC2626")
		"zirhli_insan": return Color("#F59E0B")
		"muhafiz": return Color("#3B82F6")
		"robot_tank": return Color("#06B6D4")
		"tas_golemi": return Color("#78716C")
		"alev_buyucusu": return Color("#F97316")
		"golge_suikastci": return Color("#8B5CF6")
		"sifaci_peri": return Color("#14B8A6")
		"buz_cadisi": return Color("#0284C7")
		_: return Color("#FFD166")
