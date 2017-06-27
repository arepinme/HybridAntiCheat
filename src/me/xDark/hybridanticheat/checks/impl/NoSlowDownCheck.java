package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;

public class NoSlowDownCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		Player p = user.getHandle();
		if (HybridAntiCheat.checkPermission(p, "bypass.noslowdown"))
			return;
		if (p.isBlocking() && p.isSprinting()) {
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.NoSlowDown));
		}
	}
}
