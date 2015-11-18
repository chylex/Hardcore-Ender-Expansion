package chylex.hee.mechanics.curse;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import chylex.hee.entity.fx.FXType;
import chylex.hee.init.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.MathUtil;

public enum CurseType{
	TELEPORTATION(0, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			NBTTagCompound nbt = entity.getEntityData();
			
			if (!nbt.hasKey("HEE_C0_t")){
				nbt.setByte("HEE_C0_t",(byte)0);
				return false;
			}
			
			byte timer = nbt.getByte("HEE_C0_t");
			boolean hasTeleported = false;
			
			if (timer == 0){
				Random rand = entity.getRNG();
				timer = (byte)(20+rand.nextInt(60));
				
				double prevX = entity.posX, prevY = entity.posY, prevZ = entity.posZ, tpX, tpY, tpZ;
				
				for(int attempt = 0; attempt < 200; attempt++){
					tpX = prevX+(rand.nextDouble()-0.5D)*64D;
					tpY = prevY+rand.nextInt(64)-32;
					tpZ = prevZ+(rand.nextDouble()-0.5D)*64D;
					
					BlockPosM pos = BlockPosM.tmp(tpX,tpY,tpZ);

					if (entity.worldObj.blockExists(pos.x,pos.y,pos.z)){
						boolean foundTopBlock = false;

						while(!foundTopBlock && pos.y > 0){
							if (pos.moveDown().getMaterial(entity.worldObj).blocksMovement())foundTopBlock = true;
							else --tpY;
						}

						if (foundTopBlock){
							entity.setPositionAndUpdate(tpX,tpY,tpZ);

							if ((entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty() && !entity.worldObj.isAnyLiquid(entity.boundingBox))){
								hasTeleported = true;
							}
						}
					}

					if (!hasTeleported)entity.setPosition(prevX,prevY,prevZ);
					else{
						if (!entity.worldObj.isRemote){
							PacketPipeline.sendToAllAround(entity,256D,new C22EffectLine(FXType.Line.ENDERMAN_TELEPORT,prevX,prevY,prevZ,tpX,tpY,tpZ));
							PacketPipeline.sendToAllAround(entity,256D,new C21EffectEntity(FXType.Entity.SIMPLE_TELEPORT_NOSOUND,prevX,prevY,prevZ,entity.width,entity.height));
						}
						
						break;
					}
				}
			}
			else --timer;
			
			nbt.setByte("HEE_C0_t",timer);
			return hasTeleported;
		}
		
		@Override
		public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.getEntityData().removeTag("HEE_C0_t");
		}
	}),
	
	CONFUSION(1, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			NBTTagCompound nbt = entity.getEntityData();
			byte cooldown = nbt.getByte("HEE_C1_c");
			if (cooldown > 0)--cooldown;
			
			if (entity instanceof EntityCreature){
				EntityCreature creature = (EntityCreature)entity;
				
				Entity target = creature.getEntityToAttack();
				if (target == null)target = creature.getAttackTarget();
				
				if (target == null || target instanceof EntityPlayer){
					EntityLiving newTarget = getMob(creature,16D);
					
					if (newTarget != null){
						cooldown = 60;
						creature.setTarget(newTarget);
						creature.setAttackTarget(newTarget);
					}
				}
			}
			else if (entity instanceof EntityGhast){
				EntityGhast ghast = (EntityGhast)entity;
				
				if (ghast.targetedEntity == null || ghast.targetedEntity instanceof EntityPlayer){
					EntityLiving newTarget = getMob(ghast,64D);
					
					if (newTarget != null){
						cooldown = 60;
						ghast.targetedEntity = newTarget;
					}
				}
			}
			else return true;
			
			nbt.setByte("HEE_C1_c",cooldown);
			return cooldown == 60;
		}
		
		private EntityLiving getMob(EntityLiving me, double dist){
			List<EntityLiving> mobs = new ArrayList<>();
			
			for(EntityLiving entity:EntitySelector.mobs(me.worldObj,me.boundingBox.expand(dist,dist/2,dist))){
				if (entity == me || me.getDistanceToEntity(entity) > dist)continue;
				mobs.add(entity);
			}
			
			return mobs.isEmpty() ? null : mobs.get(me.getRNG().nextInt(mobs.size()));
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.getEntityData().removeTag("HEE_C1_t");
		}
	}),
	
	TRANQUILITY(2, new ICurseHandler(){
		private AttributeModifier noNavigation = new AttributeModifier("HEE NoNavigationCurse",-1D,2);
		
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			if (!(entity instanceof EntityLiving))return true;
			
			EntityAttributes.applyModifier(entity,EntityAttributes.followRange,noNavigation);
			
			if (entity instanceof EntityCreature){
				EntityCreature creature = (EntityCreature)entity;
				if (creature.getEntityToAttack() != null)creature.setTarget(null);
				if (creature.getAttackTarget() != null)creature.setAttackTarget(null);
			}
			else if (entity instanceof EntityGhast)((EntityGhast)entity).targetedEntity = null;
			
			return entity.ticksExisted%20 == 0;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			if (entity instanceof EntityLiving)EntityAttributes.removeModifier(entity,EntityAttributes.followRange,noNavigation);
		}
	}),
	
	SLOWNESS(3, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			PotionEffect eff = entity.getActivePotionEffect(Potion.moveSlowdown);
			
			if (eff == null || eff.getAmplifier() < 1 || eff.getDuration() < 25){
				entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),125,1,true));
				return true;
			}
			else return false;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),125,1,true));
		}
	}),
	
	WEAKNESS(4, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			PotionEffect eff = entity.getActivePotionEffect(Potion.weakness);
			
			if (eff == null || eff.getAmplifier() < 1 || eff.getDuration() < 25){
				entity.addPotionEffect(new PotionEffect(Potion.weakness.getId(),125,1,true));
				return true;
			}
			else return false;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.addPotionEffect(new PotionEffect(Potion.weakness.getId(),125,1,true));
		}
	}),
	
	BLINDNESS(5, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			PotionEffect eff = entity.getActivePotionEffect(Potion.blindness);
			
			if (eff == null || eff.getDuration() < 25){
				entity.addPotionEffect(new PotionEffect(Potion.blindness.getId(),125,0,true));
				return true;
			}
			else return false;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.addPotionEffect(new PotionEffect(Potion.blindness.getId(),125,1,true));
		}
	}),
	
	DEATH(6, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			if (entity.hurtTime == 0){
				entity.attackEntityFrom(DamageSource.magic,1.5F);
				return true;
			}
			else return false;
		}
		
		@Override
		public void end(EntityLivingBase entity, ICurseCaller caller){}
	}),
	
	DECAY(7, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			Random rand = entity.getRNG();
			NBTTagCompound nbt = entity.getEntityData();
			byte timer = nbt.getByte("HEE_C7_t");
			
			if (timer == 0)timer = (byte)(21+rand.nextInt(20));
			else if (--timer == 1){
				if (entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;

					TIntArrayList indexes = new TIntArrayList();
					for(int a = -4; a < player.inventory.mainInventory.length; a++)indexes.add(a);
					
					while(!indexes.isEmpty()){
						int index = indexes.get(rand.nextInt(indexes.size()));
						indexes.remove(index);
						
						ItemStack is = index < 0 ? player.inventory.armorInventory[4+index] : player.inventory.mainInventory[index];
						if (is == null || !is.isItemStackDamageable())continue;
						
						is.setItemDamage(is.getItemDamage()+1+rand.nextInt(3));
						player.renderBrokenItemStack(is);
						
						if (is.getItemDamage() >= is.getMaxDamage()){
							if (index < 0)player.inventory.armorInventory[4+index] = null;
							else player.inventory.mainInventory[index] = null;
						}
						
						break;
					}
				}
				else if (entity instanceof EntityLiving){
					EntityLiving living = (EntityLiving)entity;
					ItemStack[] equipment = living.getLastActiveItems();
					
					TIntArrayList indexes = new TIntArrayList();
					for(int a = 0; a < equipment.length; a++)indexes.add(a);
					
					while(!indexes.isEmpty()){
						int index = indexes.get(rand.nextInt(indexes.size()));
						indexes.remove(index);
						
						if (equipment[index] == null)continue;
						else if (equipment[index].isItemStackDamageable()){
							equipment[index].setItemDamage(equipment[index].getItemDamageForDisplay()+1+rand.nextInt(3));
							living.renderBrokenItemStack(equipment[index]);

							if (equipment[index].getItemDamageForDisplay() >= equipment[index].getMaxDamage()){
								((EntityLiving)entity).setCurrentItemOrArmor(index,null);
							}

							break;
						}
					}
				}
				
				nbt.setByte("HEE_C7_t",--timer);
				return true;
			}
			
			nbt.setByte("HEE_C7_t",timer);
			return false;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.getEntityData().removeTag("HEE_C7_t");
		}
	}),
	
	VAMPIRE(8, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			if (entity.ticksExisted%20 == 0){
				if (entity.worldObj.isDaytime() && entity.getBrightness(1F) > 0.5F && entity.worldObj.canBlockSeeTheSky(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ))){
					entity.hurtResistantTime = 0;
					entity.attackEntityFrom(DamageSource.onFire,1F);
					entity.setFire(8);
				}
				
				return true;
			}
			else return false;
		}
		
		@Override
		public void end(EntityLivingBase entity, ICurseCaller caller){}
	}),
	
	REBOUND(9, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			NBTTagCompound nbt = entity.getEntityData();
			nbt.setLong("HEE_C9_l",entity.worldObj.getTotalWorldTime());
			
			if (nbt.getBoolean("HEE_C9_a")){
				nbt.removeTag("HEE_C9_a");
				return true;
			}
			else return false;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.getEntityData().removeTag("HEE_C9_l");
		}
	}),
	
	LOSS(10, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			Random rand = entity.getRNG();
			NBTTagCompound nbt = entity.getEntityData();
			byte timer = nbt.getByte("HEE_C10_t");
			
			if (timer == 0)timer = (byte)(61+rand.nextInt(60));
			else if (--timer == 1){
				if (entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;

					TIntArrayList indexes = new TIntArrayList();
					for(int a = -4; a < player.inventory.mainInventory.length; a++)indexes.add(a);
					
					while(!indexes.isEmpty()){
						int index = indexes.get(rand.nextInt(indexes.size()));
						indexes.remove(index);
						
						ItemStack is = index < 0 ? player.inventory.armorInventory[4+index] : player.inventory.mainInventory[index];
						if (is == null)continue;
						
						player.func_146097_a(is,false,true);
						
						if (index < 0)player.inventory.armorInventory[4+index] = null;
						else player.inventory.mainInventory[index] = null;
						
						break;
					}
				}
				else if (entity instanceof EntityLiving){
					EntityLiving living = (EntityLiving)entity;
					ItemStack[] equipment = living.getLastActiveItems();
					
					TIntArrayList indexes = new TIntArrayList();
					for(int a = 0; a < equipment.length; a++)indexes.add(a);
					
					while(!indexes.isEmpty()){
						int index = indexes.get(rand.nextInt(indexes.size()));
						indexes.remove(index);
						if (equipment[index] == null || MathUtil.floatEquals(living.equipmentDropChances[index],0F))continue;
						
						ItemStack is = equipment[index];
						
						if (living.equipmentDropChances[index] <= 1F && is.isItemStackDamageable()){
							int maxDmg = Math.max(is.getMaxDamage()-25,1), dmg = is.getMaxDamage()-rand.nextInt(rand.nextInt(maxDmg)+1);
							if (dmg > maxDmg)dmg = maxDmg;
							is.setItemDamage(Math.max(dmg,1));
						}
						
						living.entityDropItem(is,0F);
						living.setCurrentItemOrArmor(index,null);
						break;
					}
				}
				
				nbt.setByte("HEE_C10_t",--timer);
				return true;
			}
			
			nbt.setByte("HEE_C10_t",timer);
			return false;
		}
		
		@Override public void end(EntityLivingBase entity, ICurseCaller caller){
			entity.getEntityData().removeTag("HEE_C10_t");
		}
	});
	
	static{
		TELEPORTATION
		.setRecipe(Items.ender_pearl,Items.ender_pearl,Items.ender_pearl,Items.ender_pearl)
		.setUses(EnumCurseUse.BLOCK,22,34).setUses(EnumCurseUse.ENTITY,7,12).setUses(EnumCurseUse.PLAYER,3,6)
		.setColor1h(290).setColor2h(270);
		
		CONFUSION
		.setRecipe(Items.flint,new ItemStack(Items.dye,1,0),ItemList.instability_orb,Items.blaze_powder)
		.setUses(EnumCurseUse.BLOCK,11,15).setUses(EnumCurseUse.ENTITY,6,10)
		.setColor1h(180).setColor2h(0);
		
		TRANQUILITY
		.setRecipe(Items.flint,new ItemStack(Items.dye,1,0),Items.string,Items.leather)
		.setUses(EnumCurseUse.BLOCK,68>>1,96>>1).setUses(EnumCurseUse.ENTITY,150>>1,210>>1)
		.setColor1h(180).setColor2h(210);
		
		SLOWNESS
		.setRecipe(Items.sugar,Items.sugar,Items.fermented_spider_eye,Items.fermented_spider_eye)
		.setUses(EnumCurseUse.BLOCK,35,42).setUses(EnumCurseUse.ENTITY,16,21).setUses(EnumCurseUse.PLAYER,5,8)
		.setColor1g(50).setColor2h(35);
		
		WEAKNESS
		.setRecipe(Items.blaze_powder,Items.blaze_powder,Items.fermented_spider_eye,Items.fermented_spider_eye)
		.setUses(EnumCurseUse.BLOCK,35,42).setUses(EnumCurseUse.ENTITY,16,21).setUses(EnumCurseUse.PLAYER,5,8)
		.setColor1g(50).setColor2h(0);
		
		BLINDNESS
		.setRecipe(Items.flint,Items.fermented_spider_eye,new ItemStack(Items.dye,1,0),new ItemStack(Items.dye,1,0))
		.setUses(EnumCurseUse.BLOCK,30,39).setUses(EnumCurseUse.ENTITY,12,16).setUses(EnumCurseUse.PLAYER,3,5)
		.setColor1g(50).setColor2h(150);
		
		DEATH
		.setRecipe(Items.blaze_rod,ItemList.ectoplasm,ItemList.silverfish_blood,Items.bone)
		.setUses(EnumCurseUse.BLOCK,28,36).setUses(EnumCurseUse.ENTITY,12,16).setUses(EnumCurseUse.PLAYER,6,9)
		.setColor1g(40).setColor2g(57);
		
		DECAY
		.setRecipe(ItemList.stardust,ItemList.stardust,Items.iron_ingot,ItemList.instability_orb)
		.setUses(EnumCurseUse.BLOCK,18,25).setUses(EnumCurseUse.ENTITY,14,19).setUses(EnumCurseUse.PLAYER,9,14)
		.setColor1h(46).setColor2g(52);
		
		VAMPIRE
		.setRecipe(Items.glowstone_dust,Items.quartz,ItemList.igneous_rock,ItemList.silverfish_blood)
		.setUses(EnumCurseUse.BLOCK,15,22).setUses(EnumCurseUse.ENTITY,8,13).setUses(EnumCurseUse.PLAYER,6,9)
		.setColor1h(55).setColor2h(9);
		
		REBOUND
		.setRecipe(Items.ender_eye,Items.gold_nugget,Items.flint,Items.gold_nugget)
		.setUses(EnumCurseUse.BLOCK,20,26).setUses(EnumCurseUse.ENTITY,15,22).setUses(EnumCurseUse.PLAYER,12,16)
		.setColor1h(0).setColor2h(200);
		
		LOSS
		.setRecipe(ItemList.instability_orb,Items.book,Items.redstone,Items.emerald)
		.setUses(EnumCurseUse.BLOCK,14,19).setUses(EnumCurseUse.ENTITY,10,12).setUses(EnumCurseUse.PLAYER,8,11)
		.setColor1h(300).setColor2g(50);
	}
	
	public static CurseType getFromDamage(int damage){
		damage = damage&0b11111111;
		
		for(CurseType type:values()){
			if (damage == type.damage)return type;
		}
		
		return null;
	}
	
	public static boolean isEternal(int damage){
		return (damage&0b100000000) != 0;
	}
	
	public final byte damage;
	public final ICurseHandler handler;
	private ItemStack[] items = new ItemStack[4];
	private byte usesBlockMin, usesBlockMax, usesEntityMin, usesEntityMax, usesPlayerMin, usesPlayerMax;
	private int color1, color2;
	
	CurseType(int damage, ICurseHandler handler){
		this.damage = (byte)damage;
		this.handler = handler;
	}
	
	private CurseType setRecipe(Object o1, Object o2, Object o3, Object o4){
		Object[] array = new Object[]{ o1, o2, o3, o4 };
		
		for(int a = 0; a < 4; a++){
			if (array[a] instanceof Block)items[a] = new ItemStack((Block)array[a]);
			else if (array[a] instanceof Item)items[a] = new ItemStack((Item)array[a]);
			else if (array[a] instanceof ItemStack)items[a] = (ItemStack)array[a];
			else throw new IllegalArgumentException("Invalid recipe object, accepting only Block, Item and ItemStack, got "+array[a].getClass());
		}
		
		return this;
	}
	
	private CurseType setColor1h(int hue){
		return setColor1(hue,75,90);
	}
	
	private CurseType setColor1g(int value){
		return setColor1(0,0,value);
	}
	
	private CurseType setColor1(int hue, int saturation, int value){
		float[] rgb = ColorUtil.hsvToRgb(hue/360F,saturation/100F,value/100F);
		this.color1 = (MathUtil.floor(rgb[0]*255)<<16)|(MathUtil.floor(rgb[1]*255)<<8)|MathUtil.floor(rgb[2]*255);
		return this;
	}
	
	private CurseType setColor2h(int hue){
		return setColor2(hue,75,90);
	}
	
	private CurseType setColor2g(int value){
		return setColor2(0,0,value);
	}
	
	private CurseType setColor2(int hue, int saturation, int value){
		float[] rgb = ColorUtil.hsvToRgb(hue/360F,saturation/100F,value/100F);
		this.color2 = (MathUtil.floor(rgb[0]*255)<<16)|(MathUtil.floor(rgb[1]*255)<<8)|MathUtil.floor(rgb[2]*255);
		return this;
	}
	
	private CurseType setUses(EnumCurseUse useType, int minUses, int maxUses){
		switch(useType){
			case BLOCK:
				usesBlockMin = (byte)minUses;
				usesBlockMax = (byte)maxUses;
				break;
				
			case ENTITY:
				usesEntityMin = (byte)minUses;
				usesEntityMax = (byte)maxUses;
				break;
				
			case PLAYER:
				usesPlayerMin = (byte)minUses;
				usesPlayerMax = (byte)maxUses;
				break;
				
			default:
		}
		
		return this;
	}
	
	public int getColor(int pass){
		return pass == 0 ? color1 : pass == 1 ? color2 : 16777215;
	}
	
	public ItemStack getRecipeItem(int index){
		return items[index];
	}
	
	public int getUses(EnumCurseUse useType, Random rand){
		switch(useType){
			case BLOCK: return usesBlockMin+rand.nextInt(usesBlockMax-usesBlockMin+1);
			case ENTITY: return usesEntityMin+rand.nextInt(usesEntityMax-usesEntityMin+1);
			case PLAYER: return usesPlayerMin+rand.nextInt(usesPlayerMax-usesPlayerMin+1);
			default: return 0;
		}
	}
	
	public enum EnumCurseUse{
		BLOCK, ENTITY, PLAYER
	}
}
