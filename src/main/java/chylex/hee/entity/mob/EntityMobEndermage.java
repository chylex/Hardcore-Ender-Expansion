package chylex.hee.entity.mob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;

public class EntityMobEndermage extends EntityMob implements IIgnoreEnderGoo{
	public EntityMobEndermage(World world){
		super(world);
		setSize(0.6F,2.7F);
		stepHeight = 1F;
	}
	
	public EntityMobEndermage(World world, double x, double y, double z){
		this(world);
		setPosition(x,y,z);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5D);
	}
}
