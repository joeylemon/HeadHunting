package com.pwncraftpvp.hh.core;

import org.bukkit.inventory.ItemStack;

public class MobDrop {
	
	private ItemStack item;
	private int money;
	private int chance;
	public MobDrop(ItemStack item, int money, int chance){
		this.item = item;
		this.money = money;
		this.chance = chance;
	}
	
	/**
	 * Get the drop's itemstack
	 * @return The drop's itemstack
	 */
	public ItemStack getItemStack(){
		return item;
	}
	
	/**
	 * Get the drop's money
	 * @return The drop's money
	 */
	public int getMoney(){
		return money;
	}
	
	/**
	 * Get the drop's chance
	 * @return The drop's chance
	 */
	public int getChance(){
		return chance;
	}

}
