package ru.hofwq.storyline.playersit;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStopSittingEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	
	private ArmorStand seat;
	
	private String message;
	
	public PlayerStopSittingEvent(Player player, ArmorStand seat) {
		this.player = player;
		this.seat = seat;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public ArmorStand getSeat() {
		return this.seat;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
