package chylex.hee.entity.mob.teleport;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;

public interface ITeleportPredicate<T extends Entity>{
	boolean isValid(T entity, Vec startPos, Random rand);
	
	public static final ITeleportPredicate noCollision = (entity, startPos, rand) -> {
		return entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty();
	};
	
	public static final ITeleportPredicate noLiquid = (entity, startPos, rand) -> {
		return !entity.worldObj.isAnyLiquid(entity.boundingBox);
	};
	
	public static <T extends Entity> ITeleportPredicate<T> airAboveSolid(final int airBlocks){
		return (entity, startPos, rand) -> {
			Pos entityPos = Pos.at(entity);
			if (!entityPos.getDown().getMaterial(entity.worldObj).blocksMovement())return false;
			
			for(int y = 0; y < airBlocks; y++){
				if (!entityPos.offset(0,y,0).isAir(entity.worldObj))return false;
			}
			
			return true;
		};
	}
	
	public static <T extends Entity> ITeleportPredicate<T> minDistance(final double minDist){
		double minDistSq = MathUtil.square(minDist);
		
		return (entity, startPos, rand) -> {
			return MathUtil.distanceSquared(entity.posX-startPos.x,entity.posY-startPos.y,entity.posZ-startPos.z) >= minDistSq;
		};
	}
}
