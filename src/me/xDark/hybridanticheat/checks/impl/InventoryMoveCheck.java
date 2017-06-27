package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;

public class InventoryMoveCheck implements Check {
	
	@Override
	public void doCheck(User user, Event e) {
		if (HybridAntiCheat.checkPermission(user.getHandle(), "bypass.invmove"))
			return;
		if (user.isInventoryOpen()) {
			PlayerMoveEvent event = CastUtil.cast(e);
			event.setTo(event.getFrom());
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.InvMove));
		}
	}
}
