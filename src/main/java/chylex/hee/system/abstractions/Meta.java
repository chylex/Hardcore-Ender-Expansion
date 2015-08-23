package chylex.hee.system.abstractions;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySkull;
import chylex.hee.system.util.MathUtil;
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
		
		slabStoneSmoothBottom = 0,
		slabStoneSmoothTop = 0+8,
		slabStoneBrickBottom = 5,
		slabStoneBrickTop = 5+8,
		
		cobbleWallNormal = 0,
		cobbleWallMossy = 1,
		
		skullGround = 1;
	
	public static byte getTorch(Facing4 attachedTo){
		switch(attachedTo){
			case WEST_NEGX: return 1;
			case EAST_POSX: return 2;
			case NORTH_NEGZ: return 3;
			case SOUTH_POSZ: return 4;
			default: return 0;
		}
	}
	
	public static byte getButton(Facing4 attachedTo){
		switch(attachedTo){
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
	
	public enum BlockColor{ WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK }
	
	public static byte getColor(BlockColor color){
		return (byte)color.ordinal();
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
	
	public enum Skull { SKELETON, WITHER, ZOMBIE, PLAYER, CREEPER }
	
	public static IStructureTileEntity generateSkullGround(final Skull skullType, Pos placedAt, Pos looksAt){
		final Pos placedAtCopy = Pos.at(placedAt);
		final Pos looksAtCopy = Pos.at(looksAt);
		
		return (tile, rand) -> {
			TileEntitySkull skull = (TileEntitySkull)tile;
			skull.func_152107_a(skullType.ordinal()); // OBFUSCATED setSkullType
			
			double deg = 67.5D+MathUtil.toDeg(Math.atan2(looksAtCopy.getZ()-placedAtCopy.getZ(),looksAtCopy.getX()-placedAtCopy.getX()));
			if (deg < 0D)deg += 360D;
			
			skull.func_145903_a((1+MathUtil.floor(deg*16D/360D))&15); // OBFUSCATED setSkullRotation
		};
	}
	
	private Meta(){}
}
