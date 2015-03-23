package chylex.hee.entity.mob;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.system.util.MathUtil;

public class EntityMobSanctuaryOverseer extends EntityFlying{
	private short provocation;
	
	public EntityMobSanctuaryOverseer(World world){
		super(world);
		setSize(0.85F,0.85F);
		isImmuneToFire = true;
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(18D+rand.nextDouble()*29D);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (ticksExisted%3 == 0){
			for(EntityPlayer player:(List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(8D,8D,8D))){
				if (!player.isDead && canEntityBeSeen(player)){
					provocation += MathUtil.ceil(player.motionX*player.motionX);
					provocation += MathUtil.ceil(player.motionY*player.motionY);
					provocation += MathUtil.ceil(player.motionZ*player.motionZ);
					System.out.println("movement prov "+provocation);
				}
			}
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.getSourceOfDamage() instanceof EntityPlayer && source.getDamageType().equals("player")){ // melee only
			amount = Math.max(Math.min(amount*0.5F,6F),3F);
			
			if (super.attackEntityFrom(source,amount)){
				provocation += 100; // TODO check values
				System.out.println("added prov after attack: "+provocation);
				
				for(EntityMobSanctuaryOverseer overseer:(List<EntityMobSanctuaryOverseer>)worldObj.getEntitiesWithinAABB(EntityMobSanctuaryOverseer.class,boundingBox.expand(8D,8D,8D))){
					if (overseer == this || !canEntityBeSeen(overseer))continue;
					overseer.provocation += 25;
					System.out.println("added prov to other entity: "+overseer.provocation);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){
		Vec3 vec = Vec3.createVectorHelper(rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D).normalize();
		motionX = vec.xCoord*0.2D;
		motionY = vec.yCoord*0.2D;
		motionZ = vec.zCoord*0.2D;
		isAirBorne = true;
	}
	
	@Override
	protected void onDeathUpdate(){
		if (++deathTime >= 2){
			// TODO particles
			setDead();
		}
	}
}
