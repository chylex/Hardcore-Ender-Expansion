package chylex.hee.world.feature.stronghold.doors;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;

public class StrongholdDoorGrates extends StrongholdDoor{
	public static StrongholdDoorGrates[] generateDoors(){
		return new StrongholdDoorGrates[]{
			new StrongholdDoorGrates(Facing4.EAST_POSX),
			new StrongholdDoorGrates(Facing4.SOUTH_POSZ)
		};
	}
	
	public StrongholdDoorGrates(Facing4 facing){
		super(facing);
	}

	@Override
	protected void generateDoor(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		PosMutable archPos = new PosMutable(x+maxX/2,0,z+maxZ/2);
		Facing4 perpendicular = facing.perpendicular();
		IBlockPicker placeIronBars = IBlockPicker.basic(Blocks.iron_bars);
		
		archPos.move(perpendicular,-1);
		placeLine(world,rand,placeIronBars,archPos.x,y+1,archPos.z,archPos.x,y+3,archPos.z);
		archPos.move(perpendicular,1);
		placeBlock(world,rand,placeIronBars,archPos.x,y+3,archPos.z);
		archPos.move(perpendicular,1);
		placeLine(world,rand,placeIronBars,archPos.x,y+1,archPos.z,archPos.x,y+3,archPos.z);
	}
}
