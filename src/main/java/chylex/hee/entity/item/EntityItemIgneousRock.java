package chylex.hee.entity.item;
import java.util.IdentityHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.technical.EntityTechnicalPuzzleChain;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

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
		if (thrower != null)thrownDirection = (byte)(MathHelper.floor_double((thrower.rotationYaw*4F/360F)+0.5D)&3);
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
					BlockPosM tmpPos = BlockPosM.tmp(this);
					tmpPos.move(MathUtil.floor((rand.nextDouble()-0.5D)*4D),MathUtil.floor((rand.nextDouble()-0.5D)*4D),MathUtil.floor((rand.nextDouble()-0.5D)*4D));
					
					Block block = tmpPos.getBlock(worldObj);
					Block target = blockTransformations.get(block);
					
					if (target != null)tmpPos.setBlock(worldObj,target);
					else if (block.getMaterial() == Material.air){
						if (rand.nextInt(5) == 0)tmpPos.setBlock(worldObj,Blocks.fire);
						else continue;
					}
					else if (block == Blocks.tnt){
						tmpPos.setAir(worldObj);
						worldObj.createExplosion(null,tmpPos.x,tmpPos.y,tmpPos.z,3.9F,true);
					}
					else if (block == Blocks.tallgrass && tmpPos.getMetadata(worldObj) != 0){
						tmpPos.setMetadata(worldObj,0,2);
					}
					else continue;
					
					if (block.getMaterial() != Material.air){
						PacketPipeline.sendToAllAround(this,64D,new C20Effect(FXType.Basic.IGNEOUS_ROCK_MELT,tmpPos.x+0.5D,tmpPos.y+0.5D,tmpPos.z+0.5D));
					}
					
					if (rand.nextInt(3) == 0)break;
				}
			}
			
			if (rand.nextInt(80-Math.min(32,is.stackSize/3)) == 0){
				List<EntityLivingBase> nearbyEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(3D,3D,3D));
				if (!nearbyEntities.isEmpty())nearbyEntities.get(rand.nextInt(nearbyEntities.size())).setFire(1+rand.nextInt(4)+getEntityItem().stackSize/10);
			}
		}
		
		int ix = MathUtil.floor(posX), iy = MathUtil.floor(posY), iz = MathUtil.floor(posZ);
		
		if (rand.nextInt(6) == 0 && worldObj.getBlock(ix,iy,iz).getMaterial() == Material.water){
			HardcoreEnderExpansion.fx.bubble(worldObj,posX+0.2F*(rand.nextFloat()-0.5F),posY+0.2F*(rand.nextFloat()-0.5F),posZ+0.2F*(rand.nextFloat()-0.5F),0D,0.6D,0D);		
		}
		
		if (worldObj.getBlock(ix,iy-1,iz) == BlockList.dungeon_puzzle){
			int meta = worldObj.getBlockMetadata(ix,iy-1,iz);
			
			if (BlockDungeonPuzzle.canTrigger(meta)){
				for(int a = 0; a < 4; a++)HardcoreEnderExpansion.fx.igneousRockBreak(this);
				
				if (!worldObj.isRemote && onGround){
					worldObj.spawnEntityInWorld(new EntityTechnicalPuzzleChain(worldObj,ix,iy-1,iz,thrownDirection));
					setDead();
				}
			}
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
