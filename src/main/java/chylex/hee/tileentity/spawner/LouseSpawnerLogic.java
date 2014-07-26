package chylex.hee.tileentity.spawner;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class LouseSpawnerLogic extends CustomSpawnerLogic{
	public LouseSpawnerLogic(TileEntityCustomSpawner spawnerTile){ // TODO finish
		super(spawnerTile);
		this.minSpawnDelay = 65;
		this.maxSpawnDelay = 115;
		this.spawnRange = 5;
		this.spawnCount = 2;
		this.maxNearbyEntities = 6;
		this.activatingRangeFromPlayer = 127;
	}
	
	@Override
	protected AxisAlignedBB getSpawnerCheckBB(){
		int sx = getSpawnerX(), sy = getSpawnerY(), sz = getSpawnerZ();
		return AxisAlignedBB.getBoundingBox(sx,sy,sz,sx+1,sy+1,sz+1).expand(spawnRange*2,0.5D,spawnRange*2D);
	}

	@Override
	protected boolean checkSpawnerConditions(){
		int sx = getSpawnerX(), sy = getSpawnerY(), sz = getSpawnerZ();
		return getSpawnerWorld().getEntitiesWithinAABB(EntitySilverfish.class,AxisAlignedBB.getBoundingBox(sx,sy,sz,sx+1,sy+6,sz+1).expand(40D,30D,40D)).size() > 35;
	}

	@Override
	protected boolean canMobSpawn(EntityLiving entity){
		for(int yy = 0; yy <= 6; yy++){
			entity.setLocationAndAngles(entity.posX,getSpawnerY()+yy,entity.posZ,entity.rotationYaw,0F);
			
			if (entity.worldObj.checkNoEntityCollision(entity.boundingBox) && entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty()){
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected EntityLiving createMob(World world){
		return new EntityMobLouse(world,new LouseSpawnData(0,world.rand));
	}
	
	public static final class LouseSpawnData{
		public enum EnumLouseAttribute{
			HEALTH, SPEED, ATTACK, KNOCKBACK
		}
		
		public enum EnumLouseAbility{
			
		}
		
		public final byte level;
		private final boolean[] attributes = new boolean[EnumLouseAttribute.values().length];
		private final boolean[] abilities = new boolean[EnumLouseAbility.values().length];
		
		public LouseSpawnData(int level, Random rand){
			this.level = (byte)level;
			
			// TODO generate
		}
		
		public LouseSpawnData(NBTTagCompound tag){
			this.level = tag.getByte("lvl");
			for(int a = 0; a < attributes.length; a++)attributes[a] = tag.getBoolean("attr"+a);
			for(int a = 0; a < abilities.length; a++)abilities[a] = tag.getBoolean("abil"+a);
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound tag){
			tag.setByte("lvl",level);
			for(int a = 0; a < attributes.length; a++)tag.setBoolean("attr"+a,attributes[a]);
			for(int a = 0; a < abilities.length; a++)tag.setBoolean("abil"+a,abilities[a]);
			return tag;
		}
		
		public int attribute(EnumLouseAttribute attribute){
			return attributes[attribute.ordinal()] ? level : 0;
		}
		
		public int ability(EnumLouseAbility ability){
			return abilities[ability.ordinal()] ? level : 0;
		}
	}
}
