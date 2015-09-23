package chylex.hee.system.abstractions;
import gnu.trove.map.hash.TLongFloatHashMap;
import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C01Explosion;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.util.BooleanByte;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Explosion{
	private static final byte precision = 16;
	private static final Set<Vec3> iterationPoints;
	
	static{
		Set<Vec3> pts = new LinkedHashSet<>();
		double distX, distY, distZ, totalDist;
		
		for(int x = 0; x < precision; x++){
			for(int y = 0; y < precision; y++){
				for(int z = 0; z < precision; z++){
					if (x == 0 || y == 0 || z == 0 || x == precision-1 || y == precision-1 || z == precision-1){
						distX = x/(precision-1F)*2F-1F;
						distY = y/(precision-1F)*2F-1F;
						distZ = z/(precision-1F)*2F-1F;
						totalDist = Math.sqrt(distX*distX+distY*distY+distZ*distZ);
						distX /= totalDist;
						distY /= totalDist;
						distZ /= totalDist;
						pts.add(Vec3.createVectorHelper(distX,distY,distZ));
					}
				}
			}
		}
		
		iterationPoints = Collections.unmodifiableSet(pts);
	}
	
	protected final World world;
	protected final Random calcRand;
	protected final Entity explodingEntity;
	protected final Entity cause;
	protected final float x, y, z;
	protected final float radius;
	
	public boolean damageBlocks = true;
	public boolean damageEntities = true;
	public boolean knockEntities = true;
	public boolean spawnFire = false;
	
	private int randSeed;
	private final TLongFloatHashMap blockResistanceCache = new TLongFloatHashMap(150); // TODO implement and profile
	
	public Explosion(World world, double x, double y, double z, float radius, Entity explodingEntity, Entity cause){
		this.world = world;
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
		this.radius = radius;
		this.calcRand = new Random(this.randSeed = world.rand.nextInt());
		this.explodingEntity = explodingEntity;
		this.cause = null;
	}
	
	public Explosion(World world, double x, double y, double z, float radius, Entity explodingEntity){
		this(world,x,y,z,radius,explodingEntity,explodingEntity);
	}
	
	@SideOnly(Side.CLIENT)
	public Explosion(World world, ByteBuf buffer){
		this.world = world;
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.radius = buffer.readShort()/1000F;
		this.calcRand = new Random(this.randSeed = buffer.readInt());
		this.explodingEntity = this.cause = null;
		
		BooleanByte bb = new BooleanByte(buffer.readByte());
		damageBlocks = bb.get(0);
		damageEntities = bb.get(1);
		knockEntities = bb.get(2);
		spawnFire = bb.get(3);
	}
	
	public void writePacket(ByteBuf buffer){
		buffer.writeFloat(x).writeFloat(y).writeFloat(z);
		buffer.writeShort((short)MathUtil.floor(radius*1000F));
		buffer.writeInt(randSeed);
		buffer.writeByte(BooleanByte.of(damageBlocks,damageEntities,knockEntities,spawnFire).toByte());
	}
	
	public void trigger(){
		if (!world.isRemote){
			PacketPipeline.sendToAllAround(world.provider.dimensionId,x,y,z,256D,new C01Explosion(this));
			explode(false);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void triggerClient(){
		explode(true);
	}
	
	private void explode(boolean client){ // TODO profile
		if (damageEntities || knockEntities)handleEntities(client);
		if (damageBlocks || spawnFire)handleBlocks(client);
	}
	
	protected void handleBlocks(boolean client){
		final net.minecraft.world.Explosion vanillaExplosion = new net.minecraft.world.Explosion(world,explodingEntity,x,y,z,radius);
		
		Map<Pos,Block> affected = new HashMap<>(client ? 400 : 150);
		PosMutable mpos = new PosMutable(), prevPos = new PosMutable();
		float prevResist = 0F;
		double tempX, tempY, tempZ;
		
		for(Vec3 vec:iterationPoints){
			float affectedDistance = radius*(0.7F+calcRand.nextFloat()*0.6F);
			tempX = x;
			tempY = y;
			tempZ = z;

			for(float mp = 0.3F; affectedDistance > 0F; affectedDistance -= mp*0.75F){
				mpos.set(tempX,tempY,tempZ);
				
				if (prevPos.equals(mpos)){
					affectedDistance -= (prevResist+0.3F)*mp;
				}
				else{
					Block block = mpos.getBlock(world);
					
					if (block.getMaterial() != Material.air){
						float resistance = block.getExplosionResistance(explodingEntity,world,mpos.x,mpos.y,mpos.z,x,y,z);
						affectedDistance -= (resistance+0.3F)*mp;
						
						prevPos.set(mpos);
						prevResist = resistance;
					}
					
					if (affectedDistance > 0F && (block.getMaterial() != Material.air || client))affected.put(mpos.immutable(),block);
				}
				
				tempX += vec.xCoord*mp;
				tempY += vec.yCoord*mp;
				tempZ += vec.zCoord*mp;
			}
		}

		if (client){
			world.playSound(x,y,z,"random.explode",4F,(1F+(world.rand.nextFloat()-0.5F)*0.4F)*0.7F,true);
			
			if (radius >= 2F && damageBlocks)world.spawnParticle("hugeexplosion",x,y,z,1D,0D,0D);
			else world.spawnParticle("largeexplode",x,y,z,1D,0D,0D);
		}
		
		if (damageBlocks){
			int nearbyTNT = client ? world.getEntitiesWithinAABB(EntityTNTPrimed.class,AxisAlignedBB.getBoundingBox(x,y,z,x,y,z).expand(16D,16D,16D)).size() : 0;
			
			float dropChance = 1F/radius;
			
			for(Entry<Pos,Block> entry:affected.entrySet()){
				Pos pos = entry.getKey();
				Block block = entry.getValue();
				
				if (client && world.rand.nextInt(3) == 0 && (nearbyTNT <= 1 || world.rand.nextInt(1+(nearbyTNT>>1)) == 0)){
					double partX = pos.getX()+world.rand.nextFloat();
					double partY = pos.getY()+world.rand.nextFloat();
					double partZ = pos.getZ()+world.rand.nextFloat();
					double diffX = partX-x, diffY = partY-y, diffZ = partZ-z;
					double dist = MathUtil.distance(diffX,diffY,diffZ);
					
					diffX /= dist;
					diffY /= dist;
					diffZ /= dist;
					double mp = (0.5D/(dist/radius+0.1D))*(world.rand.nextFloat()*world.rand.nextFloat()+0.3F);
					diffX *= mp;
					diffY *= mp;
					diffZ *= mp;
					
					HardcoreEnderExpansion.fx.setOmnipresent();
					HardcoreEnderExpansion.fx.global("explosion",(partX+x)/2D,(partY+y)/2D,(partZ+z)/2D,diffX,diffY,diffZ);
					HardcoreEnderExpansion.fx.global("smoke",partX,partY,partZ,diffX,diffY,diffZ);
				}
				
				if (!client || block.getMaterial() != Material.air){
					if (block.canDropFromExplosion(vanillaExplosion) && !client){
						block.dropBlockAsItemWithChance(world,pos.getX(),pos.getY(),pos.getZ(),pos.getMetadata(world),dropChance,0);
					}
					
					block.onBlockExploded(world,pos.getX(),pos.getY(),pos.getZ(),vanillaExplosion);
				}
			}
		}
		
		if (spawnFire){
			for(Entry<Pos,Block> entry:affected.entrySet()){
				if (entry.getValue().func_149730_j() && entry.getKey().getUp().isAir(world) && calcRand.nextInt(3) == 0){ // OBFUSCATED isOpaque
					entry.getKey().setBlock(world,Blocks.fire);
				}
			}
		}
	}
	
	protected void handleEntities(boolean client){
		final net.minecraft.world.Explosion vanillaExplosion = new net.minecraft.world.Explosion(world,explodingEntity,x,y,z,radius);
		
		double tempX, tempY, tempZ, totalDist;
		float doubleRadius = radius*2F;
		
		final List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,AxisAlignedBB.getBoundingBox(x,y,z,x,y,z).expand(doubleRadius,doubleRadius,doubleRadius));
		final Vec3 locationVec = Vec3.createVectorHelper(x,y,z);

		for(Entity entity:entities){
			if (entity == explodingEntity)continue;
			
			double entityDist = entity.getDistance(x,y,z)/doubleRadius;

			if (entityDist <= 1D){
				tempX = entity.posX-x;
				tempY = entity.posY+(entity instanceof EntityPlayer ? 1.62F : entity.getEyeHeight())-y;
				tempZ = entity.posZ-z;
				totalDist = MathUtil.distance(tempX,tempY,tempZ);

				if (totalDist != 0D){
					tempX /= totalDist;
					tempY /= totalDist;
					tempZ /= totalDist;
					
					double blastPower = (1D-entityDist)*world.getBlockDensity(locationVec,entity.boundingBox);
					
					if (damageEntities && !client){
						entity.attackEntityFrom(DamageSource.setExplosionSource(vanillaExplosion),((int)((blastPower*blastPower+blastPower)/2D*8D*doubleRadius+1D))); // TODO
					}
					
					if (knockEntities){
						double knockbackMp = EnchantmentProtection.func_92092_a(entity,blastPower);
						entity.motionX += tempX*knockbackMp;
						entity.motionY += tempY*knockbackMp;
						entity.motionZ += tempZ*knockbackMp;
					}
				}
			}
		}
	}
}
