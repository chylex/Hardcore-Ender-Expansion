package chylex.hee.mechanics.charms.handler;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
import chylex.hee.entity.mob.teleport.ITeleportPredicate;
import chylex.hee.entity.mob.teleport.MobTeleporter;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportXZ;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportY;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C06SetPlayerVelocity;
import chylex.hee.packets.client.C07AddPlayerVelocity;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntityAttributes.Operation;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.WorldUtil;
import chylex.hee.system.util.WorldUtil.GameRule;
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
	
	private final AttributeModifier speedModifier = EntityAttributes.createModifier("Charm speed",Operation.MULTIPLY,1.15D);
	private final MobTeleporter<EntityPlayer> lastResortTeleporter = new MobTeleporter<>();
	
	CharmEvents(){
		lastResortTeleporter.setAttempts(128);
		lastResortTeleporter.addLocationPredicate(ITeleportPredicate.airAboveSolid(2));
		
		lastResortTeleporter.onTeleport((entity, startPos, rand) -> {
			PacketPipeline.sendToAllAround(entity.dimension,startPos.x,startPos.y,startPos.z,64D,new C21EffectEntity(FXType.Entity.CHARM_LAST_RESORT,startPos.x,startPos.y,startPos.z,entity.width,entity.height));
			entity.setPositionAndUpdate(entity.posX,entity.posY+0.01D,entity.posZ);
		});
	}
	
	public void onDisabled(){
		if (!playerSpeed.isEmpty()){
			for(EntityPlayerMP player:EntitySelector.players()){
				UUID id = player.getGameProfile().getId();
				
				if (playerSpeed.containsKey(id)){
					IAttributeInstance attribute = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
					if (attribute != null)attribute.removeModifier(speedModifier);
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
					attribute.removeModifier(speedModifier);
					attribute.applyModifier(new AttributeModifier(speedModifier.getID(),speedModifier.getName()+spd,speedModifier.getAmount()*spd,speedModifier.getOperation()));
				}
				
				playerSpeed.put(playerID,spd);
			}
			
			// BASIC_VIGOR, EQUALITY
			if (e.player.shouldHeal() && e.player.getFoodStats().getFoodLevel() >= 18){
				float regen = getPropPercentDecrease(e.player,"regenspd",90F); // 100 was too much
				
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
		
		if (e.source.getEntity() == null){
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
			boolean isSourcePlayer = e.source.getEntity() instanceof EntityPlayer;
			
			if (isSourcePlayer){
				EntityPlayer sourcePlayer = (EntityPlayer)e.source.getEntity();

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
					List<Potion> potionEffects = CollectionUtil.newList(
						Potion.weakness, Potion.moveSlowdown, Potion.blindness, Potion.poison, null // null = fire
					);
					
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
						e.source.getEntity().attackEntityFrom(DamageSource.causePlayerDamage(targetPlayer),reflected);
						showBlockingEffect = true;
					}
					
					// BLOCKING REPULSION
					float repulseAmt = getPropSummed(targetPlayer,"blockrepulsepower");
					
					if (repulseAmt > 0.001F){
						float mp = 0.5F+0.8F*repulseAmt;
						Entity source = e.source.getEntity();
						
						Vec vec = Vec.between(targetPlayer,source).normalized().multiplied(mp);
						
						if (source instanceof EntityPlayer){
							PacketPipeline.sendToPlayer((EntityPlayer)source,new C07AddPlayerVelocity(vec.x,0.25D,vec.z));
							source.motionX += vec.x;
							source.motionY += 0.25D;
							source.motionZ += vec.z;
						}
						else source.addVelocity(vec.x,0.25D,vec.z);
						
						showBlockingEffect = true;
					}
					
					// BLOCKING REFLECTION & BLOCKING REPULSION EFFECT
					if (showBlockingEffect)PacketPipeline.sendToAllAround(e.entity,64,new C21EffectEntity(FXType.Entity.CHARM_BLOCK_EFFECT,e.entity));
				}
				
				// DAMAGE_REDIRECTION
				float[] redirMobs = getProp(targetPlayer,"redirmobs");
				
				if (redirMobs.length > 0){
					float[] redirAmt = getProp(targetPlayer,"rediramt");
					List<EntityLivingBase> nearbyEntities = EntitySelector.living(e.entity.worldObj,targetPlayer.boundingBox.expand(6D,3D,6D));
					Iterator<EntityLivingBase> iter = nearbyEntities.iterator();
					
					for(int a = 0; a < redirMobs.length; a++){
						for(int mob = 0; mob < Math.round(redirMobs[a]); mob++){
							while(iter.hasNext()){
								EntityLivingBase entity = iter.next();
								if (entity == targetPlayer || entity == e.source.getEntity())continue;
								
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
				EntityPlayer sourcePlayer = (EntityPlayer)e.source.getEntity();
				
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
				final float[] lastResortDist = getProp(targetPlayer,"lastresortblocks");
				final int randIndex = targetPlayer.worldObj.rand.nextInt(lastResortCooldown.length);
				
				lastResortTeleporter.setLocationSelector(
					ITeleportXZ.exactDistance(lastResortDist[randIndex]),
					ITeleportY.findSolidBottom((entity, startPos, rand) -> MathUtil.floor(startPos.y)-2,-6)
				);
				
				lastResortTeleporter.teleport(targetPlayer,targetPlayer.worldObj.rand);
				
				targetPlayer.setHealth(targetPlayer.prevHealth);
				targetPlayer.motionX = targetPlayer.motionY = targetPlayer.motionZ = 0D;
				playerLastResortCooldown.put(targetPlayer.getGameProfile().getId(),(byte)(-100+lastResortCooldown[randIndex]*20));
				
				PacketPipeline.sendToPlayer(targetPlayer,new C06SetPlayerVelocity(0D,0D,0D));
				PacketPipeline.sendToAllAround(targetPlayer,64D,new C21EffectEntity(FXType.Entity.CHARM_LAST_RESORT,targetPlayer));
				e.setCanceled(true);
				return;
			}
		}
		
		if (e.source.getEntity() instanceof EntityPlayer){
			EntityPlayer sourcePlayer = (EntityPlayer)e.source.getEntity();
			
			// SLAUGHTER_IMPACT
			float[] impactRad = getProp(sourcePlayer,"impactrad");
			
			if (impactRad.length > 0){
				float[] impactAmt = getProp(sourcePlayer,"impactamt");
				float lastDamage = e.entityLiving.lastDamage;
				
				for(int a = 0; a < impactRad.length; a++){
					for(EntityLivingBase entity:EntitySelector.living(e.entity.worldObj,e.entity.boundingBox.expand(impactRad[a],impactRad[a],impactRad[a]))){
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
		if (e.recentlyHit && e.source.getEntity() instanceof EntityPlayer && e.entityLiving instanceof EntityLiving &&
			!e.entityLiving.isChild() && WorldUtil.getRuleBool(e.entity.worldObj,GameRule.DO_MOB_LOOT)){
			// BASIC_MAGIC / EQUALITY
			int xp = ReflectionPublicizer.m__getExperiencePoints__EntityLivingBase(e.entityLiving,(EntityPlayer)e.source.getEntity());
			xp = MathUtil.ceil(getPropPercentIncrease((EntityPlayer)e.source.getEntity(),"exp",xp)); // extra xp only
			DragonUtil.spawnXP(e.entity,xp);
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
