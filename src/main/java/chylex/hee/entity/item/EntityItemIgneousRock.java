package chylex.hee.entity.item;
import java.util.IdentityHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C30Effect;

public class EntityItemIgneousRock extends EntityItem{
	private static final IdentityHashMap<Block,Block> blockTransformations = new IdentityHashMap<>();
	
	static{
		blockTransformations.put(Blocks.ice, Blocks.flowing_water);
		blockTransformations.put(Blocks.snow_layer, Blocks.flowing_water);
		blockTransformations.put(Blocks.snow, Blocks.flowing_water);
		blockTransformations.put(Blocks.water, Blocks.cobblestone);
		blockTransformations.put(Blocks.flowing_water, null);
		blockTransformations.put(Blocks.grass, Blocks.dirt);
		blockTransformations.put(Blocks.red_flower, Blocks.tallgrass);
		blockTransformations.put(Blocks.yellow_flower, Blocks.tallgrass);
		blockTransformations.put(Blocks.cobblestone, Blocks.stone);
		blockTransformations.put(Blocks.clay, Blocks.hardened_clay);
		blockTransformations.put(Blocks.sand, Blocks.glass);
	}
	
	private short rockLife = 700;
	private byte thrownDirection;
	
	public EntityItemIgneousRock(World world){
		super(world);
	}
	
	public EntityItemIgneousRock(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
		
		EntityPlayer thrower = world.getClosestPlayer(x,y-1.62D,z,1D);
		if (thrower != null){
			int dir = MathHelper.floor_double((thrower.rotationYaw*4F/360F)+0.5D)&3;
			thrownDirection = (byte)(dir == 2?0:dir == 1?3:dir == 0?1:2);
		}
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (!worldObj.isRemote){
			ItemStack is = getEntityItem();
			if ((rockLife -= rand.nextInt(isInWater()?5:3)) < 0){
				if (--is.stackSize == 0)setDead();
				else{
					rockLife = 700;
					setEntityItemStack(is);
				}
			}
			
			if (rand.nextInt(64-Math.min(32,is.stackSize/2)) == 0){
				for(int attempt = 0; attempt < 4+(is.stackSize/8); attempt++){
					int[] pos = new int[]{ (int)Math.floor(posX),(int)Math.floor(posY),(int)Math.floor(posZ) };
					for(int a = 0; a < pos.length; a++)pos[a] += (int)Math.floor((rand.nextDouble()-0.5D)*4D);
					
					Block block = worldObj.getBlock(pos[0],pos[1],pos[2]);
					Block target = blockTransformations.get(block);
					
					if (target != null)worldObj.setBlock(pos[0],pos[1],pos[2],target);
					else if (block.getMaterial() == Material.air){
						if (rand.nextInt(5) == 0)worldObj.setBlock(pos[0],pos[1],pos[2],Blocks.fire);
						else continue;
					}
					else if (block == Blocks.tnt){
						worldObj.setBlockToAir(pos[0],pos[1],pos[2]);
						worldObj.createExplosion(null,pos[0],pos[1],pos[2],3.9F,true);
					}
					else if (block == Blocks.tallgrass && worldObj.getBlockMetadata(pos[0],pos[1],pos[2]) != 0){
						worldObj.setBlockMetadataWithNotify(pos[0],pos[1],pos[2],0,2);
					}
					else continue;
					
					if (block.getMaterial() != Material.air){
						PacketPipeline.sendToAllAround(this,64D,new C30Effect(FXType.IGNEOUS_ROCK_MELT,pos[0]+0.5D,pos[1]+0.5D,pos[2]+0.5D));
					}
					
					if (rand.nextInt(3) == 0)break;
				}
			}
			
			if (rand.nextInt(80-Math.min(32,is.stackSize/3)) == 0){
				List<?> nearbyEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(3D,3D,3D));
				if (!nearbyEntities.isEmpty()){
					Object o = nearbyEntities.get(rand.nextInt(nearbyEntities.size()));
					((Entity)o).setFire(1+rand.nextInt(4)+getEntityItem().stackSize/10);
				}
			}
		}
		
		int ix = (int)Math.floor(posX),iy = (int)Math.floor(posY),iz = (int)Math.floor(posZ);
		
		if (rand.nextInt(6) == 0 && worldObj.getBlock(ix,iy,iz).getMaterial() == Material.water){
			HardcoreEnderExpansion.fx.bubble(worldObj,posX+0.2F*(rand.nextFloat()-0.5F),posY+0.2F*(rand.nextFloat()-0.5F),posZ+0.2F*(rand.nextFloat()-0.5F),0D,0.6D,0D);		
		}
		
		if (worldObj.getBlock(ix,iy-1,iz) == BlockList.dungeon_puzzle){
			int meta = worldObj.getBlockMetadata(ix,iy-1,iz);
			
			if (meta != BlockDungeonPuzzle.metaWall){
				for(int a = 0; a < 4; a++)HardcoreEnderExpansion.fx.igneousRockBreak(this);
				
				if (!worldObj.isRemote && onGround){
					worldObj.setBlockMetadataWithNotify(ix,iy-1,iz,(meta == BlockDungeonPuzzle.metaUnlit?BlockDungeonPuzzle.metaSpreadingLitN:BlockDungeonPuzzle.metaSpreadingUnlitN)+thrownDirection,2);
					worldObj.scheduleBlockUpdate(ix,iy-1,iz,BlockList.dungeon_puzzle,8);
					setDead();
				}
			}
		}
		
		if (!worldObj.isRemote && ticksExisted%65 == 0){
			for(EntityPlayer observer:ObservationUtil.getAllObservers(this,16D))KnowledgeRegistrations.IGNEOUS_ROCK.tryUnlockFragment(observer,0.15F);
		}
		
		if (rand.nextInt(30) == 0){
			for(int a = 0; a < 2; a++)worldObj.spawnParticle("lava",posX+0.2F*(rand.nextFloat()-0.5F),posY+0.2F*(rand.nextFloat()-0.5F),posZ+0.2F*(rand.nextFloat()-0.5F),0D,0D,0D);
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount){
		return damageSource.isFireDamage()?false:super.attackEntityFrom(damageSource,amount);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setShort("rockLife",rockLife);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		rockLife = nbt.hasKey("rockLife")?nbt.getShort("rockLife"):rockLife;
	}
}
