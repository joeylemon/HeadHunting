package com.pwncraftpvp.hh.utils;

import net.minecraft.server.v1_8_R3.EnumParticle;

/**
 * A universal class used throughout my plugins for particle effects
 * @author JjPwN1
 */
public enum ParticleEffect {
	 
	HUGE_EXPLODE(EnumParticle.EXPLOSION_HUGE), 
	LARGE_EXPLODE(EnumParticle.EXPLOSION_LARGE), 
	FIREWORK_SPARK(EnumParticle.FIREWORKS_SPARK), 
	AIR_BUBBLE(EnumParticle.WATER_BUBBLE),
	DARK_DOTS(EnumParticle.TOWN_AURA), 
	CRITICAL_HIT(EnumParticle.CRIT), 
	MAGIC_CRITICAL_HIT(EnumParticle.CRIT_MAGIC), 
	BLACK_SQUIGGLE(EnumParticle.SPELL_MOB), 
	TRANSPARENT_BLACK_SQUIGGLE(EnumParticle.SPELL_MOB_AMBIENT), 
	WHITE_SQUIGGLE(EnumParticle.SPELL), 
	WHITE_SPARKLE(EnumParticle.SPELL_INSTANT), 
	BLUE_SPARKLE(EnumParticle.SPELL_WITCH), 
	NOTE_BLOCK(EnumParticle.NOTE), 
	ENDER(EnumParticle.PORTAL),
	EXPLODE(EnumParticle.EXPLOSION_NORMAL), 
	FLAME(EnumParticle.FLAME), 
	LAVA_SPARK(EnumParticle.LAVA), 
	FOOTSTEP(EnumParticle.FOOTSTEP), 
	SPLASH(EnumParticle.WATER_SPLASH), 
	LARGE_SMOKE(EnumParticle.SMOKE_LARGE), 
	CLOUD(EnumParticle.CLOUD), 
	REDSTONE_POWERED(EnumParticle.REDSTONE), 
	SNOWBALL_HIT(EnumParticle.SNOWBALL), 
	DRIP_WATER(EnumParticle.DRIP_WATER), 
	DRIP_LAVA(EnumParticle.DRIP_LAVA), 
	SNOW_DIG(EnumParticle.SNOW_SHOVEL),
	HEART(EnumParticle.HEART), 
	BROKEN_HEART(EnumParticle.VILLAGER_ANGRY), 
	GREEN_SPARKLE(EnumParticle.VILLAGER_HAPPY),
	BARRIER(EnumParticle.BARRIER),
	MOB_APPEARANCE(EnumParticle.MOB_APPEARANCE),
	WATER_WAKE(EnumParticle.WATER_WAKE);
 
	private EnumParticle particle;
 
	ParticleEffect(EnumParticle particle) {
		this.particle = particle;
	}
 
	/**
	 * Gets the EnumParticle of the particle effect
	 * @return The EnumParticle of the particle effect
	 */
	public EnumParticle getEnumParticle() {
		return particle;
	}
}
