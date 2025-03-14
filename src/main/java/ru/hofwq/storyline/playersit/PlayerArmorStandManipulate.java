package ru.hofwq.storyline.playersit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class PlayerArmorStandManipulate implements Listener{
	@EventHandler
	public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		SitArmorStand SitArmorStand = new SitArmorStand(e.getRightClicked());
		
		if (SitArmorStand.isSeat()) {
			e.setCancelled(true); 
		}
	}
}
