package chylex.hee.entity.mob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.system.util.MathUtil;

public class EntityMobSanctuaryOverseer extends EntityFlying{
	private Map<UUID,double[]> prevPlayerLocs = new HashMap<>();
	private short provocation, maxProvocation;
	
	public EntityMobSanctuaryOverseer(World world){
		super(world);
		setSize(0.85F,0.85F);
		isImmuneToFire = true;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16,Short.valueOf((short)0));
		dataWatcher.addObject(17,Short.valueOf((short)0));
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(18D+rand.nextDouble()*29D);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			provocation = dataWatcher.getWatchableObjectShort(16);
			if (maxProvocation == 0)maxProvocation = dataWatcher.getWatchableObjectShort(17);
			
			// TODO scream the f-ing ears out
			return;
		}
		
		if (ticksExisted == 1){
			maxProvocation = (short)(1000+rand.nextInt(1500+rand.nextInt(800)));
			dataWatcher.updateObject(17,maxProvocation);
		}
		
		if (!worldObj.isRemote && ticksExisted%4 == 0){
			boolean updated = false;
			
			for(EntityPlayer player:(List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(8D,8D,8D))){
				if (!player.isDead && canEntityBeSeen(player)){
					double[] prevLocs = prevPlayerLocs.get(player.getUniqueID());
					
					if (prevLocs != null){
						provocation += MathUtil.floor(MathUtil.square(prevLocs[0]-player.posX)*10D);
						provocation += MathUtil.floor(MathUtil.square(prevLocs[1]-player.posY)*8D);
						provocation += MathUtil.floor(MathUtil.square(prevLocs[2]-player.posZ)*10D);
						updated = true;
					}
					
					prevPlayerLocs.put(player.getUniqueID(),new double[]{ player.posX, player.posY, player.posZ });
				}
				else prevPlayerLocs.remove(player.getUniqueID());
			}
			
			if (updated){
				dataWatcher.updateObject(16,provocation);
				System.out.println("movement prov "+provocation);
			}
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.getSourceOfDamage() instanceof EntityPlayer && source.getDamageType().equals("player")){ // melee only
			amount = Math.max(Math.min(amount*0.75F,6F),3F);
			
			if (super.attackEntityFrom(source,amount)){
				if (worldObj.isRemote)return true;
				
				provocation += 300;
				dataWatcher.updateObject(16,provocation);
				System.out.println("added prov after attack: "+provocation);
				
				for(EntityMobSanctuaryOverseer overseer:(List<EntityMobSanctuaryOverseer>)worldObj.getEntitiesWithinAABB(EntityMobSanctuaryOverseer.class,boundingBox.expand(8D,8D,8D))){
					if (overseer == this || !canEntityBeSeen(overseer))continue;
					overseer.provocation += 100;
					overseer.dataWatcher.updateObject(16,provocation);
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
		double mod = 0.3D+rand.nextDouble()*rand.nextDouble();
		motionX = vec.xCoord*mod;
		motionY = vec.yCoord*mod;
		motionZ = vec.zCoord*mod;
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
