class_name CombatHero
extends Node3D

enum Team { TEAM_1, TEAM_2 }

signal damaged(amount: int, is_crit: bool, is_skill: bool)
signal healed(amount: int)
signal died()
signal skill_cast(skill_name: String)

@export var data: HeroData
@export var team: Team = Team.TEAM_1
@export var slot_index: int = 0

var current_hp: int = 1000
var max_hp: int = 1000
var current_mana: int = 20
var max_mana: int = 100
var current_shield: int = 0

var attack_cooldown: float = 0.0
var is_alive: bool = true
var is_stunned: bool = false
var stun_timer: float = 0.0
var burn_timer: float = 0.0

var total_damage_dealt: int = 0
var total_damage_taken: int = 0
var total_healing_done: int = 0

var visual_node: Node3D = null

func setup(p_data: HeroData, p_team: Team, p_slot: int) -> void:
	data = p_data
	team = p_team
	slot_index = p_slot
	max_hp = data.max_hp
	current_hp = max_hp
	current_mana = 20
	attack_cooldown = randf_range(0.0, 0.4)
	is_alive = true

func is_frontline() -> bool:
	return slot_index < 2

func update_tick(delta: float) -> void:
	if not is_alive:
		return

	if stun_timer > 0.0:
		stun_timer -= delta
		is_stunned = (stun_timer > 0.0)

	if burn_timer > 0.0:
		burn_timer -= delta
		take_damage(int(30 * delta), false, false, "Yanma")

	if is_stunned:
		return

	attack_cooldown -= delta

func take_damage(raw_amount: int, is_crit: bool = false, is_skill: bool = false, _tag: String = "") -> int:
	if not is_alive:
		return 0

	var defense = data.defense
	var reduction = float(defense) / float(defense + 100)
	var final_dmg = maxi(10, int(raw_amount * (1.0 - reduction)))

	var left_dmg = final_dmg
	if current_shield > 0:
		if current_shield >= left_dmg:
			current_shield -= left_dmg
			left_dmg = 0
		else:
			left_dmg -= current_shield
			current_shield = 0

	current_hp -= left_dmg
	total_damage_taken += final_dmg
	current_mana = mini(max_mana, current_mana + 10)

	damaged.emit(final_dmg, is_crit, is_skill)

	if current_hp <= 0:
		current_hp = 0
		is_alive = false
		died.emit()

	return final_dmg

func heal(amount: int) -> void:
	if not is_alive:
		return
	var missing = max_hp - current_hp
	var actual = mini(amount, missing)
	current_hp += actual
	total_healing_done += actual
	healed.emit(actual)
