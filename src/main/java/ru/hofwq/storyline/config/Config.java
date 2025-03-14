package ru.hofwq.storyline.config;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.hofwq.storyline.Storyline;

public class Config implements EventListener{
	Storyline plugin = Storyline.getPlugin();
	private FileConfiguration config;
	
	public void checkConfig() {
	    File configFolder = plugin.getDataFolder();
	    if (!configFolder.exists()) {
	        configFolder.mkdirs();
	    }
	    
	    File configFile = new File(configFolder, "config.yml");

	    config = new YamlConfiguration();
	    
	    if(!configFile.exists()) {
	        plugin.log.info("Config is not exists, creating.");
	        
	        try {
	            configFile.createNewFile();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	    try {
	        config.load(configFile);
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	    
	    File playerFolder = new File(configFolder, "Players");
	    if (!playerFolder.exists()) {
	        playerFolder.mkdirs();
	    }
	    
	    createStringLists();
	}

	
	private void createStringLists() {
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		
		if(!config.contains("semyonRoom.X")){
		    config.set("semyonRoom.X", 0);
		}
		
		if(!config.contains("semyonRoom.Y")){
		    config.set("semyonRoom.Y", 0);
		}
		
		if(!config.contains("semyonRoom.Z")){
		    config.set("semyonRoom.Z", 0);
		}
		
		if(!config.contains("blackRoom.X")){
		    config.set("blackRoom.X", 0);
		}
		
		if(!config.contains("blackRoom.Y")){
		    config.set("blackRoom.Y", 0);
		}
		
		if(!config.contains("blackRoom.Z")){
		    config.set("blackRoom.Z", 0);
		}

		if(!config.contains("keyLocation.X")){
		    config.set("keyLocation.X", 0);
		}
		
		if(!config.contains("keyLocation.Y")){
		    config.set("keyLocation.Y", 0);
		}
		
		if(!config.contains("keyLocation.Z")){
		    config.set("keyLocation.Z", 0);
		}

		if(!config.contains("enterLocation.X")){
		    config.set("enterLocation.X", 0);
		}
		
		if(!config.contains("enterLocation.Y")){
		    config.set("enterLocation.Y", 0);
		}
		
		if(!config.contains("enterLocation.Z")){
		    config.set("enterLocation.Z", 0);
		}

		saveConfig(configFile);
	}
	
    private void saveConfig(File configFile) {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
