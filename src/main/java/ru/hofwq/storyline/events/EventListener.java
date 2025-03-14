package ru.hofwq.storyline.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import ru.hofwq.storyline.utils.PlayerLists;
import ru.hofwq.storyline.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;

public class EventListener implements Listener{
	public static Storyline plugin = Storyline.getPlugin();
	public HashMap<UUID, ItemStack[]> playerInventories = new HashMap<>();
	public HashMap<Block, Boolean> doorStates = new HashMap<>();
	Location semyonRoom = new Location(Bukkit.getWorld("world"), Utils.semyonRoomX + 0.5, Utils.semyonRoomY + 0.5, Utils.semyonRoomZ);
	Location ironDoorLoc = new Location(Bukkit.getWorld("world"), 2881, 58, 3006);
	
	@EventHandler
	public void onLogin(LoginEvent e) {
		Player player = e.getPlayer();
		
		if (!AuthMeApi.getInstance().isAuthenticated(player)) {
        	return;
        }
        
        FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
        File playerFile = Utils.getPlayerFile(player);
        
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
			playerConfig.set("storylineLevel", 0);
			Utils.saveConfig(playerFile, playerConfig);
        }

        float yaw = 90.8f;
        float pitch = 22.1f;
        
        semyonRoom.setYaw(yaw);
        semyonRoom.setPitch(pitch);
        
        int delaySeconds = 2;
        int delayTicks = delaySeconds * 20;
        
        new BukkitRunnable() {
        	@Override
        	public void run() {
        		if(playerConfig.getInt("storylineLevel") == 0 && PlayerLists.playersWithLoadedPack.contains(player.getUniqueId())) {
        			cancel();
        			String voice_1 = "minecraft:my_sounds.voice1";
        			String voice_2 = "minecraft:my_sounds.voice2";
        			String voice_3 = "minecraft:my_sounds.voice3";
        			String voice_4 = "minecraft:my_sounds.voice4";
        			String voice_5 = "minecraft:my_sounds.voice5";
        			
        			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
        				@Override
        				public void run() {
        					World world = Bukkit.getWorld("world");
        					Utils.saveInventory(player);
        					
        					if(!world.getChunkAt(semyonRoom).isLoaded()) {
        						world.getChunkAt(semyonRoom).setForceLoaded(true);
        					}
        					
        					player.teleport(semyonRoom); //2877 58 3002
        					player.setPlayerTime(18000, false);
        				    
        					player.setGameMode(GameMode.SURVIVAL);
        					player.setFoodLevel(8);

        					
        					plugin.sitPlayer(player);
        					
        					for(Player p : Bukkit.getOnlinePlayers()) {
        						p.hidePlayer(plugin, player);
        						player.hidePlayer(plugin, p);
        					}
        					
        					int delay = 0;
        					
        					Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Я сидел дома шестые сутки, смотрел в монитор, читая новые треды на форуме несколько часов подряд.", delay, voice_1);
        					Utils.sendDelayedMessage(player, "", delay);
        					delay += 7;
        					Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Сейчас все обсуждали новое аниме под названием \"Советский мир\", в котором действия происходят в мире, в котором ещё не распался советский союз, в который попадает главный герой.", delay, voice_2);
        					Utils.sendDelayedMessage(player, "", delay);
        					delay += 11;
        					Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Мне не особо нравились аниме подобного жанра, в которых гг попадает в альтернативные миры, ведь того, что происходит в этих аниме, никак не может произойти в жизни...", delay, voice_3);
        					Utils.sendDelayedMessage(player, "", delay);
        					delay += 11;
        					Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Пора бы уже выйти на улицу, сходить в магазин...", delay, voice_4);
        					Utils.sendDelayedMessage(player, "", delay);
        					delay += 5;
        					Utils.sendDelayedMessage(player, ChatColor.YELLOW + "А то так и помру с голода..", delay, voice_5);
        					
        					delay += 6;
        					Utils.allowPlayerWalk(player, delay);
        					
        					Utils.sendDelayedMessage(player, "", delay);
        					Utils.sendDelayedMessage(player, ChatColor.GRAY + "Возьмите ключи и выходите на улицу.", delay, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE);
        				}
        			}, delayTicks);
        		}
        	}
        }.runTaskTimer(plugin, 0L, 20L);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		
		Utils.resetPlayerState(player);
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			plugin.log.info(player.getName() + " exits without completed story");
		}
	}
	
	@EventHandler
	public void onResourcePackLoad(PlayerResourcePackStatusEvent e) {
		Player player = e.getPlayer();
		
		if(e.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED || e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					PlayerLists.playersWithLoadedPack.add(player.getUniqueId());
					plugin.log.info(player.getName() + " successfully loaded resourcepack");
				}
			}, 20L);
	    }
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		String command = e.getMessage().split(" ")[0];
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			if(!Utils.allowedCmds.contains(command)) {
				plugin.log.info(player.getName() + " запрещено использовать команды во время сюжета.");
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
	    if(e.getEntity() instanceof Player) {
	        Player player = (Player) e.getEntity();
	        FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
	        
	        if(playerConfig.getInt("storylineLevel") == 0) {
	            e.setCancelled(true);
	            return;
	        }
	    }
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
	        Player player = (Player) e.getEntity();
	        
	        if(PlayerLists.playersToGoOutside.contains(player.getUniqueId()) || PlayerLists.playersInBlackRoom.contains(player.getUniqueId())) {
	        	e.setCancelled(true);
	        	return;
	        }
	    }
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		
		Location enterLocation = new Location(player.getWorld(), Utils.enterLocationX, Utils.enterLocationY, Utils.enterLocationZ);
		String voice_6 = "minecraft:my_sounds.voice6";

		if(!PlayerLists.playersToGoOutside.contains(player.getUniqueId()) && playerConfig.getInt("storylineLevel") == 0) {
			e.setCancelled(true);
			return;
		} else if(PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
		    if(player.getLocation().distance(enterLocation) <= 6 && (!PlayerLists.playerMessageCount.containsKey(player.getUniqueId()) || PlayerLists.playerMessageCount.get(player.getUniqueId()) < 1)) {
		    	Utils.sendDelayedMessage(player, ChatColor.YELLOW + "После выхода из подъезда, я иду до пешеходного перехода напротив Девяточки.", 0, voice_6);
		        Utils.sendDelayedMessage(player, "", 0);
		        PlayerLists.playerMessageCount.put(player.getUniqueId(), 1);
		    }
		}
		
		if(PlayerLists.playersInBlackRoom.contains(player.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(PlayerLists.playersToGoOutside.contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(PlayerLists.playersToGoOutside.contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		
		Block block = e.getClickedBlock();
		
		Action action = e.getAction();
		Material material = null;
		
		ItemStack item = e.getItem();
		ItemStack stick = new ItemStack(Material.STICK);
		ItemMeta meta = stick.getItemMeta();
		String stickName = ChatColor.BOLD + "Ключи";
		List<String> stickLore = new ArrayList<>();
		stickLore.add(0, ChatColor.GRAY + "Ключи от квартиры.");
		meta.setDisplayName(stickName);
		meta.setLore(stickLore);
		stick.setItemMeta(meta);
		
		if (e.getClickedBlock() != null) {
			material = e.getClickedBlock().getType();
		}
		
		if(!PlayerLists.playersToGoOutside.contains(player.getUniqueId()) && playerConfig.getInt("storylineLevel") == 0) {
		    e.setCancelled(true);
		    return;
		} else if(PlayerLists.playersToGoOutside.contains(player.getUniqueId()) && action == Action.LEFT_CLICK_BLOCK && item == null && material == Material.BIRCH_STAIRS) {
			if(!player.getInventory().contains(stick)) {
				player.getInventory().setItemInMainHand(stick);
				PlayerLists.playersWhoTakedKeys.add(player.getUniqueId());
			} else {
				player.sendMessage(ChatColor.RED + "Вы уже взяли ключи!");
			}
		    
			if(!PlayerLists.playersWhoTakedKeys.contains(player.getUniqueId())) {
				Utils.sendDelayedMessage(player, ChatColor.YELLOW + "Я одеваюсь и беру ключи.", 0, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE);
				Utils.sendDelayedMessage(player, "", 0);
			}
		}

		if(PlayerLists.playersToGoOutside.contains(player.getUniqueId()) && action == Action.LEFT_CLICK_BLOCK && item != null && item.getType() == Material.STICK) {
			ItemMeta itemMeta = item.getItemMeta();
			
			if(itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(stickName) && itemMeta.hasLore() && itemMeta.getLore().equals(stickLore)) {
				
				if(block.getLocation().distance(ironDoorLoc) <= 2) {
					if (block.getType() == Material.IRON_DOOR) {
						if(!doorStates.containsKey(block) || !doorStates.get(block)) {
							Block ironDoor = ironDoorLoc.getBlock();
							
							Utils.openDoorForPlayer(player, ironDoor);
							
							doorStates.put(block, true);
							closeDoorAfter(block, 5, player);
						}
					}
				}
			}
		}
	}
	
	private void closeDoorAfter(Block door, int delaySeconds, Player player) {
	    int delayTicks = delaySeconds * 20;
	    
	    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
	        @Override
	        public void run() {
	            if(door.getType() == Material.IRON_DOOR) {
	            	Block ironDoor = ironDoorLoc.getBlock();
	            	
	            	Utils.closeDoorForPlayer(player, ironDoor);
	            	
	            	doorStates.put(door, false);
	            }
	        }
	    }, delayTicks);
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin().getName().equals("Storyline")) {
			World world = Bukkit.getWorld("world");
			
		    Bukkit.getServer().getScheduler().cancelTasks(plugin);

		    for(Player player : Bukkit.getOnlinePlayers()) {
				Utils.resetPlayerState(player);
				FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);

				if(playerConfig.getInt("storylineLevel") == 0) {
					player.sendMessage(ChatColor.RED + "The Storyline plugin has been reloaded or disabled. Try reconnect to the server, this may help solve the problem. If the plugin doesn't work, report it to the administration.");
				}
			}
		    
		    if(world.getChunkAt(semyonRoom).isLoaded()) {
				world.getChunkAt(semyonRoom).setForceLoaded(false);
			}
		}
	}

	@EventHandler
	public void onChatMessage(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		FileConfiguration playerConfig = Utils.getPlayerConfiguration(player);
		
		if(playerConfig.getInt("storylineLevel") == 0) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            if (event.getDamager() instanceof Player) {
                event.setCancelled(true);
            }
            
            if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    event.getDamager().remove();
                    event.setCancelled(true);
                }
            }
        }
    }
}
