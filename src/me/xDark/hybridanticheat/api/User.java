package me.xDark.hybridanticheat.api;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.bot.FakeBot;
import me.xDark.hybridanticheat.utils.Timer;

public class User {

	private final Player handle;

	private final HashMap<String, Object> values = Maps.<String, Object>newHashMap();

	private final Timer packetTimer = new Timer(), attackTimer = new Timer(), reportTimer = new Timer(),
			sneakTimer = new Timer(), inventoryTmier = new Timer(), pingTimer = new Timer();

	private int oldPing, newPing;

	private final FakeBot bot;

	public User(Player handle) {
		this.handle = handle;
		init();
		List<Player> random = Lists.newArrayList(Bukkit.getOnlinePlayers());
		bot = new FakeBot(handle, random.get(ThreadLocalRandom.current().nextInt(random.size())));
		random.clear();
	}

	public void init() {
		gc();
		values.put("sentPackets", new AtomicInteger(0));
		values.put("lastReceivedUpdatePacket", System.currentTimeMillis());
		values.put("vl", new AtomicInteger(0));
		values.put("verbose", false);
		values.put("safeLocation", handle.getLocation());
		values.put("sleeping", false);
		values.put("invOpen", false);
		reportTimer.setStartMS(System.currentTimeMillis() - HybridAntiCheat.instance().getSettings().getReportDelay());
		oldPing = newPing = HybridAPI.getPing(handle);
		values.put("init", System.currentTimeMillis());
	}

	public void gc() {
		if (bot != null)
			bot.destory();
		values.clear();
	}

	@Override
	protected void finalize() throws Throwable {
		gc();
		super.finalize();
	}

	public long initTime() {
		return System.currentTimeMillis() - (long) values.get("init");
	}

	public boolean isVerbose() {
		return ((boolean) values.get("verbose"));
	}

	public void setVerbose(boolean verbose) {
		values.put("verbose", verbose);
	}

	public boolean updateVerbose() {
		values.put("verbose", !isVerbose());
		return isVerbose();
	}

	public int getSentPackets() {
		return ((AtomicInteger) values.get("sentPackets")).get();
	}

	public int incrementSentPackets() {
		return ((AtomicInteger) values.get("sentPackets")).incrementAndGet();
	}

	public void resetSentPackets() {
		((AtomicInteger) values.get("sentPackets")).set(0);
	}

	public long getLastUpdatePacket() {
		return ((long) values.get("lastReceivedUpdatePacket"));
	}

	public void updateLastUpdatePacket() {
		values.put("lastReceivedUpdatePacket", System.currentTimeMillis());
	}

	public boolean isFrozen() {
		return (System.currentTimeMillis() - getLastUpdatePacket()) >= 5000L;
	}

	public int getVL() {
		return ((AtomicInteger) values.get("vl")).get();
	}

	public int incrementVL() {
		return ((AtomicInteger) values.get("vl")).incrementAndGet();
	}

	public void resetVL() {
		((AtomicInteger) values.get("vl")).set(0);
	}

	public Location getSafeLocation() {
		return ((Location) values.get("safeLocation"));
	}

	public void updateSafeLocation(Location safe) {
		values.remove("safeLocation");
		values.put("safeLocation", safe);
	}

	public boolean isFloodingSneak() {
		return !sneakTimer.hasMSPassed(60L);
	}

	public void resetSneak() {
		sneakTimer.reset();
	}

	public boolean hasReportTimePassed() {
		return reportTimer.hasMSPassed(HybridAntiCheat.instance().getSettings().getReportDelay());
	}

	public boolean isSleeping() {
		return values.get("sleeping") == null ? false : ((boolean) values.get("sleeping"));
	}

	public void setSleeping(boolean sleep) {
		values.put("sleeping", sleep);
	}

	public boolean isInventoryOpen() {
		return values.get("invOpen") == null ? false
				: ((boolean) values.get("invOpen")) && inventoryTmier.hasMSPassed(300L);
	}

	public void setInventoryOpen(boolean open) {
		inventoryTmier.reset();
		values.put("invOpen", open);
	}

	public boolean isFlooding() {
		if (packetTimer.hasMSPassed(1000L)) {
			packetTimer.reset();
			resetSentPackets();
			return false;
		} else {
			return getSentPackets() >= 400;
		}
	}
	
	public boolean shouldUpdatePing() {
		if (pingTimer.hasMSPassed(5000L)) {
			pingTimer.reset();
			return true;
		}
		return false;
	}
	
	public void updatePing() {
		oldPing = newPing;
		newPing = HybridAPI.getPing(handle);
	}

	public int getOldPing() {
		return oldPing;
	}

	public int getNewPing() {
		return newPing;
	}

	public Player getHandle() {
		return handle;
	}

	public FakeBot getBot() {
		return bot;
	}

	public Timer getAttackTimer() {
		return attackTimer;
	}

	public Timer getReportTimer() {
		return reportTimer;
	}

}
