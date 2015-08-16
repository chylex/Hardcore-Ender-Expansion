package chylex.hee.world.feature.stronghold.corridors;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceStairsStraight extends StrongholdPiece{
	public static StrongholdPieceStairsStraight[] generateStairs(){
		return new StrongholdPieceStairsStraight[]{
			new StrongholdPieceStairsStraight(Facing4.EAST_POSX),
			new StrongholdPieceStairsStraight(Facing4.WEST_NEGX),
			new StrongholdPieceStairsStraight(Facing4.NORTH_NEGZ),
			new StrongholdPieceStairsStraight(Facing4.SOUTH_POSZ)
		};
	}
	
	private final Facing4 ascendsTo;
	
	public StrongholdPieceStairsStraight(Facing4 ascendsTo){
		super(Type.CORRIDOR,new Size(ascendsTo.getX() != 0 ? 8 : 5,10,ascendsTo.getX() != 0 ? 5 : 8));
		
		if (ascendsTo.getX() != 0){
			addConnection(Facing4.EAST_POSX,7,5,2,withAnything);
			addConnection(Facing4.WEST_NEGX,0,0,2,withAnything);
		}
		else{
			addConnection(Facing4.NORTH_NEGZ,2,0,0,withAnything);
			addConnection(Facing4.SOUTH_POSZ,2,5,7,withAnything);
		}
		
		this.ascendsTo = ascendsTo;
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// basic layout
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeWalls(world,rand,placeStoneBrick,x,y+1,z,x+maxX,y+maxY-1,z+maxZ);
		
		// stairs
		PosMutable stairPos = new PosMutable(x+(ascendsTo.getX() != 0 ? 1 : 2),y+1,z+(ascendsTo.getZ() != 0 ? 1 : 2));
		Facing4 perpendicular = ascendsTo.rotateRight();
		
		IBlockPicker placeStairs = IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(ascendsTo,false));
		IBlockPicker placeStairsRev = IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(ascendsTo.opposite(),true));
		
		for(int level = 0; level < 6; level++){
			stairPos.move(Math.abs(ascendsTo.getX()),1,Math.abs(ascendsTo.getZ())).move(perpendicular,-1);
			
			for(int stair = 0; stair < 3; stair++){
				placeBlock(world,rand,placeStairs,stairPos.x,stairPos.y,stairPos.z); // floor stairs
				placeBlock(world,rand,placeStairsRev,stairPos.x-Math.abs(ascendsTo.getX()),stairPos.y+3,stairPos.z-Math.abs(ascendsTo.getZ())); // ceiling stairs
				stairPos.move(perpendicular,1);
			}
			
			stairPos.move(perpendicular,-1);
		}
		
		// holes
		if (ascendsTo.getX() != 0){
			placeCube(world,rand,placeAir,x+1,y+1,z,x+3,y+3,z);
			placeCube(world,rand,placeAir,x+1,y+6,z+maxZ,x+3,y+9,z+maxZ);
		}
		else{
			placeCube(world,rand,placeAir,x,y+1,z+1,x,y+3,z+3);
			placeCube(world,rand,placeAir,x+maxX,y+6,z+1,x+maxX,y+9,z+3);
		}
	}
}
