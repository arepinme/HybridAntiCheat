package me.xDark.hybridanticheat.checks.impl;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;

public class NoSlowDownCheck implements Check {

	public static final HashMap<Player, AtomicInteger> attempts = new HashMap<>();

	@Override
	public void doCheck(User user, Event e) {
		Player p = user.getHandle();
		if (HybridAntiCheat.checkPermission(p, "bypass.noslowdown"))
			return;
		if ((p.isBlocking() || p.isSneaking()) && p.isSprinting()) {
			if (attempts.get(p).incrementAndGet() >= 30)
				Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.NoSlowDown));
		} else
			attempts.get(p).set(0);
	}
}
