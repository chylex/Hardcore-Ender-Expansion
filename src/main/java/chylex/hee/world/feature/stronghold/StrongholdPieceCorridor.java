package chylex.hee.world.feature.stronghold;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceCorridor extends StrongholdPiece{
	public StrongholdPieceCorridor(int weight){
		super(weight,0,20,new Size(5,5,3));
		addConnection(new Connection(Facing4.NORTH_NEGZ,0,0,2));
		addConnection(new Connection(Facing4.SOUTH_POSZ,2,0,2));
	}

	@Override
	public void generate(StructureWorld world, Random rand, int x, int y, int z){
		placeCube(world,rand,placeStoneBrick,x,y,z,x+size.sizeX-1,y,z+size.sizeZ-1);
	}
}
