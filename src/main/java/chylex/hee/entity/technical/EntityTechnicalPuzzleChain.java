package chylex.hee.entity.technical;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.entity.fx.FXType;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.world.util.Direction;

public class EntityTechnicalPuzzleChain extends EntityTechnicalBase{
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
			
			BlockPosM tmpPos = BlockPosM.tmp(this);
			
			if (tmpPos.getBlock(worldObj) == BlockList.dungeon_puzzle){
				if (((BlockDungeonPuzzle)BlockList.dungeon_puzzle).updateChain(worldObj,tmpPos.x,tmpPos.y,tmpPos.z,dir)){
					PacketPipeline.sendToAllAround(dimension,tmpPos.x+0.5D,tmpPos.y+0.5D,tmpPos.z+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,tmpPos.x+0.5D,tmpPos.y+0.5D,tmpPos.z+0.5D));
				}
				else setDead();
			}
			else{
				((BlockDungeonPuzzle)BlockList.dungeon_puzzle).checkWinConditions(worldObj,tmpPos.x-Direction.offsetX[dir],tmpPos.y,tmpPos.z-Direction.offsetZ[dir]);
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
