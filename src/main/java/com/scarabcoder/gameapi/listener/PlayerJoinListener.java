package com.scarabcoder.gameapi.listener;

import com.scarabcoder.gameapi.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.scarabcoder.gameapi.game.Game;
import com.scarabcoder.gameapi.game.GamePlayer;
import com.scarabcoder.gameapi.manager.GameManager;
import com.scarabcoder.gameapi.manager.PlayerManager;

public class PlayerJoinListener implements Listener{
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e){
		GamePlayer player = PlayerManager.getGamePlayer(e.getPlayer());
		player.setPlayer(e.getPlayer());
		
		for(Game game : GameManager.getGames()){
			
			
			if(game.getGameSettings().usesBungee() || game.getGameSettings().shouldAutoJoin()){
				
				game.addPlayer(player);
				
				break;
			}
		}

		if(player.getGame() == null) {
			GameAPI.getPlugin().getLogger().warning("El jugador " + e.getPlayer().getName()  + " no tiene una instancia de juego creada.");
			return;
		}

		if(player.getGame().getGameSettings() == null) {
			GameAPI.getPlugin().getLogger().warning("El jugador " + e.getPlayer().getName()  + " esta asignado al juego " + player.getGame().getID() + " el cual no tiene cofiguracion activa.");
			return;
		}

		if(player.getGame().getGameSettings().shouldDisableVanillaJoinLeaveMessages()){
			e.setJoinMessage("");
		}
		
	}
	
}
