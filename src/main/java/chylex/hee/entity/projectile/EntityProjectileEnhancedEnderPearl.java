package chylex.hee.entity.projectile;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import chylex.hee.mechanics.enhancements.EnhancementList;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import cpw.mods.fml.client.FMLClientHandler;

public class EntityProjectileEnhancedEnderPearl extends EntityEnderPearl{
	private final EnhancementList<EnderPearlEnhancements> enhancements;
	private EntityPlayer ride = null;
	private short life = 0;
	
	public EntityProjectileEnhancedEnderPearl(World world){
		super(world);
		this.enhancements = new EnhancementList<>(EnderPearlEnhancements.class);
	}

	public EntityProjectileEnhancedEnderPearl(World world, EntityLivingBase thrower, EnhancementList<EnderPearlEnhancements> enhancements){
		super(world, thrower);
		this.enhancements = enhancements;
		
		if (thrower instanceof EntityPlayer){
			/* TODO EntityPlayer player = (EntityPlayer)thrower;
			
			if (pearlTypes.contains(EnderPearlEnhancements.RIDING)){
				ride = (EntityPlayer)thrower;
				
				for(Object o:world.loadedEntityList){
					if (o instanceof EntityProjectileEnhancedEnderPearl){
						EntityProjectileEnhancedEnderPearl pearl = (EntityProjectileEnhancedEnderPearl)o;
						
						if (pearl.ride != null && pearl.ride.getCommandSenderName().equals(player.getCommandSenderName())){
							pearl.ride = null;
							if (!pearl.pearlTypes.contains(EnderPearlEnhancements.NO_FALL_DAMAGE))player.attackEntityFrom(DamageSource.fall, 5F);
							pearl.setDead();
							break;
						}
					}
				}
			}*/
		}
	}
	
	@Override
	public void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16, ride == null ? "" : ride.getCommandSenderName());
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (!worldObj.isRemote){
			if (ride != null){
				dataWatcher.updateObject(16, ride.getCommandSenderName());
				updateRidePosition();
				ride.fallDistance = 0F;
				ride.setPosition(posX, posY+ride.height, posZ);
			}
			
			/* TODO if (pearlTypes.contains(EnderPearlEnhancements.DOUBLE_SPEED)){
				if (inGround){
					motionX *= 0.2D;
					motionY *= 0.2D;
					motionZ *= 0.2D;
				}
				else{
					super.onUpdate();
					if (ride != null)updateRidePosition();
				}
			}*/
			
			if (++life > 200)setDead();
		}
		else{
			EntityClientPlayerMP clientPlayer = FMLClientHandler.instance().getClient().thePlayer;
			if (dataWatcher.getWatchableObjectString(16).equals(clientPlayer.getCommandSenderName()))clientPlayer.setPosition(posX, posY+clientPlayer.height, posZ);
		}
	}

	private void updateRidePosition(){
		ride.lastTickPosX = ride.prevPosX = ride.posX = posX;
		ride.lastTickPosY = ride.prevPosY = ride.posY = posY+ride.height+yOffset;
		ride.lastTickPosZ = ride.prevPosZ = ride.posZ = posZ;
		NetHandlerPlayServer serverHandler = ((EntityPlayerMP)ride).playerNetServerHandler;
		serverHandler.floatingTickCount = 0;
		serverHandler.lastPosX = ride.posX;
		serverHandler.lastPosY = ride.posY;
		serverHandler.lastPosZ = ride.posZ;
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (mop.entityHit != null){
			if (ride != null && mop.entityHit.equals(ride))return;
			mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0F);
		}

		for(int i = 0; i < 32; ++i){
			worldObj.spawnParticle("portal", posX, posY+rand.nextDouble()*2D, posZ, rand.nextGaussian(), 0D, rand.nextGaussian());
		}

		if (!worldObj.isRemote){
			if (getThrower() != null && getThrower() instanceof EntityPlayerMP){
				EntityPlayerMP player = (EntityPlayerMP)getThrower();

				if (player.playerNetServerHandler.func_147362_b().isChannelOpen() && player.worldObj == worldObj){ // OBFUSCATED get network manager
					EnderTeleportEvent event = new EnderTeleportEvent(player, posX, posY, posZ, 5F);
					
					if (!MinecraftForge.EVENT_BUS.post(event)){
						// TODO if (pearlTypes.contains(EnderPearlEnhancements.EXPLOSIVE))worldObj.newExplosion(this, posX, posY, posZ, 2.7F, false, true);
						/* TODO if (pearlTypes.contains(EnderPearlEnhancements.FREEZE)){
							for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(5D, 3D, 5D))){
								double dist = entity.getDistanceSqToEntity(this);
								if (dist <= 5D)entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80+(int)(10D*(6D-dist)), 3, true));
							}
						}*/
						
						if (player.isRiding())player.mountEntity((Entity)null);
						player.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
						player.fallDistance = 0F;
						
						// TODO if (!pearlTypes.contains(EnderPearlEnhancements.NO_FALL_DAMAGE))player.attackEntityFrom(DamageSource.fall, event.attackDamage);
						// TODO if (pearlTypes.contains(EnderPearlEnhancements.FREEZE))PacketPipeline.sendToAllAround(this, 64D, new C20Effect(FXType.Basic.ENDER_PEARL_FREEZE, this));
					}
				}
			}

			setDead();
		}
	}
	
	@Override
	protected float getGravityVelocity(){
		return 1F; // TODO pearlTypes.contains(EnderPearlEnhancements.NO_GRAVITY) ? 0F : (super.getGravityVelocity()*(pearlTypes.contains(EnderPearlEnhancements.INCREASED_RANGE) ? 0.75F : 1F));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("enhancements2", enhancements.serialize());
		nbt.setShort("life", life);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		enhancements.deserialize(nbt.getString("enhancements2"));
		life = nbt.getShort("life");
	}
}
