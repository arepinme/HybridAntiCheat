package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;

public class NoSlowDownCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		Player p = user.getHandle();
		if (HybridAntiCheat.checkPermission(p, "bypass.noslowdown"))
			return;
		if ((p.isSneaking() || p.isBlocking()) && p.isSprinting()) {
			PlayerMoveEvent event = CastUtil.cast(e);
			event.setTo(event.getFrom());
			if (p.getHealth() > 0.1D)
				p.damage(0.8D);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.NoSlowDown));
		}
	}
}
