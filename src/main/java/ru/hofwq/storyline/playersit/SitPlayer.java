package ru.hofwq.storyline.playersit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import ru.hofwq.storyline.Storyline;

public class SitPlayer{
	private static Player player;
	
	private static Storyline storyline;
	
	public SitPlayer(Player player) {
		SitPlayer.player = player;
		storyline = (Storyline)JavaPlugin.getPlugin(Storyline.class);
	}
	
	public Player getBukkitPlayer() {
		return player;
	}
	
	@SuppressWarnings("deprecation")
	public void setSitting(boolean arg) {
		if (arg && !isSitting()) {
			Location location = player.getLocation();
			ArmorStand seat = (ArmorStand)location.getWorld().spawn(location.clone().subtract(0.0D, 1.7D, 0.0D), ArmorStand.class);
			seat.setGravity(false);
			seat.setVisible(false);
			PlayerSitEvent playerSitEvent = new PlayerSitEvent(player, seat);
			Bukkit.getPluginManager().callEvent((Event)playerSitEvent);
			if (playerSitEvent.isCancelled()) {
				seat.remove();
				return;
			} 
			seat.setPassenger((Entity)player);
			storyline.getSeats().put(player.getUniqueId(), seat);
		} else if (!arg && isSitting()) {
	        ArmorStand seat = storyline.getSeats().get(player.getUniqueId());
	        PlayerStopSittingEvent playerStopSittingEvent = new PlayerStopSittingEvent(player, seat);
	        Bukkit.getPluginManager().callEvent((Event)playerStopSittingEvent);
	        storyline.getSeats().remove(player.getUniqueId());
	        player.eject();
	        if(!player.isDead()) {
	            float pitch = player.getLocation().getPitch();
	            float yaw = player.getLocation().getYaw();

	            player.teleport(seat.getLocation().clone().add(0.0D, 1.7D, 0.0D));

	            Location loc = player.getLocation();
	            loc.setPitch(pitch);
	            loc.setYaw(yaw);
	            player.teleport(loc);
	        }
	        seat.remove();
	    } 
	}
	
	public boolean isSitting() {
		return storyline.getSeats().containsKey(player.getUniqueId());
	}
}
