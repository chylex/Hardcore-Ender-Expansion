package chylex.hee.entity.mob;
import java.util.IdentityHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.causatum.Causatum;
import chylex.hee.mechanics.causatum.Causatum.Actions;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.mechanics.causatum.CausatumEventHandler;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventTypes;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.util.EntitySelector;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootMobInfo;

public class EntityMobEnderman extends EntityEnderman implements IIgnoreEnderGoo{
	private static final PercentageLootTable drops = new PercentageLootTable();
	
	static{
		drops.addLoot(Items.ender_pearl).<LootMobInfo>setChances(obj -> {
			switch(obj.looting){
				case 0: return new float[]{ 0.60F };
				case 1: return new float[]{ 0.70F };
				case 2: return new float[]{ 0.65F, 0.10F };
				default: return new float[]{ 0.72F, 0.16F };
			}
		});
		
		drops.addLoot(ItemList.ethereum).<LootMobInfo>setChances(obj -> {
			switch(obj.looting){
				case 0: return new float[]{ 0.12F };
				case 1: return new float[]{ 0.16F };
				case 2: return new float[]{ 0.22F };
				default: return new float[]{ 0.25F };
			}
		});
		
		drops.addLoot(BlockList.enderman_head).setChances(obj -> {
			return new float[]{ 0.03F };
		});
		
		ReflectionPublicizer.f__carriable__EntityEnderman(new IdentityHashMap<Block,Boolean>(){
			@Override
			public Boolean get(Object key){
				return Boolean.FALSE;
			}
		});
	}
	
	public EntityMobEnderman(World world){
		super(world);
		AIUtil.clearEntityTasks(this);
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(2,new EntityAIAttackOnCollide(this,1D,false));
		tasks.addTask(3,new EntityAIWander(this,1D));
		tasks.addTask(4,new EntityAIWatchClosest(this,EntityPlayer.class,8F));
		tasks.addTask(4,new EntityAILookIdle(this));
		
		experienceValue = 10;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(30,Short.valueOf((short)0));
		dataWatcher.addObject(31,Byte.valueOf((byte)0));
	}
	
	@Override
	protected boolean isAIEnabled(){
		return true;
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		// TODO
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		LootMobInfo lootInfo = new LootMobInfo(this,recentlyHit,looting);
		
		EntityLivingBase attacker = func_94060_bK();
		
		if (attacker != null && attacker instanceof EntityPlayerMP && Causatum.progress((EntityPlayer)attacker,Progress.ENDERMAN_KILLED,Actions.STAGE_ADVANCE_TO_ENDERMAN_KILLED)){
			for(EntityPlayer nearbyPlayer:EntitySelector.players(worldObj,boundingBox.expand(12D,4D,12D))){
				Causatum.progress(nearbyPlayer,Progress.ENDERMAN_KILLED);
			}
			
			entityDropItem(new ItemStack(Items.ender_pearl),0F);
			CausatumEventHandler.tryStartEvent((EntityPlayerMP)attacker,EventTypes.STAGE_ADVANCE_TO_ENDERMAN_KILLED);
			return;
		}
		
		for(ItemStack drop:drops.generateLoot(lootInfo,rand))entityDropItem(drop,0F);
		
		Block carrying = func_146080_bZ(); // OBFUSCATED getCarryingBlock
		if (carrying != Blocks.air)entityDropItem(new ItemStack(carrying,1,getCarryingData()),0F);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (super.attackEntityFrom(source,amount)){
			// TODO if (dimension == 0)CausatumUtils.increase(source,CausatumMeters.OVERWORLD_ENDERMAN_DAMAGE,amount*0.25F);
			// TODO else if (dimension == 1)CausatumUtils.increase(source,CausatumMeters.END_MOB_DAMAGE,amount*0.5F);
			
			return true;
		}
		else return false;
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
		return super.getCanSpawnHere() && (worldObj.provider.dimensionId != 0 || worldObj.skylightSubtracted <= 5);
	}
	
	/**
	 * Replacement method for the ones that set the carried block and metadata.
	 */
	public void setCarrying(BlockInfo info){
		dataWatcher.updateObject(30,Short.valueOf((short)Block.getIdFromBlock(info.block)));
		dataWatcher.updateObject(31,info.meta);
	}
	
	/**
	 * Replacement method for the ones that return the carried block and metadata.
	 */
	public BlockInfo getCarrying(){
		return new BlockInfo(Block.getBlockById(dataWatcher.getWatchableObjectShort(30)),dataWatcher.getWatchableObjectByte(31));
	}
	
	/**
	 * Replacement method for isWet() to avoid automatic damage in EntityEnderman.onLivingUpdate() and replace it with a custom system.
	 */
	public boolean isEndermanWet(){
		return super.isWet();
	}
	
	// Disabled methods
	
	@Override
	public boolean isWet(){ return false; }
	
	@Override
	public void setScreaming(boolean isScreaming){}
	
	@Override
	public boolean isScreaming(){ return false; }

	@Override
	public void func_146081_a(Block carriedBlock){}

	@Override
	public Block func_146080_bZ(){ return Blocks.air; }
	
	@Override
	public void setCarryingData(int data){}
	
	@Override
	public int getCarryingData(){ return 0; }
	
	@Override
	protected boolean teleportRandomly(){ return false; }
	
	@Override
	protected boolean teleportTo(double x, double y, double z){ return false; }
	
	@Override
	protected boolean teleportToEntity(Entity entity){ return false; }
}
