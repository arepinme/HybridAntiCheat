package me.xDark.hybridanticheat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.events.ValidateEvent;

public class ValidateListener implements Listener {

	@EventHandler
	public void onValidate(ValidateEvent e) {
		if (e.getUser() == null)
			return;
		int vl = e.getUser().incrementVL();
		HybridAntiCheat.instance().notify("Player §6" + e.getUser().getHandle().getName() + " §ffailed §c"
				+ e.getCheckType().name() + "§f. §cVL: " + vl, "hac.notify.staff");
		if (vl >= HybridAntiCheat.instance().getSettings().getMaxVL()) {
			e.getUser().resetVL();
			e.getUser().getHandle().getWorld().strikeLightningEffect(e.getUser().getHandle().getLocation());
			HybridAntiCheat.instance().notify(
					"Player §6" + e.getUser().getHandle().getName() + "§r was kicked for §c" + e.getCheckType().name(),
					"hac.notify.staff");
			HybridAPI.disconnectUser(e.getUser(), e.getCheckType());
		}
	}
}
