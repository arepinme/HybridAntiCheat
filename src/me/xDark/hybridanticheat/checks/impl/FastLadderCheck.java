package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;
import me.xDark.hybridanticheat.utils.MathUtil;
import me.xDark.hybridanticheat.wrapper.MathHelper;

public class FastLadderCheck implements Check {

	@SuppressWarnings("deprecation")
	@Override
	public void doCheck(User user, Event e) {
		if (user.getHandle().hasPermission("hac.bypass.fastladder"))
			return;
		PlayerMoveEvent event = CastUtil.cast(e);
		if (event.getTo().getY() <= event.getFrom().getY())
			return;
		Material mat = user.getHandle().getLocation().getBlock().getType();
		double difference = MathUtil.diff(event.getFrom().getY(), event.getTo().getY());
		if ((mat == Material.LADDER || mat == Material.VINE) && !user.getHandle().isOnGround()
				&& MathHelper.round(difference, 3) > 0.118D)
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.FastLadder));
	}

}
