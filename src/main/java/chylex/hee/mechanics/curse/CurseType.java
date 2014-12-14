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
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import chylex.hee.system.util.MathUtil;

public enum CurseType{
	TELEPORTATION(0, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
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
			
			for(EntityLiving entity:(List<EntityLiving>)me.worldObj.getEntitiesWithinAABB(EntityLiving.class,me.boundingBox.expand(dist,dist/2,dist))){
				if (entity == me || me.getDistanceToEntity(entity) > dist)continue;
				mobs.add(entity);
			}
			
			return mobs.isEmpty() ? null : mobs.get(me.getRNG().nextInt(mobs.size()));
		}
	}),
	
	TRANQUILITY(2, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			if (entity instanceof EntityCreature){
				EntityCreature creature = (EntityCreature)entity;
				if (creature.getEntityToAttack() != null)creature.setTarget(null);
				if (creature.getAttackTarget() != null)creature.setAttackTarget(null);
			}
			else if (entity instanceof EntityGhast)((EntityGhast)entity).targetedEntity = null;
			
			return true;
		}
	}),
	
	SLOWNESS(3, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			PotionEffect eff = entity.getActivePotionEffect(Potion.moveSlowdown);
			
			if (eff == null || eff.getAmplifier() < 1){
				entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),100,1,true));
				return true;
			}
			else return false;
		}
	}),
	
	WEAKNESS(4, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			PotionEffect eff = entity.getActivePotionEffect(Potion.weakness);
			
			if (eff == null || eff.getAmplifier() < 1){
				entity.addPotionEffect(new PotionEffect(Potion.weakness.getId(),100,1,true));
				return true;
			}
			else return false;
		}
	}),
	
	BLINDNESS(5, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			if (entity.getActivePotionEffect(Potion.blindness) == null){
				entity.addPotionEffect(new PotionEffect(Potion.blindness.getId(),100,0,true));
				return true;
			}
			else return false;
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
					for(int a = -4; a < player.inventory.getSizeInventory(); a++)indexes.add(a);
					
					while(!indexes.isEmpty()){
						int index = indexes.get(rand.nextInt(indexes.size()));
						indexes.remove(index);
						
						ItemStack is = index < 0 ? player.inventory.armorInventory[4+index] : player.inventory.mainInventory[index];
						if (is == null || !is.isItemStackDamageable())continue;
						
						is.setItemDamage(is.getItemDamage()+1+rand.nextInt(2));
						
						if (is.getItemDamage() >= is.getMaxDamage()){
							player.renderBrokenItemStack(is);
							
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
							equipment[index].setItemDamage(equipment[index].getItemDamageForDisplay()+1+rand.nextInt(2));

							if (equipment[index].getItemDamageForDisplay() >= equipment[index].getMaxDamage()){
								entity.renderBrokenItemStack(equipment[index]);
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
	}),
	
	REBOUND(9, new ICurseHandler(){
		@Override public boolean tickEntity(EntityLivingBase entity, ICurseCaller caller){
			return false;
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
					for(int a = -4; a < player.inventory.getSizeInventory(); a++)indexes.add(a);
					
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
	});
	
	static{
		TELEPORTATION
		.setRecipe(Items.ender_pearl,Items.ender_pearl,Items.ender_pearl,Items.ender_pearl)
		.setUses(EnumCurseUse.BLOCK,22,34).setUses(EnumCurseUse.ENTITY,7,12).setUses(EnumCurseUse.PLAYER,3,6);
		
		CONFUSION
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,11,15).setUses(EnumCurseUse.ENTITY,6,10);
		
		TRANQUILITY
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,35*20,48*20).setUses(EnumCurseUse.ENTITY,150*20,210*20);
		
		SLOWNESS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,35,42).setUses(EnumCurseUse.ENTITY,16,21).setUses(EnumCurseUse.PLAYER,5,8);
		
		WEAKNESS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,35,42).setUses(EnumCurseUse.ENTITY,16,21).setUses(EnumCurseUse.PLAYER,5,8);
		
		BLINDNESS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,30,39).setUses(EnumCurseUse.ENTITY,12,16).setUses(EnumCurseUse.PLAYER,3,5);
		
		DEATH
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,28,36).setUses(EnumCurseUse.ENTITY,12,16).setUses(EnumCurseUse.PLAYER,6,9);
		
		DECAY
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,18,25).setUses(EnumCurseUse.ENTITY,14,19).setUses(EnumCurseUse.PLAYER,9,14);
		
		VAMPIRE
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,15,22).setUses(EnumCurseUse.ENTITY,8,13).setUses(EnumCurseUse.PLAYER,6,9);
		
		REBOUND
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,20,26).setUses(EnumCurseUse.ENTITY,15,22).setUses(EnumCurseUse.PLAYER,12,16);
		
		LOSS
		.setRecipe(Blocks.air,Blocks.air,Blocks.air,Blocks.air)
		.setUses(EnumCurseUse.BLOCK,14,19).setUses(EnumCurseUse.ENTITY,10,12).setUses(EnumCurseUse.PLAYER,8,11);
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
