package chylex.hee.world.feature.stronghold.corridors;
import java.util.Arrays;
import java.util.Random;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdStairsStraight extends StrongholdPiece{
	public static StrongholdStairsStraight[] generateStairs(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdStairsStraight(facing)).toArray(StrongholdStairsStraight[]::new);
	}
	
	private final Facing4 ascendsTo;
	
	public StrongholdStairsStraight(Facing4 ascendsTo){
		super(Type.CORRIDOR, new Size(ascendsTo.getX() != 0 ? 8 : 5, 10, ascendsTo.getZ() != 0 ? 8 : 5));
		
		if (ascendsTo.getX() != 0){
			addConnection(Facing4.WEST_NEGX, 0, ascendsTo == Facing4.WEST_NEGX ? 5 : 0, 2, withAnything);
			addConnection(Facing4.EAST_POSX, 7, ascendsTo == Facing4.EAST_POSX ? 5 : 0, 2, withAnything);
		}
		else if (ascendsTo.getZ() != 0){
			addConnection(Facing4.NORTH_NEGZ, 2, ascendsTo == Facing4.NORTH_NEGZ ? 5 : 0, 0, withAnything);
			addConnection(Facing4.SOUTH_POSZ, 2, ascendsTo == Facing4.SOUTH_POSZ ? 5 : 0, 7, withAnything);
		}
		
		this.ascendsTo = ascendsTo;
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// basic layout
		placeCube(world, rand, placeStoneBrick, x, y, z, x+maxX, y, z+maxZ);
		placeCube(world, rand, placeStoneBrick, x, y+maxY, z, x+maxX, y+maxY, z+maxZ);
		placeWalls(world, rand, placeStoneBrick, x, y+1, z, x+maxX, y+maxY-1, z+maxZ);
		
		// stairs
		Facing4 perpendicular = ascendsTo.perpendicular();
		Connection start = connections.stream().filter(connection -> connection.facing == ascendsTo.opposite()).findFirst().get();
		
		PosMutable stairPos = new PosMutable(x+start.offsetX, y+start.offsetY, z+start.offsetZ).move(ascendsTo.getX(), 0, ascendsTo.getZ());
		boolean up = start.offsetY == 0;
		
		IBlockPicker placeStairs = placeStoneBrickStairs(ascendsTo, false);
		IBlockPicker placeStairsRev = placeStoneBrickStairs(ascendsTo.opposite(), true);
		
		for(int level = 0; level < 6; level++){
			stairPos.move(ascendsTo.getX(), up ? 1 : -1, ascendsTo.getZ()).move(perpendicular, -2);
			
			for(int stair = 0; stair < 3; stair++){
				stairPos.move(perpendicular, 1);
				placeBlock(world, rand, placeStairs, stairPos.x, stairPos.y, stairPos.z); // floor stairs
				if (level < 5)placeBlock(world, rand, placeStairsRev, stairPos.x-ascendsTo.getX()*2, stairPos.y+3, stairPos.z-ascendsTo.getZ()*2); // ceiling stairs
			}
			
			stairPos.move(perpendicular, -1);
		}
		
		// holes
		PosMutable mpos = new PosMutable();
		
		for(Connection connection:connections){
			if (!inst.isConnectionFree(connection)){
				int perX = connection.facing.perpendicular().getX(), perZ = connection.facing.perpendicular().getZ();
				
				mpos.set(x+connection.offsetX, y+connection.offsetY, z+connection.offsetZ);
				placeCube(world, rand, placeAir, mpos.x-perX, mpos.y+1, mpos.z-perZ, mpos.x+perX, mpos.y+3, mpos.z+perZ);
			}
		}
	}
}
