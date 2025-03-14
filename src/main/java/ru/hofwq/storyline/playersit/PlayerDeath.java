package ru.hofwq.storyline.playersit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		SitPlayer player = new SitPlayer(e.getEntity());
		if (player.isSitting()) {
			player.setSitting(false); 
		}
	}
}
