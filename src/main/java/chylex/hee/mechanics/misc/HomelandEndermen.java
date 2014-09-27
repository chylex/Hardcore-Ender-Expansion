package chylex.hee.mechanics.misc;

import java.util.ArrayList;
import java.util.List;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionEnchantedIsland;

public final class HomelandEndermen{
	public enum HomelandRole{
		WORKER, ISLAND_LEADERS, GUARD, COLLECTOR, OVERWORLD_EXPLORER, BUSINESSMAN;
		public static final HomelandRole[] values = values();
	}
	
	public enum OvertakeGroupRole{
		LEADER, CHAOSMAKER, FIGHTER, TELEPORTER;
		public static final OvertakeGroupRole[] values = values();
	}
	
	public static boolean isOvertakeHappening(EntityMobHomelandEnderman source){
		List<EntityTechnicalBiomeInteraction> list = source.worldObj.getEntitiesWithinAABB(EntityTechnicalBiomeInteraction.class,source.boundingBox.expand(260D,128D,260D));
		
		if (!list.isEmpty()){
			for(EntityTechnicalBiomeInteraction entity:list){
				if (entity.getInteractionType() == BiomeInteractionEnchantedIsland.InteractionOvertake.class && entity.ticksExisted > 2)return true;
			}
		}
		
		return false;
	}
	
	public static List<EntityMobHomelandEnderman> getByHomelandRole(EntityMobHomelandEnderman source, HomelandRole role){
		List<EntityMobHomelandEnderman> all = source.worldObj.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,source.boundingBox.expand(260D,128D,260D));
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.getHomelandRole() == role)filtered.add(enderman);
		}
		
		return filtered;
	}

	public static List<EntityMobHomelandEnderman> getByGroupRole(EntityMobHomelandEnderman source, OvertakeGroupRole role){
		List<EntityMobHomelandEnderman> all = source.worldObj.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,source.boundingBox.expand(260D,128D,260D));
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.isInSameGroup(source) && enderman.getGroupRole() == role)filtered.add(enderman);
		}
		
		return filtered;
	}
	
	private HomelandEndermen(){}
}
