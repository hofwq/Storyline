package ru.hofwq.storyline.playersit;

import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import ru.hofwq.storyline.Storyline;

public class SitArmorStand {
	private Storyline storyline;
	
	private ArmorStand armorstand;
	
	public SitArmorStand(ArmorStand armorStand) {
		this.armorstand = armorStand;
		this.storyline = (Storyline)JavaPlugin.getPlugin(Storyline.class);
	}
	
	public boolean isSeat() {
		return this.storyline.getSeats().containsValue(this.armorstand);
	}
	
	public ArmorStand getBukkitArmorStand() {
		return this.armorstand;
	}
}
