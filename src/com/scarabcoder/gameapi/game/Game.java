package com.scarabcoder.gameapi.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.scarabcoder.gameapi.GameAPI;
import com.scarabcoder.gameapi.enums.GamePlayerType;
import com.scarabcoder.gameapi.enums.GameStatus;
import com.scarabcoder.gameapi.event.GameEndEvent;
import com.scarabcoder.gameapi.event.GameStartEvent;
import com.scarabcoder.gameapi.event.PlayerJoinGameEvent;
import com.scarabcoder.gameapi.event.PlayerLeaveGameEvent;
import com.scarabcoder.gameapi.manager.PlayerManager;
import com.scarabcoder.gameapi.manager.TeamManager;

public class Game {
	
	private String id;
	
	private HashMap<GamePlayer, GamePlayerType> playerModes = new HashMap<GamePlayer, GamePlayerType>();
	
	private GameStatus status;
	
	private Arena arena;
	
	private GameSettings settings;
	
	private TeamManager teamManager;
	
	private PlayerManager playerManager;
	
	private String messagePrefix;
	
	private Plugin plugin;
	
	private List<Area> areas;
	
	private List<GamePlayer> players;
	
	private List<Location> spawns;
	
	private boolean countingDown;
	
	private int currentCountdown;
	
	private Runnable loop;
	
	public Game(String id, Arena arena, GameStatus status, Plugin plugin){
		this.id = id;
		this.arena = arena;
		this.plugin = plugin;
		this.teamManager = new TeamManager();
		this.playerManager = new PlayerManager();
		this.messagePrefix = "";
		this.status = status;
		this.areas = new ArrayList<Area>();
		this.players = new ArrayList<GamePlayer>();
		this.settings = new GameSettings();
		this.currentCountdown = 0;
		this.spawns = new ArrayList<Location>();
		this.loop = new Runnable(){

			@Override
			public void run() {
				
			}};
		final Game game = this;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(GameAPI.getPlugin(), new Runnable(){
			@Override
			public void run() {
				if(game.getGameSettings().getAutomaticCountdown() && !game.isCountingDown() && game.getGameStatus().equals(GameStatus.WAITING)){
					if(game.isMinimumPlayersFilled()){
						game.setCountingDown(true);
						game.increaseCountdown();
						game.sendMessage(ChatColor.GREEN + "Game starts in " + ChatColor.DARK_GREEN + game.getGameSettings().getCountdownTime() + ChatColor.GREEN + " seconds!");
					}
				}
				if(game.isCountingDown()){
					if(game.getCurrentCountdown() < game.getGameSettings().getCountdownTime()){
						GameAPI.sendDebugMessage(game.getCurrentCountdown() % 10d + "", GameAPI.getPlugin());
						if(game.getCurrentCountdown() % 10d == 0.0d){
							game.sendMessage(ChatColor.GREEN + "Game starts in " + ChatColor.DARK_GREEN + (game.getGameSettings().getCountdownTime() - game.getCurrentCountdown()) + ChatColor.GREEN + " seconds!");
						}else if(game.getGameSettings().getCountdownTime() - game.getCurrentCountdown()  <= 5){
							game.sendMessage(ChatColor.GREEN + "Game starts in " + ChatColor.DARK_GREEN + (game.getGameSettings().getCountdownTime() - game.getCurrentCountdown()) + ChatColor.GREEN + " seconds!");
						}
						game.increaseCountdown();
					}else{
						if(game.isMinimumPlayersFilled()){
							game.startGame();
							game.resetCurrentCountdown();
							game.setCountingDown(false);
						}else{
							game.sendMessage(ChatColor.RED + "Not enough players to start game.");
							game.resetCurrentCountdown();
							game.setCountingDown(false);
						}
					}
				}
				game.getRunnable().run();
			}
			
		}, 0L, 20L);
	}
	
	public boolean isMinimumPlayersFilled(){
		return this.getPlayers().size() >= this.getGameSettings().getMinimumPlayers();
	}
	
	public void setPlayerMode(GamePlayerType type, GamePlayer player){
		switch(type){
		case PLAYER:
			if(player.isOnline()){
				player.getOnlinePlayer().setGameMode(this.getGameSettings().getMode());
			}
			break;
		case SPECTATOR:
			if(player.isOnline()){
				player.getOnlinePlayer().setGameMode(this.getGameSettings().getSpectatorMode());
			}
			break;
		default:
			break;
		
		}
		this.playerModes.put(player, type);
	}
	
	public GamePlayerType getGamePlayerType(GamePlayer player){
		return this.playerModes.get(player);
	}
	
	public List<GamePlayer> getGamePlayerByMode(GamePlayerType type){
		List<GamePlayer> players = new ArrayList<GamePlayer>();
		for(GamePlayer player : this.getPlayers()){
			if(this.getGamePlayerType(player).equals(type)){
				players.add(player);
			}
		}
		return players;
	}
	
	public boolean isCountingDown() {
		return countingDown;
	}



	public void setCountingDown(boolean countingDown) {
		this.countingDown = countingDown;
	}



	/**
	 * Add a game spawn. These are only used when teams are disabled, for use in place of team spawns.
	 * @param location
	 */
	public void addSpawn(Location location){
		this.spawns.add(location);
	}
	
	/**
	 * Get all game spawns. These are only used when teams are disabled, for use in place of team spawns.
	 * @param location
	 */
	public List<Location> getSpawns(){
		return this.spawns;
	}
	
	/**
	 * Utility variable. Get the current countdown, can be used to start off the game when there are enough players.
	 * Starts at 0 and increases, subtract from GameSettings.getCountdownTime().
	 * @return Current countdown time variable.
	 */
	public int getCurrentCountdown(){
		return this.currentCountdown;
	}
	
	/**
	 * Utility variable. Get the current countdown, can be used to start off the game when there are enough players.
	 * Starts at 0 and increases, subtract from GameSettings.getCountdownTime().
	 * Resets to 0.
	 * @return Current countdown time variable.
	 */
	public int resetCurrentCountdown(){
		this.currentCountdown = 0;
		return this.currentCountdown;
	}
	
	/**
	 * Utility variable. Get the current countdown, can be used to start off the game when there are enough players.
	 * Starts at 0 and increases, subtract from GameSettings.getCountdownTime().
	 * Increases countdown by 1.
	 * @return Current countdown time variable.
	 */
	public int increaseCountdown(){
		this.currentCountdown++;
		return this.currentCountdown;
	}
	
	/**
	 * Add the area to the game.
	 * @param area 
	 */
	public void registerArea(Area area){
		this.areas.add(area);
	}
	
	/**
	 * Get the plugin that registered this minigame.
	 * @return Bukkit Plugin
	 */
	public Plugin getRegisteringPlugin(){
		return this.plugin;
	}
	
	/**
	 * Set the current game status.
	 * @param status
	 */
	public void setGameStatus(GameStatus status){
		this.status = status;
	}
	
	/**
	 * Start the game. Doesn't do much other then teleporting team members to their spawn and setting the game status.
	 */
	public void startGame(){
		this.setGameStatus(GameStatus.INGAME);
		GameStartEvent ev = new GameStartEvent(this);
		if(this.getGameSettings().shouldTeleportPlayersOnGameStart()){
			if(this.getGameSettings().shouldUseTeams()){
				for(Team team : this.getTeamManager().getTeams()){
					for(GamePlayer player : team.getPlayers()){
						if(player.isOnline()){
							Random rand = new Random();
							player.getOnlinePlayer().teleport(team.getTeamSpawns().get(rand.nextInt(team.getTeamSpawns().size())));
						}
					}
				}
			}else{
				for(GamePlayer player : this.getPlayers()){
					if(player.isOnline()){
						player.getOnlinePlayer().teleport(this.getSpawns().get(new Random().nextInt(this.getSpawns().size())));
					}
				}
			}
		}
		Bukkit.getPluginManager().callEvent(ev);
	}
	
	/**
	 * End the game, kicking all players and resetting the arena.
	 */
	public void endGame(){
		GameEndEvent ev = new GameEndEvent(this);
		Bukkit.getPluginManager().callEvent(ev);
		Iterator<GamePlayer> pl = this.getPlayers().iterator();
		while(pl.hasNext()){
			GamePlayer player = pl.next();
			this.removePlayer(player);
		}
		this.arena.resetWorld();
	}
	
	/**
	 * Add a player to a randomly selected team. Team selection mode is defined by GameSettings.getTeamSpreadType().
	 * 
	 * @return If teams are disabled, all teams are full, or there aren't any registered teams, returns null. Otherwise, the Team that the player was added to.
	 */
	public Team addToTeam(GamePlayer player){
		List<Team> teams = this.getTeamManager().getTeams();
		if(!this.getGameSettings().shouldUseTeams()) return null;
		if(teams.size() == 0) return null;
		switch(this.getGameSettings().getTeamSpreadType()){
		case EVEN:
			int lowest = this.getGameSettings().getTeamSize();
			Team lowestTeam = null;
			for(Team team : teams){
				if(team.getPlayers().size() <= lowest){
					lowest = team.getPlayers().size();
					lowestTeam = team;
				}
			}
			lowestTeam.addPlayer(player);
			return lowestTeam;
		case FIRST_AVAILABLE:
			for(Team team : teams){
				if(team.getPlayers().size() < this.getGameSettings().getTeamSize()){
					team.addPlayer(player);
					return team;
				}
			}
			return null;
		default:
			return null;
		
		}
	}
	
	/**
	 * Add a player to the game. You must handle teleporting. Teams are not set here, use addToTeam(GamePlayer, [Team]).
	 * Player will not be added if already part of the game. The GameMode set in GameSettings.getGameMode() will be applied here.
	 * @param player
	 */
	public void addPlayer(GamePlayer player){
		if(!this.players.contains(player)){
			PlayerJoinGameEvent ev = new PlayerJoinGameEvent(player, this);
			this.players.add(player);
			player.setGame(this);
			if(player.isOnline()){
				player.getOnlinePlayer().setGameMode(this.getGameSettings().getMode());
				player.getOnlinePlayer().setFoodLevel(this.getGameSettings().getFoodLevel());
				player.getOnlinePlayer().setHealth(this.getGameSettings().getHealthLevel());
			}
			this.setPlayerMode(GamePlayerType.PLAYER, player);
			Bukkit.getPluginManager().callEvent(ev);
			
		}
	}
	
	/**
	 * Removes the player from the game, and sends them to the lobby set in GameSettings. 
	 * 
	 * @param player
	 */
	public void removePlayer(GamePlayer player){
		if(player.isOnline()){
			this.players.remove(player);
			if(this.getGameSettings().usesBungee()){
		
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(this.getGameSettings().getLobbyServer());
		
				
				player.getOnlinePlayer().sendPluginMessage(GameAPI.getPlugin(), "BungeeCord", out.toByteArray());
			}else{
				player.getOnlinePlayer().teleport(this.getGameSettings().getLobbyLocation());
			}
		}
		player.setGame(null);
		PlayerLeaveGameEvent ev = new PlayerLeaveGameEvent(player, this);
		Bukkit.getPluginManager().callEvent(ev);
		
	}
	
	/**
	 * Set what happens every second in the game.
	 * @param runnable Runnable object.
	 */
	public void setLoopRunnable(Runnable runnable){
		this.loop = runnable;
	}
	
	/**
	 * Get what happens every second in the game.
	 * @return Runnable
	 */
	public Runnable getRunnable(){
		return this.loop;
	}
	
	
	/**
	 * Send a game message. Messages are prefixed with the set message prefix (set/getMessagePrefix())
	 * @param message Message to be sent.
	 */
	public void sendMessage(String message){
		for(GamePlayer player : this.getPlayers()){
			if(player.isOnline()){
				player.getOnlinePlayer().sendMessage(messagePrefix + " " + message);
			}
		}
	}
	
	/**
	 * Set the message prefix. For example, "[Survival Games] "
	 * @param prefix Prefix to set.
	 */
	public void setMessagePrefix(String prefix){
		this.messagePrefix = prefix;
	}
	
	/**
	 * Get the message prefix.
	 * @return String message prefix.
	 */
	public String getMessagePrefix(){
		return this.messagePrefix;
	}
	
	/**
	 * Get the unique ID for this game.
	 * @return String name
	 */
	public String getID(){
		return id;
	}
	
	/**
	 * Get the current game's status
	 * @return GameStatus
	 */
	public GameStatus getGameStatus(){
		return this.status;
	}
	
	
	/**
	 * Get the game settings, used for things like minimum and maximum player count.
	 * @return
	 */
	public GameSettings getGameSettings(){
		return this.settings;
	}
	
	/**
	 * Get the Team Manager for this Game, used for creating/managing teams.
	 * @return Team Manager
	 */
	public TeamManager getTeamManager(){
		return this.teamManager;
	}
	
	/**
	 * Get the Player Manager for this Game.
	 * @return Player Manager
	 */
	public PlayerManager getPlayerManager(){
		return this.playerManager;
	}
	
	/**
	 * Get all the GamePlayers in this game.
	 * @return List<GamePlayer>
	 */
	public List<GamePlayer> getPlayers(){
		return this.players;
	}
	
	/**
	 * Get all the areas registered to this Game.
	 * @return List<Area>
	 */
	public List<Area> getAreas(){
		return areas;
	}
	
	/**
	 * Get the global arena object for this game.
	 * @return Arena
	 */
	public Arena getArena(){
		return this.arena;
	}
	
}
