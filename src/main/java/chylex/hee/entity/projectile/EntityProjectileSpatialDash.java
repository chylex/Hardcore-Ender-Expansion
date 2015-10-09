package chylex.hee.entity.projectile;
import java.util.ArrayList;
import java.util.List;
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
import chylex.hee.mechanics.enhancements.EnhancementList;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.MathUtil;
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
		
		double speed = 0.5D*(1+enhancements.get(SpatialDashGemEnhancements.SPEED));
		this.motionX *= speed;
		this.motionY *= speed;
		this.motionZ *= speed;
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
				if (startPos.distance(this) >= 40+20*enhancements.get(SpatialDashGemEnhancements.RANGE)){
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
			
			PacketPipeline.sendToAllAround(this,128D,new C22EffectLine(FXType.Line.SPATIAL_DASH_MOVE,lastTickPosX,lastTickPosY,lastTickPosZ,posX,posY,posZ));
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
					
					List<Pos> available = new ArrayList<>(8);
					Pos hitPos = Pos.at(hitVec);
					
					Pos.forEachBlock(hitPos.offset(-1,-2,-1),hitPos.offset(1,2,1),pos -> {
						// TODO
					});
					
					/* TODOboolean found = false;
					Block block;
					BlockPosM tmpPos = BlockPosM.tmp(x,y,z);
					
					for(int yTest = y; yTest <= y+8; yTest++){
						if ((block = tmpPos.setY(yTest).getBlock(worldObj)).getBlockHardness(worldObj,x,yTest,z) == -1)break;
						
						if (canSpawnIn(block,tmpPos.moveUp().getBlock(worldObj))){
							player.setPositionAndUpdate(x+0.5D,yTest+0.01D,z+0.5D);
							found = true;
							break;
						}
					}
					
					if (!found){
						for(int xTest = x-1; xTest <= x+1; xTest++){
							for(int zTest = z-1; zTest <= z+1; zTest++){
								for(int yTest = y+1; yTest <= y+8; yTest++){
									if ((block = tmpPos.set(xTest,yTest,zTest).getBlock(worldObj)).getBlockHardness(worldObj,x,yTest,z) == -1)break;
									
									if (canSpawnIn(block,tmpPos.moveUp().getBlock(worldObj))){
										player.setPositionAndUpdate(xTest+0.5D,yTest+0.01D,zTest+0.5D);
										found = true;
										break;
									}
								}
							}
						}
					}
					
					if (!found)player.setPositionAndUpdate(x+0.5D,y+0.01D,z+0.5D);
					if (tryAchievement && BlockPosM.tmp(x,y,z).getBlock(worldObj).isOpaqueCube())player.addStat(AchievementManager.TP_NEAR_VOID,1);
					player.fallDistance = 0F;
					
					PacketPipeline.sendToAllAround(player,64D,new C20Effect(FXType.Basic.GEM_TELEPORT_TO,player));*/
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
		
		if (getThrower() != null)HardcoreEnderExpansion.notifications.report("dist: "+MathUtil.distance(posX-getThrower().posX,posY-getThrower().posY,posZ-getThrower().posZ)); // TODO
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
