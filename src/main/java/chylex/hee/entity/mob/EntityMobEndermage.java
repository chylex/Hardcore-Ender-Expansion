package chylex.hee.entity.mob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.entity.mob.ai.EntityAIRangedEnergyAttack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.misc.Baconizer;

public class EntityMobEndermage extends EntityMob implements IIgnoreEnderGoo, IRangedAttackMob{
	public EntityMobEndermage(World world){
		super(world);
		setSize(0.6F,2.7F);
		stepHeight = 1F;
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(4,new EntityAIRangedEnergyAttack(this,1D));
		tasks.addTask(5,new EntityAIWander(this,0.7D));
		tasks.addTask(6,new EntityAIWatchClosest(this,EntityPlayer.class,4F));
		tasks.addTask(6,new EntityAILookIdle(this));
		targetTasks.addTask(1,new EntityAIHurtByTarget(this,false));
	}
	
	public EntityMobEndermage(World world, double x, double y, double z){
		this(world);
		setPosition(x,y,z);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.24D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(25D);
	}
	
	@Override
	public boolean isAIEnabled(){
		return true;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase entity, float amount){}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal(Baconizer.mobName("entity.endermage.name"));
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		if (recentlyHit && (rand.nextInt(3) == 0 || looting > 0) && rand.nextBoolean())dropItem(ItemList.auricion,1);
	}
}
