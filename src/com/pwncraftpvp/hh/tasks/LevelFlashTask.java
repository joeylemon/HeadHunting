package com.pwncraftpvp.hh.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LevelFlashTask extends BukkitRunnable {
	
	private Player player;
	public LevelFlashTask(Player player){
		this.player = player;
	}
	
	private boolean running = true;
	
	private int count = 0;
	private boolean full = false;
	
	public void run(){
		if(count < 5){
			if(full == true){
				player.setExp(1F);
				full = false;
			}else{
				player.setExp(0F);
				full = true;
			}
			count++;
		}else{
			this.cancelTask();
		}
	}
	
	/**
	 * Cancel the task and perform necessary functions
	 */
	public void cancelTask(){
		player.setExp(1F);
		this.cancel();
		running = false;
	}
	
	/**
	 * Check if the task is running
	 * @return True if it is running, false if it is cancelled
	 */
	public boolean isRunning(){
		return running;
	}

}
