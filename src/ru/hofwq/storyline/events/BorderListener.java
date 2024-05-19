package ru.hofwq.storyline.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;
import ru.hofwq.storyline.utils.PlayerLists;

public class BorderListener implements Listener{
	Storyline plugin = Storyline.getPlugin();
	private Border border;
    private Location enterLocation;
    int enterLocationX = plugin.getConfig().getInt("enterLocation.X");
	int enterLocationY = plugin.getConfig().getInt("enterLocation.Y");
	int enterLocationZ = plugin.getConfig().getInt("enterLocation.Z");
	
	public BorderListener() {
        Vector p1 = new Vector(2804, 36, 2928);
        Vector p2 = new Vector(2890, 82, 3045);
        this.border = new Border(p1, p2);
        this.enterLocation = new Location(Bukkit.getWorld("world"), enterLocationX, enterLocationY, enterLocationZ);
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		if (!border.contains(player.getLocation()) && PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
            player.teleport(enterLocation);
            player.sendMessage(ChatColor.RED + "Нельзя, мне нужно до пешеходного перехода.");
        }
	}
}
