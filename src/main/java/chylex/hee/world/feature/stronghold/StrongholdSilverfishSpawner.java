package chylex.hee.world.feature.stronghold;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.entity.technical.EntityTechnicalSpawner;
import chylex.hee.entity.technical.EntityTechnicalSpawner.IVirtualSpawner;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.world.util.BoundingBox;
import com.google.common.collect.ImmutableList;

public class StrongholdSilverfishSpawner implements IVirtualSpawner<EntityMobSilverfish>{
	private BoundingBox box;
	
	@Override
	public void init(EntityTechnicalSpawner owner){
		box = new BoundingBox(Pos.at(owner).offset(-128,-32,-128),Pos.at(owner).offset(128,32,128));
	}
	
	@Override
	public EntityMobSilverfish createEntity(World world){
		return new EntityMobSilverfish(world);
	}

	@Override
	public int getCheckTimer(Random rand){
		return 20+rand.nextInt(35);
	}

	@Override
	public int getSpawnAttempts(Random rand){
		return 8;
	}

	@Override
	public int getSpawnLimit(Random rand){
		return 2+rand.nextInt(3);
	}

	@Override
	public double getSpawnRange(Random rand){
		return 7D+rand.nextDouble()*13D;
	}
	
	@Override
	public BoundingBox getCheckBox(){
		return box;
	}
	
	@Override
	public void findSpawnPosition(World world, Random rand, EntityPlayer target, EntityMobSilverfish entity, double range){
		Vec vec = Vec.xzRandom(rand);
		entity.setPositionAndRotation(target.posX+vec.x*range,target.posY+(rand.nextDouble()-0.5D)*10D,target.posZ+vec.z*range,rand.nextFloat()*360F-180F,0F);
		
		for(int floorCheck = 0; floorCheck < 5; floorCheck++){
			if (Pos.at(entity).getDown().getBlock(world) == Blocks.stonebrick)break;
			else entity.setPosition(entity.posX,entity.posY-1D,entity.posZ);
		}
	}

	@Override
	public boolean checkSpawnConditions(World world, Random rand, ImmutableList<EntityPlayer> playersInRange, EntityPlayer target, EntityMobSilverfish entity){
		Pos pos = Pos.at(entity);
		
		if (world.difficultySetting == EnumDifficulty.PEACEFUL)return false;
		if (!world.checkNoEntityCollision(entity.boundingBox))return false;
		if (!world.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty())return false;
		
		if (pos.getDown().getBlock(world) != Blocks.stonebrick)return false;
		if (world.getBlockLightValue(pos.getX(),pos.getY(),pos.getZ()) > 7)return false;
		
		if (playersInRange.stream().anyMatch(entity::canEntityBeSeen))return false;
		if (EntitySelector.type(world,EntityMobSilverfish.class,target.boundingBox.expand(16D,8D,16D)).size() >= 10)return false;
		if (EntitySelector.type(world,EntityMobSilverfish.class,box.toAABB()).size() >= 15*playersInRange.size())return false;
		
		return true;
	}
}
