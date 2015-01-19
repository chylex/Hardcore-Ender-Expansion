package chylex.hee.tileentity.spawner;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class SilverfishDungeonSpawnerLogic extends CustomSpawnerLogic{
	public SilverfishDungeonSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 65;
		this.maxSpawnDelay = 115;
		this.spawnRange = 5;
		this.attemptCount = 10;
		this.spawnCount = 2;
		this.maxNearbyEntities = 6;
		this.activatingRangeFromPlayer = 127;
	}
	
	@Override
	public void onBlockBreak(){
		World world = getSpawnerWorld();
		Random rand = world.rand;
		BlockPosM testPos = new BlockPosM();
		
		for(int attempt = 0, found = 0, targ = 4+rand.nextInt(4); attempt < 400 && found < targ; attempt++){
			if (testPos.moveTo(pos.x+rand.nextInt(11)-5,pos.y+1-rand.nextInt(5),pos.z+rand.nextInt(11)-5).getBlock(world) == Blocks.stonebrick){
				testPos.setToAir(world);
				world.playAuxSFX(2001,testPos,Block.getIdFromBlock(Blocks.stonebrick));
				
				EntitySilverfish silverfish = new EntitySilverfish(world);
				silverfish.setLocationAndAngles(testPos.x+0.5D,testPos.y+0.5D,testPos.z+0.5D,rand.nextFloat()*360F,0F);
				world.spawnEntityInWorld(silverfish);
				++found;
			}
		}
	}

	@Override
	protected AxisAlignedBB getSpawnerCheckBB(){
		return new AxisAlignedBB(pos,pos.add(1,1,1)).expand(spawnRange*2,0.5D,spawnRange*2D);
	}

	@Override
	protected boolean checkSpawnerConditions(){
		return getSpawnerWorld().getEntitiesWithinAABB(EntitySilverfish.class,new AxisAlignedBB(pos,pos.add(1,6,1)).expand(40D,30D,40D)).size() <= 35;
	}

	@Override
	protected boolean canMobSpawn(EntityLiving entity){
		for(int yy = 0; yy <= 6; yy++){
			entity.setLocationAndAngles(entity.posX,pos.y+yy,entity.posZ,entity.rotationYaw,0F);
			
			if (entity.worldObj.checkNoEntityCollision(entity.boundingBox) && entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty()){
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected EntityLiving createMob(World world){
		return new EntitySilverfish(world);
	}
}
