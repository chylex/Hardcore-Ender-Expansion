package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class PathNavigateGroundCustom extends PathNavigateGround{
	public float searchRange = 16F;
	
	public PathNavigateGroundCustom(EntityLiving entity, World world){
		super(entity,world);
	}
	
	@Override
	public float getPathSearchRange(){
		return searchRange;
	}
}
