package chylex.hee.entity.technical;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.entity.fx.FXType;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.MathUtil;

public class EntityTechnicalPuzzleChain extends EntityTechnicalBase{
	private Facing4 dir;
	
	public EntityTechnicalPuzzleChain(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleChain(World world, Pos startPos, Facing4 dir){
		super(world);
		setPosition(startPos.getX()+0.5D, startPos.getY()+0.5D, startPos.getZ()+0.5D);
		this.dir = dir;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (ticksExisted%8 == 1){
			setPosition(posX+dir.getX(), posY, posZ+dir.getZ());
			Pos pos = Pos.at(this);
			
			if (pos.getBlock(worldObj) == BlockList.dungeon_puzzle){
				if (((BlockDungeonPuzzle)BlockList.dungeon_puzzle).updateChain(worldObj, pos, dir)){
					PacketPipeline.sendToAllAround(dimension, pos, 64D, new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN, pos));
				}
				else setDead();
			}
			else{
				((BlockDungeonPuzzle)BlockList.dungeon_puzzle).checkWinConditions(worldObj, pos.offset(dir.opposite()));
				setDead();
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		nbt.setByte("chainDir", (byte)dir.ordinal());
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		dir = Facing4.list[MathUtil.clamp(nbt.getByte("chainDir"), 0, Facing4.list.length-1)];
	}
}
