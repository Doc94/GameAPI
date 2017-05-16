package com.scarabcoder.gameapi.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Area {
	
	private Location loc1;
	
	private ArenaSettings settings;
	
	private Location loc2;
	
	private String name;
	
	/**
	 * Define an area to make checking for certain events easier.
	 * You can also define areas to have their own settings (Area.inheritSettingsFromGame() and Area.getSettings())
	 * Loc1 and loc2 will be re-written so that loc1 is always the "lowest" position, and loc2 to be the "highest"
	 * 
	 * @param loc1 First corner of the area
	 * @param loc2 Second corner of the area
	 * @param name The name for this area
	 */
	public Area(Location loc1, Location loc2, String name){
		
		double x1 = Math.min(loc1.getX(), loc2.getX());
		double y1 = Math.min(loc1.getY(), loc2.getY());
		double z1 = Math.min(loc1.getZ(), loc2.getZ());
		
		double x2 = Math.max(loc1.getX(), loc2.getX());
		double y2 = Math.max(loc1.getY(), loc2.getY());
		double z2 = Math.max(loc1.getZ(), loc2.getZ());
		
		this.loc1 = new Location(loc1.getWorld(), x1, y1, z1);
		this.loc2 = new Location(loc2.getWorld(), x2, y2, z2);
		this.name = name;
		this.settings = new ArenaSettings();
	}
	
	
	/**
	 * Checks whether or not a player is in the area.
	 * @return True if player is in area, false otherwise.
	 */
	public boolean isPlayerInArea(){
		return false;
	}
	
	/**
	 * Returns a list of players in the area.
	 * @return List<GamePlayer> players.
	 */
	public List<GamePlayer> getPlayersInArea(){
		return new ArrayList<GamePlayer>();
	}
	
	/**
	 * Get the settings for this area.
	 * @return ArenaSettings for this area.
	 */
	public ArenaSettings getSettings(){
		return settings;
	}
	
	/**
	 * Get the first (corner) location of this area.
	 * @return Location object.
	 */
	public Location getLocation1(){
		return loc1;
	}
	
	/**
	 * Get the second (corner) location of this area.
	 * @return Location object.
	 */
	public Location getLocation2(){
		return loc2;
	}
	
	/**
	 * Get the set name for this area.
	 * @return String name.
	 */
	public String getName(){
		return name;
	}
	
}