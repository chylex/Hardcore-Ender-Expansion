package chylex.hee.mechanics.misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionEnchantedIsland;

public final class HomelandEndermen{
	public enum HomelandRole{
		WORKER(227), ISLAND_LEADERS(58), GUARD(0), COLLECTOR(176), OVERWORLD_EXPLORER(141), BUSINESSMAN(335), INTELLIGENCE(295);
		public static final HomelandRole[] values = values();
		
		public final float red, green, blue;
		
		HomelandRole(int hue){
			float[] col = ColorUtil.hsvToRgb(hue/359F,0.78F,0.78F);
			red = col[0];
			green = col[1];
			blue = col[2];
		}
	}
	
	public enum OvertakeGroupRole{
		LEADER, CHAOSMAKER, FIGHTER, TELEPORTER;
		public static final OvertakeGroupRole[] values = values();
		
		public static OvertakeGroupRole getRandomMember(Random rand){
			int r = rand.nextInt(10);
			
			if (r < 5)return FIGHTER;
			else if (r < 8)return CHAOSMAKER;
			else return TELEPORTER;
		}
	}
	
	public enum EndermanTask{
		NONE, RECRUIT_TO_GROUP, LISTEN_TO_RECRUITER, STROLL, WALK, COMMUNICATE
	}
	
	public static boolean isOvertakeHappening(EntityMobHomelandEnderman source){
		return getOvertakeGroup(source) != -1;
	}
	
	public static long getOvertakeGroup(EntityMobHomelandEnderman source){
		List<EntityTechnicalBiomeInteraction> list = source.worldObj.getEntitiesWithinAABB(EntityTechnicalBiomeInteraction.class,source.boundingBox.expand(260D,128D,260D));
		
		if (!list.isEmpty()){
			for(EntityTechnicalBiomeInteraction entity:list){
				if (entity.getInteractionType() == BiomeInteractionEnchantedIsland.InteractionOvertake.class && entity.ticksExisted > 2){
					return ((BiomeInteractionEnchantedIsland.InteractionOvertake)entity.getInteraction()).groupId;
				}
			}
		}
		
		return -1;
	}
	
	public static List<EntityMobHomelandEnderman> getAll(EntityMobHomelandEnderman source){
		List<EntityMobHomelandEnderman> all = source.worldObj.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,source.boundingBox.expand(260D,128D,260D));
		return all;
	}
	
	public static List<EntityMobHomelandEnderman> getByHomelandRole(EntityMobHomelandEnderman source, HomelandRole role){
		List<EntityMobHomelandEnderman> all = getAll(source);
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.getHomelandRole() == role)filtered.add(enderman);
		}
		
		return filtered;
	}
	
	public static List<EntityMobHomelandEnderman> getInSameGroup(EntityMobHomelandEnderman source){
		List<EntityMobHomelandEnderman> all = getAll(source);
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.isInSameGroup(source))filtered.add(enderman);
		}
		
		return filtered;
	}

	public static List<EntityMobHomelandEnderman> getByGroupRole(EntityMobHomelandEnderman source, OvertakeGroupRole role){
		List<EntityMobHomelandEnderman> all = getAll(source);
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.isInSameGroup(source) && enderman.getGroupRole() == role)filtered.add(enderman);
		}
		
		return filtered;
	}
	
	private HomelandEndermen(){}
}
