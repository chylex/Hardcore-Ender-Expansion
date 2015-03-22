package chylex.hee.entity.mob;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityMobSanctuaryOverseer extends EntityFlying{
	public EntityMobSanctuaryOverseer(World world){
		super(world);
		setSize(1F,1F);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30D);
	}
	
	@Override
	protected void onDeathUpdate(){
		if (++deathTime >= 2){
			// TODO particles
			setDead();
		}
	}
}
