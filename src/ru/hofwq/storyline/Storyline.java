package ru.hofwq.storyline;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import ru.hofwq.storyline.commands.ClearEnderChest;
import ru.hofwq.storyline.commands.SetStoryLevel;
import ru.hofwq.storyline.config.Config;
import ru.hofwq.storyline.events.BorderListener;
import ru.hofwq.storyline.events.EventListener;
import ru.hofwq.storyline.events.LineBorderListener;

public class Storyline extends JavaPlugin{
	public Logger log = getLogger();
	
	private static Storyline plugin;
	
	public static String NOT_ALLOWED = "§cНедостаточно прав для выполнения этой команды.";
	public static String WRONG_ARGUMENTS = "§cНеверные аргументы.";
	
	public static Location FIRST_DOOR = new Location(Bukkit.getWorld("world"), 0, 0, 0);
	public static Location SECOND_DOOR = new Location(Bukkit.getWorld("world"), 0, 0, 0);
	private World world = Bukkit.getWorld("world");
	
	@Override
	public void onEnable() {
		plugin = this;
		
		//Initializing config
		Config createConfig = new Config();
		createConfig.checkConfig();
		
		//Registering events
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getServer().getPluginManager().registerEvents(new BorderListener(), this);
		getServer().getPluginManager().registerEvents(new LineBorderListener(), this);
		
		//Registering commands
		getCommand("setstorylevel").setExecutor(new SetStoryLevel());
		getCommand("clearenderchest").setExecutor(new ClearEnderChest());
		
		//Close doors in gastronom
		new BukkitRunnable() {
			@Override
			public void run() {
				long time = world.getTime();
				Block first_door = FIRST_DOOR.getBlock();
				Block second_door = SECOND_DOOR.getBlock();
				
				if(first_door.getType() == Material.ACACIA_DOOR && second_door.getType() == Material.ACACIA_DOOR) {
					Door doorOne = (Door) first_door.getBlockData();
					Door doorTwo = (Door) second_door.getBlockData();
					
					if(time > 12500 && time < 23500 && !doorOne.isOpen() && !doorTwo.isOpen()) { 
						doorOne.setOpen(true);
						doorTwo.setOpen(true);
						first_door.setBlockData(doorOne);
						second_door.setBlockData(doorTwo);
					} else if ((time < 12500 || time > 23500) && (doorOne.isOpen() || doorTwo.isOpen())) {
						doorOne.setOpen(false);
						doorTwo.setOpen(false);
						first_door.setBlockData(doorOne);
						second_door.setBlockData(doorTwo);
					}
				}
			}
		}.runTaskTimer(this, 0L, 20L);
		
		log.info("Storyline enabled");
	}

	@Override
	public void onDisable() {
		plugin = null;
		
		log.info("Storyline disabled");
	}

	public static Storyline getPlugin() {
		return plugin;
	}
}
