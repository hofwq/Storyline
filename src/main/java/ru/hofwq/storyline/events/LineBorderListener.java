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
import ru.hofwq.storyline.utils.PlayerLists;
import ru.hofwq.storyline.utils.Utils;

public class LineBorderListener implements Listener {
	Storyline plugin = Storyline.getPlugin();
	private Border border;
	public LineBorderListener() {
        Vector p1 = new Vector(2804, 36, 2925);
        Vector p2 = new Vector(2818, 36, 2936);
        this.border = new Border(p1, p2);
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
        File playerFile = Utils.getPlayerFile(player);
        
        String busSound = "minecraft:my_sounds.stolknovenie";
        String voice_7 = "minecraft:my_sounds.voice7";
        String voice_8 = "minecraft:my_sounds.voice8";
        int blackRoomX = plugin.getConfig().getInt("blackRoom.X");
    	int blackRoomY = plugin.getConfig().getInt("blackRoom.Y");
    	int blackRoomZ = plugin.getConfig().getInt("blackRoom.Z");
    	
    	if(PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    		if (border.contains(player.getLocation()) && (!PlayerLists.playerMessageCount.containsKey(player.getUniqueId()) || PlayerLists.playerMessageCount.get(player.getUniqueId()) < 2)) {
    			Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Я начинаю переходить дорогу и...", 0, voice_7);
    			Utils.sendDelayedMessage(player, "", 0);
    			PlayerLists.playerMessageCount.put(player.getUniqueId(), 2);
    			
    			Utils.givePotionEffect(player, PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0);
    			Utils.givePotionEffect(player, PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 0);
    			
    			Utils.playDelayedSound(player, 4, busSound);
    			
    			PlayerLists.playersInBlackRoom.add(player.getUniqueId());
    			
    			Location blackRoom = new Location(player.getWorld(), blackRoomX, blackRoomY, blackRoomZ);
    			
    			int delaySeconds = 0;
    			int delayTicks = delaySeconds * 20;
    			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
    				@Override
    				public void run() {
    					player.teleport(blackRoom);
    				}
    			}, delayTicks);
    			
    			Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Автобус врезается в меня.", 6, voice_8);
    			Utils.sendDelayedMessage(player, "", 6);
    			
    			Utils.newLevelAnnouncement(player, playerFile, playerConfig, 10);
    		}
    	}
	}
}
