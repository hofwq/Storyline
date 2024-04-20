package ru.hofwq.storyline.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;

public class ClosedZone implements Listener{
	Storyline plugin = Storyline.getPlugin();
	private Border border;
    
	public ClosedZone() {
        Vector p1 = new Vector(2812, 36, 2935);
        Vector p2 = new Vector(2821, 38, 3045);
        this.border = new Border(p1, p2);
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Vector playerDirection = player.getLocation().getDirection();
		
		if(!border.contains(player.getLocation()) && EventListener.playersToGoOutside.contains(player.getUniqueId())) {
			playerDirection.multiply(-2);
			player.setVelocity(playerDirection);
			player.sendMessage(ChatColor.RED + "Нельзя, мне нужно до пешеходного перехода.");
		}
	}
}
