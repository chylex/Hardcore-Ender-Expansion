package chylex.hee.entity.mob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C07AddPlayerVelocity;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

public class EntityMobSanctuaryOverseer extends EntityFlying{
	private Map<UUID,double[]> prevPlayerLocs = new HashMap<>();
	private short provocation, maxProvocation;
	private byte attackTimer, screamTimer;
	
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
		EntityAttributes.setValue(this,EntityAttributes.maxHealth,18D+rand.nextDouble()*29D);
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
		
		if (!worldObj.isRemote){
			if (attackTimer > 0)--attackTimer;
			
			if (screamTimer > 0){
				if (attackTimer > 0 || ++screamTimer > 55 && screamTimer > 55+rand.nextInt(45)){
					if (attackTimer > 0 && attackingPlayer != null){
						// TODO MultiDamage.from(this).addMagic(7F).addScaled(ModCommonProxy.opMobs ? 29F : 24F).attack(attackingPlayer);
						
						Vec vec = Vec.between(this,attackingPlayer).normalized();
						attackingPlayer.addVelocity(vec.x,0.2D,vec.z);
						PacketPipeline.sendToPlayer(attackingPlayer,new C07AddPlayerVelocity(vec.x,0.2D,vec.z));
						
						PacketPipeline.sendToAllAround(this,10D,new C21EffectEntity(FXType.Entity.SANCTUARY_OVERSEER_SINGLE,this));
					}
					else{
						setHealth(getHealth()*0.5F);
						
						BlockPosM pos1 = new BlockPosM(this), pos2 = pos1.copy();
						
						for(int att = 0; att < 30; att++){
							if (worldObj.isAirBlock(pos1.x-1,pos1.y,pos1.z))--pos1.x;
							if (worldObj.isAirBlock(pos2.x+1,pos1.y,pos1.z))++pos2.x;
							if (worldObj.isAirBlock(pos1.x,pos1.y-1,pos1.z))--pos1.y;
							if (worldObj.isAirBlock(pos1.x,pos2.y+1,pos1.z))++pos2.y;
							if (worldObj.isAirBlock(pos1.x,pos1.y,pos1.z-1))--pos1.z;
							if (worldObj.isAirBlock(pos1.x,pos1.y,pos2.z+1))++pos2.z;
						}
						
						for(EntityPlayer player:(List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(pos1.x,pos1.y,pos1.z,pos2.x,pos2.y,pos2.z).expand(0.9D,0.9D,0.9D))){
							// TODO MultiDamage.from(this).addMagic(5F).addScaled(ModCommonProxy.opMobs ? 22F : 18F).attack(player);
						}
						
						for(EntityMobSanctuaryOverseer overseer:(List<EntityMobSanctuaryOverseer>)worldObj.getEntitiesWithinAABB(EntityMobSanctuaryOverseer.class,boundingBox.expand(8D,8D,8D))){
							overseer.provocation /= 2;
							overseer.dataWatcher.updateObject(16,provocation);
						}
						
						PacketPipeline.sendToAllAround(this,10D,new C22EffectLine(FXType.Line.SANCTUARY_OVERSEER_FULL,pos1.x,pos1.y,pos1.z,pos2.x,pos2.y,pos2.z));
					}
					
					provocation = screamTimer = attackTimer = 0;
					dataWatcher.updateObject(16,provocation);
				}
			}
		}
		if (!worldObj.isRemote && ticksExisted%4 == 0 && screamTimer == 0){
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
				System.out.println("update prov "+provocation);
			}
			
			if (provocation >= maxProvocation)screamTimer = 1;
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
				
				attackTimer = 12;
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
			for(int a = 0; a < 8; a++)HardcoreEnderExpansion.fx.global("portalbig",posX,posY,posZ,0D,0D,0D,0.8F+rand.nextFloat()*0.2F);
			setDead();
		}
	}
}
