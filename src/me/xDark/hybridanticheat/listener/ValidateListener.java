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
		HybridAntiCheat.instance().notify("Игрок §6" + e.getUser().getHandle().getName()
				+ " §fпопытался использовать §c" + e.getCheckType().name() + "§f. §cVL: " + vl, "hac.notify.staff");
		if (vl >= HybridAntiCheat.instance().getSettings().getMaxVL()) {
			e.getUser().resetVL();
			e.getUser().getHandle().getWorld().strikeLightningEffect(e.getUser().getHandle().getLocation());
			HybridAntiCheat.instance().notify(
					"Игрок §6" + e.getUser().getHandle().getName() + " §fдостиг максимального числа проверок: " + vl,
					"hac.notify.staff");
			HybridAPI.performActions(e.getUser(), e.getCheckType());
		}
	}
}
