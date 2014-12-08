package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
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
		if (ticksExisted == 1){
			int yy = MathUtil.floor(posY);
			
			for(int xx = minX; xx <= maxX; xx++){
				for(int zz = minZ; zz <= maxZ; zz++){
					if (worldObj.getBlock(xx,yy,zz) == BlockList.dungeon_puzzle && worldObj.getBlockMetadata(xx,yy,zz) != BlockDungeonPuzzle.metaDisabled)locs.add(new BlockLocation(xx,yy,zz));
				}
			}
		}
		else if (!locs.isEmpty() && ticksExisted%3 == 0){
			for(int a = 0; a < 1+rand.nextInt(4) && !locs.isEmpty(); a++){
				BlockLocation loc = locs.remove(rand.nextInt(locs.size()));
				worldObj.setBlockMetadataWithNotify(loc.x,loc.y,loc.z,BlockDungeonPuzzle.metaDisabled,3);
			}
		}
		else if (locs.isEmpty() && ++appearTimer > 10){
			worldObj.setBlockMetadataWithNotify(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ),BlockDungeonPuzzle.metaPortal,3);
			setDead();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		nbt.setInteger("x1",minX);
		nbt.setInteger("z1",minZ);
		nbt.setInteger("x2",maxX);
		nbt.setInteger("z2",maxZ);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		minX = nbt.getInteger("x1");
		minZ = nbt.getInteger("z1");
		maxX = nbt.getInteger("x2");
		maxZ = nbt.getInteger("z2");
	}
}
