package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomLitPole extends StrongholdRoom{
	public StrongholdRoomLitPole(){
		super(new Size(11, 7, 11));
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// basic layout
		placeOutline(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneSmoothBottom), centerX-1, y+1, centerZ-1, centerX+1, y+1, centerZ+1, 1);
		placeLine(world, rand, placeStoneBrickPlain, centerX, y+1, centerZ, centerX, y+3, centerZ);
		
		// torches around the pole
		for(Facing4 facing:Facing4.list)world.setAttentionWhore(centerX+facing.getX(), y+2, centerZ+facing.getZ(), new BlockInfo(Blocks.torch, Meta.getTorch(facing.opposite())));
	}
}
