package ru.hofwq.storyline.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;
import ru.hofwq.storyline.playersit.SitPlayer;

public class Utils {
	public static HashMap<UUID, ItemStack[]> playerInventories = new HashMap<>();
	public static Storyline plugin = Storyline.getPlugin();
	public static List<String> allowedCmds = Arrays.asList("/register", "/reg", "/login", "/l");
	private static final Map<UUID, BukkitTask> tasks = new HashMap<>();
	private static boolean isClosedDoor = false;
	public static boolean isNewLevel = false;
	public static int semyonRoomX = plugin.getConfig().getInt("semyonRoom.X");
	public static int semyonRoomY = plugin.getConfig().getInt("semyonRoom.Y");
	public static int semyonRoomZ = plugin.getConfig().getInt("semyonRoom.Z");
	
	public static int blackRoomX = plugin.getConfig().getInt("blackRoom.X");
	public static int blackRoomY = plugin.getConfig().getInt("blackRoom.Y");
	public static int blackRoomZ = plugin.getConfig().getInt("blackRoom.Z");

	public static int keyLocationX = plugin.getConfig().getInt("keyLocation.X");
	public static int keyLocationY = plugin.getConfig().getInt("keyLocation.Y");
	public static int keyLocationZ = plugin.getConfig().getInt("keyLocation.Z");
	
	public static int enterLocationX = plugin.getConfig().getInt("enterLocation.X");
	public static int enterLocationY = plugin.getConfig().getInt("enterLocation.Y");
	public static int enterLocationZ = plugin.getConfig().getInt("enterLocation.Z");
	
	public static void restoreInventory(Player player) {
	    UUID playerID = player.getUniqueId();
	    if (playerInventories.containsKey(playerID)) {
	        ItemStack[] inventoryContents = playerInventories.get(playerID);
	        player.getInventory().clear();
	        player.getInventory().setContents(inventoryContents);
	        playerInventories.remove(playerID);
	    }
	}
	
	public static void saveInventory(Player player) {
        UUID playerID = player.getUniqueId();
        ItemStack[] inventoryContents = player.getInventory().getContents();
        playerInventories.put(playerID, inventoryContents);
        player.getInventory().clear();
    }
	
	public static void sendDelayedMessage(Player player, String message, int delaySeconds) {
        int delayTicks = delaySeconds * 20;
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(message), delayTicks);
    }
	
	public static void sendDelayedMessage(Player player, String message, int delaySeconds, String sound) {
		int delayTicks = delaySeconds * 20;
		Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), sound, 1L, 1L), delayTicks);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(message), delayTicks);
	}
	
	public static void sendDelayedMessage(Player player, String message, int delaySeconds, Sound sound) {
		int delayTicks = delaySeconds * 20;
		Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), sound, 1L, 1L), delayTicks);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(message), delayTicks);
	}
	
	public static void playDelayedSound(Player player, int delaySeconds, String sound) {
		int delayTicks = delaySeconds * 20;
		Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), sound, 1L, 1L), delayTicks);
	}
	
	public static void givePotionEffect(Player player, PotionEffectType effect, int time, int delaySeconds) {
	    int delayTicks = delaySeconds * 20;
	    
	    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
	        @Override
	        public void run() {
	            player.addPotionEffect(new PotionEffect(effect, time, 127, false, false));
	        }
	    }, delayTicks);
	}
	
	public static void setPlayerSit(Player player) {
		SitPlayer SitPlayer = new SitPlayer(player);
		
		if (!SitPlayer.isSitting()) {
	        SitPlayer.setSitting(true);
	    }
	}
	
	public static void setPlayerStand(Player player) {
		SitPlayer SitPlayer = new SitPlayer(player);
		
		if (SitPlayer.isSitting()) {
	        SitPlayer.setSitting(false);
	    }
	}
	
	public static File getPlayerFile(Player player) {
		File playerFolder = new File(plugin.getDataFolder(), "Players");
		File playerFile = new File(playerFolder, player.getUniqueId() + ".yml");
		
		return playerFile;
	}
	
	public static FileConfiguration getPlayerConfiguration(Player player) {
		File playerFolder = new File(plugin.getDataFolder(), "Players");
    	File playerFile = new File(playerFolder, player.getUniqueId() + ".yml");
    	FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    	
    	return playerConfig;
	}
	
	public static void saveConfig(File playerFile, FileConfiguration playerConfig) {
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    public static void allowPlayerWalk(Player player, int delaySeconds) {
        int delayTicks = delaySeconds * 20;
        
        UUID playerId = player.getUniqueId();
        if (tasks.containsKey(playerId)) {
            tasks.get(playerId).cancel();
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                PlayerLists.playersToGoOutside.add(playerId);
                tasks.remove(playerId);
                Utils.setPlayerStand(player);
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.2f);
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }.runTaskLater(plugin, delayTicks);

        tasks.put(playerId, task);
    }
	
	public static void newLevelAnnouncement(Player player, File playerFile, FileConfiguration playerConfig, int delaySeconds) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(playerConfig.getInt("storylineLevel") == 0) {
					resetPlayerState(player);
					playerConfig.set("storylineLevel", 1);
					Utils.saveConfig(playerFile, playerConfig);
					
					Location busStop = new Location(player.getWorld(), 3409, 42, 2383);
					
					player.teleport(busStop);
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
					isNewLevel = true;
					Utils.sendTitleToPlayer(player, ChatColor.GREEN + "Вы завершили главу:", ChatColor.GRAY + "Пролог", 20, 40, 20);
				}
			}
		}.runTaskLater(plugin, delaySeconds * 20);
	}
	
	public static void resetPlayerState(Player player) {
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(plugin, player);
				player.showPlayer(plugin, p);
			}
			
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.SLOWNESS);
			player.resetPlayerTime();
			player.setFoodLevel(20);
			
			Utils.restoreInventory(player);
			
			PlayerLists.playersToGoOutside.remove(player.getUniqueId());
			PlayerLists.playersInBlackRoom.remove(player.getUniqueId());
			PlayerLists.playersWhoTakedKeys.remove(player.getUniqueId());
			PlayerLists.playerMessageCount.remove(player.getUniqueId());
			PlayerLists.playersWithLoadedPack.remove(player.getUniqueId());
			
	        plugin.log.info("Status has been successfully reset for " + player.getName() + ". Caused by: Disabling Plugin / Player Leave / Player Reached New Level");
		}
	}
	
	public static void openDoorForPlayer(Player player, Block door) {
		Openable doorData = (Openable) door.getBlockData();
		if(!doorData.isOpen()) {
			doorData.setOpen(true);
			door.setBlockData(doorData);
			
			WrappedBlockData wrappedBlockData = WrappedBlockData.createData(door.getBlockData());
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
			packet.getBlockPositionModifier().write(0, new BlockPosition(door.getLocation().toVector()));
			packet.getBlockData().write(0, wrappedBlockData);
			
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
				
				switch(door.getType()){
				case IRON_DOOR:
					player.playSound(door.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1L, 1L);
					break;
				default:
					player.playSound(door.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1L, 1L);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void closeDoorForPlayer(Player player, Block door) {
		Openable doorData = (Openable) door.getBlockData();
		
		if (doorData.isOpen()) {
			doorData.setOpen(false);
			door.setBlockData(doorData);
			
			WrappedBlockData wrappedBlockData = WrappedBlockData.createData(door.getBlockData());
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
			packet.getBlockPositionModifier().write(0, new BlockPosition(door.getLocation().toVector()));
			packet.getBlockData().write(0, wrappedBlockData);
			
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
				
				switch(door.getType()){
				case IRON_DOOR:
					player.playSound(door.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1L, 1L);
					break;
				default:
					player.playSound(door.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1L, 1L);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!isClosedDoor) {
				plugin.log.info("Closed door using packets for " + player.getName());
				isClosedDoor = true;
			}
		}
	}
	
	public static void sendTitleToPlayer(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
		player.sendTitle(title, subtitle, fadein, stay, fadeout);
	}
}
