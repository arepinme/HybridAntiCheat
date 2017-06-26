package me.xDark.hybridanticheat.checks;

import org.bukkit.event.Event;

import me.xDark.hybridanticheat.api.User;

public interface Check {

	void doCheck(User user, Event e);

}
