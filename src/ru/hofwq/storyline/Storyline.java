package ru.hofwq.storyline;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import ru.hofwq.storyline.commands.SetStoryLevel;
import ru.hofwq.storyline.config.Config;
import ru.hofwq.storyline.events.EventListener;

public class Storyline extends JavaPlugin{
	public Logger log = getLogger();
	
	private static Storyline plugin;
	
	public static String NOT_ALLOWED = "§cНедостаточно прав для выполнения этой команды.";
	public static String WRONG_ARGUMENTS = "§cНеверные аргументы.";
	
	@Override
	public void onEnable() {
		plugin = this;
		
		//Initializing config
		Config createConfig = new Config();
		createConfig.checkConfig();
		
		//Registering events
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		//Registering commands
		getCommand("setstorylevel").setExecutor(new SetStoryLevel());
		
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
