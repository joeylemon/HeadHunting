package com.pwncraftpvp.hh.core;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.pwncraftpvp.hh.utils.Utils;

public enum Head {
	
	PLAYER(null, 3, null),
	CHICKEN(EntityType.CHICKEN, 3, "MHF_Chicken"),
	PIG_ZOMBIE(EntityType.PIG_ZOMBIE, 3, "MHF_PigZombie"),
	PIG(EntityType.PIG, 3, "MHF_Pig"),
	COW(EntityType.COW, 3, "MHF_Cow"),
	SHEEP(EntityType.SHEEP, 3, "MHF_Sheep"),
	MOOSHROOM(EntityType.MUSHROOM_COW, 3, "MHF_MushroomCow"),
	SPIDER(EntityType.SPIDER, 3, "MHF_Spider"),
	ZOMBIE(EntityType.ZOMBIE, 2, null),
	SKELETON(EntityType.SKELETON, 0, null),
	VILLAGER(EntityType.VILLAGER, 3, "MHF_Villager"),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, 3, "MHF_LavaSlime"),
	BLAZE(EntityType.BLAZE, 3, "MHF_Blaze"),
	CREEPER(EntityType.CREEPER, 4, null),
	ENDERMAN(EntityType.ENDERMAN, 3, "MHF_Enderman"),
	IRON_GOLEM(EntityType.IRON_GOLEM, 3, "MHF_Golem");
	
	private EntityType entity;
	private int data;
	private String skin;
	Head(EntityType entity, int data, String skin){
		this.entity = entity;
		this.data = data;
		this.skin = skin;
	}
	
	Main main = Main.getInstance();
	String yellow = ChatColor.YELLOW + "";
	String gray = ChatColor.GRAY + "";
	
	private String properName = null;
	private int chanceToDrop = -1;
	private int requiredLevel = -1;
	private int sellPrice = -1;
	private int xpForSelling = -1;
	private int spawnerPrice = -1;
	
	/**
	 * Get the entity type associated with the head
	 * @return The entity type associated with the head
	 */
	public EntityType getEntityType(){
		return entity;
	}
	
	/**
	 * Get the data of the head item to be dropped
	 * @return The data of the head item to be dropped
	 */
	public short getData(){
		return (short)data;
	}
	
	/**
	 * Get the skin of the head item
	 * @return The skin of the head item
	 */
	public String getSkin(){
		return skin;
	}
	
	/**
	 * Get the proper name of the head
	 * @return The proper name of the head
	 */
	public String getName(){
		if(properName == null){
			properName = this.toString().toLowerCase().replaceAll("_", " ");
		}
		return properName;
	}
	
	/**
	 * Get the chance that the head should be dropped
	 * @return The chance that the head should be dropped
	 */
	public float getChanceToDrop(){
		if(chanceToDrop == -1){
			chanceToDrop = main.getConfig().getInt("heads." + this.toString().toLowerCase() + ".chanceToDrop");
		}
		return (chanceToDrop * 0.01F);
	}
	
	/**
	 * Get the required level to sell the head
	 * @return The required level to sell the head
	 */
	public int getRequiredLevel(){
		if(requiredLevel == -1){
			requiredLevel = main.getConfig().getInt("heads." + this.toString().toLowerCase() + ".levelRequired");
		}
		return requiredLevel;
	}
	
	/**
	 * Get the amount of money received for selling the head
	 * @return The amount of money received for selling the head
	 */
	public double getSellPrice(){
		if(sellPrice == -1){
			sellPrice = main.getConfig().getInt("heads." + this.toString().toLowerCase() + ".sellPrice");
		}
		return sellPrice;
	}
	
	/**
	 * Get the sell price for a player
	 * @param player - The player
	 * @return The sell price for the player
	 */
	@SuppressWarnings("deprecation")
	public double getPlayerSellPrice(String player){
		if(this == PLAYER){
			return (main.econ.getBalance(player) * Utils.getPlayerHeadPercentage());
		}else{
			return 0;
		}
	}
	
	/**
	 * Get the xp earned for selling the head
	 * @return The xp earned for selling the head
	 */
	public double getXPForSelling(){
		if(xpForSelling == -1){
			xpForSelling = main.getConfig().getInt("heads." + this.toString().toLowerCase() + ".xpForSelling");
		}
		return xpForSelling;
	}
	
	/**
	 * Get the price of the head's spawner
	 * @return The price of the head's spawner
	 */
	public double getSpawnerPrice(){
		if(spawnerPrice == -1){
			spawnerPrice = main.getConfig().getInt("heads." + this.toString().toLowerCase() + ".spawnerPrice");
		}
		return spawnerPrice;
	}
	
	/**
	 * Get the head's item
	 * @return The head's item
	 */
	public ItemStack getItem(){
		ItemStack item =  Utils.renameItem(new ItemStack(Material.SKULL_ITEM, 1, this.getData()), ChatColor.GREEN + WordUtils.capitalizeFully(this.getName()), ChatColor.GRAY + "Mob Head", 
				yellow + "Sell Price: " + gray + "$" + this.getSellPrice());
		/*
		if(this.getData() == 3 && this.getSkin() != null){
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(this.getSkin());
			item.setItemMeta(meta);
		}
		*/
		return item;
	}
	
	/**
	 * Get the mob spawner item
	 * @return The mob spawner item
	 */
	public ItemStack getSpawnerItem(){
		ItemStack item = Utils.renameItem(new ItemStack(Material.MOB_SPAWNER), gray + WordUtils.capitalizeFully(this.getName()) + " Spawner", 
				yellow + "Required Level: " + gray + this.getRequiredLevel(),
				ChatColor.BLACK + "" + main.spawnerID);
		main.spawnerID++;
		return item;
	}
	
	/**
	 * Get the head from an item
	 * @param item - The item
	 * @return The head from the item
	 */
	public static final Head getFromItemStack(ItemStack item){
		Head head = Head.PLAYER;
		if(item.getType() == Material.SKULL_ITEM && item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
			for(Head h : values()){
				if(h.getName().equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()))){
					head = h;
					break;
				}
			}
		}
		return head;
	}
}
