package chylex.hee.entity.mob;
import java.util.IdentityHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
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
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootMobInfo;

public class EntityMobEnderman extends EntityAbstractEndermanCustom{
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
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		EntityAttributes.setValue(this,EntityAttributes.maxHealth,40D);
		EntityAttributes.setValue(this,EntityAttributes.movementSpeed,0.3D);
		EntityAttributes.setValue(this,EntityAttributes.attackDamage,5D);
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
		
		dropCarrying();
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
		return Baconizer.soundNormal("mob.endermen.idle");
	}
	
	@Override
	protected String getHurtSound(){
		return Baconizer.soundNormal("mob.endermen.hit");
	}
	
	@Override
	protected String getDeathSound(){
		return Baconizer.soundDeath("mob.endermen.death");
	}
	
	@Override
	public String getCommandSenderName(){
		return ModCommonProxy.hardcoreEnderbacon ? StatCollector.translateToLocal("entity.enderman.bacon.name") : super.getCommandSenderName();
	}
	
	@Override
	public boolean getCanSpawnHere(){
		return super.getCanSpawnHere() && (worldObj.provider.dimensionId != 0 || worldObj.skylightSubtracted <= 5);
	}
}
