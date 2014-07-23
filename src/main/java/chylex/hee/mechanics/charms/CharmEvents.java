package chylex.hee.mechanics.charms;
import gnu.trove.list.array.TFloatArrayList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.ReflectionPublicizer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

final class CharmEvents{
	private static float[] getProp(EntityPlayer player, String prop){
		CharmPouchInfo info = CharmPouchHandler.getActivePouch(player);
		if (info == null)return ArrayUtils.EMPTY_FLOAT_ARRAY;
		
		TFloatArrayList values = new TFloatArrayList(5);
		for(Pair<CharmType,CharmRecipe> entry:info.charms){
			float value = entry.getRight().getProp(prop);
			if (value != -1)values.add(value);
		}
		
		return values.toArray();
	}
	
	private static float getPropSummed(EntityPlayer player, String prop){
		float finalValue = 0;
		for(float val:getProp(player,prop))finalValue += val;
		return finalValue;
	}
	
	private static float getPropMultiplied(EntityPlayer player, String prop, float baseValue){
		float finalValue = 0;
		for(float val:getProp(player,prop))finalValue += (val*baseValue)-baseValue;
		return finalValue;
	}
	
	CharmEvents(){}
	
	/**
	 * BASIC_POWER, BASIC_DEFENSE, EQUALITY, BLOCKING, BLOCKING_REFLECTION, CRITICAL_STRIKE, FALLING_PROTECTION
	 * It is not called on client side, check not needed.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingHurtEvent e){
		if (!(e.entityLiving instanceof EntityPlayer))return;
		
		EntityPlayer targetPlayer = (EntityPlayer)e.entityLiving;
		
		if (e.source.getSourceOfDamage() == null){
			if (e.source == DamageSource.fall){
				// FALLING_PROTECTION
				e.ammount -= getPropSummed(targetPlayer,"fallblocks")*0.5F;
				if (e.ammount <= 0.001F)e.ammount = 0F;
			}
		}
		else{
			if (e.source.getSourceOfDamage() instanceof EntityPlayer){
				EntityPlayer sourcePlayer = (EntityPlayer)e.source.getSourceOfDamage();
	
				// BASIC_POWER / EQUALITY
				e.ammount += getPropMultiplied(sourcePlayer,"dmg",e.ammount);
	
				// CRITICAL_STRIKE
				float[] crit = getProp(sourcePlayer,"critchance");
				
				if (crit.length > 0){
					float[] critDmg = getProp(sourcePlayer,"critdmg");
					float val = 0F;
					
					for(int a = 0; a < crit.length; a++){
						if (e.entity.worldObj.rand.nextFloat() < crit[a])val += (critDmg[a]*e.ammount)-e.ammount;
					}
					
					e.ammount += val;
				}
			}
				
			// BASIC_DEFENSE / EQUALITY
			e.ammount -= getPropMultiplied(targetPlayer,"reducedmg",e.ammount);
			
			if (targetPlayer.isBlocking()){
				// BLOCKING
				e.ammount -= getPropMultiplied(targetPlayer,"reducedmgblock",e.ammount);
				
				// BLOCKING REFLECTION
				float[] reflect = getProp(targetPlayer,"reducedmgblock");
				
				if (reflect.length > 0){
					float[] reflectDmg = getProp(targetPlayer,"blockreflectdmg");
					float reflected = 0F;
					
					for(int a = 0; a < reflect.length; a++){
						if (e.entity.worldObj.rand.nextFloat() < reflect[a])reflected += e.ammount*reflectDmg[a];
					}
					
					e.source.getSourceOfDamage().attackEntityFrom(DamageSource.causePlayerDamage(targetPlayer),reflected);
				}
			}
		}
	}
	
	/**
	 * BASIC_MAGIC, EQUALITY
	 * It is not called on client side, check not needed.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingDrops(LivingDropsEvent e){
		if (e.recentlyHit && e.source.getSourceOfDamage() instanceof EntityPlayer && e.entityLiving instanceof EntityLiving &&
			!e.entityLiving.isChild() && e.entity.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")){
			// BASIC_MAGIC / EQUALITY
			int xp = (int)ReflectionPublicizer.get(ReflectionPublicizer.entityLivingExperienceValue,e.entityLiving);
			xp = (int)Math.ceil(getPropMultiplied((EntityPlayer)e.source.getSourceOfDamage(),"exp",xp));
			
			while(xp > 0){
				int split = EntityXPOrb.getXPSplit(xp);
				xp -= split;
				e.entity.worldObj.spawnEntityInWorld(new EntityXPOrb(e.entity.worldObj,e.entity.posX,e.entity.posY,e.entity.posZ,split));
			}
		}
	}
}
