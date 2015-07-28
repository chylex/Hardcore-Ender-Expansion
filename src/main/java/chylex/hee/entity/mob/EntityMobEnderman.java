package chylex.hee.entity.mob;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootMobInfo;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityMobEnderman extends EntityEnderman implements IIgnoreEnderGoo{
	private static final PercentageLootTable drops = new PercentageLootTable();
	
	static{
		drops.addLoot(Items.ender_pearl).setChances(obj -> {
			LootMobInfo info = (LootMobInfo)obj;
			
			switch(info.looting){
				case 0: return new float[]{ 0.60F };
				case 1: return new float[]{ 0.70F };
				case 2: return new float[]{ 0.65F, 0.10F };
				default: return new float[]{ 0.72F, 0.16F };
			}
		});
	}
	
	public EntityMobEnderman(World world){
		super(world);
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		for(ItemStack drop:drops.generateLoot(new LootMobInfo(this,recentlyHit,looting),rand))entityDropItem(drop,0F);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (super.attackEntityFrom(source,amount)){
			if (dimension == 0)CausatumUtils.increase(source,CausatumMeters.OVERWORLD_ENDERMAN_DAMAGE,amount*0.25F);
			else if (dimension == 1)CausatumUtils.increase(source,CausatumMeters.END_MOB_DAMAGE,amount*0.5F);
			
			return true;
		}
		else return false;
	}
	
	@Override
	public void onDeath(DamageSource source){
		if (!worldObj.isRemote && worldObj.provider.dimensionId == 1 && worldObj.getTotalWorldTime()-EntityBossDragon.lastUpdate < 20 && source.getEntity() instanceof EntityPlayer){
			for(Object o:worldObj.loadedEntityList){
				if (o instanceof EntityBossDragon){
					((EntityBossDragon)o).achievements.onPlayerKilledEnderman((EntityPlayer)source.getEntity());
					break;
				}
			}
		}
		
		super.onDeath(source);
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
	
	@Override
	public boolean getCanSpawnHere(){
		return super.getCanSpawnHere() && (worldObj.provider.dimensionId != 0 || !worldObj.isDaytime());
	}
}
