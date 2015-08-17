package chylex.hee.world.feature.stronghold;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.util.Size;

public abstract class StrongholdPiece extends StructureDungeonPiece{
	protected enum Type implements IType{ CORRIDOR, DOOR, ROOM, DEADEND }
	
	protected IConnectWith fromRoom = type -> type == Type.CORRIDOR || type == Type.DOOR;
	protected IConnectWith fromDoor = type -> type == Type.CORRIDOR || type == Type.ROOM;
	protected IConnectWith withAnything = type -> true;
	
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
	
	public StrongholdPiece(Type type, Size size){
		super(type,size);
	}
}
