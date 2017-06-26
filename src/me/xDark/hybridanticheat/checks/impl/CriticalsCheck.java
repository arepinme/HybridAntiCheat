package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;

public class CriticalsCheck implements Check {

	@SuppressWarnings("deprecation")
	@Override
	public void doCheck(User user, Event e) {
		Player p = user.getHandle();
		if (p.hasPermission("hac.bypass.criticals"))
			return;
		EntityDamageByEntityEvent event = CastUtil.cast(e);
		if (!p.getAllowFlight() && !p.isOnGround() && p.getLocation().getY() % 1 == 0.0
				&& p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
			event.setCancelled(true);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Criticals));
		}
	}
}
