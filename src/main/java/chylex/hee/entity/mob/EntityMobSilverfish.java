package chylex.hee.entity.mob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.mob.ai.EntityAIHideInBlock;
import chylex.hee.entity.mob.ai.EntityAISummonFromBlock;
import chylex.hee.entity.mob.ai.EntityAIWanderConstantly;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.util.DragonUtil;

public class EntityMobSilverfish extends EntitySilverfish{
	private EntityAISummonFromBlock canSummonSilverfish;
	private EntityAIHideInBlock canHideInBlocks;
	
	public EntityMobSilverfish(World world){ // TODO swarm AI
		super(world);
		setSize(0.35F,0.6F);
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(3,new EntityAIAttackOnCollide(this,EntityPlayer.class,1D,false));
		tasks.addTask(4,new EntityAIWanderConstantly(this,1D));
		setCanSummonSilverfish(true);
		setCanHideInBlocks(true);
		
		targetTasks.addTask(1,new EntityAIHurtByTarget(this,false));
		targetTasks.addTask(2,new EntityAINearestAttackableTarget(this,EntityPlayer.class,10,true,true));
	}
	
	public void setCanSummonSilverfish(boolean allow){
		if (allow && canSummonSilverfish == null){
			canSummonSilverfish = new EntityAISummonFromBlock(this,Blocks.monster_egg,world -> new EntityMobSilverfish(world));
			tasks.addTask(2,canSummonSilverfish);
		}
		else if (!allow && canSummonSilverfish != null){
			tasks.removeTask(canSummonSilverfish);
			canSummonSilverfish = null;
		}
	}
	
	public void setCanHideInBlocks(boolean allow){
		if (allow && canHideInBlocks == null){
			canHideInBlocks = new EntityAIHideInBlock(this,new Block[]{ Blocks.cobblestone, Blocks.stone, Blocks.stonebrick },target -> new BlockInfo(Blocks.monster_egg,BlockSilverfish.func_150195_a(target.block,target.meta)));
			tasks.addTask(5,canHideInBlocks);
		}
		else if (!allow && canHideInBlocks != null){
			tasks.removeTask(canHideInBlocks);
			canHideInBlocks = null;
		}
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2D);
	}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)rotationYaw = DragonUtil.rotateSmoothly(rotationYaw,rotationYawHead,30F);
		super.onUpdate();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable())return false;
		
		if (canSummonSilverfish != null && (source.getEntity() != null || source == DamageSource.magic))canSummonSilverfish.setSummonTimer(20);
		return super.attackEntityFrom(source,amount);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity target){
		return Damage.hostileMob(this).addModifiers(IDamageModifier.rapidDamage(5),IDamageModifier.overrideKnockback(1.5F)).deal(target);
	}
	
	@Override
	protected boolean isAIEnabled(){
		return true;
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal("entity.Silverfish.name");
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("canSummonSilverfish",canSummonSilverfish != null);
		nbt.setBoolean("canHideInBlocks",canHideInBlocks != null);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		setCanSummonSilverfish(nbt.getBoolean("canSummonSilverfish"));
		setCanHideInBlocks(nbt.getBoolean("canHideInBlocks"));
	}
}
