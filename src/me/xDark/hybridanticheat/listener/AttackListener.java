package me.xDark.hybridanticheat.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.CheckManager;

public class AttackListener implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType() != EntityType.PLAYER)
			return;
		User user = HybridAPI.getUser(e.getDamager());
		if (user == null)
			return;
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Criticals))
			CheckManager.getCheck("Criticals").doCheck(user, e);
		if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.KillAura)) {
			
		}
	}
}
