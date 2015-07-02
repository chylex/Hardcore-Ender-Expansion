package chylex.hee.entity.mob;
import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onUpdate()
	{
		// TODO Auto-generated method stub
		super.onUpdate();
		
		
		if(worldObj.getTotalWorldTime()%20==0){
			if (worldObj.isRemote)return;
			if(isDead)return;
			
			int distanceXZ = 20;
			int distanceY = 7; 
			
			ArrayList<EntityLivingBase> entitesInRange = (ArrayList<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
					AxisAlignedBB.getBoundingBox(posX - distanceXZ, posY - distanceY, posZ - distanceXZ, 
					posX + distanceXZ, posY + distanceY, posZ + distanceXZ));
			
			for(EntityLivingBase entity:entitesInRange){
				
				if(entity.isDead)continue;
				if(!(entity instanceof EntityMob )|| entity instanceof EntityMobEnderman)continue;
				if(entity.getActivePotionEffects().size() > 0)continue;
			
				switch(rand.nextInt(4)){
					case 0:
						entity.addPotionEffect(new PotionEffect(Potion.damageBoost.id,8));
						break;
					case 1:
						entity.addPotionEffect(new PotionEffect(Potion.regeneration.id,8));
						break;
					case 2:
						entity.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,8));
						break;
					case 3:
						entity.addPotionEffect(new PotionEffect(Potion.resistance.id,8));
						break;
				}
			}
		}
	}
}
