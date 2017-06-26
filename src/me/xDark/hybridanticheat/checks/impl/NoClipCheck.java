package me.xDark.hybridanticheat.checks.impl;

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

public class NoClipCheck implements Check {

	@SuppressWarnings("deprecation")
	@Override
	public void doCheck(User user, Event e) {
		if (HybridAntiCheat.checkPermission(user.getHandle(), "bypass.fastladder"))
			return;
		Player p = user.getHandle();
		if (user.isSleeping())
			return;
		PlayerMoveEvent event = CastUtil.cast(e);
		if (p.isOnGround() && event.getTo().getY() < event.getFrom().getY()
				&& p.getLocation().getBlock().getType() != Material.AIR) {
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.NoClip));
		}
	}

}
