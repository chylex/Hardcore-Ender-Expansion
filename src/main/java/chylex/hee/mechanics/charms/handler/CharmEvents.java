package chylex.hee.mechanics.charms.handler;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C06SetPlayerVelocity;
import chylex.hee.packets.client.C07AddPlayerVelocity;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public final class CharmEvents{
	public static float[] getProp(EntityPlayer player, String prop){
		CharmPouchInfo info = CharmPouchHandler.getActivePouch(player);
		if (info == null)return ArrayUtils.EMPTY_FLOAT_ARRAY;
		
		TFloatArrayList values = new TFloatArrayList(5);
		for(Pair<CharmType,CharmRecipe> entry:info.charms){
			float value = entry.getRight().getProp(prop);
			if (value != -1)values.add(value);
		}
		
		return values.toArray();
	}
	
	public static float getPropSummed(EntityPlayer player, String prop){
		float finalValue = 0;
		for(float val:getProp(player,prop))finalValue += val;
		return finalValue;
	}
	
	public static float getPropPercentIncrease(EntityPlayer player, String prop, float baseValue){
		float finalValue = 0;
		for(float val:getProp(player,prop))finalValue += (val*baseValue)-baseValue;
		return finalValue;
	}
	
	public static float getPropPercentDecrease(EntityPlayer player, String prop, float baseValue){
		float finalValue = 0, tmp;
		
		for(float val:getProp(player,prop)){
			tmp = baseValue*val;
			finalValue += tmp;
			baseValue -= tmp;
		}
		
		return finalValue;
	}
	
	private final TObjectByteHashMap<UUID> playerRegen = new TObjectByteHashMap<>();
	private final TObjectFloatHashMap<UUID> playerSpeed = new TObjectFloatHashMap<>();
	private final TObjectFloatHashMap<UUID> playerStealDealtDamage = new TObjectFloatHashMap<>();
	private final TObjectByteHashMap<UUID> playerLastResortCooldown = new TObjectByteHashMap<>();
	
	private final AttributeModifier attrSpeed = new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),"HeeCharmSpeed",0.15D,2);
	
	CharmEvents(){}
	
	public void onDisabled(){
		if (!playerSpeed.isEmpty()){
			for(EntityPlayerMP player:(List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList){
				UUID id = player.getGameProfile().getId();
				
				if (playerSpeed.containsKey(id)){
					IAttributeInstance attribute = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
					if (attribute != null)attribute.removeModifier(attrSpeed);
				}
			}
		}
		
		playerRegen.clear();
		playerSpeed.clear();
		playerStealDealtDamage.clear();
		playerLastResortCooldown.clear();
	}
	
	/**
	 * BASIC_AGILITY, BASIC_VIGOR, EQUALITY
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(PlayerTickEvent e){
		if (e.side != Side.SERVER)return;
		
		if (e.phase == Phase.START){
			// <IDLE CHECK>
			CharmPouchInfo info = CharmPouchHandler.getActivePouch(e.player);
			if (info != null && info.isIdle(e.player.worldObj))CharmPouchHandler.setActivePouch(e.player,null);
			
			UUID playerID = e.player.getGameProfile().getId();
			
			// BASIC_AGILITY, EQUALITY
			float spd = getPropSummed(e.player,"spd");
			float prevSpd = playerSpeed.get(playerID);
			
			if (MathUtil.floatEquals(prevSpd,playerSpeed.getNoEntryValue()) || !MathUtil.floatEquals(prevSpd,spd)){
				IAttributeInstance attribute = e.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
				
				if (attribute != null){
					attribute.removeModifier(attrSpeed);
					attribute.applyModifier(new AttributeModifier(attrSpeed.getID(),attrSpeed.getName()+spd,attrSpeed.getAmount()*spd,attrSpeed.getOperation()));
				}
				
				playerSpeed.put(playerID,spd);
			}
			
			// BASIC_VIGOR, EQUALITY
			if (e.player.shouldHeal() && e.player.getFoodStats().getFoodLevel() >= 18){
				float regen = getPropPercentDecrease(e.player,"regenspd",100F);
				
				if (regen > 0F && playerRegen.adjustOrPutValue(playerID,(byte)1,(byte)0) >= 100F-regen){
					e.player.heal(1F);
					playerRegen.put(playerID,(byte)0);
				}
			}
			
			if (playerLastResortCooldown.containsKey(playerID)){
				if (playerLastResortCooldown.adjustOrPutValue(playerID,(byte)-1,(byte)-100) <= -100)playerLastResortCooldown.remove(playerID);
			}
		}
	}
	
	/**
	 * BASIC_POWER, BASIC_DEFENSE, EQUALITY, BLOCKING, BLOCKING_REFLECTION, BLOCKING_REPULSION, CRITICAL_STRIKE, FALLING_PROTECTION, WITCHERY_HARM,
	 * DAMAGE_REDIRECTION, MAGIC_PENETRATION, LIFE_STEAL, LAST_RESORT
	 * It is not called on client side, check not needed.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingHurtEvent e){
		boolean isTargetPlayer = e.entityLiving instanceof EntityPlayer;
		
		if (e.source.getSourceOfDamage() == null){
			if (isTargetPlayer){
				EntityPlayer targetPlayer = (EntityPlayer)e.entity;
				
				if (e.source == DamageSource.fall){
					// FALLING_PROTECTION
					e.ammount -= getPropSummed(targetPlayer,"fallblocks")*0.5F;
					if (e.ammount <= 0.001F)e.ammount = 0F;
				}
				else if (e.source.isMagicDamage()){
					e.ammount -= getPropPercentDecrease(targetPlayer,"reducemagicdmg",e.ammount);
				}
			}
		}
		else{
			boolean isSourcePlayer = e.source.getSourceOfDamage() instanceof EntityPlayer;
			
			if (isSourcePlayer){
				EntityPlayer sourcePlayer = (EntityPlayer)e.source.getSourceOfDamage();

				// BASIC_POWER / EQUALITY
				e.ammount += getPropPercentIncrease(sourcePlayer,"dmg",e.ammount);
	
				// CRITICAL_STRIKE
				float[] crit = getProp(sourcePlayer,"critchance");
				
				if (crit.length > 0){
					float[] critDmg = getProp(sourcePlayer,"critdmg");
					float val = 0F;
					
					for(int a = 0; a < crit.length; a++){
						if (e.entity.worldObj.rand.nextFloat() < crit[a])val += (critDmg[a]*e.ammount)-e.ammount;
					}
					
					if (val > 0F){
						e.ammount += val;
						PacketPipeline.sendToAllAround(e.entity,64,new C21EffectEntity(FXType.Entity.CHARM_CRITICAL,e.entity));
					}
				}
				
				// WITCHERY_HARM
				float[] badEff = getProp(sourcePlayer,"badeffchance");
				
				if (badEff.length > 0){
					float[] badEffLvl = getProp(sourcePlayer,"badefflvl");
					float[] badEffTime = getProp(sourcePlayer,"badefftime");
					boolean causedEffect = false;
					List<Potion> potionEffects = new ArrayList<>(Arrays.asList(
						Potion.weakness, Potion.moveSlowdown, Potion.blindness, Potion.poison, null // null = fire
					));
					
					for(int a = 0; a < badEff.length && !potionEffects.isEmpty(); a++){
						if (e.entity.worldObj.rand.nextFloat() < badEff[a]){
							Potion type = potionEffects.remove(e.entity.worldObj.rand.nextInt(potionEffects.size()));
							
							if (type == null)e.entity.setFire((int)badEffTime[a]);
							else e.entityLiving.addPotionEffect(new PotionEffect(type.id,20*(int)badEffTime[a],(int)badEffLvl[a]-1));
							
							causedEffect = true;
						}
					}
					
					if (causedEffect)PacketPipeline.sendToAllAround(e.entity,64,new C21EffectEntity(FXType.Entity.CHARM_WITCH,e.entity));
				}
				
				// MAGIC_PENETRATION
				float magic = getPropPercentDecrease(sourcePlayer,"dmgtomagic",e.ammount);
				
				if (magic > 0.001F){
					e.ammount -= magic;
					e.entity.hurtResistantTime = 0;
					e.entity.attackEntityFrom(DamageSource.magic,magic);
				}
			}
			
			if (isTargetPlayer){
				EntityPlayer targetPlayer = (EntityPlayer)e.entityLiving;
				
				// BASIC_DEFENSE / EQUALITY
				e.ammount -= getPropPercentDecrease(targetPlayer,"reducedmg",e.ammount);
				
				if (targetPlayer.isBlocking()){
					// BLOCKING
					e.ammount -= getPropPercentDecrease(targetPlayer,"reducedmgblock",e.ammount);
					
					boolean showBlockingEffect = false;
					
					// BLOCKING_REFLECTION
					float[] reflectDmg = getProp(targetPlayer,"blockreflectdmg");
					
					if (reflectDmg.length > 0){
						float reflected = 0F;
						for(int a = 0; a < reflectDmg.length; a++)reflected += e.ammount*reflectDmg[a];
						e.source.getSourceOfDamage().attackEntityFrom(DamageSource.causePlayerDamage(targetPlayer),reflected);
						showBlockingEffect = true;
					}
					
					// BLOCKING REPULSION
					float repulseAmt = getPropSummed(targetPlayer,"blockrepulsepower");
					
					if (repulseAmt > 0.001F){
						float mp = 0.5F+0.8F*repulseAmt;
						Entity source = e.source.getSourceOfDamage();
						
						double[] vec = DragonUtil.getNormalizedVector(source.posX-targetPlayer.posX,source.posZ-targetPlayer.posZ);
						vec[0] *= mp;
						vec[1] *= mp;
						
						if (source instanceof EntityPlayer){
							PacketPipeline.sendToPlayer((EntityPlayer)source,new C07AddPlayerVelocity(vec[0],0.25D,vec[1]));
							source.motionX += vec[0];
							source.motionY += 0.25D;
							source.motionZ += vec[1];
						}
						else source.addVelocity(vec[0],0.25D,vec[1]);
						
						showBlockingEffect = true;
					}
					
					// BLOCKING REFLECTION & BLOCKING REPULSION EFFECT
					if (showBlockingEffect)PacketPipeline.sendToAllAround(e.entity,64,new C21EffectEntity(FXType.Entity.CHARM_BLOCK_EFFECT,e.entity));
				}
				
				// DAMAGE_REDIRECTION
				float[] redirMobs = getProp(targetPlayer,"redirmobs");
				
				if (redirMobs.length > 0){
					float[] redirAmt = getProp(targetPlayer,"rediramt");
					List<EntityLivingBase> nearbyEntities = e.entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,targetPlayer.boundingBox.expand(6D,3D,6D));
					Iterator<EntityLivingBase> iter = nearbyEntities.iterator();
					
					for(int a = 0; a < redirMobs.length; a++){
						for(int mob = 0; mob < Math.round(redirMobs[a]); mob++){
							while(iter.hasNext()){
								EntityLivingBase entity = iter.next();
								if (entity == targetPlayer || entity == e.source.getSourceOfDamage())continue;
								
								entity.attackEntityFrom(DamageSource.causePlayerDamage(targetPlayer),redirAmt[a]*e.ammount);
								PacketPipeline.sendToAllAround(targetPlayer,64D,new C22EffectLine(FXType.Line.CHARM_DAMAGE_REDIRECTION,entity,targetPlayer));
								e.ammount -= redirAmt[a];
								break;
							}
						}
					}
				}
			}
			
			if (isSourcePlayer){
				EntityPlayer sourcePlayer = (EntityPlayer)e.source.getSourceOfDamage();
				
				// LIFE_STEAL
				float[] stealHealth = getProp(sourcePlayer,"stealhealth");
				
				if (stealHealth.length > 0){
					float[] stealDealt = getProp(sourcePlayer,"stealdealt");
					int randIndex = sourcePlayer.worldObj.rand.nextInt(stealHealth.length);
					
					if (playerStealDealtDamage.adjustOrPutValue(sourcePlayer.getGameProfile().getId(),e.ammount,e.ammount) >= stealDealt[randIndex]){
						sourcePlayer.heal(stealHealth[randIndex]);
						playerStealDealtDamage.adjustValue(sourcePlayer.getGameProfile().getId(),-e.ammount);
					}
				}
			}
		}
	}
	
	/**
	 * SLAUGHTER_IMPACT, LAST_RESORT
	 * It is not called on client side, check not needed.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingDeath(LivingDeathEvent e){
		if (e.entity instanceof EntityPlayer){
			EntityPlayer targetPlayer = (EntityPlayer)e.entity;
			
			// LAST_RESORT
			float[] lastResortCooldown = getProp(targetPlayer,"lastresortcooldown");
			
			if (lastResortCooldown.length > 0 && !playerLastResortCooldown.containsKey(targetPlayer.getGameProfile().getId())){
				float[] lastResortDist = getProp(targetPlayer,"lastresortblocks");
				int randIndex = targetPlayer.worldObj.rand.nextInt(lastResortCooldown.length);
				
				for(int attempt = 0, xx, yy, zz; attempt < 128; attempt++){
					float ang = targetPlayer.worldObj.rand.nextFloat()*2F*(float)Math.PI;
					
					xx = MathUtil.floor(targetPlayer.posX+MathHelper.cos(ang)*lastResortDist[randIndex]);
					zz = MathUtil.floor(targetPlayer.posZ+MathHelper.sin(ang)*lastResortDist[randIndex]);
					yy = MathUtil.floor(targetPlayer.posY)-2;
					
					for(int yAttempt = 0; yAttempt <= 6; yAttempt++){
						if (!targetPlayer.worldObj.isAirBlock(xx,yy-1,zz) && targetPlayer.worldObj.isAirBlock(xx,yy,zz) && targetPlayer.worldObj.isAirBlock(xx,yy+1,zz)){
							PacketPipeline.sendToAllAround(targetPlayer,64D,new C21EffectEntity(FXType.Entity.CHARM_LAST_RESORT,targetPlayer));
							targetPlayer.setPositionAndUpdate(xx+0.5D,yy+0.01D,zz+0.5D);
							attempt = 129;
							break;
						}
					}
				}
				
				targetPlayer.setHealth(targetPlayer.prevHealth);
				targetPlayer.motionX = targetPlayer.motionY = targetPlayer.motionZ = 0D;
				playerLastResortCooldown.put(targetPlayer.getGameProfile().getId(),(byte)(-100+lastResortCooldown[randIndex]*20));
				
				PacketPipeline.sendToPlayer(targetPlayer,new C06SetPlayerVelocity(0D,0D,0D));
				PacketPipeline.sendToAllAround(targetPlayer,64D,new C21EffectEntity(FXType.Entity.CHARM_LAST_RESORT,targetPlayer));
				e.setCanceled(true);
				return;
			}
		}
		
		if (e.source.getSourceOfDamage() instanceof EntityPlayer){
			EntityPlayer sourcePlayer = (EntityPlayer)e.source.getSourceOfDamage();
			
			// SLAUGHTER_IMPACT
			float[] impactRad = getProp(sourcePlayer,"impactrad");
			
			if (impactRad.length > 0){
				float[] impactAmt = getProp(sourcePlayer,"impactamt");
				float lastDamage = e.entityLiving.lastDamage;//(float)ReflectionPublicizer.get(ReflectionPublicizer.entityLivingBaseLastDamage,e.entityLiving);
				
				for(int a = 0; a < impactRad.length; a++){
					List<EntityLivingBase> entities = e.entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,e.entity.boundingBox.expand(impactRad[a],impactRad[a],impactRad[a]));
					
					for(EntityLivingBase entity:entities){
						if (entity == sourcePlayer || entity == e.entity)continue;
						if (entity.getDistanceToEntity(e.entity) <= impactRad[a]){
							entity.attackEntityFrom(DamageSource.generic,impactAmt[a]*lastDamage);
							PacketPipeline.sendToAllAround(e.entity,64D,new C22EffectLine(FXType.Line.CHARM_SLAUGHTER_IMPACT,entity,e.entity));
						}
					}
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
			int xp = (int)ReflectionPublicizer.invoke(ReflectionPublicizer.entityLivingBaseGetExperiencePoints,e.entityLiving,(EntityPlayer)e.source.getSourceOfDamage());
			xp = MathUtil.ceil(getPropPercentIncrease((EntityPlayer)e.source.getSourceOfDamage(),"exp",xp)); // extra xp only
			
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
		
		// BASIC_MAGIC / EQUALITY
		e.setExpToDrop(e.getExpToDrop()+MathUtil.ceil(getPropPercentIncrease(e.getPlayer(),"exp",e.getExpToDrop())));
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
				
				if (MathUtil.floatEquals(toRepair,0F))return;
				
				ItemStack newIS = e.original.copy();
				newIS.stackSize = 1;
				newIS.setItemDamage(newIS.getMaxDamage()-MathUtil.floor(newIS.getMaxDamage()*Math.min(1F,toRepair)));
				
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
			
			float healthRecovered = getPropPercentIncrease(e.entityPlayer,"healthperhunger",hungerRecovered);
			if (healthRecovered > 0F)e.entityPlayer.heal(healthRecovered);
		}
	}
	
	/**
	 * HASTE (SERVER)
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBreakSpeed(BreakSpeed e){
		// HASTE
		if (!e.entity.worldObj.isRemote)e.newSpeed += getPropPercentIncrease(e.entityPlayer,"breakspd",e.originalSpeed);
	}
}
