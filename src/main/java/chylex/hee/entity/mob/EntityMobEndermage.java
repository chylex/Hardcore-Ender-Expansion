package chylex.hee.entity.mob;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class EntityMobEndermage extends EntityMob implements IIgnoreEnderGoo{
	public EntityMobEndermage(World world){
		super(world);
	}
	
	public EntityMobEndermage(World world, double x, double y, double z){
		super(world);
		setPosition(x,y,z);
	}
}
