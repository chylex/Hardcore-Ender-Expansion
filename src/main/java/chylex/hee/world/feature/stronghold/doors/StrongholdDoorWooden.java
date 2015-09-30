package chylex.hee.world.feature.stronghold.doors;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;

public class StrongholdDoorWooden extends StrongholdDoor{
	public static StrongholdDoorWooden[] generateDoors(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdDoorWooden(facing)).toArray(StrongholdDoorWooden[]::new);
	}
	
	public StrongholdDoorWooden(Facing4 facing){
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
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_door,Meta.getDoor(facing,false)),x+maxX/2,y+1,z+maxZ/2);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_door,Meta.getDoor(facing,true)),x+maxX/2,y+2,z+maxZ/2);
	}
}
