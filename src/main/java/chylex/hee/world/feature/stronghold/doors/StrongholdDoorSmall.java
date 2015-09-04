package chylex.hee.world.feature.stronghold.doors;
import java.util.Random;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;

public class StrongholdDoorSmall extends StrongholdDoor{
	public static StrongholdDoorSmall[] generateDoors(){
		return new StrongholdDoorSmall[]{
			new StrongholdDoorSmall(Facing4.EAST_POSX),
			new StrongholdDoorSmall(Facing4.SOUTH_POSZ)
		};
	}
	
	public StrongholdDoorSmall(Facing4 facing){
		super(facing);
	}

	@Override
	protected void generateDoor(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		PosMutable archPos = new PosMutable(x+maxX/2,0,z+maxZ/2);
		Facing4 perpendicular = facing.perpendicular();
		
		archPos.move(perpendicular,-1);
		placeLine(world,rand,placeStoneBrick,archPos.x,y+1,archPos.z,archPos.x,y+3,archPos.z);
		archPos.move(perpendicular,1);
		placeBlock(world,rand,placeStoneBrick,archPos.x,y+3,archPos.z);
		archPos.move(perpendicular,1);
		placeLine(world,rand,placeStoneBrick,archPos.x,y+1,archPos.z,archPos.x,y+3,archPos.z);
	}
}
