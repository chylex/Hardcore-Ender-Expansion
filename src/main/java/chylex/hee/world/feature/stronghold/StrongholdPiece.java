package chylex.hee.world.feature.stronghold;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public abstract class StrongholdPiece extends StructureDungeonPiece{
	protected enum Type implements IType{ CORRIDOR, DOOR, ROOM, DEADEND }
	
	protected static final IConnectWith fromRoom = type -> type == Type.CORRIDOR || type == Type.DOOR;
	protected static final IConnectWith fromDoor = type -> type == Type.CORRIDOR || type == Type.ROOM;
	protected static final IConnectWith withAnything = type -> true;
	
	private static final BlockInfo[] blocksStoneBrick = new BlockInfo[]{
		new BlockInfo(Blocks.stonebrick,Meta.stoneBrickPlain),
		new BlockInfo(Blocks.stonebrick,Meta.stoneBrickMossy),
		new BlockInfo(Blocks.stonebrick,Meta.stoneBrickCracked),
		new BlockInfo(Blocks.monster_egg,Meta.silverfishPlain),
		new BlockInfo(Blocks.monster_egg,Meta.silverfishMossy),
		new BlockInfo(Blocks.monster_egg,Meta.silverfishCracked)
	};
	
	protected static final IBlockPicker placeStoneBrick = rand -> {
		int chance = rand.nextInt(100);
		
		if (chance < 45)return blocksStoneBrick[0];
		else if (chance < 75)return blocksStoneBrick[1];
		else if (chance < 95)return blocksStoneBrick[2];
		else{
			chance = rand.nextInt(100);
			
			if (chance < 47)return blocksStoneBrick[3];
			else if (chance < 79)return blocksStoneBrick[4];
			else return blocksStoneBrick[5];
		}
	};
	
	protected static final IBlockPicker placeStoneBrickPlain = rand -> blocksStoneBrick[0];
	protected static final IBlockPicker placeAir = rand -> BlockInfo.air;
	
	// TODO test all pieces
	protected static final void placeStairOutline(StructureWorld world, Random rand, Block block, int centerX, int y, int centerZ, int distance, boolean outwards){
		IBlockPicker[] stairs = new IBlockPicker[]{
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.SOUTH_POSZ : Facing4.NORTH_NEGZ,false)),
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.NORTH_NEGZ : Facing4.SOUTH_POSZ,false)),
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.EAST_POSX : Facing4.WEST_NEGX,false)),
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.WEST_NEGX : Facing4.EAST_POSX,false))
		};
		
		for(int facingInd = 0, off, perX, perZ; facingInd < Facing4.list.length; facingInd++){
			Facing4 facing = Facing4.list[facingInd];
			off = facing.getX() == 0 ? distance-1 : distance;
			perX = facing.perpendicular().getX();
			perZ = facing.perpendicular().getZ();
			placeLine(world,rand,stairs[facingInd],centerX+distance*facing.getX()-off*perX,y,centerZ+distance*facing.getZ()-off*perZ,centerX+distance*facing.getX()+off*perX,y,centerZ+distance*facing.getZ()+off*perZ);
		}
	}
	
	public StrongholdPiece(Type type, Size size){
		super(type,size);
	}
}
