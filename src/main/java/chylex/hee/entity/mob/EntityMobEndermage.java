package chylex.hee.entity.mob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
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
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.mob.ai.EntityAIRangedEnergyAttack;
import chylex.hee.entity.mob.teleport.ITeleportPredicate;
import chylex.hee.entity.mob.teleport.MobTeleporter;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportXZ;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportY;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.util.MathUtil;

public class EntityMobEndermage extends EntityMob implements IIgnoreEnderGoo, IRangedAttackMob{
	private static final MobTeleporter<EntityMobEndermage> teleportAround = new MobTeleporter<>();
	
	static{
		teleportAround.setLocationSelector(
			ITeleportXZ.inSquare(15D), 
			ITeleportY.findSolidBottom((entity, startPos, rand) -> MathUtil.floor(entity.posY)+7, 14)
		);
		
		teleportAround.setAttempts(15);
		teleportAround.addLocationPredicate(ITeleportPredicate.noCollision);
		teleportAround.addLocationPredicate((entity, startPos, rand) -> MathUtil.distance(entity.posX-entity.lastSource.posX, entity.posZ-entity.lastSource.posZ) > entity.lastSourceDist*4D);
		teleportAround.addLocationPredicate((entity, startPos, rand) -> entity.canEntityBeSeen(entity.lastSource));
		// TODO PacketPipeline.sendToAllAround(this, 64D, new C22EffectLine(FXType.Line.ENDERMAN_TELEPORT, posX, posY, posZ, tmpPos.x+0.5D, tmpPos.y, tmpPos.z+0.5D));
	}
	
	private short lastAttacked;
	private Entity lastSource;
	private double lastSourceDist;
	
	public EntityMobEndermage(World world){
		super(world);
		setSize(0.6F, 2.7F);
		stepHeight = 1F;
		
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(4, new EntityAIRangedEnergyAttack(this, 1D));
		tasks.addTask(5, new EntityAIWander(this, 0.7D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 4F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}
	
	public EntityMobEndermage(World world, double x, double y, double z){
		this(world);
		setPosition(x, y, z);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		EntityAttributes.setValue(this, EntityAttributes.maxHealth, ModCommonProxy.opMobs ? 80D : 65D);
		EntityAttributes.setValue(this, EntityAttributes.movementSpeed, 0.24D);
		EntityAttributes.setValue(this, EntityAttributes.attackDamage, 12D);
		EntityAttributes.setValue(this, EntityAttributes.followRange, 25D);
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
		if (super.attackEntityFrom(source, amount)){
			if ((lastAttacked == 0 || (rand.nextInt(3) != 0 && getHealth()-amount*2D <= 0D)) && source.getEntity() != null){
				lastAttacked = (short)(130+rand.nextInt(160));
				lastSource = source.getEntity();
				lastSourceDist = MathUtil.distance(lastSource.posX-posX, lastSource.posZ-posZ);
				
				boolean result = teleportAround.teleport(this, rand);
				lastSource = null;
				return result;
			}
			
			return true;
		}
		else return false;
	}
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase entity, float amount){}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		if (recentlyHit && (rand.nextInt(3) == 0 || looting > 0) && rand.nextBoolean())dropItem(ItemList.auricion, 1);
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
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal(Baconizer.mobName("entity.endermage.name"));
	}
}
