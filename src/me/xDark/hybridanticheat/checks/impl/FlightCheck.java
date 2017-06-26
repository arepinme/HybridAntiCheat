package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.wrapper.MathHelper;

public class FlightCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		if (user.getHandle().hasPermission("hac.bypass.flight"))
			return;
		if (user.getHandle().isFlying() && !user.getHandle().getAllowFlight()) {
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
			return;
		}
		double ym = user.getHandle().getVelocity().getY();
		ym = MathHelper.round(ym, 3);
		System.out.println(ym);
		double must = HighJumpCheck.getYMotion(user.getHandle());
		if (((ym > must) || (ym % 1 == 0.0) || ym <= -1.5D) && !user.getHandle().getAllowFlight()) {
			user.getHandle().damage(1);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
			return;
		}
	}

}
