package chylex.hee.entity.mob;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;

public class EntityMobEnderman extends EntityEnderman{
	public EntityMobEnderman(World world){
		super(world);
	}
	
	@Override
	protected String getLivingSound(){
		return Baconizer.soundNormal(super.getLivingSound());
	}
	
	@Override
	protected String getHurtSound(){
		return Baconizer.soundNormal(super.getHurtSound());
	}
	
	@Override
	protected String getDeathSound(){
		return Baconizer.soundDeath(super.getDeathSound());
	}
	
	@Override
	public String getCommandSenderName(){
		return ModCommonProxy.hardcoreEnderbacon ? StatCollector.translateToLocal("entity.enderman.bacon.name") : super.getCommandSenderName();
	}
}
