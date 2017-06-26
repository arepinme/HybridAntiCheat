package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;

public class FlightCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		if (user.getHandle().hasPermission("hac.bypass.flight"))
			return;
		if (user.getHandle().isFlying() && !user.getHandle().getAllowFlight()) {
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
			return;
		}
	}

}
