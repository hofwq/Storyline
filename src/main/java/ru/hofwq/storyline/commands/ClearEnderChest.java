package ru.hofwq.storyline.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;
import ru.hofwq.storyline.Storyline;

public class ClearEnderChest implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1) {
			sender.sendMessage(Storyline.WRONG_ARGUMENTS);
			return true;
		}
		
		if(!sender.hasPermission("esmc.clear.enderchest")) {
			sender.sendMessage(Storyline.NOT_ALLOWED);
			return true;
		}
		
		String nickname = args[0].toString();

		if(getPlayer(nickname) != null) {
			Player player = getPlayer(nickname);
			
			clearEnderChest(player);
			sender.sendMessage(ChatColor.GREEN + "Успешно очищен эндер-сундук игроку " + ChatColor.YELLOW + player.getName());
		}
		
		return true;
	}
	
	private Player getPlayer(String nickname) {
	    Player player = (Player) Bukkit.getOnlinePlayers();

	    if (player.getName().toLowerCase().startsWith(nickname.toLowerCase())) {
	    	return player;
	    }

	    return null;
	}
	
	private void clearEnderChest(Player player) {
	    Inventory enderChest = player.getEnderChest();
	    
	    enderChest.clear();
	}
}
