package me.xDark.hybridanticheat.listener;

import static me.xDark.hybridanticheat.api.HybridAPI.registerPlayer;
import static me.xDark.hybridanticheat.api.HybridAPI.unregisterPlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.xDark.hybridanticheat.api.HybridAPI;

public class UserListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		HybridAPI.actionsPerformed.remove(e.getPlayer().getName());
		registerPlayer(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		unregisterPlayer(e.getPlayer());
	}
}
