package com.pwncraftpvp.hh.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.hh.core.Main;
import com.pwncraftpvp.hh.utils.Utils;

public class CannonCooldownTask extends BukkitRunnable {
	
	private Main main = Main.getInstance();
	
	private Player player;
	public CannonCooldownTask(Player player){
		this.player = player;
	}
	
	private int time = Utils.getCannonCooldown();
	private int runtime = 0;
	
	public void run(){
		if((time - runtime) > 0){
			runtime++;
		}else{
			if(main.cannon.containsKey(player.getName())){
				main.cannon.remove(player.getName());
			}
			this.cancel();
		}
	}
	
	/**
	 * Get the time left in the cooldown
	 * @return The time, in seconds, left in the cooldown
	 */
	public int getTimeLeft(){
		return (time - runtime);
	}

}
