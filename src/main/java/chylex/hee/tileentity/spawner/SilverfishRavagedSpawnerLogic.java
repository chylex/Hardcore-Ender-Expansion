package chylex.hee.tileentity.spawner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class SilverfishRavagedSpawnerLogic extends CustomSpawnerLogic{
	public SilverfishRavagedSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 120;
		this.maxSpawnDelay = 220;
		this.spawnRange = 4;
		this.spawnCount = 2;
		this.maxNearbyEntities = 5;
		this.activatingRangeFromPlayer = 12;
	}
	
	@Override
	protected AxisAlignedBB getSpawnerCheckBB(){
		return new AxisAlignedBB(pos,pos.add(1,1,1)).expand(spawnRange*2D,0.5D,spawnRange*2D);
	}

	@Override
	protected boolean checkSpawnerConditions(){
		return getSpawnerWorld().getEntitiesWithinAABB(EntitySilverfish.class,new AxisAlignedBB(pos,pos.add(1,1,1)).expand(10D,10D,10D)).size() <= 10;
	}

	@Override
	protected boolean canMobSpawn(EntityLiving entity){
		BlockPosM testPos = new BlockPosM();
		
		for(int spawnerY = pos.y, yy = spawnerY; yy > spawnerY-5; yy--){
			if (!testPos.moveTo(entity.posX,yy,entity.posZ).isAir(entity.worldObj) || yy == spawnerY-4){
				entity.setLocationAndAngles(entity.posX,yy+1,entity.posZ,entity.rotationYaw,0F);
				
				if (entity.worldObj.checkNoEntityCollision(entity.boundingBox) && entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty()){
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected EntityLiving createMob(World world){
		return new EntitySilverfish(world);
	}
}
