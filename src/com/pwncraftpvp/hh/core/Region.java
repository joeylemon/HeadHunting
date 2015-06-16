package com.pwncraftpvp.hh.core;

import org.bukkit.Location;

public class Region {
	
	private int step = 1;
	
	private Location pos1;
	private Location pos2;
	private int level;
	public Region(Location pos1, Location pos2, int level){
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.level = level;
	}
	
	/**
	 * Set a position
	 * @param pos - The position to set
	 * @param loc - The location to set it to
	 */
	public void setPosition(int pos, Location loc){
		if(pos == 1){
			pos1 = loc;
		}else{
			pos2 = loc;
		}
	}
	
	/**
	 * Get a position
	 * @param pos - The position to get (1 or 2)
	 * @return The position location
	 */
	public Location getPosition(int pos){
		if(pos == 1){
			return pos1;
		}else{
			return pos2;
		}
	}
	
	/**
	 * Set the level required to enter
	 * @param level - The level required to enter
	 */
	public void setLevel(int level){
		this.level = level;
	}
	
	/**
	 * Get the level required to enter
	 * @return The level required to enter
	 */
	public int getLevel(){
		return level;
	}
	
	/**
	 * Check if a location is in the region
	 * @param loc - The location to check
	 * @return True if the location is in the region, false if not
	 */
	public boolean isInRegion(Location loc){
		double highX = 0, lowX = 0, highZ = 0, lowZ = 0;
		if(pos1.getX() > pos2.getX()){
			highX = pos1.getX();
		}else if(pos2.getX() > pos1.getX()){
			highX = pos2.getX();
		}
		if(pos1.getX() < pos2.getX()){
			lowX = pos1.getX();
		}else if(pos2.getX() < pos1.getX()){
			lowX = pos2.getX();
		}
		if(pos1.getZ() > pos2.getZ()){
			highZ = pos1.getZ();
		}else if(pos2.getZ() > pos1.getZ()){
			highZ = pos2.getZ();
		}
		if(pos1.getZ() < pos2.getZ()){
			lowZ = pos1.getZ();
		}else if(pos2.getZ() < pos1.getZ()){
			lowZ = pos2.getZ();
		}
		if(loc.getX() >= lowX && loc.getX() <= highX && loc.getZ() >= lowZ && loc.getZ() <= highZ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Get the creation step
	 * @return The creation step
	 */
	public int getStep(){
		return step;
	}
	
	/**
	 * Set the creation step
	 * @param step - The creation step
	 */
	public void setStep(int step){
		this.step = step;
	}

}
