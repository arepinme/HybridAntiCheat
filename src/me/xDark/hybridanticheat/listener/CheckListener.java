package me.xDark.hybridanticheat.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.CheckManager;

public class CheckListener implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		User user = HybridAPI.getUser(e.getPlayer());
		if (user == null)
			return;
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.HighJump))
			CheckManager.getCheck("HighJump").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.SpeedHack))
			CheckManager.getCheck("SpeedHack").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.SpeedHack))
			CheckManager.getCheck("SpeedHack").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.FastLadder))
			CheckManager.getCheck("FastLadder").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Flight))
			CheckManager.getCheck("Flight").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.NoClip))
			CheckManager.getCheck("NoClip").doCheck(user, e);
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType() != EntityType.PLAYER)
			return;
		User user = HybridAPI.getUser(e.getDamager());
		if (user == null)
			return;
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Criticals))
			CheckManager.getCheck("Criticals").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.KillAura))
			CheckManager.getCheck("KillAura").doCheck(user, e);
	}
}
