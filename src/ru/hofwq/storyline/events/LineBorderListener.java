package ru.hofwq.storyline.events;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;

public class LineBorderListener implements Listener {
	Storyline plugin = Storyline.getPlugin();
	private Border border;
	
	public LineBorderListener() {
        Vector p1 = new Vector(2817, 36, 2936);
        Vector p2 = new Vector(2817, 67, 3036);
        this.border = new Border(p1, p2);
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = EventListener.getPlayerConfiguration(player);
        File playerFile = EventListener.getPlayerFile(player);
        
        int blackRoomX = plugin.getConfig().getInt("blackRoom.X");
    	int blackRoomY = plugin.getConfig().getInt("blackRoom.Y");
    	int blackRoomZ = plugin.getConfig().getInt("blackRoom.Z");
    	
    	if(EventListener.playersToGoOutside.contains(player.getUniqueId())) {
    		if (border.contains(player.getLocation()) && (!EventListener.playerMessageCount.containsKey(player.getUniqueId()) || EventListener.playerMessageCount.get(player.getUniqueId()) < 2)) {
    			EventListener.sendDelayedMessage(player, ChatColor.YELLOW + "Я начинаю переходить дорогу и...", 0);
    			EventListener.sendDelayedMessage(player, "", 0);
    			EventListener.playerMessageCount.put(player.getUniqueId(), 2);
    			
    			EventListener.givePotionEffect(player, PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0);
    			EventListener.givePotionEffect(player, PotionEffectType.SLOW, Integer.MAX_VALUE, 0);
    			
    			//playsound avtobus
    			
    			EventListener.playersInBlackRoom.add(player.getUniqueId());
    			
    			Location blackRoom = new Location(player.getWorld(), blackRoomX, blackRoomY, blackRoomZ);
    			
    			int delaySeconds = 0;
    			int delayTicks = delaySeconds * 20;
    			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
    				@Override
    				public void run() {
    					player.teleport(blackRoom);
    				}
    			}, delayTicks);
    			
    			
    			EventListener.sendDelayedMessage(player, ChatColor.YELLOW + "Автобус врезается в меня.", 3);
    			EventListener.sendDelayedMessage(player, "", 3);
    			
    			EventListener.newLevelAnnouncement(player, playerFile, playerConfig, 6);
    			
    		}
    	}
	}
}
