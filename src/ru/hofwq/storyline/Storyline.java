package ru.hofwq.storyline;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import ru.hofwq.storyline.commands.ClearEnderChest;
import ru.hofwq.storyline.commands.SetStoryLevel;
import ru.hofwq.storyline.config.Config;
import ru.hofwq.storyline.events.BorderListener;
import ru.hofwq.storyline.events.CloseDoorsAt;
import ru.hofwq.storyline.events.ClosedZone;
import ru.hofwq.storyline.events.EventListener;
import ru.hofwq.storyline.events.LineBorderListener;

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
		getServer().getPluginManager().registerEvents(new BorderListener(), this);
		getServer().getPluginManager().registerEvents(new LineBorderListener(), this);
		getServer().getPluginManager().registerEvents(new ClosedZone(), this);
		getServer().getPluginManager().registerEvents(new CloseDoorsAt(), this);
		
		//Registering commands
		getCommand("setstorylevel").setExecutor(new SetStoryLevel());
		getCommand("clearenderchest").setExecutor(new ClearEnderChest());
		
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
