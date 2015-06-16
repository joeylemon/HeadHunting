package com.pwncraftpvp.hh.core;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * A universal class used throughout my plugins for utilizing player files
 * @author JjPwN1
 */
public class PlayerFile {
	
	Main main = Main.getInstance();
	
	String player;
	public PlayerFile(String p){
		player = p;
	}
	
	File file;
	public PlayerFile(File file){
		this.file = file;
	}
	
	/**
	 * Get the player's file
	 */
	@SuppressWarnings("deprecation")
	public File getFile(){
		if(file != null){
			return file;
		}else{
			return new File(main.getDataFolder() + File.separator + "players", Bukkit.getOfflinePlayer(player).getUniqueId() + ".yml");
		}
	}
	
	/**
	 * Get the player's config
	 */
	public FileConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	/**
	 * Set a value in the player's config
	 * 
	 * @param key - The location of the value to set
	 * @param entry - The value to set
	 */
	public void setConfigValue(String key, Object entry){
		FileConfiguration fc = getConfig();
	    fc.set(key, entry);
	    try{
	      fc.save(getFile());
	    }catch (IOException e) {
	      e.printStackTrace();
	    }
	}

}
