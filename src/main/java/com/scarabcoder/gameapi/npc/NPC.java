package com.scarabcoder.gameapi.npc;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;

public class NPC {

	private final Animals entity;
	private final boolean allowAIMovement;
	private final boolean invincible;

	public NPC(Animals entity){
		this.entity = entity;
		this.allowAIMovement = false;
		this.invincible = true;
	}
	
	public Entity getEntity(){
		return this.entity;
	}

	public boolean isAllowAIMovement() {
		return allowAIMovement;
	}

	public boolean isInvincible() {
		return invincible;
	}
}
