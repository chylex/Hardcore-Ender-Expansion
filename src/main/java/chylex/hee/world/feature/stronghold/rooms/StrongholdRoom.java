package chylex.hee.world.feature.stronghold.rooms;
import java.util.Random;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public abstract class StrongholdRoom extends StrongholdPiece{
	public StrongholdRoom(Size size){
		super(Type.ROOM,size);
		
		addConnection(Facing4.NORTH_NEGZ,maxX/2,0,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,0,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,0,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,0,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		PosMutable mpos = new PosMutable();
		
		for(Facing4 facing:Facing4.list){
			if (!inst.isConnectionFree(facing)){
				int perX = facing.perpendicular().getX(), perZ = facing.perpendicular().getZ();
				
				mpos.set(x+maxX/2,y+1,z+maxZ/2).move(facing,6);
				placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ);
			}
		}
	}
}
