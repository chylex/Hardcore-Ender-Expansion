package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.util.BlockLocation;

public class EntityTechnicalPuzzleSolved extends Entity{
	private int minX, minZ, maxX, maxZ;
	private final List<BlockLocation> locs = new ArrayList<>();
	private byte appearTimer;
	
	public EntityTechnicalPuzzleSolved(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleSolved(World world, int x, int y, int z, int minX, int minZ, int maxX, int maxZ){
		super(world);
		setPosition(x+0.5D,y+0.5D,z+0.5D);
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (ticksExisted == 1 && appearTimer == 0){
			int yy = MathUtil.floor(posY);
			
			for(int xx = minX; xx <= maxX; xx++){
				for(int zz = minZ; zz <= maxZ; zz++){
					if (worldObj.getBlock(xx,yy,zz) == BlockList.dungeon_puzzle && worldObj.getBlockMetadata(xx,yy,zz) != BlockDungeonPuzzle.metaDisabled)locs.add(new BlockLocation(xx,yy,zz));
				}
			}
		}
		else if (!locs.isEmpty() && ticksExisted%4 == 0){
			for(int a = 0; a < 1+rand.nextInt(3) && !locs.isEmpty(); a++){
				BlockLocation loc = locs.remove(rand.nextInt(locs.size()));
				worldObj.setBlockMetadataWithNotify(loc.x,loc.y,loc.z,BlockDungeonPuzzle.metaDisabled,3);
				worldObj.addBlockEvent(loc.x,loc.y,loc.z,BlockList.dungeon_puzzle,69,0);
			}
		}
		else if (locs.isEmpty() && appearTimer < 12 && ++appearTimer == 12){
			worldObj.setBlockMetadataWithNotify(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ),BlockDungeonPuzzle.metaPortal,3);
			worldObj.addBlockEvent(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ),BlockList.dungeon_puzzle,69,1);
			appearTimer = 69;
		}
		else if (appearTimer == 69){
			if (worldObj.getBlock(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ)) != BlockList.dungeon_puzzle)setDead();
			else if (worldObj.getClosestPlayerToEntity(this,1D) != null){
				int iy = MathUtil.floor(posY);
				
				for(EntityPlayer player:(List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(minX,posY,minZ,maxX,posY+3D,maxZ))){
					int ix = MathUtil.floor(player.posX), iz = MathUtil.floor(player.posZ);
					
					if (worldObj.getBlock(ix,iy,iz) == BlockList.dungeon_puzzle){
						if (player.isRiding())player.mountEntity(null);
						
						double prevX = player.posX, prevY = player.posY, prevZ = player.posZ;
						player.setPositionAndUpdate(ix+0.5D,worldObj.getTopSolidOrLiquidBlock(ix,iz),iz+0.5D);
						player.fallDistance = 0F;
						PacketPipeline.sendToAllAround(this,64D,new C22EffectLine(FXType.Line.DUNGEON_PUZZLE_TELEPORT,player.posX,player.posY,player.posZ,prevX,prevY,prevZ));
					}
				}
				
				int blockX = MathUtil.floor(posX), blockZ = MathUtil.floor(posZ);
				
				EntityMiniBossFireFiend fiend = new EntityMiniBossFireFiend(worldObj);
				fiend.setLocationAndAngles(blockX+0.5D+(rand.nextDouble()-0.5D)*18D,worldObj.getTopSolidOrLiquidBlock(blockX,blockZ)+10,blockZ+0.5D+(rand.nextDouble()-0.5D)*18D,rand.nextFloat()*360F,0F);
				worldObj.spawnEntityInWorld(fiend);
				
				worldObj.setBlockMetadataWithNotify(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ),BlockDungeonPuzzle.metaDisabled,3);
				setDead();
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setInteger("x1",minX);
		nbt.setInteger("z1",minZ);
		nbt.setInteger("x2",maxX);
		nbt.setInteger("z2",maxZ);
		nbt.setByte("appearTim",appearTimer);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		minX = nbt.getInteger("x1");
		minZ = nbt.getInteger("z1");
		maxX = nbt.getInteger("x2");
		maxZ = nbt.getInteger("z2");
		appearTimer = nbt.getByte("appearTim");
	}
}
