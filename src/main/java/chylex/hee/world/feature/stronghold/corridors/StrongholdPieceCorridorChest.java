package chylex.hee.world.feature.stronghold.corridors;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceCorridorChest extends StrongholdPieceCorridorEmbedded{
	private static final byte variationCount = 5;
	
	public static StrongholdPieceCorridorChest[] generateCorridors(){
		StrongholdPieceCorridorChest[] corridors = new StrongholdPieceCorridorChest[variationCount*2];
		
		for(int variation = 0; variation < variationCount; variation++){
			corridors[variation*2] = new StrongholdPieceCorridorChest(true,(byte)variation);
			corridors[variation*2+1] = new StrongholdPieceCorridorChest(false,(byte)variation);
		}
		
		return corridors;
	}
	
	private byte variation;
	
	public StrongholdPieceCorridorChest(boolean dirX, byte variation){
		super(dirX,new Size(dirX ? 5 : 7,5,dirX ? 7 : 5));
		this.variation = variation;
		if (variation < 0 || variation >= variationCount)throw new IllegalArgumentException("Invalid StrongholdPieceCorridorChest variation: "+variation);
	}
	
	@Override
	protected void generateEmbedded(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// basic logic
		Facing4 dir = dirX ? (rand.nextBoolean() ? Facing4.NORTH_NEGZ : Facing4.SOUTH_POSZ) : (rand.nextBoolean() ? Facing4.EAST_POSX : Facing4.WEST_NEGX);
		Facing4 perpendicular = dir.perpendicular(), left = dir.rotateLeft(), right = dir.rotateRight();

		placeOutsideWall(world,rand,x,y,z,dir,3);
		placeOutsideWall(world,rand,x,y,z,dir.opposite(),2);
		
		x = x+maxX/2+dir.getX()*2;
		z = z+maxZ/2+dir.getZ()*2;
		
		// chest
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),x,y+2,z);
		
		world.setTileEntity(x,y+2,z,Meta.generateChest(dir.opposite(),(tile, random) -> {
			
		}));
		
		// top slabs that are shared across all patterns
		placeCube(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x-perpendicular.getX(),y+3,z-perpendicular.getZ(),x+perpendicular.getX(),y+3,z+perpendicular.getZ());
		
		// pattern
		switch(variation){
			case 0:
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x,y+1,z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(left,true)),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(right,true)),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 1:
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(dir,true)),x,y+1,z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(right,false)),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(left,false)),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 2:
				placeBlock(world,rand,placeStoneBrickPlain,x,y+1,z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(right,false)),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(left,false)),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 3:
				placeBlock(world,rand,placeStoneBrickPlain,x,y+1,z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(dir,true)),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(dir,true)),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 4:
				placeBlock(world,rand,placeStoneBrickPlain,x,y+1,z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),x+left.getX(),y+2,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+right.getX(),y+1,z+right.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),x+right.getX(),y+2,z+right.getZ());
				break;
		}
	}
}
