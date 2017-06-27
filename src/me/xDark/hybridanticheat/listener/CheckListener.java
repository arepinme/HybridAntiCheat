package me.xDark.hybridanticheat.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.NoSlowDown))
			CheckManager.getCheck("NoSlowDown").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.NoFall))
			CheckManager.getCheck("NoFall").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.InvMove))
			CheckManager.getCheck("InvMove").doCheck(user, e);
	}

	@EventHandler(ignoreCancelled = true)
	public void onOpen(InventoryOpenEvent e) {
		Player p = (Player) e.getPlayer();
		if (p.hasPermission("hac.bypass.invmove"))
			return;
		User user = HybridAPI.getUser(p);
		if (user == null)
			return;
		user.setInventoryOpen(true);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (p.hasPermission("hac.bypass.invmove"))
			return;
		User user = HybridAPI.getUser(p);
		if (user == null)
			return;
		user.setInventoryOpen(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		User user = HybridAPI.getUser(p);
		if (user == null)
			return;
		user.setInventoryOpen(false);
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
