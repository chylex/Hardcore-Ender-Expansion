package chylex.hee.world.feature.stronghold.rooms;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Size;

public class StrongholdRoomLargeIntersection extends StrongholdRoom{
	public StrongholdRoomLargeIntersection(){
		super(new Size(13,7,13));
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		placeCube(world,rand,placeStoneBrick,x+1,y+1,z+1,x+3,y+maxY-1,z+3);
		placeCube(world,rand,placeStoneBrick,x+maxX-3,y+1,z+1,x+maxX-1,y+maxY-1,z+3);
		placeCube(world,rand,placeStoneBrick,x+1,y+1,z+maxZ-3,x+3,y+maxY-1,z+maxZ-1);
		placeCube(world,rand,placeStoneBrick,x+maxX-3,y+1,z+maxZ-3,x+maxX-1,y+maxY-1,z+maxZ-1);
	}
}
