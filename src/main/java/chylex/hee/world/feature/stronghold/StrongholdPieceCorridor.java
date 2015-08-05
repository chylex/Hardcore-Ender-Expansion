package chylex.hee.world.feature.stronghold;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceCorridor extends StrongholdPiece{
	private boolean dirX;
	
	public StrongholdPieceCorridor(boolean dirX){
		super(Type.CORRIDOR,new Size(dirX ? 3 : 5,5,dirX ? 5 : 3));
		
		if (dirX){
			addConnection(Facing4.EAST_POSX,0,0,2);
			addConnection(Facing4.WEST_NEGX,2,0,2);
		}
		else{
			addConnection(Facing4.NORTH_NEGZ,2,0,0);
			addConnection(Facing4.SOUTH_POSZ,2,0,2);
		}
		
		this.dirX = dirX;
	}

	@Override
	public void generate(StructureWorld world, Random rand, int x, int y, int z){
		placeCube(world,rand,placeStoneBrick,x,y,z,x+size.sizeX-1,y,z+size.sizeZ-1);
	}
}
