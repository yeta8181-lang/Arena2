extends Node3D

@onready var team1_container: Node3D = $Team1Container
@onready var team2_container: Node3D = $Team2Container
@onready var lbl_timer: Label = $CanvasLayer/TopBar/LblTimer
@onready var lbl_skill_announcement: Label = $CanvasLayer/SkillAnnouncement
@onready var combat_log_box: VBoxContainer = $CanvasLayer/LogPanel/Scroll/VBox
@onready var victory_panel: Panel = $CanvasLayer/VictoryPanel
@onready var lbl_winner: Label = $CanvasLayer/VictoryPanel/VBox/LblWinner

var engine: BattleEngine
var team1_units: Array[CombatHero] = []
var team2_units: Array[CombatHero] = []

var speed_mult: float = 1.0
var is_paused: bool = false

# Default squads
var team1_hero_ids = ["tas_golemi", "muhafiz", "okcu_elf", "alev_buyucusu"]
var team2_hero_ids = ["siyah_ork", "zirhli_insan", "robot_tank", "sifaci_peri"]

func _ready() -> void:
	start_battle()

func start_battle() -> void:
	victory_panel.visible = false
	lbl_skill_announcement.visible = false

	# Clear previous units
	for c in team1_container.get_children(): c.queue_free()
	for c in team2_container.get_children(): c.queue_free()
	for c in combat_log_box.get_children(): c.queue_free()

	team1_units.clear()
	team2_units.clear()

	# Spawn Team 1 (Blue - Z: 1.5)
	for i in range(team1_hero_ids.size()):
		var hid = team1_hero_ids[i]
		var data = HeroRegistry.get_hero(hid)
		var hero_unit = CombatHero.new()
		hero_unit.setup(data, CombatHero.Team.TEAM_1, i)

		# Position: 0,1 Frontline (Z=0.8), 2,3 Backline (Z=2.0)
		var is_front = (i < 2)
		var x_pos = -0.9 if (i % 2 == 0) else 0.9
		var z_pos = 0.8 if is_front else 2.0
		hero_unit.position = Vector3(x_pos, 0, z_pos)

		var mesh = Hero3DMeshBuilder.build_hero_3d(hid)
		hero_unit.add_child(mesh)
		team1_container.add_child(hero_unit)
		team1_units.append(hero_unit)

	# Spawn Team 2 (Red - Z: -1.5, facing Z+)
	for i in range(team2_hero_ids.size()):
		var hid = team2_hero_ids[i]
		var data = HeroRegistry.get_hero(hid)
		var hero_unit = CombatHero.new()
		hero_unit.setup(data, CombatHero.Team.TEAM_2, i)

		var is_front = (i < 2)
		var x_pos = -0.9 if (i % 2 == 0) else 0.9
		var z_pos = -0.8 if is_front else -2.0
		hero_unit.position = Vector3(x_pos, 0, z_pos)
		hero_unit.rotation_degrees.y = 180

		var mesh = Hero3DMeshBuilder.build_hero_3d(hid)
		hero_unit.add_child(mesh)
		team2_container.add_child(hero_unit)
		team2_units.append(hero_unit)

	engine = BattleEngine.new()
	engine.event_logged.connect(_on_event_logged)
	engine.battle_finished.connect(_on_battle_finished)
	engine.setup_battle(team1_units, team2_units)

func _process(delta: float) -> void:
	if engine == null or is_paused or engine.is_finished:
		return

	var d = delta * speed_mult
	engine.tick(d)

	var secs = int(engine.elapsed_time)
	lbl_timer.text = "%02d:%02d" % [secs / 60, secs % 60]

	# Update unit rotation and death appearance
	for u in engine.get_all_heroes():
		if not u.is_alive and u.visible:
			u.rotation_degrees.z = 90
			u.position.y = -0.3

func _on_event_logged(msg: String, col: Color) -> void:
	var lbl = Label.new()
	lbl.text = msg
	lbl.add_theme_color_override("font_color", col)
	lbl.add_theme_font_size_override("font_size", 14)
	combat_log_box.add_child(lbl)

	if msg.contains("yeteneğini"):
		_show_skill_banner(msg, col)

func _show_skill_banner(msg: String, col: Color) -> void:
	lbl_skill_announcement.text = "⚡ " + msg
	lbl_skill_announcement.add_theme_color_override("font_color", col)
	lbl_skill_announcement.visible = true
	var timer = get_tree().create_timer(1.8)
	timer.timeout.connect(func(): lbl_skill_announcement.visible = false)

func _on_battle_finished(winner_team: int) -> void:
	var team_name = "🔵 Mavi Birlik (1. Oyuncu)" if winner_team == 0 else "🔴 Kızıl Lejyon (2. Oyuncu)"
	lbl_winner.text = "🏆 ZAFER!\n%s KAZANDI!" % team_name
	victory_panel.visible = true

func _on_btn_speed_pressed() -> void:
	if speed_mult == 1.0:
		speed_mult = 2.0
	elif speed_mult == 2.0:
		speed_mult = 4.0
	else:
		speed_mult = 1.0
	$CanvasLayer/TopBar/BtnSpeed.text = "%.0fx" % speed_mult

func _on_btn_pause_pressed() -> void:
	is_paused = not is_paused
	$CanvasLayer/TopBar/BtnPause.text = "▶" if is_paused else "⏸"

func _on_btn_rematch_pressed() -> void:
	start_battle()

func _on_btn_menu_pressed() -> void:
	get_tree().change_scene_to_file("res://scenes/main_menu.tscn")
