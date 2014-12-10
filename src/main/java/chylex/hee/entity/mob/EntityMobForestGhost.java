package chylex.hee.entity.mob;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;

public class EntityMobForestGhost extends EntityFlying implements IMob{
	private EntityPlayer target;
	private byte lifeLeft;
	
	public EntityMobForestGhost(World world){
		super(world);
		setSize(0.5F,0.5F);
		noClip = true;
		lifeLeft = 120;
	}
	
	public EntityMobForestGhost(World world, EntityPlayer target){
		this(world);
		this.target = target;
	}

	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (--lifeLeft<-9 || target == null || target.isDead || DragonUtil.canEntitySeePoint(target,posX,posY,posZ,1D,true)){
			if (target != null){
				PotionEffect blindness = target.getActivePotionEffect(Potion.blindness);
				if (blindness != null && blindness.getDuration() <= 80)target.removePotionEffect(Potion.blindness.id);
			}
			
			setDead();
		}
		else{
			posY = target.posY;
			double xDiff = target.posX-posX;
			double zDiff = target.posZ-posZ;
			double dist = Math.sqrt(xDiff*xDiff+zDiff*zDiff);
			
			if (dist >= 1.9D){
				rotationYaw = (float)(Math.atan2(zDiff,xDiff)*180D/Math.PI);
				setPositionAndUpdate(posX+Math.cos(Math.toRadians(rotationYaw))*0.35D,posY,posZ+Math.sin(Math.toRadians(rotationYaw))*0.35D);
			}
			else if (lifeLeft < 80)target.attackEntityFrom(new DamageSourceMobUnscaled(this),DamageSourceMobUnscaled.getDamage(ModCommonProxy.opMobs ? 9F : 5F,worldObj.difficultySetting));
			
			if (dist < 2.1D && lifeLeft < 80)target.addPotionEffect(new PotionEffect(Potion.blindness.id,80,0,true));
		}
	}
	
	@Override
	public void moveEntityWithHeading(float strafe, float forward){}
	
	@Override
	public boolean isEntityInvulnerable(){
		return true;
	}
	
	@Override
	public boolean canBePushed(){
		return false;
	}
	
	@Override
	public void despawnEntity(){}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.forestGhost.name");
	}
}
