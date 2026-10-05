class_name Hero3DMeshBuilder
extends RefCounted

static func build_hero_3d(hero_id: String) -> Node3D:
	var root = Node3D.new()
	root.name = "Hero3D_" + hero_id

	# Realistic Pedestal with Double Bevel & Glowing Energy Disc
	var base_plinth = _create_cylinder(Vector3(0, -0.65, 0), 1.05, 0.15, Color("#151C2C"), 0.8, 0.3)
	root.add_child(base_plinth)
	var upper_disc = _create_cylinder(Vector3(0, -0.52, 0), 0.9, 0.12, Color("#1E2638"), 0.5, 0.5)
	root.add_child(upper_disc)
	var energy_ring = _create_cylinder(Vector3(0, -0.45, 0), 0.78, 0.04, _get_hero_color(hero_id), 0.9, 0.2, true)
	root.add_child(energy_ring)

	match hero_id:
		"okcu_elf": _build_elf_archer(root)
		"siyah_ork": _build_black_orc(root)
		"zirhli_insan": _build_knight(root)
		"muhafiz": _build_paladin(root)
		"robot_tank": _build_robot_tank(root)
		"tas_golemi": _build_stone_golem(root)
		"alev_buyucusu": _build_fire_mage(root)
		"golge_suikastci": _build_shadow_assassin(root)
		"sifaci_peri": _build_fairy(root)
		"buz_cadisi": _build_frost_witch(root)
		_: _build_knight(root)

	return root

static func _build_elf_archer(root: Node3D) -> void:
	var green = Color("#059669")
	var dark_leather = Color("#064E3B")
	var skin = Color("#FDE68A")
	var gold = Color("#FBBF24")
	var wood = Color("#78350F")

	# Anatomical Limbs (Capsules)
	root.add_child(_create_capsule(Vector3(-0.2, -0.22, 0), 0.1, 0.55, dark_leather))
	root.add_child(_create_capsule(Vector3(0.2, -0.22, 0), 0.1, 0.55, dark_leather))
	# Tapered Torso
	root.add_child(_create_capsule(Vector3(0, 0.32, 0), 0.18, 0.52, green))
	# Head & Hood
	root.add_child(_create_sphere(Vector3(0, 0.78, 0.02), 0.19, skin))
	root.add_child(_create_prism(Vector3(0, 1.05, -0.05), Vector3(0.25, 0.35, 0.25), dark_leather))
	# Golden Circlet
	root.add_child(_create_cylinder(Vector3(0, 0.82, 0.02), 0.2, 0.03, gold, 0.9, 0.2, true))
	# Recurve Bow (Multi-cylinder curve)
	root.add_child(_create_cylinder(Vector3(-0.48, 0.45, 0.25), 0.03, 0.5, wood, 0.3, 0.7))
	root.add_child(_create_cylinder(Vector3(-0.48, -0.05, 0.25), 0.03, 0.5, wood, 0.3, 0.7))
	# Glowing Arrow
	root.add_child(_create_cylinder(Vector3(-0.12, 0.28, 0.25), 0.015, 0.65, green, 0.8, 0.2, true))

static func _build_black_orc(root: Node3D) -> void:
	var dark_green = Color("#1E3A20")
	var iron = Color("#1F242D")
	var red = Color("#DC2626")
	var bone = Color("#F3F4F6")

	# Heavy Muscular Legs
	root.add_child(_create_capsule(Vector3(-0.28, -0.22, 0), 0.16, 0.6, iron, 0.7, 0.4))
	root.add_child(_create_capsule(Vector3(0.28, -0.22, 0), 0.16, 0.6, iron, 0.7, 0.4))
	# Broad Torso
	root.add_child(_create_capsule(Vector3(0, 0.38, 0), 0.32, 0.65, dark_green))
	root.add_child(_create_cylinder(Vector3(0, 0.38, 0.08), 0.34, 0.45, iron, 0.85, 0.3)) # Iron Cuirass
	# Spiked Shoulders (Spheres + Horns)
	root.add_child(_create_sphere(Vector3(-0.58, 0.62, 0), 0.22, iron, 0.8, 0.3))
	root.add_child(_create_prism(Vector3(-0.62, 0.85, 0), Vector3(0.12, 0.35, 0.12), bone))
	root.add_child(_create_sphere(Vector3(0.58, 0.62, 0), 0.22, iron, 0.8, 0.3))
	root.add_child(_create_prism(Vector3(0.62, 0.85, 0), Vector3(0.12, 0.35, 0.12), bone))
	# Orc Head, Horns & Tusks
	root.add_child(_create_sphere(Vector3(0, 0.88, 0.08), 0.24, dark_green))
	root.add_child(_create_cylinder(Vector3(0, 0.88, 0.31), 0.14, 0.04, red, 0.0, 0.0, true)) # Eyes
	# Greataxe
	root.add_child(_create_cylinder(Vector3(0.75, 0.35, 0.25), 0.05, 1.55, iron, 0.8, 0.3))
	root.add_child(_create_prism(Vector3(1.05, 0.88, 0.25), Vector3(0.48, 0.52, 0.06), iron, 0.9, 0.2))
	root.add_child(_create_prism(Vector3(0.45, 0.88, 0.25), Vector3(0.48, 0.52, 0.06), iron, 0.9, 0.2))

static func _build_knight(root: Node3D) -> void:
	var steel = Color("#CBD5E1")
	var gold = Color("#F59E0B")
	var blue = Color("#1D4ED8")

	root.add_child(_create_capsule(Vector3(-0.22, -0.22, 0), 0.13, 0.6, steel, 0.88, 0.25))
	root.add_child(_create_capsule(Vector3(0.22, -0.22, 0), 0.13, 0.6, steel, 0.88, 0.25))
	root.add_child(_create_capsule(Vector3(0, 0.35, 0), 0.24, 0.6, steel, 0.88, 0.25))
	# Helmet & Plume
	root.add_child(_create_sphere(Vector3(0, 0.82, 0.04), 0.2, steel, 0.88, 0.25))
	root.add_child(_create_prism(Vector3(0, 1.1, -0.06), Vector3(0.08, 0.35, 0.28), blue))
	# Broadsword
	root.add_child(_create_cylinder(Vector3(0.6, 0.28, 0.2), 0.03, 1.35, steel, 0.92, 0.18))
	root.add_child(_create_cylinder(Vector3(0.6, 0.62, 0.2), 0.04, 0.38, gold, 0.9, 0.2)) # Crossguard

static func _build_paladin(root: Node3D) -> void:
	var white = Color("#F8FAFC")
	var gold = Color("#FBBF24")
	var blue = Color("#0284C7")

	root.add_child(_create_torus(Vector3(0, 1.25, 0), 0.36, 0.03, gold, 0.9, 0.2, true)) # Halo
	root.add_child(_create_capsule(Vector3(0, 0.35, 0), 0.28, 0.65, white, 0.8, 0.3))
	root.add_child(_create_sphere(Vector3(0, 0.85, 0.04), 0.22, gold, 0.85, 0.25))
	# Tower Shield
	root.add_child(_create_prism(Vector3(-0.55, 0.3, 0.28), Vector3(0.6, 1.15, 0.08), blue, 0.8, 0.3))
	# Warhammer
	root.add_child(_create_cylinder(Vector3(0.55, 0.3, 0.2), 0.04, 1.15, gold, 0.85, 0.25))
	root.add_child(_create_cylinder(Vector3(0.55, 0.75, 0.2), 0.16, 0.32, white, 0.85, 0.25))

static func _build_robot_tank(root: Node3D) -> void:
	var plate = Color("#334155")
	var dark = Color("#0F172A")
	var cyan = Color("#06B6D4")

	root.add_child(_create_box(Vector3(-0.45, -0.22, 0), Vector3(0.28, 0.38, 1.05), dark, 0.7, 0.4))
	root.add_child(_create_box(Vector3(0.45, -0.22, 0), Vector3(0.28, 0.38, 1.05), dark, 0.7, 0.4))
	root.add_child(_create_cylinder(Vector3(0, 0.25, 0), 0.38, 0.45, plate, 0.85, 0.25))
	root.add_child(_create_cylinder(Vector3(0, 0.28, 0.32), 0.24, 0.06, cyan, 0.0, 0.0, true)) # Visor
	root.add_child(_create_cylinder(Vector3(0, 0.25, 0.75), 0.08, 0.7, dark, 0.85, 0.25)) # Cannon

static func _build_stone_golem(root: Node3D) -> void:
	var rock = Color("#57534E")
	var dark_rock = Color("#292524")
	var gold = Color("#FBBF24")

	root.add_child(_create_sphere(Vector3(0, 0.4, 0), 0.48, rock, 0.1, 0.85))
	root.add_child(_create_sphere(Vector3(-0.75, 0.2, 0.15), 0.26, dark_rock, 0.1, 0.85))
	root.add_child(_create_sphere(Vector3(0.75, 0.2, 0.15), 0.26, dark_rock, 0.1, 0.85))
	root.add_child(_create_sphere(Vector3(0, 0.9, 0.1), 0.24, rock, 0.1, 0.85))
	root.add_child(_create_cylinder(Vector3(0, 0.92, 0.31), 0.12, 0.03, gold, 0.0, 0.0, true))

static func _build_fire_mage(root: Node3D) -> void:
	var red = Color("#B91C1C")
	var orange = Color("#F97316")
	var gold = Color("#FBBF24")

	root.add_child(_create_cylinder(Vector3(0, 0.05, 0), 0.45, 0.85, red))
	root.add_child(_create_sphere(Vector3(0, 0.7, 0.04), 0.18, orange))
	root.add_child(_create_prism(Vector3(0, 1.1, -0.05), Vector3(0.24, 0.5, 0.24), red))
	root.add_child(_create_cylinder(Vector3(0.5, 0.4, 0.15), 0.035, 1.45, Color("#451A03")))
	root.add_child(_create_sphere(Vector3(0.5, 1.15, 0.15), 0.16, gold, 0.0, 0.0, true))

static func _build_shadow_assassin(root: Node3D) -> void:
	var dark = Color("#0F172A")
	var purple = Color("#581C87")
	var violet = Color("#A855F7")

	root.add_child(_create_capsule(Vector3(0, 0.3, 0), 0.2, 0.58, dark, 0.4, 0.6))
	root.add_child(_create_sphere(Vector3(0, 0.75, 0.04), 0.16, dark))
	root.add_child(_create_prism(Vector3(0, 0.25, -0.16), Vector3(0.45, 0.75, 0.04), purple))
	root.add_child(_create_cylinder(Vector3(-0.45, 0.1, 0.15), 0.02, 0.55, violet, 0.9, 0.2, true))
	root.add_child(_create_cylinder(Vector3(0.45, 0.1, 0.15), 0.02, 0.55, violet, 0.9, 0.2, true))

static func _build_fairy(root: Node3D) -> void:
	var pale = Color("#CCFBF1")
	var teal = Color("#0D9488")
	var wing = Color("#2DD4BF")
	var gold = Color("#FDE047")

	root.add_child(_create_capsule(Vector3(0, 0.25, 0), 0.16, 0.55, pale))
	root.add_child(_create_sphere(Vector3(0, 0.68, 0.04), 0.14, Color("#FEF3C7")))
	root.add_child(_create_torus(Vector3(0, 0.78, 0.04), 0.16, 0.02, gold, 0.9, 0.2, true))
	root.add_child(_create_prism(Vector3(-0.48, 0.65, -0.12), Vector3(0.7, 0.7, 0.02), wing, 0.0, 0.0, true))
	root.add_child(_create_prism(Vector3(0.48, 0.65, -0.12), Vector3(0.7, 0.7, 0.02), wing, 0.0, 0.0, true))
	root.add_child(_create_sphere(Vector3(0.4, 0.95, 0.15), 0.12, teal, 0.0, 0.0, true))

static func _build_frost_witch(root: Node3D) -> void:
	var navy = Color("#0369A1")
	var cyan = Color("#38BDF8")
	var ice = Color("#E0F2FE")

	root.add_child(_create_cylinder(Vector3(0, 0.1, 0), 0.42, 0.85, navy))
	root.add_child(_create_sphere(Vector3(0, 0.7, 0.04), 0.17, ice, 0.4, 0.3))
	root.add_child(_create_prism(Vector3(0, 1.05, 0.08), Vector3(0.08, 0.35, 0.08), ice, 0.9, 0.1, true))
	root.add_child(_create_cylinder(Vector3(0.48, 0.45, 0.15), 0.035, 1.4, navy, 0.7, 0.3))
	root.add_child(_create_sphere(Vector3(0.48, 1.18, 0.15), 0.14, cyan, 0.0, 0.0, true))

# Enhanced PBR Primitive Builders
static func _create_capsule(pos: Vector3, radius: float, height: float, col: Color, metallic: float = 0.0, roughness: float = 0.5) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var cap = CapsuleMesh.new()
	cap.radius = radius
	cap.height = height
	cap.radial_segments = 16
	cap.rings = 8
	mi.mesh = cap
	mi.position = pos
	mi.material_override = _create_pbr_material(col, metallic, roughness)
	return mi

static func _create_sphere(pos: Vector3, radius: float, col: Color, metallic: float = 0.0, roughness: float = 0.5, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var sph = SphereMesh.new()
	sph.radius = radius
	sph.height = radius * 2.0
	sph.radial_segments = 16
	sph.rings = 8
	mi.mesh = sph
	mi.position = pos
	mi.material_override = _create_pbr_material(col, metallic, roughness, emissive)
	return mi

static func _create_cylinder(pos: Vector3, radius: float, height: float, col: Color, metallic: float = 0.0, roughness: float = 0.5, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var cyl = CylinderMesh.new()
	cyl.top_radius = radius
	cyl.bottom_radius = radius
	cyl.height = height
	cyl.radial_segments = 16
	mi.mesh = cyl
	mi.position = pos
	mi.material_override = _create_pbr_material(col, metallic, roughness, emissive)
	return mi

static func _create_torus(pos: Vector3, inner_rad: float, outer_rad: float, col: Color, metallic: float = 0.0, roughness: float = 0.5, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var tor = TorusMesh.new()
	tor.inner_radius = inner_rad
	tor.outer_radius = inner_rad + outer_rad
	tor.ring_segments = 16
	tor.pipe_segments = 8
	mi.mesh = tor
	mi.position = pos
	mi.material_override = _create_pbr_material(col, metallic, roughness, emissive)
	return mi

static func _create_box(pos: Vector3, sz: Vector3, col: Color, metallic: float = 0.0, roughness: float = 0.5) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var box = BoxMesh.new()
	box.size = sz
	mi.mesh = box
	mi.position = pos
	mi.material_override = _create_pbr_material(col, metallic, roughness)
	return mi

static func _create_prism(pos: Vector3, sz: Vector3, col: Color, metallic: float = 0.0, roughness: float = 0.5, emissive: bool = false) -> MeshInstance3D:
	var mi = MeshInstance3D.new()
	var prism = PrismMesh.new()
	prism.size = sz
	mi.mesh = prism
	mi.position = pos
	mi.material_override = _create_pbr_material(col, metallic, roughness, emissive)
	return mi

static func _create_pbr_material(col: Color, metallic: float = 0.0, roughness: float = 0.5, emissive: bool = false) -> StandardMaterial3D:
	var mat = StandardMaterial3D.new()
	mat.albedo_color = col
	mat.metallic = metallic
	mat.roughness = roughness
	mat.rim_enabled = true
	mat.rim = 0.45
	if emissive:
		mat.emission_enabled = true
		mat.emission = col
		mat.emission_energy_multiplier = 1.8
	return mat

static func _get_hero_color(hero_id: String) -> Color:
	match hero_id:
		"okcu_elf": return Color("#059669")
		"siyah_ork": return Color("#DC2626")
		"zirhli_insan": return Color("#F59E0B")
		"muhafiz": return Color("#3B82F6")
		"robot_tank": return Color("#06B6D4")
		"tas_golemi": return Color("#78716C")
		"alev_buyucusu": return Color("#F97316")
		"golge_suikastci": return Color("#8B5CF6")
		"sifaci_peri": return Color("#0D9488")
		"buz_cadisi": return Color("#0284C7")
		_: return Color("#FBBF24")
