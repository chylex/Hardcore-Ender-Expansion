package chylex.hee.tileentity.spawner;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LouseSpawnerLogic extends CustomSpawnerLogic{
	public LouseSpawnerLogic(TileEntityCustomSpawner spawnerTile){ // TODO finish
		super(spawnerTile);
		this.minSpawnDelay = 30;
		this.maxSpawnDelay = 220;
		this.spawnRange = 3;
		this.spawnCount = 3;
		this.maxNearbyEntities = 7;
		this.activatingRangeFromPlayer = 32;
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
		return new EntityMobLouse(world,world == null ? null : new LouseSpawnData(0,world.rand));
	}
	
	public static final class LouseSpawnData{
		public enum EnumLouseAttribute{
			ATTACK, // increases damage dealt
			SPEED, // increases speed per level
			HEALTH, // increases health per level, lv1 adds regen, lv2 increases regen
			ARMOR, // adds armor that absorbs damage, absorbing stops knockback, armor amount increases per level
			TELEPORT; // allows teleportation with increasing distance per level
			
			public static final EnumLouseAttribute[] values = values();
		}
		
		public enum EnumLouseAbility{
			KNOCKBACK, // increases knockback amount massively per level
			MAGICDMG, // deals additional magic damage
			HEAL; // when not attacked, it heals nearby mobs
			
			public static final EnumLouseAbility[] values = values();
		}
		
		public final byte level;
		private Set<EnumLouseAttribute> attributes = new HashSet<>();
		private Set<EnumLouseAbility> abilities = new HashSet<>();
		
		public LouseSpawnData(int level, Random rand){
			this.level = (byte)level;
			
			if (level == 0 || rand.nextInt(3) != 0){ // one or two attributes, no abilities
				
			}
			else{ // one attribute, one ability
				attributes.add(EnumLouseAttribute.values[rand.nextInt(EnumLouseAttribute.values.length)]);
				abilities.add(EnumLouseAbility.values[rand.nextInt(EnumLouseAbility.values.length)]);
			}
		}
		
		public LouseSpawnData(NBTTagCompound tag){
			this.level = tag.getByte("lvl");
			for(EnumLouseAttribute attribute:attributes)tag.setBoolean("attr-"+attribute.name(),true);
			for(EnumLouseAbility ability:abilities)tag.setBoolean("abil-"+ability.name(),true);
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound tag){
			tag.setByte("lvl",level);
			
			for(EnumLouseAttribute attribute:EnumLouseAttribute.values){
				if (tag.hasKey("attr-"+attribute.name()))attributes.add(attribute);
			}
			
			for(EnumLouseAbility ability:EnumLouseAbility.values){
				if (tag.hasKey("attr-"+ability.name()))abilities.add(ability);
			}
			
			return tag;
		}
		
		public int attribute(EnumLouseAttribute attribute){
			return attributes.contains(attribute) ? (level+1) : 0;
		}
		
		public int ability(EnumLouseAbility ability){
			return abilities.contains(ability) ? (level+1) : 0;
		}
		
		@SideOnly(Side.CLIENT)
		public Set<EnumLouseAttribute> getAttributeSet(){
			return attributes;
		}
		
		@SideOnly(Side.CLIENT)
		public Set<EnumLouseAbility> getAbilitySet(){
			return abilities;
		}
	}
}
