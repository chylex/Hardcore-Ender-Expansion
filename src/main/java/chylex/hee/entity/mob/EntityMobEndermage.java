package chylex.hee.entity.mob;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class EntityMobEndermage extends EntityMob{
	public EntityMobEndermage(World world){
		super(world);
	}
	
	public EntityMobEndermage(World world, double x, double y, double z){
		super(world);
		setPosition(x,y,z);
	}
}
