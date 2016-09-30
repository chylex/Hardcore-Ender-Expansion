package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.util.IBlockPicker;

/**
 * Base class for structure pieces, used to provide utility fields and methods for worldgen.
 */
public abstract class StructurePiece{
	protected static final IBlockPicker
		placeAir = BlockInfo.air,
		placeChest = new BlockInfo(Blocks.chest),
		placeWater = new BlockInfo(Blocks.flowing_water),
		placeLava = new BlockInfo(Blocks.flowing_lava),
		placeStoneBrickWall = new BlockInfo(BlockList.stone_brick_wall),
		placeFlowerPot = new BlockInfo(Blocks.flower_pot),
		placeAncientWeb = new BlockInfo(BlockList.ancient_web),
		placeEtherealLantern = new BlockInfo(BlockList.ethereal_lantern),
		placeIronBars = new BlockInfo(Blocks.iron_bars),
		placeObsidian = new BlockInfo(Blocks.obsidian);
	
	protected static final void placeBlock(StructureWorld world, Random rand, IBlockPicker picker, int x, int y, int z){
		world.setBlock(x, y, z, picker.pick(rand));
	}
	
	protected static final void placeCube(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		int xMin = Math.min(x1, x2), xMax = Math.max(x1, x2);
		int yMin = Math.min(y1, y2), yMax = Math.max(y1, y2);
		int zMin = Math.min(z1, z2), zMax = Math.max(z1, z2);
		
		for(int x = xMin; x <= xMax; x++){
			for(int y = yMin; y <= yMax; y++){
				for(int z = zMin; z <= zMax; z++){
					world.setBlock(x, y, z, picker.pick(rand));
				}
			}
		}
	}
	
	protected static final void placeOutline(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2, int insideThickness){
		int xMin = Math.min(x1, x2), xMax = Math.max(x1, x2);
		int zMin = Math.min(z1, z2), zMax = Math.max(z1, z2);
		
		for(int y = Math.min(y1, y2), yMax = Math.max(y1, y2); y <= yMax; y++){
			for(int level = 0; level < insideThickness; level++){
				for(int x = xMin+level; x <= xMax-level; x++){
					world.setBlock(x, y, zMin+level, picker.pick(rand));
					world.setBlock(x, y, zMax-level, picker.pick(rand));
				}
				
				for(int z = zMin+1+level; z <= zMax-1-level; z++){
					world.setBlock(xMin+level, y, z, picker.pick(rand));
					world.setBlock(xMax-level, y, z, picker.pick(rand));
				}
			}
		}
	}
	
	protected static final void placeLine(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		if (x1 == x2 || z1 == z2){
			for(int y = Math.min(y1, y2), yMax = Math.max(y1, y2); y <= yMax; y++){
				if (x1 == x2){
					for(int z = Math.min(z1, z2), zMax = Math.max(z1, z2); z <= zMax; z++)world.setBlock(x1, y, z, picker.pick(rand));
				}
				else{
					for(int x = Math.min(x1, x2), xMax = Math.max(x1, x2); x <= xMax; x++)world.setBlock(x, y, z1, picker.pick(rand));
				}
			}
		}
		else throw new IllegalArgumentException("Lines can only be generated on one axis: "+x1+", "+z1+" - "+x2+", "+z2);
	}
	
	protected static final void placeWalls(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		for(int y = Math.min(y1, y2), yMax = Math.max(y1, y2); y <= yMax; y++){
			for(int x = Math.min(x1, x2), xMax = Math.max(x1, x2); x <= xMax; x++){
				world.setBlock(x, y, z1, picker.pick(rand));
				world.setBlock(x, y, z2, picker.pick(rand));
			}
			
			for(int z = Math.min(z1, z2)+1, zMax = Math.max(z1, z2)-1; z <= zMax; z++){
				world.setBlock(x1, y, z, picker.pick(rand));
				world.setBlock(x2, y, z, picker.pick(rand));
			}
		}
	}
	
	protected static final void placeStairOutline(StructureWorld world, Random rand, Block block, int centerX, int y, int centerZ, int distance, boolean outwards, boolean flip){
		IBlockPicker[] stairs = new IBlockPicker[]{
			IBlockPicker.basic(block, Meta.getStairs(outwards ? Facing4.SOUTH_POSZ : Facing4.NORTH_NEGZ, flip)),
			IBlockPicker.basic(block, Meta.getStairs(outwards ? Facing4.NORTH_NEGZ : Facing4.SOUTH_POSZ, flip)),
			IBlockPicker.basic(block, Meta.getStairs(outwards ? Facing4.EAST_POSX : Facing4.WEST_NEGX, flip)),
			IBlockPicker.basic(block, Meta.getStairs(outwards ? Facing4.WEST_NEGX : Facing4.EAST_POSX, flip))
		};
		
		for(int facingInd = 0, off, perX, perZ; facingInd < Facing4.list.length; facingInd++){
			Facing4 facing = Facing4.list[facingInd];
			off = facing.getX() == 0 ? distance-1 : distance;
			perX = facing.perpendicular().getX();
			perZ = facing.perpendicular().getZ();
			placeLine(world, rand, stairs[facingInd], centerX+distance*facing.getX()-off*perX, y, centerZ+distance*facing.getZ()-off*perZ, centerX+distance*facing.getX()+off*perX, y, centerZ+distance*facing.getZ()+off*perZ);
		}
	}
}
