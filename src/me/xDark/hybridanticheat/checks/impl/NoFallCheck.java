package me.xDark.hybridanticheat.checks.impl;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.checks.Check;

public class NoFallCheck implements Check {
	
	public static final HashMap<Player, Float> fallDistance = new HashMap<>();

	@Override
	public void doCheck(User user, Event e) {
		
	}

}
