package chylex.hee.entity.mob;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityMobHauntedMiner extends EntityFlying implements IMob{
	private AxisAlignedBB bottomBB = AxisAlignedBB.getBoundingBox(0D,0D,0D,0D,0D,0D);
	private EntityLivingBase target;
	private double targetX, targetY, targetZ;
	private byte wanderResetTimer = 0;
	
	public EntityMobHauntedMiner(World world){
		super(world);
		setSize(2.2F,1.7F);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(45D);
	}
	
	@Override
	protected void updateEntityActionState(){
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL)setDead();
		despawnEntity();
		
		if (target == null){
			if (--wanderResetTimer < 0 || rand.nextInt(50) == 0){
				wanderResetTimer = 0;
				
				for(int attempt = 0, xx, yy, zz; attempt < 32; attempt++){
					xx = (int)Math.floor(posX)+rand.nextInt(12)-rand.nextInt(12);
					zz = (int)Math.floor(posZ)+rand.nextInt(12)-rand.nextInt(12);
					yy = (int)Math.floor(posY);
					
					if (worldObj.isAirBlock(xx,yy,zz)){
						while(worldObj.isAirBlock(xx,--yy,zz) && Math.abs(posY-yy) < 10);
						if (Math.abs(posY-yy) >= 10)continue;
					}
					else{
						while(!worldObj.isAirBlock(xx,++yy,zz) && Math.abs(posY-yy) < 10);
						if (Math.abs(posY-yy) >= 10)continue;
					}
					
					targetX = xx+rand.nextDouble();
					targetY = yy+rand.nextDouble()*0.2D+3D;
					targetZ = zz+rand.nextDouble();
					wanderResetTimer += 60;
					break;
				}
				
				wanderResetTimer += rand.nextInt(30)+20;
			}
		}
		else{
			double ang = Math.atan2(targetZ-posZ,targetX-posX);
			targetX = target.posX+Math.cos(ang)*6D;
			targetZ = target.posZ+Math.sin(ang)*6D;
			
			targetY = target.posY+3D;
		}
		
		double[] xz = DragonUtil.getNormalizedVector(posX-targetX,posZ-targetZ);
		motionX = xz[0]*0.1D;
		motionZ = xz[1]*0.1D;
		if (Math.abs(targetY-posY) > 1D)motionY = (targetY-posY)*0.02D;
		
		renderYawOffset = rotationYaw = -MathUtil.toDeg((float)Math.atan2(motionX,motionZ));
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			for(int a = 0; a < 2; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+(rand.nextDouble()-0.5D)*0.2D,posY,posZ+(rand.nextDouble()-0.5D)*0.2D,0D,-0.05D,0D,8);
			
		}
		else{
			List<Entity> nearEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this,bottomBB.setBounds(posX-1.65D,posY-3,posZ-1.65D,posX+1.65D,posY,posZ+1.65D));
			
			for(Entity entity:nearEntities){
				if (entity instanceof EntityMobHauntedMiner)continue;
				entity.attackEntityFrom(DamageSource.causeMobDamage(this),2F);
				entity.setFire(5);
				entity.hurtResistantTime -= 2;
			}
		}
	}
	
	@Override
	public void setRevengeTarget(EntityLivingBase newTarget){
		if (target == null || newTarget.getDistanceSqToEntity(this) < target.getDistanceSqToEntity(this))target = newTarget;
    }
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){}
	
	@Override
	public void dropFewItems(boolean recentlyHit, int looting){
		for(int a = 0; a < rand.nextInt(2+rand.nextInt(2)+looting); a++)dropItem(ItemList.fire_shard,1);
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.hauntedMiner.name");
	}
}
