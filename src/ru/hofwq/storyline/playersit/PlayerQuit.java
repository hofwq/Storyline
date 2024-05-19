package ru.hofwq.storyline.playersit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener{
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		SitPlayer player = new SitPlayer(e.getPlayer());
		if (player.isSitting()) {
			player.setSitting(false); 
		}
	}
}