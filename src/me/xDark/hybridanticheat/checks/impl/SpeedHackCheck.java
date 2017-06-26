package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;

public class SpeedHackCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		if (user.getHandle().hasPermission("hac.bypass.speedhack"))
			return;
		PlayerMoveEvent event = CastUtil.cast(e);
		double xDifference = (Math.max(event.getFrom().getX(), event.getTo().getX())
				- Math.min(event.getFrom().getX(), event.getTo().getX())),
				zDifference = (Math.max(event.getFrom().getZ(), event.getTo().getZ())
						- Math.min(event.getFrom().getZ(), event.getTo().getZ()));
		if (user.getHandle().getGameMode() != GameMode.CREATIVE && (xDifference > 7D || zDifference > 7D))
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.SpeedHack));
	}

}
