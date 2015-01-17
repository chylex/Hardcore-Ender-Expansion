package chylex.hee.entity.mob;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.ai.EntityAIRangedEnergyAttack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.util.MathUtil;

public class EntityMobEndermage extends EntityMob implements IIgnoreEnderGoo, IRangedAttackMob{
	private short lastAttacked;
	
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
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(70D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.24D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(25D);
	}
	
	@Override
	public boolean isAIEnabled(){
		return true;
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (lastAttacked > 0)--lastAttacked;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (super.attackEntityFrom(source,amount)){
			if (lastAttacked == 0 && source.getSourceOfDamage() != null){
				lastAttacked = (short)(100+rand.nextInt(140));
				
				Entity sourceEntity = source.getSourceOfDamage();
				double dist = MathUtil.distance(sourceEntity.posX-posX,sourceEntity.posZ-posZ);
				
				for(int attempt = 0, xx, yy, zz; attempt < 15; attempt++){
					xx = MathUtil.floor(posX)+rand.nextInt(31)-15;
					zz = MathUtil.floor(posZ)+rand.nextInt(31)-15;
					
					if (MathUtil.distance(xx+0.5D-sourceEntity.posX,zz+0.5D-sourceEntity.posZ) > dist*4D){
						boolean found = false;
						yy = MathUtil.floor(posY)+7;
						
						for(int yAttempt = 0; yAttempt < 14; yAttempt++){
							if (!worldObj.isAirBlock(xx,yy-1,zz) && worldObj.isAirBlock(xx,yy,zz) && worldObj.isAirBlock(xx,yy+1,zz)){
								PacketPipeline.sendToAllAround(this,64D,new C22EffectLine(FXType.Line.ENDERMAN_TELEPORT,posX,posY,posZ,xx+0.5D,yy,zz+0.5D));
								setPosition(xx+0.5D,yy,zz+0.5D);
								found = true;
								break;
							}
							else --yy;
						}
						
						if (found)break;
					}
				}
			}
			
			return true;
		}
		else return false;
	}
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase entity, float amount){}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		if (recentlyHit && (rand.nextInt(3) == 0 || looting > 0) && rand.nextBoolean())dropItem(ItemList.auricion,1);
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
		return StatCollector.translateToLocal(Baconizer.mobName("entity.endermage.name"));
	}
}
