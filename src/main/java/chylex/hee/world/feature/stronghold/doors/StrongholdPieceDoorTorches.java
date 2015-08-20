package chylex.hee.world.feature.stronghold.doors;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;

public class StrongholdPieceDoorTorches extends StrongholdPieceDoor{
	public static StrongholdPieceDoorTorches[] generateDoors(){
		return new StrongholdPieceDoorTorches[]{
			new StrongholdPieceDoorTorches(Facing4.EAST_POSX),
			new StrongholdPieceDoorTorches(Facing4.SOUTH_POSZ)
		};
	}
	
	public StrongholdPieceDoorTorches(Facing4 facing){
		super(facing);
	}

	@Override
	protected void generateDoor(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		PosMutable mpos = new PosMutable(x+maxX/2,y+3,z+maxZ/2);
		
		mpos.move(facing.rotateLeft());
		world.setAttentionWhore(mpos.x,mpos.y,mpos.z,new BlockInfo(Blocks.torch,Meta.getTorch(facing.rotateLeft())));
		
		mpos.move(facing.rotateRight(),2);
		world.setAttentionWhore(mpos.x,mpos.y,mpos.z,new BlockInfo(Blocks.torch,Meta.getTorch(facing.rotateRight())));
	}
}
