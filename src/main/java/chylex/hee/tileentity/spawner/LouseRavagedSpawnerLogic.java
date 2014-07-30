package chylex.hee.tileentity.spawner;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LouseRavagedSpawnerLogic extends CustomSpawnerLogic{
	private LouseSpawnData louseData;
	
	public LouseRavagedSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 30;
		this.maxSpawnDelay = 230;
		this.spawnRange = 3;
		this.spawnCount = 3;
		this.maxNearbyEntities = 5;
		this.activatingRangeFromPlayer = 16;
	}
	
	public void setLouseSpawnData(LouseSpawnData louseData){
		this.louseData = louseData;
	}
	
	@Override
	protected AxisAlignedBB getSpawnerCheckBB(){
		int sx = getSpawnerX(), sy = getSpawnerY(), sz = getSpawnerZ();
		return AxisAlignedBB.getBoundingBox(sx,sy,sz,sx+1,sy+1,sz+1).expand(spawnRange*2D,0.5D,spawnRange*2D);
	}

	@Override
	protected boolean checkSpawnerConditions(){
		int sx = getSpawnerX(), sy = getSpawnerY(), sz = getSpawnerZ();
		return getSpawnerWorld().getEntitiesWithinAABB(EntityMobLouse.class,AxisAlignedBB.getBoundingBox(sx,sy,sz,sx+1,sy+1,sz+1).expand(8D,10D,8D)).size() <= 10;
	}

	@Override
	protected boolean canMobSpawn(EntityLiving entity){
		for(int spawnerY = getSpawnerY(), yy = spawnerY; yy > spawnerY-5; yy--){
			if (!entity.worldObj.isAirBlock((int)entity.posX,yy,(int)entity.posZ) || yy == spawnerY-4){
				entity.setLocationAndAngles(entity.posX,yy+1,entity.posZ,entity.rotationYaw,0F);
				
				if (entity.worldObj.checkNoEntityCollision(entity.boundingBox) && entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty()){
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setTag("louseData",louseData.writeToNBT(new NBTTagCompound()));
	}
		
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		louseData = new LouseSpawnData(nbt.getCompoundTag("louseData"));
		System.out.println(louseData.serializeToString());
	}

	@Override
	protected EntityLiving createMob(World world){
		return new EntityMobLouse(world,louseData);
	}
	
	public static final class LouseSpawnData{
		public static final byte maxLevel = 3;
		
		public enum EnumLouseAttribute{
			ATTACK, // increases damage dealt
			SPEED, // increases speed per level
			HEALTH, // increases health per level, lv1 adds regen, lv2 increases regen
			ARMOR, // adds armor that absorbs damage, absorbing stops knockback, armor amount & regen speed increases per level
			TELEPORT; // allows teleportation with increasing distance & decreasing cooldown per level
			
			public static final EnumLouseAttribute[] values = values();
		}
		
		public enum EnumLouseAbility{
			KNOCKBACK, // increases knockback amount massively per level
			MAGICDMG/*,*/ // deals additional magic damage, 2 hearts + 1 more per level
			/*HEAL*/; // when not attacked, it heals nearby mobs TODO finish
			
			public static final EnumLouseAbility[] values = values();
		}
		
		public final byte level;
		private final Set<EnumLouseAttribute> attributes = new HashSet<>();
		private final Set<EnumLouseAbility> abilities = new HashSet<>();
		
		public LouseSpawnData(int level, Random rand){
			this.level = (byte)level;
			
			if (level == 0 || rand.nextInt(3) != 0){ // one or two attributes, no abilities
				int amt = ((level == 1 && rand.nextBoolean()) || (level == 2 && rand.nextInt(4) != 0)) ? 2 : 1;
				
				for(int a = 0; a < amt; a++)attributes.add(EnumLouseAttribute.values[rand.nextInt(EnumLouseAttribute.values.length)]);
			}
			else{ // one attribute, one ability
				attributes.add(EnumLouseAttribute.values[rand.nextInt(EnumLouseAttribute.values.length)]);
				abilities.add(EnumLouseAbility.values[rand.nextInt(EnumLouseAbility.values.length)]);
			}
		}
		
		public LouseSpawnData(NBTTagCompound tag){
			this.level = tag.getByte("lvl");
			
			for(EnumLouseAttribute attribute:EnumLouseAttribute.values){
				if (tag.hasKey("attr-"+attribute.name()))attributes.add(attribute);
			}
			
			for(EnumLouseAbility ability:EnumLouseAbility.values){
				if (tag.hasKey("attr-"+ability.name()))abilities.add(ability);
			}
		}
		
		public LouseSpawnData(String serializedString){
			String[] parts = serializedString.split("/");
			if (parts.length != 3)this.level = 0;
			else{
				byte lvl = 0;
				
				try{
					lvl = (byte)Integer.parseInt(parts[0]);
					
					for(String s:parts[1].split("\\+")){
						if (s.equals("nil"))break;
						int ordinal = Integer.parseInt(s);
						if (ordinal >= 0 && ordinal < EnumLouseAttribute.values.length)attributes.add(EnumLouseAttribute.values[ordinal]);
					}
					
					for(String s:parts[2].split("\\+")){
						if (s.equals("nil"))break;
						int ordinal = Integer.parseInt(s);
						if (ordinal >= 0 && ordinal < EnumLouseAbility.values.length)abilities.add(EnumLouseAbility.values[ordinal]);
					}
				}catch(NumberFormatException e){}
				
				this.level = lvl;
			}
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound tag){
			tag.setByte("lvl",level);
			for(EnumLouseAttribute attribute:attributes)tag.setBoolean("attr-"+attribute.name(),true);
			for(EnumLouseAbility ability:abilities)tag.setBoolean("abil-"+ability.name(),true);
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
		
		public String serializeToString(){
			StringBuilder build = new StringBuilder();
			build.append(level).append('/');
			
			for(EnumLouseAttribute attribute:attributes)build.append(attribute.ordinal()).append('+');
			if (!attributes.isEmpty())build.deleteCharAt(build.length()-1);
			else build.append("nil");
			build.append('/');
			
			for(EnumLouseAbility ability:abilities)build.append(ability.ordinal()).append('+');
			if (!abilities.isEmpty())build.deleteCharAt(build.length()-1);
			else build.append("nil");
			
			return build.toString();
		}
	}
}
