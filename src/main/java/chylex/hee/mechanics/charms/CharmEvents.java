package chylex.hee.mechanics.charms;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.ReflectionPublicizer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

final class CharmEvents{
	private static final float DEFAULT_PLAYER_SPEED = 0.1F;
	
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
	
	private final TObjectIntHashMap<UUID> playerProperties = new TObjectIntHashMap<>();
	
	CharmEvents(){}
	
	/**
	 * BASIC_AGILITY, BASIC_VIGOR, EQUALITY
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(PlayerTickEvent e){
		if (e.side == Side.CLIENT){
			if (e.phase == Phase.START){
				// BASIC_AGILITY, EQUALITY
				float spd = getPropMultiplied(e.player,"spd",DEFAULT_PLAYER_SPEED); // TODO attributes
				if (spd > 0F)e.player.capabilities.setPlayerWalkSpeed(DEFAULT_PLAYER_SPEED+spd);
			}
		}
		else if (e.side == Side.SERVER){
			if (e.phase == Phase.START){
				// BASIC_VIGOR, EQUALITY
				float regen = getPropMultiplied(e.player,"regenspd",40F);
				
				if (regen > 0F && playerProperties.adjustOrPutValue(e.player.getGameProfile().getId(),1,0) >= 40F-regen){
					e.player.heal(1F);
				}
			}
		}
	}
	
	/**
	 * BASIC_POWER, BASIC_DEFENSE, EQUALITY, BLOCKING, BLOCKING_REFLECTION, CRITICAL_STRIKE, FALLING_PROTECTION, WITCHERY_HARM
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
				
				// WITCHERY_HARM
				float[] badEff = getProp(sourcePlayer,"badeffchance");
				
				if (badEff.length > 0){
					float[] badEffLvl = getProp(sourcePlayer,"badefflvl");
					float[] badEffTime = getProp(sourcePlayer,"badefftime");
					List<Potion> potionEffects = new ArrayList<>(Arrays.asList(
						Potion.weakness, Potion.moveSlowdown, Potion.blindness, Potion.poison, null // null = fire
					));
					
					for(int a = 0; a < badEff.length && !potionEffects.isEmpty(); a++){
						if (e.entity.worldObj.rand.nextFloat() < badEff[a]){
							Potion type = potionEffects.remove(e.entity.worldObj.rand.nextInt(potionEffects.size()));
							
							if (type == null)sourcePlayer.setFire((int)badEffTime[a]);
							else sourcePlayer.addPotionEffect(new PotionEffect(type.id,20*(int)badEffTime[a],(int)badEffLvl[a]-1));
						}
					}
				}
			}
				
			// BASIC_DEFENSE / EQUALITY
			e.ammount -= getPropMultiplied(targetPlayer,"reducedmg",e.ammount);
			
			if (targetPlayer.isBlocking()){
				// BLOCKING
				e.ammount -= getPropMultiplied(targetPlayer,"reducedmgblock",e.ammount);
				
				// BLOCKING_REFLECTION
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
	
	/**
	 * BASIC_MAGIC, EQUALITY
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBlockBreak(BreakEvent e){
		if (e.getPlayer() == null)return;
		
		e.setExpToDrop(e.getExpToDrop()+(int)Math.ceil(getPropMultiplied(e.getPlayer(),"exp",e.getExpToDrop())));
	}
	
	/**
	 * SECOND_DURABILITY
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemDestroyed(PlayerDestroyItemEvent e){
		if (e.entity.worldObj.isRemote)return;
		
		// SECOND_DURABILITY
		if (e.original.isItemStackDamageable() && e.original.getItem().isRepairable()){
			float[] repair = getProp(e.entityPlayer,"recdurabilitychance");
			
			if (repair.length > 0){
				float[] repairAmt = getProp(e.entityPlayer,"recdurabilityamt");
				float toRepair = 0F;
				
				for(int a = 0; a < repair.length; a++){
					if (e.entity.worldObj.rand.nextFloat() < repair[a])toRepair += repairAmt[a];
				}
				
				ItemStack newIS = e.original.copy();
				newIS.setItemDamage(newIS.getMaxDamage()-(int)Math.floor(newIS.getMaxDamage()*Math.min(1F,toRepair)));
				
				EntityItem newItem = new EntityItem(e.entity.worldObj,e.entity.posX,e.entity.posY+e.entityPlayer.getEyeHeight()-0.3D,e.entity.posZ,newIS);
				newItem.delayBeforeCanPickup = 40;
				
				float power = 0.3F, yawRadians = (float)Math.toRadians(e.entityPlayer.rotationYaw), randomAngle = e.entity.worldObj.rand.nextFloat()*(float)Math.PI*2F;
				
				newItem.motionX = -MathHelper.sin(yawRadians)*MathHelper.cos(yawRadians)*power;
				newItem.motionZ = MathHelper.cos(yawRadians)*MathHelper.cos(yawRadians)*power;
				newItem.motionY = -MathHelper.sin((float)Math.toRadians(e.entity.rotationPitch))*power+0.1F;

				power = 0.02F*e.entity.worldObj.rand.nextFloat();
				newItem.motionX += MathHelper.cos(randomAngle)*power;
				newItem.motionY += (e.entity.worldObj.rand.nextFloat()-e.entity.worldObj.rand.nextFloat())*0.1F;
				newItem.motionZ += MathHelper.sin(randomAngle)*power;
				
				e.entity.worldObj.spawnEntityInWorld(newItem);
			}
		}
	}
	
	/**
	 * DIGESTIVE_RECOVER
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerFinishUsingItem(PlayerUseItemEvent.Finish e){
		if (e.entity.worldObj.isRemote)return;
		
		// DIGESTIVE_RECOVER
		if (e.item.getItemUseAction() == EnumAction.eat && e.item.getItem() instanceof ItemFood){
			int hungerRecovered = ((ItemFood)e.item.getItem()).func_150905_g(e.item);
			
			float healthRecovered = getPropMultiplied(e.entityPlayer,"healthperhunger",hungerRecovered);
			if (healthRecovered > 0F)e.entityPlayer.heal(healthRecovered);
		}
	}
	
	/**
	 * HASTE
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBreakSpeed(BreakSpeed e){
		if (e.entity.worldObj.isRemote)return;
		
		// HASTE
		e.newSpeed *= 1F+getPropMultiplied(e.entityPlayer,"breakspd",1F);
	}
}
