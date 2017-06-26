package me.xDark.hybridanticheat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;

public class ValidateEvent extends Event {

	private static final HandlerList handlerList = new HandlerList();

	private final User user;

	private final CheckType checkType;

	public ValidateEvent(User user, CheckType checkType) {
		this.user = user;
		this.checkType = checkType;
	}

	public User getUser() {
		return user;
	}

	public CheckType getCheckType() {
		return checkType;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
