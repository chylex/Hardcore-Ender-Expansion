package chylex.hee.entity.technical;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.system.util.Direction;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.BlockPosM;

public class EntityTechnicalPuzzleChain extends EntityTechnicalBase{
	private byte dir;
	
	public EntityTechnicalPuzzleChain(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleChain(World world, BlockPosM pos, int dir){
		super(world);
		setPosition(pos.x+0.5D-Direction.offsetX[dir],pos.y+0.5D,pos.z+0.5D-Direction.offsetZ[dir]);
		this.dir = (byte)dir;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (ticksExisted%8 == 1){
			setPosition(posX+Direction.offsetX[dir],posY,posZ+Direction.offsetZ[dir]);
			
			BlockPosM pos = new BlockPosM(this);
			
			if (pos.getBlock(worldObj) == BlockList.dungeon_puzzle){
				if (((BlockDungeonPuzzle)BlockList.dungeon_puzzle).updateChain(worldObj,pos,dir)){
					PacketPipeline.sendToAllAround(dimension,pos,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,pos.x+0.5D,pos.y+0.5D,pos.z+0.5D));
				}
				else setDead();
			}
			else{
				((BlockDungeonPuzzle)BlockList.dungeon_puzzle).checkWinConditions(worldObj,pos.moveBy(-Direction.offsetX[dir],0,-Direction.offsetZ[dir]));
				setDead();
			}
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
