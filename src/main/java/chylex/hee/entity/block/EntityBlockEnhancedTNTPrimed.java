package chylex.hee.entity.block;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBlockEnhancedTNTPrimed extends EntityTNTPrimed{
	private List<Enum> tntEnhancements = new ArrayList<>();
	private boolean wentIntoWall = false;
	
	public EntityBlockEnhancedTNTPrimed(World world){
		super(world);
		fuse = 80;
		yOffset = 0;
	}

	public EntityBlockEnhancedTNTPrimed(World world, double x, double y, double z, EntityLivingBase igniter, List<Enum> enhancements){
		super(world,x,y,z,igniter);
		this.tntEnhancements.addAll(enhancements);
		
		if (tntEnhancements.contains(TNTEnhancements.NOCLIP)){
			noClip = true;
			fuse = 40;
			dataWatcher.updateObject(16,Byte.valueOf((byte)1));
		}
		
		if (tntEnhancements.contains(TNTEnhancements.NO_FUSE))fuse = 1;
	}
	
	@Override
	public void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16,Byte.valueOf((byte)0));
		dataWatcher.addObject(17,Byte.valueOf((byte)0));
		dataWatcher.addObject(18,"");
	}

	@Override
	public void onUpdate(){
		if (!worldObj.isRemote && ticksExisted == 1)dataWatcher.updateObject(18,EnhancementEnumHelper.serialize(tntEnhancements));
		
		if (worldObj.isRemote && !noClip && dataWatcher.getWatchableObjectByte(16) == 1){
			noClip = true;
			fuse = 40;
		}
		
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		motionY -= 0.04D;
		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.98D;
		motionY *= 0.98D;
		motionZ *= 0.98D;
		
		if (!worldObj.isRemote && noClip){
			Block block = BlockPosM.tmp(this).getBlock(worldObj);
			
			if (!wentIntoWall && block.isOpaqueCube())wentIntoWall = true;
			else if (wentIntoWall && block.getMaterial() == Material.air){
				fuse = 1;
				dataWatcher.updateObject(17,Byte.valueOf((byte)1));
			}
		}

		if (onGround && !noClip){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;
		}

		if (fuse-- == 5 || (fuse == 0 && tntEnhancements.contains(TNTEnhancements.NO_FUSE)))dataWatcher.updateObject(17,Byte.valueOf((byte)1));
		
		if (fuse <= 0){
			setDead();
			if (!worldObj.isRemote)explode();
		}
		else worldObj.spawnParticle("smoke",posX,posY+0.5D,posZ,0D,0D,0D);
		
		setPosition(posX,posY,posZ);
	}

	private void explode(){
		EnhancedTNTExplosion explosion = new EnhancedTNTExplosion(worldObj,this,posX,posY,posZ,tntEnhancements.contains(TNTEnhancements.EXTRA_POWER) ? 5.2F : 4F);
		explosion.isFlaming = tntEnhancements.contains(TNTEnhancements.FIRE);
		explosion.isSmoking = !tntEnhancements.contains(TNTEnhancements.NO_BLOCK_DAMAGE);
		explosion.damageEntities = !tntEnhancements.contains(TNTEnhancements.NO_ENTITY_DAMAGE);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
	}
	
	@Override
	public void setDead(){
		super.setDead();
		
		if (!isDead && worldObj.isRemote && dataWatcher.getWatchableObjectByte(17) == 1){
			tntEnhancements = EnhancementEnumHelper.deserialize(dataWatcher.getWatchableObjectString(18),TNTEnhancements.class);
			explode();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int par9){
		setPosition(x,y,z);
		setRotation(yaw,pitch);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setString("enhancements",EnhancementEnumHelper.serialize(tntEnhancements));
		nbt.setBoolean("wentIntoWall",wentIntoWall);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		tntEnhancements = EnhancementEnumHelper.deserialize(nbt.getString("enhancements"),TNTEnhancements.class);
		wentIntoWall = nbt.getBoolean("wentIntoWall");
	}
	
	@Deprecated
	private static final class EnhancedTNTExplosion extends Explosion{
		private final World worldObj;
		private final int dist = 16;
		private final Map<EntityPlayer,Vec3> hurtPlayers = new HashMap<>();
		
		public boolean damageEntities = true;

		public EnhancedTNTExplosion(World world, Entity sourceEntity, double x, double y, double z, float power){
			super(world,sourceEntity,x,y,z,power);
			this.worldObj = world;
		}
		
		@Override
		public void doExplosionA(){
			float explosionSizeBackup = explosionSize;
			
			HashSet<ChunkPosition> affectedBlocks = new HashSet<>();
			double tempX, tempY, tempZ, distX, distY, distZ, totalDist;
			BlockPosM testPos = new BlockPosM();

			for(int x = 0; x < dist; ++x){
				for(int y = 0; y < dist; ++y){
					for(int z = 0; z < dist; ++z){
						if (x == 0 || x == dist-1 || y == 0 || y == dist-1 || z == 0 || z == dist-1){
							distX = (x/(dist-1F)*2F-1F);
							distY = (y/(dist-1F)*2F-1F);
							distZ = (z/(dist-1F)*2F-1F);
							totalDist = Math.sqrt(distX*distX+distY*distY+distZ*distZ);
							
							distX /= totalDist;
							distY /= totalDist;
							distZ /= totalDist;
							
							float affectedDistance = explosionSize*(0.7F+worldObj.rand.nextFloat()*0.6F);
							tempX = explosionX;
							tempY = explosionY;
							tempZ = explosionZ;

							for(float mp = 0.3F; affectedDistance > 0F; affectedDistance -= mp*0.75F){
								testPos.set(tempX,tempY,tempZ);
								Block block = testPos.getBlock(worldObj);

								if (block.getMaterial() != Material.air){
									float resistance = exploder != null ? exploder.func_145772_a(this,worldObj,testPos.x,testPos.y,testPos.z,block) : block.getExplosionResistance(exploder,worldObj,testPos.x,testPos.y,testPos.z,explosionX,explosionY,explosionZ);
									affectedDistance -= (resistance+0.3F)*mp;
								}

								if (affectedDistance > 0F && (exploder == null || exploder.func_145774_a(this,worldObj,testPos.x,testPos.y,testPos.z,block,affectedDistance))){
									affectedBlocks.add(new ChunkPosition(testPos.x,testPos.y,testPos.z));
								}

								tempX += distX*mp;
								tempY += distY*mp;
								tempZ += distZ*mp;
							}
						}
					}
				}
			}

			affectedBlockPositions.addAll(affectedBlocks);
			explosionSize *= 2F;
			
			int minX = MathHelper.floor_double(explosionX-explosionSize-1D), maxX = MathHelper.floor_double(explosionX+explosionSize+1D);
			int minY = MathHelper.floor_double(explosionY-explosionSize-1D), maxY = MathHelper.floor_double(explosionY+explosionSize+1D);
			int minZ = MathHelper.floor_double(explosionZ-explosionSize-1D), maxZ = MathHelper.floor_double(explosionZ+explosionSize+1D);
			
			List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(exploder,AxisAlignedBB.getBoundingBox(minX,minY,minZ,maxX,maxY,maxZ));
			Vec3 locationVec = Vec3.createVectorHelper(explosionX,explosionY,explosionZ);

			for(int a = 0; a < entities.size(); ++a){
				Entity entity = entities.get(a);
				double entityDist = entity.getDistance(explosionX,explosionY,explosionZ)/explosionSize;

				if (entityDist <= 1D){
					tempX = entity.posX-explosionX;
					tempY = entity.posY+entity.getEyeHeight()-explosionY;
					tempZ = entity.posZ-explosionZ;
					totalDist = MathHelper.sqrt_double(tempX*tempX+tempY*tempY+tempZ*tempZ);

					if (totalDist != 0D){
						tempX /= totalDist;
						tempY /= totalDist;
						tempZ /= totalDist;
						
						double blastPower = (1D-entityDist)*worldObj.getBlockDensity(locationVec,entity.boundingBox);
						if (damageEntities)entity.attackEntityFrom(DamageSource.setExplosionSource(this),((int)((blastPower*blastPower+blastPower)/2D*8D*explosionSize+1D)));
						
						double knockbackMp = EnchantmentProtection.func_92092_a(entity,blastPower);
						entity.motionX += tempX*knockbackMp;
						entity.motionY += tempY*knockbackMp;
						entity.motionZ += tempZ*knockbackMp;

						if (damageEntities && entity instanceof EntityPlayer){
							hurtPlayers.put((EntityPlayer)entity,Vec3.createVectorHelper(tempX*blastPower,tempY*blastPower,tempZ*blastPower));
						}
					}
				}
			}

			explosionSize = explosionSizeBackup;
		}
		
		@Override
		public Map func_77277_b(){
			return hurtPlayers;
		}
	}
}
