package chylex.hee.world.feature.stronghold.corridors;
import java.util.Random;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Size;

public class StrongholdStairsStraight extends StrongholdPiece{
	public static StrongholdStairsStraight[] generateStairs(){
		return new StrongholdStairsStraight[]{
			new StrongholdStairsStraight(true),
			new StrongholdStairsStraight(false)
		};
	}
	
	private final boolean dirX;
	
	public StrongholdStairsStraight(boolean dirX){
		super(Type.CORRIDOR,new Size(dirX ? 8 : 5,10,dirX ? 5 : 8));
		
		if (dirX){
			addConnection(Facing4.WEST_NEGX,0,0,2,withAnything);
			addConnection(Facing4.EAST_POSX,7,5,2,withAnything);
		}
		else{
			addConnection(Facing4.NORTH_NEGZ,2,0,0,withAnything);
			addConnection(Facing4.SOUTH_POSZ,2,5,7,withAnything);
		}
		
		this.dirX = dirX;
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// basic layout
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeWalls(world,rand,placeStoneBrick,x,y+1,z,x+maxX,y+maxY-1,z+maxZ);
		
		// stairs
		Facing4 ascendsTo = dirX ? Facing4.EAST_POSX : Facing4.SOUTH_POSZ;
		Facing4 perpendicular = ascendsTo.perpendicular();
		PosMutable stairPos = new PosMutable(x+(ascendsTo.getX() != 0 ? 1 : 2),y,z+(ascendsTo.getZ() != 0 ? 1 : 2));
		
		IBlockPicker placeStairs = placeStoneBrickStairs(ascendsTo,false);
		IBlockPicker placeStairsRev = placeStoneBrickStairs(ascendsTo.opposite(),true);
		
		for(int level = 0; level < 6; level++){
			stairPos.move(Math.abs(ascendsTo.getX()),1,Math.abs(ascendsTo.getZ())).move(perpendicular,-2);
			
			for(int stair = 0; stair < 3; stair++){
				stairPos.move(perpendicular,1);
				placeBlock(world,rand,placeStairs,stairPos.x,stairPos.y,stairPos.z); // floor stairs
				if (level < 5)placeBlock(world,rand,placeStairsRev,stairPos.x-Math.abs(ascendsTo.getX()*2),stairPos.y+3,stairPos.z-Math.abs(ascendsTo.getZ()*2)); // ceiling stairs
			}
			
			stairPos.move(perpendicular,-1);
		}
		
		// holes
		if (dirX){
			if (!inst.isConnectionFree(Facing4.WEST_NEGX))placeCube(world,rand,placeAir,x,y+1,z+1,x,y+3,z+3);
			if (!inst.isConnectionFree(Facing4.EAST_POSX))placeCube(world,rand,placeAir,x+maxX,y+6,z+1,x+maxX,y+8,z+3);
		}
		else{
			if (!inst.isConnectionFree(Facing4.NORTH_NEGZ))placeCube(world,rand,placeAir,x+1,y+1,z,x+3,y+3,z);
			if (!inst.isConnectionFree(Facing4.SOUTH_POSZ))placeCube(world,rand,placeAir,x+1,y+6,z+maxZ,x+3,y+8,z+maxZ);
		}
	}
}
