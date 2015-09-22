package chylex.hee.entity.item;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.mechanics.orb.WeightedItem;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.BlockPosM;

public class EntityItemInstabilityOrb extends EntityItem{
	public EntityItemInstabilityOrb(World world){
		super(world);
	}
	
	public EntityItemInstabilityOrb(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
		
		for(int a = 0; a < is.stackSize-1; a++){
			ItemStack newIS = is.copy();
			newIS.stackSize = 1;
			EntityItem item = new EntityItemInstabilityOrb(world,x,y,z,newIS);
			item.delayBeforeCanPickup = 40;
			world.spawnEntityInWorld(item);
		}
		
		is.stackSize = 1;
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (!worldObj.isRemote){
			age += 1+rand.nextInt(2+rand.nextInt(4))*rand.nextInt(3);
			if (age >= 666)detonate();
		}
		else{
			int chance = age/17-7;
			for(int a = 0; a < Math.min(7,(chance <= 0 ? (rand.nextInt(Math.abs(chance)+1) == 0 ? 1 : 0) : chance)); a++){
				HardcoreEnderExpansion.fx.instability(this);
			}
			
			if (rand.nextInt(3+Math.max(0,(int)((680F-age)/70F))) == 0)worldObj.playSound(posX,posY,posZ,"random.pop",0.15F+rand.nextFloat()*0.1F,0.7F+rand.nextFloat()*0.6F,false);
		}
	}
	
	private void detonate(){
		if (rand.nextInt(6) == 0){
			ExplosionOrb explosion = new ExplosionOrb(worldObj,this,posX,posY,posZ,2.8F+rand.nextFloat()*0.8F);
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ORB_EXPLOSION,posX,posY,posZ,0F,explosion.explosionSize));
		}
		else if (rand.nextInt(6) == 0){
			Class<?> cls = null;
			int ele = rand.nextInt(OrbSpawnableMobs.classList.size());
			Iterator<Class<?>> iter = OrbSpawnableMobs.classList.iterator();
			
			while(iter.hasNext()){
				cls = iter.next();
				if (--ele < 0)break;
			}
			
			try{
				Entity e = (Entity)cls.getConstructor(World.class).newInstance(worldObj);
				e.setPositionAndRotation(posX,posY,posZ,rand.nextFloat()*360F-180F,0F);
				worldObj.spawnEntityInWorld(e);
				
				PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ORB_TRANSFORMATION,e));
			}catch(Exception ex){
				Log.throwable(ex,"Error spawning entity $0 in EntityItemInstabilityOrb",cls == null ? "<null>" : cls.getSimpleName());
			}
		}
		else{
			WeightedItem item = OrbAcquirableItems.idList.getRandomItem(rand);
			
			if (item == null){ // list is empty
				String[] list = new String[]{
					ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.BONUS_CHEST, ChestGenHooks.MINESHAFT_CORRIDOR,
					ChestGenHooks.VILLAGE_BLACKSMITH, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
					ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.STRONGHOLD_CORRIDOR
				};
				
				WeightedRandomChestContent[] content = ChestGenHooks.getItems(list[rand.nextInt(list.length)],rand);
				if (content.length == 0)return;
				
				ItemStack is = content[rand.nextInt(content.length)].theItemId;
				item = new WeightedItem(is.getItem(),is.getItemDamage(),1);
			}
			
			int meta = item.getDamageValues()[rand.nextInt(item.getDamageValues().length)];
			if (meta == 32767)meta = 0;
			
			EntityItem entityitem = new EntityItem(worldObj,posX,posY,posZ,new ItemStack(item.getItem(),1,meta));
			entityitem.motionX = entityitem.motionY = entityitem.motionZ = 0D;
			entityitem.delayBeforeCanPickup = 10;
			worldObj.spawnEntityInWorld(entityitem);
			
			PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ORB_TRANSFORMATION,posX,posY,posZ,0.25F,0.4F));
		}
		
		setDead();
	}
	
	@Override
	public boolean combineItems(EntityItem item){
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isExplosion()){
			if (rand.nextInt(6) != 0){
				age -= 10-rand.nextInt(80);
				return false;
			}
		}
		
		return super.attackEntityFrom(source,amount);
	}
	
	public static final class ExplosionOrb extends Explosion{
		private final World worldObj;
		private final int dist = 16;
		private final Map<EntityPlayer,Vec3> hurtPlayers = new HashMap<>();
		
		public ExplosionOrb(World world, Entity sourceEntity, double x, double y, double z, float power){
			super(world,sourceEntity,x,y,z,power);
			this.worldObj = world;
			isSmoking = true;
			isFlaming = false;
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
						
						if (canDamageEntity(entity))entity.attackEntityFrom(DamageSource.setExplosionSource(this),((int)((blastPower*blastPower+blastPower)/2D*8D*explosionSize+1D)));
						else continue;
						
						double knockbackMp = EnchantmentProtection.func_92092_a(entity,blastPower);
						entity.motionX += tempX*knockbackMp;
						entity.motionY += tempY*knockbackMp;
						entity.motionZ += tempZ*knockbackMp;

						if (entity instanceof EntityPlayer){
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
		
		private boolean canDamageEntity(Entity entity){
			return !(entity instanceof EntityLiving);
		}
	}
}
