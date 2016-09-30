package chylex.hee.entity.mob.teleport;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;

public final class TeleportLocation<T extends Entity> implements ITeleportLocation<T>{
	@FunctionalInterface
	public static interface ITeleportXZ<T extends Entity>{
		Vec findXZ(T entity, Vec startPos, Random rand);
		
		public static <T extends Entity> ITeleportXZ<T> inSquare(final double maxDist){
			return (entity, startPos, rand) -> startPos.offset((rand.nextDouble()-0.5D)*2D*maxDist, 0D, (rand.nextDouble()-0.5D)*2D*maxDist);
		}
		
		public static <T extends Entity> ITeleportXZ<T> inCircle(final double minDist, final double maxDist){
			return (entity, startPos, rand) -> {
				Vec offXZ = Vec.xzRandom(rand);
				double dist = minDist+rand.nextDouble()*(maxDist-minDist);
				
				return startPos.offset(offXZ.x*dist, 0D, offXZ.z*dist);
			};
		}
		
		public static <T extends Entity> ITeleportXZ<T> exactDistance(final double dist){
			return (entity, startPos, rand) -> {
				Vec offXZ = Vec.xzRandom(rand);
				return startPos.offset(offXZ.x*dist, 0D, offXZ.z*dist);
			};
		}
	}

	@FunctionalInterface
	public static interface ITeleportY<T extends Entity>{
		int findY(T entity, Vec startPos, Random rand);
		
		public static <T extends Entity> ITeleportY<T> around(final double maxDist){
			return (entity, startPos, rand) -> MathUtil.floor(startPos.y+(rand.nextDouble()-0.5D)*2D*maxDist);
		}
		
		public static <T extends Entity> ITeleportY<T> around(final double maxDist, int offset){
			return (entity, startPos, rand) -> MathUtil.floor(startPos.y+offset+(rand.nextDouble()-0.5D)*2D*maxDist);
		}
		
		/**
		 * Keeps going down (or up if {@code maxOffset} is negative) {@code maxOffset} blocks until it hits a solid block.
		 */
		public static <T extends Entity> ITeleportY<T> findSolidBottom(final ITeleportY<T> provider, final int maxOffset){
			return (entity, startPos, rand) -> {
				final int startY = provider.findY(entity, startPos, rand);
				
				for(int y = startY; y != startY-maxOffset; y -= (int)Math.signum(maxOffset)){
					if (Pos.at(entity.posX, y-1, entity.posZ).getMaterial(entity.worldObj).blocksMovement())return y;
				}
				
				return maxOffset <= 0 ? startY : startY-maxOffset;
			};
		}
	}
	
	private final ITeleportXZ<T> findXZ;
	private final ITeleportY<T> findY;
	
	TeleportLocation(ITeleportXZ<T> findXZ, ITeleportY<T> findY){
		this.findXZ = findXZ;
		this.findY = findY;
	}
	
	@Override
	public Vec findPosition(T entity, Vec startPos, Random rand){
		Vec vec = findXZ.findXZ(entity, startPos, rand);
		entity.setPosition(vec.x, entity.posY, vec.z);
		vec.y = findY.findY(entity, startPos, rand);
		return vec;
	}
}
