package me.xDark.hybridanticheat.checks;

import java.util.HashMap;

import me.xDark.hybridanticheat.checks.impl.BedCheck;
import me.xDark.hybridanticheat.checks.impl.CriticalsCheck;
import me.xDark.hybridanticheat.checks.impl.FastLadderCheck;
import me.xDark.hybridanticheat.checks.impl.FlightCheck;
import me.xDark.hybridanticheat.checks.impl.HighJumpCheck;
import me.xDark.hybridanticheat.checks.impl.InventoryMoveCheck;
import me.xDark.hybridanticheat.checks.impl.KillAuraCheck;
import me.xDark.hybridanticheat.checks.impl.NoClipCheck;
import me.xDark.hybridanticheat.checks.impl.NoFallCheck;
import me.xDark.hybridanticheat.checks.impl.NoSlowDownCheck;
import me.xDark.hybridanticheat.checks.impl.SpeedHackCheck;

public class CheckManager {

	private static final HashMap<String, Check> checks = new HashMap<>();

	public static void init() {
		checks.clear();
		checks.put("Flight", new FlightCheck());
		checks.put("HighJump", new HighJumpCheck());
		checks.put("SpeedHack", new SpeedHackCheck());
		checks.put("FastLadder", new FastLadderCheck());
		checks.put("KillAura", new KillAuraCheck());
		checks.put("Criticals", new CriticalsCheck());
		checks.put("NoClip", new NoClipCheck());
		checks.put("BedExploit", new BedCheck());
		checks.put("NoSlowDown", new NoSlowDownCheck());
		checks.put("NoFall", new NoFallCheck());
		checks.put("InvMove", new InventoryMoveCheck());
	}

	public static HashMap<String, Check> getChecks() {
		return checks;
	}

	public static Check getCheck(String check) {
		return checks.get(check);
	}

}
