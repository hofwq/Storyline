package ru.hofwq.storyline;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import ru.hofwq.storyline.commands.ClearEnderChest;
import ru.hofwq.storyline.commands.SetStoryLevel;
import ru.hofwq.storyline.config.Config;
import ru.hofwq.storyline.events.BorderListener;
import ru.hofwq.storyline.events.CloseDoorsAt;
import ru.hofwq.storyline.events.ClosedZone;
import ru.hofwq.storyline.events.EventListener;
import ru.hofwq.storyline.events.LineBorderListener;
import ru.hofwq.storyline.playersit.PlayerArmorStandManipulate;
import ru.hofwq.storyline.playersit.PlayerDeath;
import ru.hofwq.storyline.playersit.PlayerQuit;
import ru.hofwq.storyline.playersit.SitPlayer;
import ru.hofwq.storyline.utils.PlayerLists;

public class Storyline extends JavaPlugin{
	public Logger log = getLogger();
	
	private static Storyline plugin;
	private Map<UUID, ArmorStand> seats = new HashMap<>();
	
	public static String NOT_ALLOWED = "§cНедостаточно прав для выполнения этой команды.";
	public static String WRONG_ARGUMENTS = "§cНеверные аргументы.";
	private ProtocolManager protocolManager;
	
	@Override
	public void onEnable() {
		plugin = this;
		protocolManager = ProtocolLibrary.getProtocolManager();
		
		//Initializing config
		Config createConfig = new Config();
		createConfig.checkConfig();
		
		//Registering events
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getServer().getPluginManager().registerEvents(new BorderListener(), this);
		getServer().getPluginManager().registerEvents(new LineBorderListener(), this);
		getServer().getPluginManager().registerEvents(new ClosedZone(), this);
		getServer().getPluginManager().registerEvents(new CloseDoorsAt(), this);
		getServer().getPluginManager().registerEvents(new PlayerArmorStandManipulate(), this);
		getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
		
		//Registering commands
		getCommand("setstorylevel").setExecutor(new SetStoryLevel());
		getCommand("clearenderchest").setExecutor(new ClearEnderChest());
		
		log.info("Storyline enabled");
	}

	@Override
	public void onDisable() {
		plugin = null;
		byte b;
	    int i;
	    Object[] arrayOfObject;
	    
	    for (i = (arrayOfObject = this.seats.keySet().toArray()).length, b = 0; b < i; ) {
	      Object uuid = arrayOfObject[b];
	      SitPlayer player = new SitPlayer(Bukkit.getPlayer((UUID)uuid));
	      player.setSitting(false);
	      b++;
	    }
	    
		log.info("Storyline disabled");
	}

	public static Storyline getPlugin() {
		return plugin;
	}
	
	public Map<UUID, ArmorStand> getSeats() {
	    return this.seats;
	}
	
	public void sitPlayer(Player player) {
		if(!PlayerLists.playersToGoOutside.contains(player.getUniqueId())) {
			PacketContainer sitPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			watcher.setEntity(player);
			watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x02);
			
			sitPacket.getIntegers().write(0, player.getEntityId());
			sitPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
			
			try {
				protocolManager.sendServerPacket(player, sitPacket);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
