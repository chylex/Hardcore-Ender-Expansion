package chylex.hee.entity.technical;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;

public class EntityTechnicalPuzzleChain extends Entity{
	private byte dir;
	
	public EntityTechnicalPuzzleChain(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleChain(World world, int x, int y, int z, int dir){
		super(world);
		setPosition(x+0.5D-Direction.offsetX[dir],y+0.5D,z+0.5D-Direction.offsetZ[dir]);
		this.dir = (byte)dir;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (ticksExisted%8 == 1){
			setPosition(posX+Direction.offsetX[dir],posY,posZ+Direction.offsetZ[dir]);
			
			int x = (int)Math.floor(posX), y = (int)Math.floor(posY), z = (int)Math.floor(posZ);
			
			if (worldObj.getBlock(x,y,z) == BlockList.dungeon_puzzle){
				if (((BlockDungeonPuzzle)BlockList.dungeon_puzzle).updateChain(worldObj,x,y,z,dir)){
					PacketPipeline.sendToAllAround(dimension,x+0.5D,y+0.5D,z+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,x+0.5D,y+0.5D,z+0.5D));
				}
				else setDead();
			}
			else setDead();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		nbt.setByte("chainDir",dir);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		dir = nbt.getByte("chainDir");
	}
}
