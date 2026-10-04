extends Node3D

@onready var hero_pivot: Node3D = $HeroPivot
@onready var lbl_name: Label = $CanvasLayer/Panel/VBox/LblName
@onready var lbl_role: Label = $CanvasLayer/Panel/VBox/LblRole
@onready var lbl_stats: Label = $CanvasLayer/Panel/VBox/LblStats
@onready var lbl_skill: Label = $CanvasLayer/Panel/VBox/LblSkill

var hero_keys = [
	"okcu_elf", "siyah_ork", "zirhli_insan", "muhafiz", "robot_tank",
	"tas_golemi", "alev_buyucusu", "golge_suikastci", "sifaci_peri", "buz_cadisi"
]
var current_index: int = 0
var current_mesh: Node3D = null

var is_dragging: bool = false
var drag_start: Vector2 = Vector2.ZERO

func _ready() -> void:
	load_hero(0)

func load_hero(idx: int) -> void:
	current_index = posmod(idx, hero_keys.size())
	var hero_id = hero_keys[current_index]

	if current_mesh != null:
		current_mesh.queue_free()

	current_mesh = Hero3DMeshBuilder.build_hero_3d(hero_id)
	hero_pivot.add_child(current_mesh)

	var data: HeroData = HeroRegistry.get_hero(hero_id)
	lbl_name.text = "%s - %s" % [data.emoji, data.name]
	lbl_role.text = "%s  |  %s" % [data.title, _role_to_string(data.role)]
	lbl_stats.text = "❤️ Can: %d   ⚔️ Saldırı: %d   🛡️ Zırh: %d   ⚡ Hız: %.1fs" % [data.max_hp, data.attack, data.defense, data.attack_speed]
	lbl_skill.text = "✨ %s: %s" % [data.skill_name, data.skill_desc]

func _process(delta: float) -> void:
	if not is_dragging:
		hero_pivot.rotate_y(delta * 0.8)

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventMouseButton:
		if event.button_index == MOUSE_BUTTON_LEFT:
			is_dragging = event.pressed
			drag_start = event.position
	elif event is InputEventMouseMotion and is_dragging:
		var delta_x = event.relative.x * 0.01
		hero_pivot.rotate_y(delta_x)

func _on_btn_next_pressed() -> void:
	load_hero(current_index + 1)

func _on_btn_prev_pressed() -> void:
	load_hero(current_index - 1)

func _on_btn_back_pressed() -> void:
	get_tree().change_scene_to_file("res://scenes/main_menu.tscn")

func _role_to_string(r: HeroData.HeroRole) -> String:
	match r:
		HeroData.HeroRole.TANK: return "Tank"
		HeroData.HeroRole.WARRIOR: return "Savaşçı"
		HeroData.HeroRole.RANGED: return "Menzilli Nişancı"
		HeroData.HeroRole.MAGE: return "Büyücü"
		HeroData.HeroRole.ASSASSIN: return "Suikastçı"
		HeroData.HeroRole.SUPPORT: return "Destek / Şifacı"
		_: return "Savaşçı"
