package me.xDark.hybridanticheat.bot;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;

import me.xDark.hybridanticheat.utils.Timer;

public class FakeBot {

	private final Player packetReceiver, source;

	private boolean spawned, wasAttacked;

	private final Timer ticksExistedTimer = new Timer();

	private WrapperPlayServerNamedEntitySpawn spawnPacket;

	private final int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE) + 150;

	private final double distance = ThreadLocalRandom.current().nextDouble();

	public FakeBot(Player receiver, Player source) {
		packetReceiver = receiver;
		this.source = source;
	}

	public Player getPacketReceiver() {
		return packetReceiver;
	}

	public Player getSource() {
		return source;
	}

	public void spawn() {
		spawnPacket = new WrapperPlayServerNamedEntitySpawn();
		spawnPacket.setEntityID(id);
		spawnPacket.setPosition(packetReceiver.getLocation().toVector().add(new Vector(0, 2, 0)));
		spawnPacket.setPlayerUUID(source.getUniqueId().toString());
		spawnPacket.sendPacket(packetReceiver);
		spawned = true;
		ticksExistedTimer.reset();
	}

	public void runTick() {
		moveAround();
	}

	private void move(Location loc) {
		getTeleportPacket(id, loc).sendPacket(packetReceiver);
	}

	private void moveAround() {
		move(getAroundPos(packetReceiver, 90, distance));
	}

	private static Location getAroundPos(Player p, double angle, double distance) {
		Location loc = p.getLocation().clone();
		double realAngle = angle + 90;

		float deltaX = (float) (distance * Math.cos(Math.toRadians(loc.getYaw() + realAngle)));
		float deltaZ = (float) (distance * Math.sin(Math.toRadians(loc.getYaw() + realAngle)));

		loc.add(deltaX, -0.1, deltaZ);

		return loc;
	}

	public void destory() {
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] { id });
		destroy.sendPacket(packetReceiver);
		spawned = false;
	}

	public long getTicksExisted() {
		return ticksExistedTimer.getMSPassed();
	}

	public void onAttack() {
		wasAttacked = true;
	}

	public boolean wasAttacked() {
		return wasAttacked;
	}

	public int getId() {
		return id;
	}

	public boolean isSpawned() {
		return spawned;
	}

	private WrapperPlayServerEntityTeleport getTeleportPacket(int entityId, Location loc) {
		WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
		packet.setEntityID(entityId);
		packet.setX(loc.getX());
		packet.setY(loc.getY());
		packet.setZ(loc.getZ());
		packet.setPitch(loc.getPitch());
		packet.setYaw(loc.getYaw());

		return packet;
	}

}
