package chylex.hee.system.abstractions;
import net.minecraft.tileentity.TileEntityChest;
import chylex.hee.world.structure.IStructureTileEntity;
import chylex.hee.world.structure.util.Facing4;

public final class Meta{
	public static final byte
		stoneBrickPlain = 0,
		stoneBrickMossy = 1,
		stoneBrickCracked = 2,
		stoneBrickChiseled = 3,
		
		silverfishPlain = 2,
		silverfishMossy = 3,
		silverfishCracked = 4,
		silverfishChiseled = 5,
		
		slabStoneBrickBottom = 5,
		slabStoneBrickTop = 5+8;
	
	public static byte getTorch(Facing4 attachedOn){
		switch(attachedOn){
			case WEST_NEGX: return 1;
			case EAST_POSX: return 2;
			case NORTH_NEGZ: return 3;
			case SOUTH_POSZ: return 4;
			default: return 0;
		}
	}
	
	public static byte getStairs(Facing4 ascendsTowards, boolean flip){
		switch(ascendsTowards){
			case EAST_POSX: return (byte)(0+(flip ? 4 : 0));
			case WEST_NEGX: return (byte)(1+(flip ? 4 : 0));
			case SOUTH_POSZ: return (byte)(2+(flip ? 4 : 0));
			case NORTH_NEGZ: return (byte)(3+(flip ? 4 : 0));
			default: return 0;
		}
	}
	
	public static byte getDoor(Facing4 opensTowards, boolean upper){
		if (upper)return 8;
		
		switch(opensTowards){
			case EAST_POSX: return 0;
			case SOUTH_POSZ: return 1;
			case WEST_NEGX: return 2;
			case NORTH_NEGZ: return 3;
			default: return 0;
		}
	}
	
	public static IStructureTileEntity generateChest(Facing4 facingTo, IStructureTileEntity call){
		final int meta;
		
		switch(facingTo){
			case EAST_POSX: meta = 5; break;
			case WEST_NEGX: meta = 4; break;
			case SOUTH_POSZ: meta = 3; break;
			case NORTH_NEGZ: meta = 2; break;
			default: meta = 0;
		}
		
		return (tile, rand) -> {
			tile.getWorldObj().setBlockMetadataWithNotify(tile.xCoord,tile.yCoord,tile.zCoord,meta,3);
			((TileEntityChest)tile).adjacentChestChecked = true;
			call.generateTile(tile,rand);
		};
	}
	
	private Meta(){}
}
