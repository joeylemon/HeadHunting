package com.pwncraftpvp.hh.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.pwncraftpvp.hh.tasks.LevelFlashTask;
import com.pwncraftpvp.hh.utils.Help;
import com.pwncraftpvp.hh.utils.ParticleEffect;
import com.pwncraftpvp.hh.utils.ParticleUtils;
import com.pwncraftpvp.hh.utils.TextUtils;
import com.pwncraftpvp.hh.utils.Utils;

public class HPlayer {
	
	Main main = Main.getInstance();
	String yellow = ChatColor.YELLOW + "";
	String gray = ChatColor.GRAY + "";
	
	Player player = null;
	OfflinePlayer offlineplayer = null;
	PlayerFile file = null;
	public HPlayer(Player player){
		this.player = player;
		this.offlineplayer = player;
		this.file = new PlayerFile(player.getName());
	}
	
	@SuppressWarnings("deprecation")
	public HPlayer(String player){
		this.offlineplayer = Bukkit.getOfflinePlayer(player);
		this.file = new PlayerFile(player);
	}
	
	/**
	 * Get the player's file
	 */
	public PlayerFile getFile(){
		return file;
	}
	
	/**
	 * Send a message header to the player
	 * @param header - The header to be sent
	 */
	public void sendMessageHeader(String header){
		player.sendMessage(TextUtils.centerText(yellow + "-=-(" + gray + TextUtils.getDoubleArrow() + yellow + ")-=-" + "  " + gray + header + "  " + yellow + "-=-(" + gray + TextUtils.getBackwardsDoubleArrow()
				+ yellow + ")-=-"));
	}
	
	/**
	 * Send a message to the player
	 * @param message - The message to be sent
	 */
	public void sendMessage(String message){
		player.sendMessage(ChatColor.GOLD + TextUtils.getArrow() + yellow + " " + message);
	}
	
	/**
	 * Send an error message to the player
	 * @param error - The error message to be sent
	 */
	public void sendError(String error){
		player.sendMessage(ChatColor.GOLD + TextUtils.getArrow() + ChatColor.DARK_RED + " " + error);
	}
	
	/**
	 * Send the player command help
	 * @param help - The type of help to send the player
	 */
	public void sendCommandHelp(Help help){
		if(help == Help.GENERAL){
			this.sendMessageHeader("Help v" + main.getDescription().getVersion());
			this.sendMessage(gray + "/hh reload - " + yellow + "Reload the configuration.");
			this.sendMessage(gray + "/hh booster enable <time> <multiplier> - " + yellow + "Enable a booster.");
			player.sendMessage(ChatColor.GOLD + "  - " + yellow + "Example: " + gray + "/hh booster enable 1:30 1.5");
			this.sendMessage(gray + "/hh booster disable - " + yellow + "Disable the active booster.");
			this.sendMessage(gray + "/hh set level <player> <level> - " + yellow + "Set a player's level.");
			this.sendMessage(gray + "/hh get level <player> - " + yellow + "Get a player's level.");
			this.sendMessage(gray + "/hh createregion - " + yellow + "Create a level region.");
		}
	}
	
	/**
	 * Check if the player has an available slot
	 * @return True if they do, false if not
	 */
	public boolean isInventoryFull(){
		boolean full = true;
		for(int i = 0; i <= 35; i++){
			if(player.getInventory().getItem(i) == null){
				full = false;
				break;
			}
		}
		return full;
	}
	
	/**
	 * Get the balance of the player
	 * @return The balance of the player
	 */
	public double getBalance(){
		return main.econ.getBalance(offlineplayer);
	}
	
	/**
	 * Give the player money
	 * @param value - The amount of money to give
	 */
	public void giveMoney(double value){
		if(offlineplayer.getName().contains("skull_of") == false){
			main.econ.depositPlayer(offlineplayer, value);
		}
	}
	
	/**
	 * Take money from the player
	 * @param value - The amount of money to take
	 */
	public void removeMoney(double value){
		main.econ.withdrawPlayer(offlineplayer, value);
	}
	
	/**
	 * Get the player's level
	 * @return The player's level
	 */
	public int getLevel(){
		return file.getConfig().getInt("stats.level");
	}
	
	/**
	 * Set the player's level
	 * @param xp - The player's level
	 */
	public void setLevel(int level){
		file.setConfigValue("stats.level", level);
		
		if(player != null){
			player.setLevel(level);
		}
	}
	
	/**
	 * Get the player's xp
	 * @return The player's xp
	 */
	public double getXP(){
		return file.getConfig().getDouble("stats.xp");
	}
	
	/**
	 * Set the player's xp
	 * @param xp - The player's xp
	 */
	public void setXP(double xp){
		file.setConfigValue("stats.xp", xp);
		
		if(player != null){
			float percent = (float)((Utils.getPercent(this.getXP(), Utils.getLevelRequiredXP(this.getLevel() + 1))) * (0.01F));
			player.setExp(percent);
		}
	}
	
	/**
	 * Add xp to the player
	 * @param xp - The amount of xp to add
	 */
	public void addXP(double xp){
		if(this.getLevel() < 15){
			double newxp = (this.getXP() + xp);
			double requiredxp = Utils.getLevelRequiredXP(this.getLevel() + 1);
			if(newxp < requiredxp){
				this.setXP(newxp);
			}else{
				this.setXP(requiredxp);
				if(player != null){
					if(!main.levelUpMessage.containsKey(player.getName())){
						this.sendMessage("You are able to level up! Type " + gray + "/levelup" + yellow + "! (Cost: " + gray + "$" + Utils.getLevelPrice(this.getLevel() + 1) + yellow + ")");
						LevelFlashTask task = new LevelFlashTask(player);
						task.runTaskTimer(main, 0, 7);
						main.levelUpMessage.put(player.getName(), task);
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
							public void run(){
								main.levelUpMessage.remove(player.getName());
							}
						}, 600);
					}
				}
			}
		}
	}
	
	/**
	 * Level the player up
	 */
	public void levelUp(){
		if(this.getLevel() < 15){
			if(main.levelUpMessage.containsKey(player.getName())){
				if(main.levelUpMessage.get(player.getName()).isRunning() == true){
					main.levelUpMessage.get(player.getName()).cancelTask();
				}
			}
			this.setXP(0);
			int newlevel = this.getLevel() + 1;
			this.setLevel(newlevel);
			ParticleUtils.sendToLocation(ParticleEffect.GREEN_SPARKLE, player.getEyeLocation().subtract(0, .25, 0), .6F, .6F, .6F, 0F, 15);
			Utils.launchFirework(player.getEyeLocation().subtract(0, .25, 0));
			//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cratekey give " + player.getName() + " Monster 1");
			
			this.sendMessage("You are now level " + gray + newlevel + yellow + "!");
			String mob = "";
			for(Head h : Head.values()){
				if(h.getRequiredLevel() == newlevel){
					mob = h.getName();
					break;
				}
			}
			player.sendMessage(ChatColor.GOLD + "  - " + gray + "You now have access to " + mob + " spawners and heads!");
		}
	}
	
	/**
	 * Get the time, in milliseconds, the player has last used /fixall
	 * @return The time, in milliseconds, the player has last used /fixall
	 */
	public long getLastRepaired(){
		return file.getConfig().getLong("fixall");
	}
	
	/**
	 * Set the last time, in milliseconds, the player has used /fixall
	 * @param repaired - The last time, in milliseconds, the player has used /fixall
	 */
	public void setLastRepaired(long repaired){
		file.setConfigValue("fixall", repaired);
	}
	
	/**
	 * Check if the player can use /fixall
	 * @return True if the player can, false if not
	 */
	public boolean canRepair(){
		if((System.currentTimeMillis() - this.getLastRepaired()) > 0){
			return true;
		}else{
			return false;
		}
	}
}
