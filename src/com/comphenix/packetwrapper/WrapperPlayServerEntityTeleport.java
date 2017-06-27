package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerEntityTeleport extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

	public WrapperPlayServerEntityTeleport() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerEntityTeleport(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve entity ID.
	 * 
	 * @return The current EID
	 */
	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set entity ID.
	 * 
	 * @param value
	 *            - new value.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve the entity.
	 * 
	 * @param world
	 *            - the current world of the entity.
	 * @return The entity.
	 */
	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/**
	 * Retrieve the entity.
	 * 
	 * @param event
	 *            - the packet event.
	 * @return The entity.
	 */
	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	public double getX() {
		return handle.getIntegers().read(0);
	}

	public void setX(double value) {
		handle.getIntegers().write(0, (int) value);
	}

	public double getY() {
		return handle.getIntegers().read(1);
	}

	public void setY(double value) {
		handle.getIntegers().write(1, (int) value);
	}

	public double getZ() {
		return handle.getIntegers().read(2);
	}

	public void setZ(double value) {
		handle.getIntegers().write(2, (int) value);
	}

	/**
	 * Retrieve the yaw of the current entity.
	 * 
	 * @return The current Yaw
	 */
	public float getYaw() {
		return (handle.getBytes().read(0) * 360.F) / 256.0F;
	}

	/**
	 * Set the yaw of the current entity.
	 * 
	 * @param value
	 *            - new yaw.
	 */
	public void setYaw(float value) {
		handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
	}

	/**
	 * Retrieve the pitch of the current entity.
	 * 
	 * @return The current pitch
	 */
	public float getPitch() {
		return (handle.getBytes().read(1) * 360.F) / 256.0F;
	}

	/**
	 * Set the pitch of the current entity.
	 * 
	 * @param value
	 *            - new pitch.
	 */
	public void setPitch(float value) {
		handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
	}

	public boolean getOnGround() {
		return handle.getBooleans().read(0);
	}

	public void setOnGround(boolean value) {
		handle.getBooleans().write(0, value);
	}
}