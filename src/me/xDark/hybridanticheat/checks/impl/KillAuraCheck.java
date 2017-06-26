package me.xDark.hybridanticheat.checks.impl;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.CastUtil;

public class KillAuraCheck implements Check {

	@Override
	public void doCheck(User user, Event e) {
		if (HybridAntiCheat.checkPermission(user.getHandle(), "bypass.killaura"))
			return;
		Player p = user.getHandle();
		EntityDamageByEntityEvent event = CastUtil.cast(e);
		if (event.getEntity() == p) {
			event.setCancelled(true);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.KillAura));
			HybridAPI.performActions(user, CheckType.KillAura);
			return;
		}
		if (p.getLocation().distance(event.getEntity().getLocation()) > 4.6D) {
			event.setCancelled(true);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.KillAura));
		}
		if (!isLookingAtEntity(p, event.getEntity())) {
			event.setCancelled(true);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.KillAura));
		}
		if (!user.getAttackTimer().hasMSPassed(90L)) {
			event.setCancelled(true);
			Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.KillAura));
		} else
			user.getAttackTimer().reset();
	}

	private static boolean isLookingAtEntity(Player damager, Entity damaged) {
		return getNearestEntityInSight(damager, 10) == damaged;
	}

	// https://www.spigotmc.org/threads/get-player-a-player-is-looking-at.212814/

	private static Entity getNearestEntityInSight(Player player, int range) {
		ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
		ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight((Set<Material>) null, range);
		ArrayList<Location> sight = new ArrayList<Location>();
		for (int i = 0; i < sightBlock.size(); i++)
			sight.add(sightBlock.get(i).getLocation());
		for (int i = 0; i < sight.size(); i++) {
			for (int k = 0; k < entities.size(); k++) {
				if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
					if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
						if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
							return entities.get(k);
						}
					}
				}
			}
		}
		return null;
	}
}
