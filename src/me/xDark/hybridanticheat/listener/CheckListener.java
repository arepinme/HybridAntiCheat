package me.xDark.hybridanticheat.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.CheckManager;
import me.xDark.hybridanticheat.events.ValidateEvent;

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
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.InvalidAction))
			CheckManager.getCheck("BedExploit").doCheck(user, e);
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

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerBedEnterEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("hac.bypass.bed"))
			return;
		User user = HybridAPI.getUser(p);
		if (user == null)
			return;
		if (user.isSleeping()) {
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.InvalidAction));
			HybridAPI.performActions(user, CheckType.InvalidAction);
		} else
			HybridAPI.getUser(p).setSleeping(true);
	}

	@EventHandler
	public void onBedEnter(PlayerBedLeaveEvent e) {
		HybridAPI.getUser(e.getPlayer()).setSleeping(false);
	}

	@EventHandler
	public void onToggle(PlayerToggleFlightEvent e) {
		User user = HybridAPI.getUser(e.getPlayer());
		if (user != null)
			user.updateSafeLocation(e.getPlayer().getLocation());
	}
}
