package ru.hofwq.storyline.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;
import ru.hofwq.storyline.utils.Utils;

public class SetStoryLevel implements CommandExecutor{
	Storyline plugin = Storyline.getPlugin();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		File playerFolder = new File(plugin.getDataFolder(), "Players");
		Player player;
		int newStorylineLevel;
		
		if(!(args.length >= 1 && args.length <= 2)) {
			sender.sendMessage(Storyline.WRONG_ARGUMENTS);
			return true;
		}
		
		if(!sender.hasPermission("storyline.setstorylevel")) {
			sender.sendMessage(Storyline.NOT_ALLOWED);
			return true;
		}
		
		if(!(sender instanceof Player) && args.length == 2) {
			player = Bukkit.getPlayer(args[0]);
		} else if(sender instanceof Player && args.length == 1) {
			player = (Player) sender;
		} else {
			player = Bukkit.getPlayer(args[0]);
		}
		
		try {
			newStorylineLevel = Integer.parseInt(args[args.length - 1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Уровень должен быть числом!");
			return true;
		}
		
		if(player == null) {
			sender.sendMessage(ChatColor.RED + "Игрок не найден!");
			return true;
		}
		
		File playerFile = new File(playerFolder, player.getUniqueId() + ".yml");
		FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		
		if (!playerFile.exists()) {
			sender.sendMessage(ChatColor.RED + "Игрок не зарегистрирован на сервере!");
			return true;
		}
		
		if(newStorylineLevel < 0) {
			sender.sendMessage(ChatColor.RED + "Уровень не может быть меньше нуля!");
			return true;
		}
		
		if(playerConfig.getInt("storylineLevel") == newStorylineLevel) {
			sender.sendMessage(ChatColor.RED + "У игрока уже данный уровень!");
			return true;
		}
		
		Utils.resetPlayerState(player);
		
		playerConfig.set("storylineLevel", newStorylineLevel);
		saveConfig(playerFile, playerConfig);
		
		sender.sendMessage(ChatColor.GREEN + "Успешно выставлен уровень " + playerConfig.getInt("storylineLevel") + " игроку " + player.getName());
		player.kickPlayer("Успешно выставлен " + playerConfig.getInt("storylineLevel") + " уровень сюжета, перезайдите на сервер.");
		
		return true;
	}

	private void saveConfig(File playerFile, FileConfiguration playerConfig) {
		try {
			playerConfig.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
