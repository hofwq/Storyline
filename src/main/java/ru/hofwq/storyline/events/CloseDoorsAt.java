package ru.hofwq.storyline.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;
import ru.hofwq.storyline.utils.PlayerLists;
import ru.hofwq.storyline.utils.Utils;

public class CloseDoorsAt implements Listener{
    public static Storyline plugin = Storyline.getPlugin();
    private static boolean isShedulerEnabledMessage = false;

    World world = Bukkit.getWorld("world");
    Location FIRST_DOOR = new Location(world, 2827, 37, 2990);
    Location SECOND_DOOR = new Location(world, 2827, 37, 2992);
    Location THIRD_DOOR = new Location(world, 2828, 37, 3009);
    Location FOURTH_DOOR = new Location(world, 3579, 42, 2355); //stolovaya 1
    Location FIFTH_DOOR = new Location(world, 3580, 42, 2355); //stolovaya 2

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	Player player = e.getPlayer();
    	
    	Block block = e.getClickedBlock();
    	
    	if(block != null) {
    		long time = world.getTime();
    		
    		if(block.getLocation().distance(FIRST_DOOR) <= 3 || block.getLocation().distance(SECOND_DOOR) <= 3 || block.getLocation().distance(THIRD_DOOR) <= 3) {
    			switch(block.getType()) {
    			case ACACIA_DOOR:
    				if(time > 13500 && time <= 23500) {
    					if(!PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    						player.sendMessage(ChatColor.RED + "Магазин закрыт на ночь!");
    					}
    					
    					e.setCancelled(true);
    				}
    				
    				break;
    			case BIRCH_DOOR:
    				if(time > 13500 && time <= 23500) {
    					if(!PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    						player.sendMessage(ChatColor.RED + "Аптека закрыта на ночь!");
    					}
    					
    					e.setCancelled(true);
    				}
    				
    				break;
    			default:
    				break;
    			}
    			
    			if(block.getType() == Material.BIRCH_DOOR && PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    				player.sendMessage(ChatColor.GRAY + "Мне нужно в Девяточку, а не в аптеку.");
    				
    				e.setCancelled(true);
    			}
    			
    			if(block.getType() == Material.ACACIA_DOOR && PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    				player.sendMessage(ChatColor.GRAY + "Поздно, магазин уже закрыт, нужно в Девяточку, она открыта 24/7.");
    				
    				e.setCancelled(true);
    			}
    		} else if(block.getLocation().distance(FOURTH_DOOR) <= 3 || block.getLocation().distance(FIFTH_DOOR) <= 3) {
    			if(block.getType() == Material.BIRCH_DOOR) {
    				if(time > 13500 && time <= 23500) {
    					player.sendMessage(ChatColor.RED + "Столовая закрыта на ночь.");
    					
    					e.setCancelled(true);
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Block first_door = FIRST_DOOR.getBlock();
                Block second_door = SECOND_DOOR.getBlock();
                Block third_door = THIRD_DOOR.getBlock();
                Block fourth_door = FOURTH_DOOR.getBlock();
                Block fifth_door = FIFTH_DOOR.getBlock();
                long time = world.getTime();
                
                if(first_door.getType() == Material.ACACIA_DOOR && second_door.getType() == Material.ACACIA_DOOR && third_door.getType() == Material.BIRCH_DOOR) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                    	if(PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
                    		closeDoorsForPlayer(player);
                    	}
                    	
                    	closeDoors(first_door, second_door, third_door, player);
                    }
                } 
                
                if(fourth_door.getType() == Material.BIRCH_DOOR && fifth_door.getType() == Material.BIRCH_DOOR) {
                	Openable fourthDoor = (Openable) fourth_door.getBlockData();
                	Openable fifthDoor = (Openable) fifth_door.getBlockData();
                	
                	if(time >= 0 && time <= 13500) {
                		fourthDoor.setOpen(true);
                		fourth_door.setBlockData(fourthDoor);
                		
                		fifthDoor.setOpen(true);
                		fifth_door.setBlockData(fifthDoor);
                	} else {
                		fourthDoor.setOpen(false);
                		fourth_door.setBlockData(fourthDoor);
                		
                		fifthDoor.setOpen(false);
                		fifth_door.setBlockData(fifthDoor);
                	}
                }
                
                if(!isShedulerEnabledMessage) {
                	plugin.log.info(ChatColor.YELLOW + "Day-night door cycle is active.");
                	isShedulerEnabledMessage = true;
                }
            }
        }.runTaskTimer(plugin, 0L, 300L);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
    	Player player = e.getPlayer();
        Location firstDoorInside = new Location(world, 2828, 37, 2990);
        Location secondDoorInside = new Location(world, 2828, 37, 2992);
        Location thirdDoorInside = new Location(world, 2829, 37, 3009);
        Location backupLocation = new Location(world, 2823, 36, 2991);
        
    	if(PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    		if(isSameBlock(player.getLocation(), firstDoorInside) || isSameBlock(player.getLocation(), secondDoorInside) || isSameBlock(player.getLocation(), thirdDoorInside)) {
                player.teleport(backupLocation);
            }
    	}
    }
    
    private boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }
    
    private void closeDoors(Block first_door, Block second_door, Block third_door, Player player) {
    	long time = world.getTime();
    	Openable firstDoor = (Openable) first_door.getBlockData();
    	Openable secondDoor = (Openable) second_door.getBlockData();
    	Openable thirdDoor = (Openable) third_door.getBlockData();
    	
    	if(time >= 0 && time <= 13500) {
    		if(PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
    			closeDoorsForPlayer(player);
    		} else { 
    			openDoorsForPlayer(player);
    		}
    	} else {
    		firstDoor.setOpen(false);
    		first_door.setBlockData(firstDoor);
    		
    		secondDoor.setOpen(false);
    		second_door.setBlockData(secondDoor);
    		
    		thirdDoor.setOpen(false);
    		third_door.setBlockData(thirdDoor);
    	}
    }
    
	private void closeDoorsForPlayer(Player player) {
		Block first_door = FIRST_DOOR.getBlock();
        Block second_door = SECOND_DOOR.getBlock();
        Block third_door = THIRD_DOOR.getBlock();
		
		Utils.closeDoorForPlayer(player, first_door);
		Utils.closeDoorForPlayer(player, second_door);
		Utils.closeDoorForPlayer(player, third_door);
	}
	
	private void openDoorsForPlayer(Player player) {
		Block first_door = FIRST_DOOR.getBlock();
        Block second_door = SECOND_DOOR.getBlock();
        Block third_door = THIRD_DOOR.getBlock();
		
        Utils.openDoorForPlayer(player, first_door);
        Utils.openDoorForPlayer(player, second_door);
        Utils.openDoorForPlayer(player, third_door);
	}
}
