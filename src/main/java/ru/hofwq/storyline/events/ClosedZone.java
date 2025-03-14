package ru.hofwq.storyline.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;
import ru.hofwq.storyline.utils.PlayerLists;

public class ClosedZone implements Listener{
	Storyline plugin = Storyline.getPlugin();
	private Border border;
    private List<UUID> alreadyTiltedBack = new ArrayList<>();
	
	public ClosedZone() {
        Vector p1 = new Vector(2812, 36, 2935);
        Vector p2 = new Vector(2821, 38, 3045);
        this.border = new Border(p1, p2);
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		
		Location firstLocation = new Location(world, 2812, 36, 2935);
		Location secondLocation = new Location(world, 2821, 38, 3045);
		
		if(border.contains(player.getLocation()) && PlayerLists.playersToGoOutside.contains(player.getUniqueId()) 
				&& !alreadyTiltedBack.contains(player.getUniqueId())) {
			if(isPlayerFacingAway(player, border.getCenter(firstLocation, secondLocation))) {
				knockbackPlayer(player);
			} else if(!isPlayerFacingAway(player, border.getCenter(firstLocation, secondLocation))) {
				knockbackPlayer(player);
			}
		}
	}
	
	private void removeFromList(Player player) {
		if(alreadyTiltedBack.contains(player.getUniqueId())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					alreadyTiltedBack.remove(player.getUniqueId());
				}
			}.runTaskLater(plugin, 5L);
		}
	}
	
	private void knockbackPlayer(Player player) {
		Vector direction = new Vector(1, -1, 0);
		player.setVelocity(direction);
	    alreadyTiltedBack.add(player.getUniqueId());
	    player.sendMessage(ChatColor.RED + "Нельзя, мне нужно до пешеходного перехода.");
	    removeFromList(player);
	}

	private boolean isPlayerFacingAway(Player player, Location location) {
        Vector toLocation = location.toVector().subtract(player.getLocation().toVector());
        Vector direction = player.getLocation().getDirection();

        double angle = toLocation.angle(direction);

        return Math.abs(angle) > Math.PI / 2;
    }
}
