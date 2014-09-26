package chylex.hee.entity.mob;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionEnchantedIsland;

public class EntityMobHomelandEnderman extends EntityEnderman{
	private HomelandRole homelandRole;
	private long groupId = -1;
	private OvertakeGroupRole overtakeGroupRole;
	
	public EntityMobHomelandEnderman(World world){
		super(world);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		// TODO AI
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		if (groupId != -1 && !isOvertakeHappening()){
			teleportRandomly();
			return null;
		}
		
		return super.findPlayerToAttack();
	}
	
	@Override
	protected boolean teleportTo(double x, double y, double z){
		if (isOvertakeHappening())return false;
		else return super.teleportTo(x,y,z);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("homelandRole",(byte)homelandRole.ordinal());
		nbt.setLong("groupId",groupId);
		
		if (groupId != -1){
			nbt.setByte("groupRole",(byte)overtakeGroupRole.ordinal());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		
		byte homelandRoleId = nbt.getByte("homelandRole");
		homelandRole = homelandRoleId >= 0 && homelandRoleId < HomelandRole.values.length ? HomelandRole.values[homelandRoleId] : null;
		
		groupId = nbt.getLong("groupId");
		
		if (groupId != -1){
			byte groupRoleId = nbt.getByte("groupRole");
			overtakeGroupRole = groupRoleId >= 0 && groupRoleId < OvertakeGroupRole.values.length ? OvertakeGroupRole.values[groupRoleId] : null;
		}
		
		if (homelandRole == null || (groupId != -1 && overtakeGroupRole == null))setDead();
	}
	
	@Override
	protected void despawnEntity(){}
	
	private boolean isOvertakeHappening(){
		List<EntityTechnicalBiomeInteraction> list = worldObj.getEntitiesWithinAABB(EntityTechnicalBiomeInteraction.class,boundingBox.expand(260D,128D,260D));
		
		if (!list.isEmpty()){
			for(EntityTechnicalBiomeInteraction entity:list){
				if (entity.getInteractionType() == BiomeInteractionEnchantedIsland.InteractionOvertake.class)return true;
			}
		}
		
		return false;
	}
	
	private List<EntityMobHomelandEnderman> getByHomelandRole(HomelandRole role){
		List<EntityMobHomelandEnderman> all = worldObj.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,boundingBox.expand(260D,128D,260D));
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.homelandRole == homelandRole)filtered.add(enderman);
		}
		
		return filtered;
	}

	private List<EntityMobHomelandEnderman> getByGroupRope(OvertakeGroupRole role){
		List<EntityMobHomelandEnderman> all = worldObj.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,boundingBox.expand(260D,128D,260D));
		List<EntityMobHomelandEnderman> filtered = new ArrayList<>();
		
		for(EntityMobHomelandEnderman enderman:all){
			if (enderman.groupId == groupId && enderman.overtakeGroupRole == overtakeGroupRole)filtered.add(enderman);
		}
		
		return filtered;
	}
	
	enum HomelandRole{
		WORKER, ISLAND_LEADER, COLLECTOR, OVERWORLD_EXPLORER, BUSINESSMAN;
		static final HomelandRole[] values = values();
	}
	
	enum OvertakeGroupRole{
		LEADER, CHAOSMAKER, FIGHTER, TELEPORTER;
		static final OvertakeGroupRole[] values = values();
	}
}
