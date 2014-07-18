package chylex.hee.mechanics.spawner;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TowerEndermanSpawnerLogic extends CustomSpawnerLogic{
	private List<PotionEffect> effects = new ArrayList<>();
	private int minY,maxY;
	
	public TowerEndermanSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 85;
		this.maxSpawnDelay = 155;
		this.spawnRange = 3;
		this.spawnCount = 2;
		this.maxNearbyEntities = 3;
		
		minY = getSpawnerY()-1;
		maxY = getSpawnerY()+4;
	}
	
	public TowerEndermanSpawnerLogic setSpawnEffects(List<PotionEffect> effects){
		this.effects.clear();
		this.effects.addAll(effects);
		return this;
	}
	
	public TowerEndermanSpawnerLogic setTestingY(int minY, int maxY){
		this.minY = minY;
		this.maxY = maxY;
		return this;
	}
	
	@Override
	public void updateSpawner(){
		if (!isActivated())return;

		World world = getSpawnerWorld();
		
		if (world.isRemote){
			double particleX = getSpawnerX()+world.rand.nextFloat(),
				   particleY = getSpawnerY()+world.rand.nextFloat(),
				   particleZ = getSpawnerZ()+world.rand.nextFloat();
			
			world.spawnParticle("smoke",particleX,particleY,particleZ,0D,0D,0D);
			world.spawnParticle("flame",particleX,particleY,particleZ,0D,0D,0D);

			if (spawnDelay > 0)--spawnDelay;

			field_98284_d = field_98287_c;
			field_98287_c = (field_98287_c+(1000F/(spawnDelay+200F)))%360D;
		}
		else{
			if (spawnDelay == -1)resetTimer();
			if (spawnDelay > 0){
				--spawnDelay;
				return;
			}

			boolean flag = false;

			for(int attempt = 0,i = 0; attempt < 6 && i < spawnCount; ++attempt){
				EntityLiving entity = createMob(world);
				if (entity == null)return;

				int nearbyEntityAmount = world.getEntitiesWithinAABB(entity.getClass(),AxisAlignedBB.getBoundingBox(getSpawnerX(),minY,getSpawnerZ(),getSpawnerX()+1,maxY,getSpawnerZ()+1).expand(spawnRange*2D,0.5D,spawnRange*2D)).size();
				if (nearbyEntityAmount >= maxNearbyEntities){
					resetTimer();
					return;
				}

				double xx = getSpawnerX()+(world.rand.nextDouble()-world.rand.nextDouble())*spawnRange,
					   zz = getSpawnerZ()+(world.rand.nextDouble()-world.rand.nextDouble())*spawnRange;
				
				for(int yy = minY; yy <= maxY; yy++){
					entity.setLocationAndAngles(xx,yy,zz,world.rand.nextFloat()*360F,0F);
	
					if (entity.getCanSpawnHere()){
						func_98265_a(entity); // OBFUSCATED spawn entity
						
						for(PotionEffect effect:effects)entity.addPotionEffect(effect);
						
						world.playAuxSFX(2004,getSpawnerX(),getSpawnerY(),getSpawnerZ(),0);
						entity.spawnExplosionParticle();
						
						++i;
						flag = true;
						break;
					}
				}
			}

			if (flag)resetTimer();
		}
	}
	
	@Override
	protected void resetTimer(){
		World world = spawnerTile.getWorldObj();
		spawnDelay = minSpawnDelay+world.rand.nextInt(maxSpawnDelay-minSpawnDelay)+(world.provider.dimensionId != 1?120+world.rand.nextInt(80):0);
		func_98267_a(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Entity func_98281_h(){
		if (entityCache == null)entityCache = func_98265_a(createMob(null));
		return entityCache;
	}
	
	private EntityMobAngryEnderman createMob(World world){
		EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
		if (world != null)enderman.setCanDespawn(true);
		return enderman;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		NBTTagList nbtEffList = new NBTTagList();
		for(PotionEffect eff:effects)nbtEffList.appendTag(eff.writeCustomPotionEffectToNBT(new NBTTagCompound()));
		nbt.setTag("spawnEffects",nbtEffList);
		
		nbt.setInteger("minY",minY);
		nbt.setInteger("maxY",maxY);
	}
        
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		NBTTagList nbtEffList = nbt.getTagList("spawnEffects",Constants.NBT.TAG_COMPOUND);
		for(int a = 0; a < nbtEffList.tagCount(); a++)effects.add(PotionEffect.readCustomPotionEffectFromNBT(nbtEffList.getCompoundTagAt(a)));
		
		minY = nbt.getInteger("minY");
		maxY = nbt.getInteger("maxY");
	}
}
