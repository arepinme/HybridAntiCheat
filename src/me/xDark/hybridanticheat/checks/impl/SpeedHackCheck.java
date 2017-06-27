package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;
import me.xDark.hybridanticheat.utils.MathUtil;
import me.xDark.hybridanticheat.wrapper.MathHelper;

public class SpeedHackCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		if (HybridAntiCheat.checkPermission(user.getHandle(), "bypass.speedhack"))
			return;
		if (user.getHandle().getAllowFlight() && user.getHandle().isFlying())
			return;
		PlayerMoveEvent event = CastUtil.cast(e);
		double xDifference = (Math.max(event.getFrom().getX(), event.getTo().getX())
				- Math.min(event.getFrom().getX(), event.getTo().getX())),
				zDifference = (Math.max(event.getFrom().getZ(), event.getTo().getZ())
						- Math.min(event.getFrom().getZ(), event.getTo().getZ()));
		double yDifference = MathHelper.round(MathUtil.diff(event.getTo().getY(), event.getFrom().getY()), 5);
		if (user.getHandle().getGameMode() != GameMode.CREATIVE && (xDifference > 5D || zDifference > 5D
				|| yDifference != 0.0D && (yDifference >= 0.3993D && yDifference <= 0.4D || yDifference == 0.2)))
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.SpeedHack));
	}

}
