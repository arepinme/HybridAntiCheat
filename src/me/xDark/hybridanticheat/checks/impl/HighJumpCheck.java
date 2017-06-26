package me.xDark.hybridanticheat.checks.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;
import me.xDark.hybridanticheat.wrapper.MathHelper;

public class HighJumpCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		if (user.isSleeping())
			return;
		Player p = user.getHandle();
		if (p.getAllowFlight() && p.isFlying())
			return;
		if (p.hasPermission("hac.bypass.highjump"))
			return;
		PlayerMoveEvent moveEvent = CastUtil.cast(e);
		double difference = moveEvent.getTo().getY() - moveEvent.getFrom().getY();
		double originalDifference = getYMotion(p);
		Material fristMaterial = p.getLocation().getBlock().getType(),
				secondMaterial = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

		if (fristMaterial != Material.WATER && secondMaterial != Material.WATER)
			if (difference > originalDifference
					|| ((difference = MathHelper.round(difference, 3)) <= 0.15D && difference >= 0.1D)) {
				moveEvent.getPlayer().teleport(moveEvent.getFrom(), TeleportCause.PLUGIN);
				Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.HighJump));
			}
	}

	@SuppressWarnings("deprecation")
	public static double getYMotion(Player p) {
		double originalDifference = 0.425D;
		if (p.hasPotionEffect(PotionEffectType.JUMP)) {
			PotionEffect jump = null;
			for (PotionEffect potion : p.getActivePotionEffects())
				if (potion.getType().getId() == PotionEffectType.JUMP.getId()) {
					jump = potion;
					break;
				}
			if (jump != null)
				originalDifference += ((double) (jump.getAmplifier() + 1) * 0.1F);
		}
		return originalDifference;
	}

}
