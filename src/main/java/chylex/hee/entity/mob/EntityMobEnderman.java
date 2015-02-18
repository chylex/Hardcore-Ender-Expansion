package chylex.hee.entity.mob;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;

public class EntityMobEnderman extends EntityEnderman implements IIgnoreEnderGoo{
	public EntityMobEnderman(World world){
		super(world);
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		super.dropFewItems(recentlyHit,looting);
		if (recentlyHit && rand.nextInt(Math.max(1,50-looting)) == 0)dropItem(ItemList.enderman_head,1);
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
	public String getName(){
		return hasCustomName() ? getCustomNameTag() : (ModCommonProxy.hardcoreEnderbacon ? StatCollector.translateToLocal("entity.enderman.bacon.name") : super.getName());
	}
}
