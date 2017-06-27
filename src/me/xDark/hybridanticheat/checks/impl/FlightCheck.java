package me.xDark.hybridanticheat.checks.impl;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import me.xDark.hybridanticheat.wrapper.MathHelper;

public class FlightCheck implements Check {

	public static final HashMap<Player, AtomicInteger> floatingTime = new HashMap<>();

	@SuppressWarnings("deprecation")
	@Override
	public void doCheck(User user, Event e) {
		Player p = user.getHandle();
		if (HybridAntiCheat.checkPermission(user.getHandle(), "bypass.flight"))
			return;
		if (p.getAllowFlight() && p.isFlying())
			return;
		if (p.isOnGround() || p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
			user.updateSafeLocation(p.getLocation());
		if (p.isFlying() && !p.getAllowFlight()) {
			p.setFlying(false);
			p.setAllowFlight(false);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
			return;
		}
		double ym = p.getVelocity().getY();
		ym = MathHelper.round(ym, 3);
		double must = HighJumpCheck.getYMotion(p);
		if (ym > 0.0D)
			if (((ym > must) || (ym % 1 == 0.0) || (MathHelper.round(ym, 6) == 0.0D) || ym <= -2D)
					&& !p.getAllowFlight()) {
				p.damage(1);
				Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
				return;
			}
		if (ym < 0 && (ym <= -1.25D && ym >= -2D)) {
			p.damage(1);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
			return;
		}
		PlayerMoveEvent event = CastUtil.cast(e);
		double xm = MathUtil.diff(event.getFrom().getX(), event.getTo().getX()),
				zm = MathUtil.diff(event.getFrom().getZ(), event.getTo().getZ());
		if (MathHelper.round(xm, 3) >= 1.36D || MathHelper.round(zm, 3) >= 1.36D) {
			event.setTo(event.getFrom());
			p.setFlying(false);
			p.setAllowFlight(false);
			p.damage(1);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
			return;
		}
		Block down = p.getLocation().getBlock();
		if (down.getType() != Material.AIR && !p.isOnGround())
			if (floatingTime.get(p).incrementAndGet() >= 40) {
				p.setFlying(false);
				p.setAllowFlight(false);
				p.damage(1);
				event.setTo(event.getFrom());
				Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
				return;
			} else
				floatingTime.get(p).set(0);
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
				&& (event.getFrom().getBlockY() == event.getTo().getBlockY())) {
			if (floatingTime.get(p).incrementAndGet() >= 60) {
				p.setFlying(false);
				p.setAllowFlight(false);
				p.damage(1);
				event.setTo(event.getFrom());
				Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
				return;
			}
		} else
			floatingTime.get(p).set(0);
		if (event.getTo().getY() > event.getFrom().getY() && p.getLocation().distance(user.getSafeLocation()) > 7D) {
			p.damage(p.getHealth() / 2);
			event.setTo(user.getSafeLocation());
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Flight));
		}

	}
}
