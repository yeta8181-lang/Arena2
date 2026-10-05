class_name BattleEngine
extends RefCounted

const CombatHeroScript = preload("res://scripts/combat_hero.gd")

signal event_logged(message: String, color: Color)
signal battle_finished(winner_team: int)

var team1_heroes: Array = []
var team2_heroes: Array = []

var elapsed_time: float = 0.0
var is_finished: bool = false
var winner: int = -1

func setup_battle(t1_nodes: Array, t2_nodes: Array) -> void:
	team1_heroes = t1_nodes
	team2_heroes = t2_nodes
	elapsed_time = 0.0
	is_finished = false
	winner = -1

func _is_unit_alive(u) -> bool:
	if u == null:
		return false
	return bool(u.get("is_alive"))

func _is_unit_frontline(u) -> bool:
	if u == null:
		return false
	return int(u.get("slot_index")) < 2

func tick(delta: float) -> void:
	if is_finished:
		return

	elapsed_time += delta

	for h in get_all_heroes():
		h.update_tick(delta)

	for h in get_all_alive():
		if not _is_unit_alive(h) or bool(h.get("is_stunned")):
			continue

		# Cast skill when mana full
		if h.current_mana >= h.max_mana:
			cast_skill(h)
			check_win_condition()
			if is_finished:
				return
			continue

		# Normal attack
		if h.attack_cooldown <= 0.0:
			perform_normal_attack(h)
			h.attack_cooldown = h.data.attack_speed
			check_win_condition()
			if is_finished:
				return

	check_win_condition()

func get_all_heroes() -> Array:
	var list: Array = []
	list.append_array(team1_heroes)
	list.append_array(team2_heroes)
	return list

func get_all_alive() -> Array:
	var list: Array = []
	for h in get_all_heroes():
		if _is_unit_alive(h):
			list.append(h)
	return list

func find_target_for(attacker) -> Object:
	var enemy_team = team2_heroes if attacker.team == 0 else team1_heroes
	var alive_enemies: Array = []
	for e in enemy_team:
		if _is_unit_alive(e):
			alive_enemies.append(e)

	if alive_enemies.is_empty():
		return null

	# Assassin attacks backline squishies
	if attacker.data.role == HeroData.HeroRole.ASSASSIN:
		var backline: Array = []
		for e in alive_enemies:
			if not _is_unit_frontline(e):
				backline.append(e)
		if not backline.is_empty():
			backline.sort_custom(func(a, b): return a.current_hp < b.current_hp)
			return backline[0]

	# Standard: Frontline first
	var frontliners: Array = []
	for e in alive_enemies:
		if _is_unit_frontline(e):
			frontliners.append(e)
	if not frontliners.is_empty():
		frontliners.sort_custom(func(a, b): return a.current_hp < b.current_hp)
		return frontliners[0]

	alive_enemies.sort_custom(func(a, b): return a.current_hp < b.current_hp)
	return alive_enemies[0]

func perform_normal_attack(attacker) -> void:
	var target = find_target_for(attacker)
	if target == null:
		return

	var is_crit = (randf() < (0.35 if attacker.data.role == HeroData.HeroRole.ASSASSIN else 0.15))
	var mult = 1.8 if is_crit else 1.0
	var dmg = int(attacker.data.attack * mult * randf_range(0.95, 1.05))

	var dealt = target.take_damage(dmg, is_crit, false)
	attacker.total_damage_dealt += dealt
	attacker.current_mana = mini(attacker.max_mana, attacker.current_mana + 18)

func cast_skill(caster) -> void:
	caster.current_mana = 0
	caster.skill_cast.emit(caster.data.skill_name)
	event_logged.emit("%s [%s] yeteneğini kullandı!" % [caster.data.name, caster.data.skill_name], caster.data.color)

	var enemy_team = team2_heroes if caster.team == 0 else team1_heroes
	var ally_team = team1_heroes if caster.team == 0 else team2_heroes

	match caster.data.id:
		"okcu_elf":
			for e in enemy_team:
				if _is_unit_alive(e):
					e.take_damage(180, false, true)
		"siyah_ork":
			var target = find_target_for(caster)
			if target != null:
				var dealt = target.take_damage(270, true, true)
				caster.heal(int(dealt * 0.45))
		"zirhli_insan":
			var target = find_target_for(caster)
			if target != null:
				target.take_damage(230, false, true)
				target.stun_timer = 1.5
		"muhafiz":
			for a in ally_team:
				if _is_unit_alive(a):
					a.current_shield += 260
		"robot_tank":
			for e in enemy_team:
				if _is_unit_alive(e) and _is_unit_frontline(e):
					e.take_damage(150, false, true)
					e.burn_timer = 3.0
		"tas_golemi":
			for e in enemy_team:
				if _is_unit_alive(e) and _is_unit_frontline(e):
					e.take_damage(160, false, true)
					e.stun_timer = 2.0
		"alev_buyucusu":
			var max_hp_enemy = null
			for e in enemy_team:
				if _is_unit_alive(e):
					if max_hp_enemy == null or e.current_hp > max_hp_enemy.current_hp:
						max_hp_enemy = e
			if max_hp_enemy != null:
				max_hp_enemy.take_damage(330, true, true)
				max_hp_enemy.burn_timer = 3.5
		"golge_suikastci":
			var target = find_target_for(caster)
			if target != null:
				target.take_damage(360, true, true)
		"sifaci_peri":
			for a in ally_team:
				if _is_unit_alive(a):
					a.heal(290)
		"buz_cadisi":
			for e in enemy_team:
				if _is_unit_alive(e):
					e.take_damage(170, false, true)
					e.stun_timer = 1.8

func check_win_condition() -> void:
	if is_finished:
		return

	var t1_alive = false
	for h in team1_heroes:
		if _is_unit_alive(h):
			t1_alive = true
			break

	var t2_alive = false
	for h in team2_heroes:
		if _is_unit_alive(h):
			t2_alive = true
			break

	if not t1_alive or not t2_alive:
		is_finished = true
		winner = 0 if t1_alive else 1
		battle_finished.emit(winner)
