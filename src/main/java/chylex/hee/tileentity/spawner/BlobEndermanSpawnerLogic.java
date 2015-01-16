package chylex.hee.tileentity.spawner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class BlobEndermanSpawnerLogic extends CustomSpawnerLogic{
	public BlobEndermanSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 70;
		this.maxSpawnDelay = 140;
		this.spawnRange = 5;
		this.attemptCount = 10;
		this.spawnCount = 1;
		this.maxNearbyEntities = 6;
	}
	
	@Override
	protected boolean canMobSpawn(EntityLiving entity){
		return entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty() && !entity.worldObj.isAirBlock(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY)-1,MathUtil.floor(entity.posZ));
	}

	@Override
	protected EntityLiving createMob(World world){
		EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
		if (world != null)enderman.setCanDespawn(true);
		return enderman;
	}
}
