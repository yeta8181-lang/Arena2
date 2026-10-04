extends Control

func _on_play_2p_pressed() -> void:
	get_tree().change_scene_to_file("res://scenes/battle_arena.tscn")

func _on_view_3d_pressed() -> void:
	get_tree().change_scene_to_file("res://scenes/hero_viewer_3d.tscn")

func _on_quit_pressed() -> void:
	get_tree().quit()
