package chylex.hee.tileentity.spawner;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.system.util.NBTUtil;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class TowerEndermanSpawnerLogic extends CustomSpawnerLogic{
	private final List<PotionEffect> effects = new ArrayList<>();
	private int minY,maxY;
	
	public TowerEndermanSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 85;
		this.maxSpawnDelay = 155;
		this.spawnRange = 3;
		this.attemptCount = 6;
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
	protected AxisAlignedBB getSpawnerCheckBB(){
		return AxisAlignedBB.getBoundingBox(getSpawnerX(),minY,getSpawnerZ(),getSpawnerX()+1,maxY,getSpawnerZ()+1).expand(spawnRange*2D,0.5D,spawnRange*2D);
	}
	
	@Override
	protected boolean canMobSpawn(EntityLiving entity){
		for(int yy = minY; yy <= maxY; yy++){
			entity.setLocationAndAngles(entity.posX,yy,entity.posZ,entity.rotationYaw,0F);
			if (entity.getCanSpawnHere())return true;
		}
		
		return false;
	}
	
	@Override
	protected void onMobSpawned(EntityLiving entity){
		for(PotionEffect effect:effects)entity.addPotionEffect(effect);
	}

	@Override
	protected EntityLiving createMob(World world){
		EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
		if (world != null)enderman.setCanDespawn(true);
		return enderman;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		NBTUtil.writeList(nbt,"spawnEffects",effects.stream().map(eff -> eff.writeCustomPotionEffectToNBT(new NBTTagCompound())));
		nbt.setInteger("minY",minY);
		nbt.setInteger("maxY",maxY);
	}
		
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		NBTUtil.readCompoundList(nbt,"spawnEffects").forEach(tag -> effects.add(PotionEffect.readCustomPotionEffectFromNBT(tag)));
		minY = nbt.getInteger("minY");
		maxY = nbt.getInteger("maxY");
	}
}
