package ru.hofwq.storyline.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;

public class EventListener implements Listener{
	public static Storyline plugin = Storyline.getPlugin();
	public static List<UUID> playersToGoOutside = new ArrayList<>();
	public static List<UUID> playersInBlackRoom = new ArrayList<>();
	public static List<UUID> playersWhoTakedKeys = new ArrayList<>();
	public static List<UUID> playersWithLoadedPack = new ArrayList<>();
	public static HashMap<UUID, Integer> playerMessageCount = new HashMap<>();
	public static HashMap<UUID, ItemStack[]> playerInventories = new HashMap<>();
	public static HashMap<Block, Boolean> doorStates = new HashMap<>();

	int semyonRoomX = plugin.getConfig().getInt("semyonRoom.X");
	int semyonRoomY = plugin.getConfig().getInt("semyonRoom.Y");
	int semyonRoomZ = plugin.getConfig().getInt("semyonRoom.Z");
	
	int blackRoomX = plugin.getConfig().getInt("blackRoom.X");
	int blackRoomY = plugin.getConfig().getInt("blackRoom.Y");
	int blackRoomZ = plugin.getConfig().getInt("blackRoom.Z");

	int keyLocationX = plugin.getConfig().getInt("keyLocation.X");
	int keyLocationY = plugin.getConfig().getInt("keyLocation.Y");
	int keyLocationZ = plugin.getConfig().getInt("keyLocation.Z");
	
	int enterLocationX = plugin.getConfig().getInt("enterLocation.X");
	int enterLocationY = plugin.getConfig().getInt("enterLocation.Y");
	int enterLocationZ = plugin.getConfig().getInt("enterLocation.Z");
	
	@EventHandler
	public void onLogin(LoginEvent e) {
		Player player = e.getPlayer();
		
		if (!AuthMeApi.getInstance().isAuthenticated(player)) {
        	return;
        }
        
        FileConfiguration playerConfig = getPlayerConfiguration(player);
        File playerFile = getPlayerFile(player);
        
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
			playerConfig.set("storylineLevel", 0);
			saveConfig(playerFile, playerConfig);
        }
        
        Location semyonRoom = new Location(player.getWorld(), semyonRoomX + 0.5, semyonRoomY + 0.5, semyonRoomZ);

        float yaw = 90.8f;
        float pitch = 22.1f;
        
        semyonRoom.setYaw(yaw);
        semyonRoom.setPitch(pitch);
    
        if(playerConfig.getInt("storylineLevel") == 0) {
        	int delaySeconds = 2;
        	int delayTicks = delaySeconds * 20;
        	Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
        		@Override
        		public void run() {
        			saveInventory(player);
        			player.teleport(semyonRoom); //2877 58 3002
        			player.setPlayerTime(18000, false);
        			
        			for(Player p : Bukkit.getOnlinePlayers()) {
        				p.hidePlayer(plugin, player);
        				player.hidePlayer(plugin, p);
        			}
        			
        			sendDelayedMessage(player, ChatColor.YELLOW + "Я сидел дома шестые сутки, смотрел в монитор, читая новые треды на форуме несколько часов подряд.", 0);
        			sendDelayedMessage(player, "", 0);
        			sendDelayedMessage(player, ChatColor.YELLOW + "Сейчас все обсуждали новое аниме под названием \"Советский мир\", в котором действия происходят в мире, в котором ещё не распался советский союз, в который попадает главный герой.", 5);
        			sendDelayedMessage(player, "", 5);
        			sendDelayedMessage(player, ChatColor.YELLOW + "Мне не особо нравились аниме подобного жанра, в которых гг попадает в альтернативные миры, ведь того, что происходит в этих аниме, никак не может произойти в жизни...", 11);
        			sendDelayedMessage(player, "", 11);
        			sendDelayedMessage(player, ChatColor.YELLOW + "Пора бы уже выйти на улицу, сходить в магазин...", 15);
        			sendDelayedMessage(player, "", 15);
        			sendDelayedMessage(player, ChatColor.YELLOW + "А то так и помру с голода..", 18);
        			sendDelayedMessage(player, "", 18);
        			
        			int delaySeconds = 25;
        			int delayTicks = delaySeconds * 20;
        			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
        				@Override
        				public void run() {
        					playersToGoOutside.add(player.getUniqueId());
        				}
        			}, delayTicks);
        			
        			sendDelayedMessage(player, ChatColor.GRAY + "Возьмите ключи и выходите на улицу.", 25);
        			sendDelayedMessage(player, "", 25);
        		}
        	}, delayTicks);
        	
        }
	}
	
	@EventHandler
	public void onResourcePackLoad(PlayerResourcePackStatusEvent e) {
		Player player = e.getPlayer();

		if(e.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
	        playersWithLoadedPack.add(player.getUniqueId());
	        plugin.log.info(player.getName() + " successfully loaded resourcepack");
	    }
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
	        Player player = (Player) e.getEntity();
	        
	        if(playersToGoOutside.contains(player.getUniqueId()) || playersInBlackRoom.contains(player.getUniqueId())) {
	        	e.setCancelled(true);
	        	return;
	        }
	    }
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		FileConfiguration playerConfig = getPlayerConfiguration(player);
		
		Location enterLocation = new Location(player.getWorld(), enterLocationX, enterLocationY, enterLocationZ);
		
		if(!playersToGoOutside.contains(player.getUniqueId()) && playerConfig.getInt("storylineLevel") == 0) {
			e.setCancelled(true);
			return;
		} else if(playersToGoOutside.contains(player.getUniqueId())) {
		    if(player.getLocation().distance(enterLocation) <= 6 && (!playerMessageCount.containsKey(player.getUniqueId()) || playerMessageCount.get(player.getUniqueId()) < 1)) {
		        sendDelayedMessage(player, ChatColor.YELLOW + "После выхода из подъезда, я иду до пешеходного перехода напротив Девяточки.", 0);
		        sendDelayedMessage(player, "", 0);
		        playerMessageCount.put(player.getUniqueId(), 1);
		    }
		}
		
		if(playersInBlackRoom.contains(player.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(playersToGoOutside.contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(playersToGoOutside.contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = getPlayerConfiguration(player);
		
		Block block = e.getClickedBlock();
		
		Action action = e.getAction();
		Material material = null;
		
		ItemStack item = e.getItem();
		ItemStack stick = new ItemStack(Material.STICK);
		ItemMeta meta = stick.getItemMeta();
		String stickName = ChatColor.BOLD + "Ключи";
		List<String> stickLore = new ArrayList<>();
		stickLore.add(0, ChatColor.GRAY + "Ключи от квартиры Семёна.");
		meta.setDisplayName(stickName);
		meta.setLore(stickLore);
		stick.setItemMeta(meta);
		
		if (e.getClickedBlock() != null) {
			material = e.getClickedBlock().getType();
		}
		
		if(!playersToGoOutside.contains(player.getUniqueId()) && playerConfig.getInt("storylineLevel") == 0) {
		    e.setCancelled(true);
		    return;
		} else if(playersToGoOutside.contains(player.getUniqueId()) && action == Action.LEFT_CLICK_BLOCK && item == null && material == Material.BIRCH_STAIRS) {
			if(!player.getInventory().contains(stick)) {
				player.getInventory().setItemInMainHand(stick);
				playersWhoTakedKeys.add(player.getUniqueId());
			} else {
				player.sendMessage(ChatColor.RED + "Вы уже взяли ключи!");
			}
		    
			if(!playersWhoTakedKeys.contains(player.getUniqueId())) {
				sendDelayedMessage(player, ChatColor.YELLOW + "Я одеваюсь и беру ключи.", 0);
				sendDelayedMessage(player, "", 0);
			}
		}

		if(playersToGoOutside.contains(player.getUniqueId()) && action == Action.LEFT_CLICK_BLOCK && item != null && item.getType() == Material.STICK) {
			ItemMeta itemMeta = item.getItemMeta();
			
			if(itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(stickName) && itemMeta.hasLore() && itemMeta.getLore().equals(stickLore)) {
				if(block.getType() == Material.IRON_DOOR) {
					Openable ironDoor = (Openable) block.getBlockData();

					if(!doorStates.containsKey(block) || !doorStates.get(block)) {
						ironDoor.setOpen(true);
						block.setBlockData(ironDoor);
						
						doorStates.put(block, true);
						closeDoorAfter(block, 5);
					}
				}
			}
		}
		
		if(block != null && (block.getLocation().equals(Storyline.FIRST_DOOR) || block.getLocation().equals(Storyline.SECOND_DOOR))) {
			if(playersToGoOutside.contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.GRAY + "Поздно, магазин уже закрыт, нужно в Девяточку, она открыта 24/7.");
				e.setCancelled(true);
			}
		}
	}

	private void closeDoorAfter(Block door, int delaySeconds) {
	    int delayTicks = delaySeconds * 20;
	    
	    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
	        @Override
	        public void run() {
	            if(door.getType() == Material.IRON_DOOR) {
	                Openable doorBlock = (Openable) door.getBlockData();
	                if(doorBlock.isOpen()) {
	                    doorBlock.setOpen(false);
	                    door.setBlockData(doorBlock);
	                    
	                    doorStates.put(door, false);
	                }
	            }
	        }
	    }, delayTicks);
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
	
	public static void sendDelayedMessage(Player player, String message, int delaySeconds) {
        int delayTicks = delaySeconds * 20;
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(message), delayTicks);
    }
	
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin().getName().equals("Storyline")) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				resetPlayerState(player);
				FileConfiguration playerConfig = getPlayerConfiguration(player);
				
				if(playerConfig.getInt("storylineLevel") == 0) {
					player.sendMessage(ChatColor.RED + "The Storyline plugin has been reloaded or disabled. Try reconnect to the server, this may help solve the problem. If the plugin doesn't work, report it to the administration.");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = getPlayerConfiguration(player);
		
		resetPlayerState(player);
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			plugin.log.info(player.getName() + " exits without completed story");
		}
	}

	public static void newLevelAnnouncement(Player player, File playerFile, FileConfiguration playerConfig, int delaySeconds) {
		new BukkitRunnable() {
			@Override
			public void run() {
				resetPlayerState(player);
				
				if(playerConfig.getInt("storylineLevel") == 0) {
					playerConfig.set("storylineLevel", 1);
					saveConfig(playerFile, playerConfig);
					
					Location busStop = new Location(player.getWorld(), 2753, 36, 2947);
					
					player.teleport(busStop);
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
					player.sendMessage(ChatColor.GREEN + "Вы перешли на новый уровень " + playerConfig.getInt("storylineLevel"));
				}
			}
		}.runTaskLater(plugin, delaySeconds * 20);
	}
	
	private static void resetPlayerState(Player player) {
		FileConfiguration playerConfig = getPlayerConfiguration(player);
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(plugin, player);
				player.showPlayer(plugin, p);
			}
			
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.SLOW);
			player.resetPlayerTime();
			
			restoreInventory(player);
			
			playersToGoOutside.remove(player.getUniqueId());
	        playersInBlackRoom.remove(player.getUniqueId());
	        playersWhoTakedKeys.remove(player.getUniqueId());
	        playerMessageCount.remove(player.getUniqueId());
	        playersWithLoadedPack.remove(player.getUniqueId());
	        
	        plugin.log.info("Status has been successfully reset for " + player.getName() + ". Caused by: Disabling Plugin / Player Leave / Player Reached New Level");
		}
	}
	
	private void saveInventory(Player player) {
        UUID playerID = player.getUniqueId();
        ItemStack[] inventoryContents = player.getInventory().getContents();
        playerInventories.put(playerID, inventoryContents);
        player.getInventory().clear();
    }

	public static void restoreInventory(Player player) {
	    UUID playerID = player.getUniqueId();
	    if (playerInventories.containsKey(playerID)) {
	        ItemStack[] inventoryContents = playerInventories.get(playerID);
	        player.getInventory().clear();
	        player.getInventory().setContents(inventoryContents);
	        playerInventories.remove(playerID);
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
}
