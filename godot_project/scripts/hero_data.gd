class_name HeroData
extends Resource

enum HeroRole { TANK, WARRIOR, RANGED, MAGE, ASSASSIN, SUPPORT }
enum AttackRange { MELEE, RANGED }

@export var id: String = ""
@export var name: String = ""
@export var title: String = ""
@export var role: HeroRole = HeroRole.WARRIOR
@export var max_hp: int = 1000
@export var attack: int = 100
@export var defense: int = 30
@export var attack_speed: float = 1.2
@export var attack_range: AttackRange = AttackRange.MELEE
@export var skill_name: String = ""
@export var skill_desc: String = ""
@export var color: Color = Color.WHITE
@export var emoji: String = "⚔️"
@export var lore: String = ""

func _init(p_id: String = "", p_name: String = "", p_title: String = "", p_role: HeroRole = HeroRole.WARRIOR,
		p_hp: int = 1000, p_atk: int = 100, p_def: int = 30, p_spd: float = 1.2,
		p_range: AttackRange = AttackRange.MELEE, p_skill: String = "", p_skill_desc: String = "",
		p_color: Color = Color.WHITE, p_emoji: String = "⚔️", p_lore: String = ""):
	id = p_id
	name = p_name
	title = p_title
	role = p_role
	max_hp = p_hp
	attack = p_atk
	defense = p_def
	attack_speed = p_spd
	attack_range = p_range
	skill_name = p_skill
	skill_desc = p_skill_desc
	color = p_color
	emoji = p_emoji
	lore = p_lore
