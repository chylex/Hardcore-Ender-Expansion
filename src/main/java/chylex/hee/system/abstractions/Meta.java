package chylex.hee.system.abstractions;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntitySkull;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.IStructureTileEntity;
import chylex.hee.world.structure.util.Facing4;

public final class Meta{
	/* === SIMPLE BLOCKS === */
	
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
		
		skullGround = 1,
		torchGround = 5;
	
	/* === TORCHES === */
	
	public static byte getTorch(Facing4 attachedTo){
		switch(attachedTo){
			case WEST_NEGX: return 1;
			case EAST_POSX: return 2;
			case NORTH_NEGZ: return 3;
			case SOUTH_POSZ: return 4;
			default: return 0;
		}
	}
	
	/* === BUTTONS === */
	
	public static byte getButton(Facing4 attachedTo){
		switch(attachedTo){
			case WEST_NEGX: return 1;
			case EAST_POSX: return 2;
			case NORTH_NEGZ: return 3;
			case SOUTH_POSZ: return 4;
			default: return 0;
		}
	}
	
	/* === LADDERS === */
	
	public static byte getLadder(Facing4 attachedTo){
		switch(attachedTo){
			case SOUTH_POSZ: return 2;
			case NORTH_NEGZ: return 3;
			case EAST_POSX: return 4;
			case WEST_NEGX: return 5;
			default: return 0;
		}
	}
	
	/* === STAIRS === */
	
	public static byte getStairs(Facing4 ascendsTowards, boolean flip){
		switch(ascendsTowards){
			case EAST_POSX: return (byte)(0+(flip ? 4 : 0));
			case WEST_NEGX: return (byte)(1+(flip ? 4 : 0));
			case SOUTH_POSZ: return (byte)(2+(flip ? 4 : 0));
			case NORTH_NEGZ: return (byte)(3+(flip ? 4 : 0));
			default: return 0;
		}
	}
	
	/* === DOORS === */
	
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
	
	/* === DYES AND COLORED BLOCKS === */
	
	public enum BlockColor { WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK }
	
	public static byte getColor(BlockColor color){
		return (byte)color.ordinal();
	}
	
	public static byte getDye(BlockColor color){
		return (byte)(15-color.ordinal());
	}
	
	/* === LOGS === */
	
	public enum LogType { OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK }
	
	public enum LogDirection { PILLAR, EAST_WEST, NORTH_SOUTH }
	
	public static BlockInfo getLog(LogType type, Facing4 facing){
		return getLog(type,(facing == Facing4.EAST_POSX || facing == Facing4.WEST_NEGX) ? LogDirection.EAST_WEST :
						   (facing == Facing4.NORTH_NEGZ || facing == Facing4.SOUTH_POSZ) ? LogDirection.NORTH_SOUTH :
						   LogDirection.PILLAR);
	}
	
	public static BlockInfo getLog(LogType type, LogDirection direction){
		final Block log;
		int meta;
		
		switch(type){
			case OAK: case SPRUCE: case BIRCH: case JUNGLE:
				log = Blocks.log;
				meta = type.ordinal();
				break;
				
			case ACACIA: case DARK_OAK: default:
				log = Blocks.log2;
				meta = type.ordinal()-LogType.ACACIA.ordinal();
				break;
		}
		
		switch(direction){
			case PILLAR: break;
			case EAST_WEST: meta += 4; break;
			case NORTH_SOUTH: meta += 8; break;
		}
		
		return new BlockInfo(log,meta);
	}
	
	/* === FLOWER POTS === */
	
	public enum FlowerPotPlant{
		DANDELION, POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, TULIP_RED, TULIP_ORANGE, TULIP_WHITE, TULIP_PINK, OXEYE_DAISY,
		SAPLING_OAK, SAPLING_SPRUCE, SAPLING_BIRCH, SAPLING_JUNGLE, SAPLING_ACACIA, SAPLING_DARK_OAK,
		MUSHROOM_RED, MUSHROOM_BROWN, CACTUS, DEAD_BUSH, FERN
	}
	
	public static IStructureTileEntity generateFlowerPot(final FlowerPotPlant plant){
		return (tile, rand) -> {
			final Block block;
			int meta = 0;
			
			switch(plant){
				default:
				case DANDELION: block = Blocks.yellow_flower; break;
				case MUSHROOM_RED: block = Blocks.red_mushroom; break;
				case MUSHROOM_BROWN: block = Blocks.brown_mushroom; break;
				case CACTUS: block = Blocks.cactus; break;
				case DEAD_BUSH: block = Blocks.deadbush; break;
				
				case POPPY: case BLUE_ORCHID: case ALLIUM: case AZURE_BLUET: case OXEYE_DAISY:
				case TULIP_RED: case TULIP_ORANGE: case TULIP_WHITE: case TULIP_PINK:
					block = Blocks.red_flower;
					meta = plant.ordinal()-FlowerPotPlant.POPPY.ordinal();
					break;
				
				case SAPLING_OAK: case SAPLING_SPRUCE: case SAPLING_BIRCH: case SAPLING_JUNGLE: case SAPLING_ACACIA: case SAPLING_DARK_OAK:
					block = Blocks.sapling;
					meta = plant.ordinal()-FlowerPotPlant.SAPLING_OAK.ordinal();
					break;
				
				case FERN:
					block = Blocks.tallgrass;
					meta = 2;
					break;
			}
			
			((TileEntityFlowerPot)tile).func_145964_a(Item.getItemFromBlock(block),meta);
		};
	}
	
	/* === CHESTS === */
	
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
	
	/* === SKULLS AND HEADS === */
	
	public enum Skull { SKELETON, WITHER, ZOMBIE, PLAYER, CREEPER }
	
	public static IStructureTileEntity generateSkullGround(final Skull skullType, Pos placedAt, Pos looksAt){
		final Pos placedAtCopy = Pos.at(placedAt);
		final Pos looksAtCopy = Pos.at(looksAt);
		
		return (tile, rand) -> {
			TileEntitySkull skull = (TileEntitySkull)tile;
			skull.func_152107_a(skullType.ordinal()); // OBFUSCATED setSkullType
			
			double deg = 78.75D+MathUtil.toDeg(Math.atan2(looksAtCopy.getZ()-placedAtCopy.getZ(),looksAtCopy.getX()-placedAtCopy.getX()));
			if (deg < 0D)deg += 360D;
			
			skull.func_145903_a((1+MathUtil.floor(deg*16D/360D))&15); // OBFUSCATED setSkullRotation
		};
	}
	
	private Meta(){}
}
