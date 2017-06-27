package me.xDark.hybridanticheat.checks.impl;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;
import me.xDark.hybridanticheat.utils.MathUtil;

public class NoFallCheck implements Check {

	public static final HashMap<Player, Float> fallDistance = new HashMap<>();

	@SuppressWarnings("deprecation")
	@Override
	public void doCheck(User user, Event e) {
		if (HybridAntiCheat.checkPermission(user.getHandle(), "bypass.killaura"))
			return;
		Player p = user.getHandle();
		if (p.getAllowFlight() && p.isFlying())
			return;
		PlayerMoveEvent event = CastUtil.cast(e);
		if (p.getLocation().subtract(0, 2, 0).getBlock().getType() != Material.AIR || p.isOnGround()) {
			fallDistance.remove(p);
			return;
		}
		if (event.getTo().getY() < event.getFrom().getY())
			return;
		updateFallState(p, p.getVelocity().getY());
		if (fallDistance.containsKey(p)) {
			double diff = MathUtil.diff(fallDistance.get(p).floatValue(), p.getFallDistance());
			if (diff > 0.7D) {
				p.damage(1D);
				Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.NoFall));
			}
		}
	}

	private static void updateFallState(Player p, double y) {
		if (y < 0.0D) {
			float must;
			if (fallDistance.containsKey(p))
				must = (fallDistance.get(p).floatValue() - (float) y);
			else
				must = p.getFallDistance();
			fallDistance.put(p, must);
		}
	}

}
