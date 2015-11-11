package chylex.hee.entity.projectile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.mechanics.enhancements.EnhancementList;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.RandUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileSpatialDash extends EntityThrowable{
	private final EnhancementList<SpatialDashGemEnhancements> enhancements;
	private Pos startPos;
	
	public EntityProjectileSpatialDash(World world){
		super(world);
		this.enhancements = new EnhancementList<>(SpatialDashGemEnhancements.class);
	}

	public EntityProjectileSpatialDash(World world, EntityLivingBase thrower, EnhancementList<SpatialDashGemEnhancements> enhancements){
		super(world,thrower);
		this.enhancements = enhancements;
		this.startPos = Pos.at(this);
		
		double speed = 1.5D+2D*enhancements.get(SpatialDashGemEnhancements.SPEED);
		
		Vec motionVec = Vec.xyz(motionX,motionY,motionZ).normalized().multiplied(speed);
		this.motionX = motionVec.x;
		this.motionY = motionVec.y;
		this.motionZ = motionVec.z;
	}

	@SideOnly(Side.CLIENT)
	public EntityProjectileSpatialDash(World world, double x, double y, double z){
		super(world,x,y,z);
		this.enhancements = new EnhancementList<>(SpatialDashGemEnhancements.class);
	}
	
	@Override
	public void setThrowableHeading(double motionX, double motionY, double motionZ, float ing, float randomMp){
		super.setThrowableHeading(motionX,motionY,motionZ,ing,0F);
	}
	
	@Override
	public void onUpdate(){
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		
		onEntityUpdate();

		if (!worldObj.isRemote){
			boolean instant = enhancements.get(SpatialDashGemEnhancements.SPEED) == 3;
			
			for(int cycles = instant ? 1000 : 1; cycles > 0; cycles--){
				if (startPos.distance(this) >= 32+32*enhancements.get(SpatialDashGemEnhancements.RANGE)){
					setDead();
					break;
				}
				
				Vec3 vecPos = Vec3.createVectorHelper(posX,posY,posZ);
				Vec3 vecPosWithMotion = Vec3.createVectorHelper(posX+motionX,posY+motionY,posZ+motionZ);
				
				MovingObjectPosition mop = worldObj.rayTraceBlocks(vecPos.addVector(0D,0D,0D),vecPosWithMotion.addVector(0D,0D,0D));
				Vec3 hitVec = Optional.ofNullable(mop).map(mopTest -> mopTest.hitVec).orElse(vecPosWithMotion).addVector(0D,0D,0D);
				
				List<Entity> collisionList = worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.addCoord(motionX,motionY,motionZ).expand(1D,1D,1D));
				
				mop = collisionList.stream()
				.filter(e -> e.canBeCollidedWith() && (e != getThrower() || ticksExisted >= 5))
				.map(e -> Pair.of(e,Optional.ofNullable(e.boundingBox.expand(0.3F,0.3F,0.3F).calculateIntercept(vecPos,hitVec)).map(mopTest -> vecPos.distanceTo(mopTest.hitVec)).orElse(Double.MAX_VALUE)))
				.min((p1, p2) -> p1.getValue().compareTo(p2.getValue())) // returns closest entity
				.map(pair -> new MovingObjectPosition(pair.getKey()))
				.orElse(mop);
				
				if (mop != null && mop.typeOfHit != MovingObjectType.MISS){
					onImpact(mop);
					break;
				}
				
				setPosition(posX+motionX,posY+motionY,posZ+motionZ);
				
				if (instant)++ticksExisted;
			}
			
			PacketPipeline.sendToAllAround(this,164D,new C22EffectLine(FXType.Line.SPATIAL_DASH_MOVE,lastTickPosX,lastTickPosY,lastTickPosZ,posX,posY,posZ));
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (getThrower() instanceof EntityPlayerMP){
				EntityPlayerMP player = (EntityPlayerMP)getThrower();
				
				PacketPipeline.sendToAllAround(player,64D,new C21EffectEntity(FXType.Entity.GEM_TELEPORT_FROM,player));

				if (player.playerNetServerHandler.func_147362_b().isChannelOpen() && player.worldObj == worldObj){ // OBFUSCATED get network manager
					if (player.isRiding())player.mountEntity(null);
					boolean tryAchievement = player.posY <= 0D;
					
					Vec3 hitVec = mop.hitVec;
					Vec3 normalizedMotion = Vec3.createVectorHelper(motionX,motionY,motionZ).normalize();
					
					if (mop.typeOfHit == MovingObjectType.BLOCK)hitVec = hitVec.addVector(normalizedMotion.xCoord*1.41D,normalizedMotion.yCoord*1.41D,normalizedMotion.zCoord*1.41D);
					else if (mop.typeOfHit == MovingObjectType.ENTITY)hitVec = hitVec.addVector(-normalizedMotion.xCoord*2.82D,-normalizedMotion.yCoord*2.82D,-normalizedMotion.zCoord*2.82D);
					
					Map<Pos,Double> available = new HashMap<>(10);
					Pos hitPos = Pos.at(hitVec);
					
					Pos.forEachBlock(hitPos.offset(-1,-2,-1),hitPos.offset(1,2,1),pos -> {
						if (pos.getMaterial(worldObj).blocksMovement() &&
							!pos.offset(Facing6.UP_POSY).getBlock(worldObj).isNormalCube() &&
							!pos.offset(Facing6.UP_POSY,2).getBlock(worldObj).isNormalCube()){
							available.put(pos.immutable(),pos.distance(hitPos));
						}
					});
					
					if (available.isEmpty())player.setPositionAndUpdate(hitVec.xCoord,hitVec.yCoord,hitVec.zCoord);
					else{
						double minDistance = available.values().stream().mapToDouble(dist -> dist).min().getAsDouble();
						Pos[] closest = available.entrySet().stream().filter(entry -> MathUtil.floatEquals(entry.getValue().floatValue(),(float)minDistance)).map(entry -> entry.getKey()).toArray(Pos[]::new);
						
						Pos selected = RandUtil.anyOf(rand,closest);
						player.setPositionAndUpdate(selected.getX()+0.5D,selected.getY()+1.001D,selected.getZ()+0.5D);
					}
					
					player.fallDistance = 0F;
					if (tryAchievement && !player.isEntityInsideOpaqueBlock())player.addStat(AchievementManager.TP_NEAR_VOID,1);
					PacketPipeline.sendToAllAround(player,64D,new C20Effect(FXType.Basic.GEM_TELEPORT_TO,player));
				}
			}

			setDead();
		}
	}
	
	@Override
	public void setDead(){
		super.setDead();
		
		if (worldObj.isRemote){
			for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.spatialDashExplode(this);
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setLong("startPos",startPos.toLong());
		nbt.setString("enhancements2",enhancements.serialize());
		nbt.removeTag("inTile");
		nbt.removeTag("shake");
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		startPos = Pos.at(nbt.getLong("startPos"));
		enhancements.deserialize(nbt.getString("enhancements2"));
	}
}
