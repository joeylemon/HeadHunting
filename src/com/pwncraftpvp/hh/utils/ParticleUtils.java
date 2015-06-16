package com.pwncraftpvp.hh.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.v1_8_R3.EnumParticle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * A universal class used throughout my plugins for particle effects
 * @author JjPwN1
 */
public class ParticleUtils {
	
	/**
	 * Send a particle effect to a player
	 * @param effect - The particle effect to send
	 * @param player - The player to send the effect to
	 * @param location - The location to send the effect to
	 * @param offsetX - The x range of the particle effect
	 * @param offsetY - The y range of the particle effect
	 * @param offsetZ - The z range of the particle effect
	 * @param speed - The speed (or color depending on the effect) of the particle effect
	 * @param count - The count of effects
	 */
	public static void sendToPlayer(ParticleEffect effect, Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
		try {
			Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
 
	/**
	 * Send a particle effect to all players
	 * @param effect - The particle effect to send
	 * @param location - The location to send the effect to
	 * @param offsetX - The x range of the particle effect
	 * @param offsetY - The y range of the particle effect
	 * @param offsetZ - The z range of the particle effect
	 * @param speed - The speed (or color depending on the effect) of the particle effect
	 * @param count - The count of effects
	 */
	public static void sendToLocation(ParticleEffect effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
		try {
			Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
			for (Player player : Bukkit.getOnlinePlayers()) {
				sendPacket(player, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a PacketPlayOutWorldParticles packet to send to a player
	 * @param effect - The particle effect to send
	 * @param location - The location to send the effect to
	 * @param offsetX - The x range of the particle effect
	 * @param offsetY - The y range of the particle effect
	 * @param offsetZ - The z range of the particle effect
	 * @param speed - The speed (or color depending on the effect) of the particle effect
	 * @param count - The count of effects
	 * @return The packet object
	 * @throws Exception
	 */
	private static Object createPacket(ParticleEffect effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (count <= 0) {
			count = 1;
		}
		Class<?> packetClass = getCraftClass("PacketPlayOutWorldParticles");
		/*Object packet = packetClass.getConstructor(String.class, float.class, float.class, float.class, float.class,
				float.class, float.class, float.class, int.class).newInstance(effect.name, (float) location.getX(),
				(float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count);
				*/
        Object packet = packetClass.getConstructor(EnumParticle.class, boolean.class, float.class,
                float.class, float.class, float.class, float.class, float.class, float.class, int.class,
                int[].class).newInstance(effect.getEnumParticle(), true, (float) location.getX(), (float) location.getY(),
                        (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, null);
		return packet;
	}
	
	/**
	 * Send a packet to a player
	 * @param p - The player to send a packet to
	 * @param packet - The packet to send to the player
	 * @throws Exception
	 */
	private static void sendPacket(Player p, Object packet) throws Exception {
		Object eplayer = getHandle(p);
		Field playerConnectionField = eplayer.getClass().getField("playerConnection");
		Object playerConnection = playerConnectionField.get(eplayer);
		for (Method m : playerConnection.getClass().getMethods()) {
			if (m.getName().equalsIgnoreCase("sendPacket")) {
				m.invoke(playerConnection, packet);
				return;
			}
		}
	}
	
	/**
	 * Get the handle of an entity
	 * @param entity - The entity to get the handle of
	 * @return The handle of the entity
	 */
	private static Object getHandle(Entity entity) {
		try {
			Method entity_getHandle = entity.getClass().getMethod("getHandle");
			Object nms_entity = entity_getHandle.invoke(entity);
			return nms_entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get an NMS class
	 * @param name - The name of the NMS class
	 * @return The NMS class
	 */
	private static Class<?> getCraftClass(String name) {
		String version = getVersion() + ".";
		String className = "net.minecraft.server." + version + name;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}
	
	/**
	 * Get the version of the server software
	 * @return The version of the server software
	 */
	private static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

}
