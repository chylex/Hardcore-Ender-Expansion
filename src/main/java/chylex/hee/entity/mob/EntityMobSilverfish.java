package chylex.hee.entity.mob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.mob.ai.EntityAIHideInBlock;
import chylex.hee.entity.mob.ai.EntityAIWanderConstantly;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.util.DragonUtil;

public class EntityMobSilverfish extends EntityMob{
	private EntityAIHideInBlock canHideInBlocks;
	
	public EntityMobSilverfish(World world){ // TODO swarm AI
		super(world);
		setSize(0.35F,0.6F);
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(2,new EntityAIAttackOnCollide(this,EntityPlayer.class,1D,false));
		tasks.addTask(3,new EntityAIWanderConstantly(this,1D));
		setCanHideInBlocks(true);
		// TODO vanilla AI
		
		targetTasks.addTask(1,new EntityAIHurtByTarget(this,false));
		targetTasks.addTask(2,new EntityAINearestAttackableTarget(this,EntityPlayer.class,10,true,true));
	}
	
	public void setCanHideInBlocks(boolean allow){
		if (allow && canHideInBlocks == null){
			tasks.addTask(5,canHideInBlocks = new EntityAIHideInBlock(this,new Block[]{ Blocks.cobblestone, Blocks.stone, Blocks.stonebrick },target -> {
				return new BlockInfo(Blocks.monster_egg,BlockSilverfish.func_150195_a(target.block,target.meta));
			}));
		}
		else if (!allow && canHideInBlocks != null)tasks.removeTask(canHideInBlocks);
	}
	
	@Override
	protected void applyEntityAttributes(){ // TODO update from GDD
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1D);
	}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)renderYawOffset = rotationYaw = DragonUtil.rotateSmoothly(rotationYaw,rotationYawHead,30F);
		super.onUpdate();
	}
	
	@Override
	protected boolean isAIEnabled(){
		return true;
	}
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute(){
		return EnumCreatureAttribute.ARTHROPOD;
	}
	
	@Override
	protected boolean canTriggerWalking(){
        return false;
    }
	
	@Override
	protected String getLivingSound(){
		return "mob.silverfish.say";
	}
	
	@Override
	protected String getHurtSound(){
		return "mob.silverfish.hit";
	}
	
	@Override
	protected String getDeathSound(){
		return "mob.silverfish.kill";
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal("entity.Silverfish.name");
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("canHideInBlocks",canHideInBlocks != null);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		setCanHideInBlocks(nbt.getBoolean("canHideInBlocks"));
	}
}
