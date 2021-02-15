package com.scarabcoder.gameapi.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.scarabcoder.gameapi.game.Team;

public class TeamManager {
	
	private List<Team> teams = new ArrayList<Team>();

	public TeamManager(){
		
	}
	
	public void registerTeam(Team team){
		this.teams.add(team);
	}
	
	public void registerTeams(Team...teams ){
		Collections.addAll(this.teams, teams);
	}
	public Team getTeam(String name){
		for(Team team : teams){
			if(team.getName().equals(name)) return team;
		}
		return null;
	}
	
	public List<Team> getTeams(){
		return this.teams;
	}
	
}
