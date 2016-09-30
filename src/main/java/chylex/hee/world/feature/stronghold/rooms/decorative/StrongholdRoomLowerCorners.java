package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomLowerCorners extends StrongholdRoom{
	public StrongholdRoomLowerCorners(){
		super(new Size(9, 8, 9));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		
		// corners
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				int px = x+2+4*cornerX, pz = z+2+4*cornerZ;
				placeBlock(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneSmoothBottom), px, y, pz);
				placeStairOutline(world, rand, Blocks.stone_brick_stairs, px, y, pz, 1, false, false);
			}
		}
		
		// ceiling
		final int centerX = x+maxX/2, centerZ = z+maxZ/2, topY = y+maxY-1;
		placeBlock(world, rand, placeStoneBrickPlain, centerX, topY, centerZ);
		
		for(Facing4 facing:Facing4.list){
			placeLine(world, rand, placeStoneBrickPlain, centerX+facing.getX(), topY, centerZ+facing.getZ(), centerX+3*facing.getX(), topY, centerZ+3*facing.getZ());
		}
		
		PosMutable mpos = new PosMutable();
		
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX, 0, centerZ).move(facing, 3).move(facing = facing.rotateRight());
			facing = facing.rotateRight();
			placeLine(world, rand, placeStoneBrickStairs(facing.rotateRight(), true), mpos.x, topY, mpos.z, mpos.x+2*facing.getX(), topY, mpos.z+2*facing.getZ());
			
			mpos.move(facing, 2).move(facing = facing.rotateLeft());
			placeLine(world, rand, placeStoneBrickStairs(facing.rotateRight(), true), mpos.x, topY, mpos.z, mpos.x+facing.getX(), topY, mpos.z+facing.getZ());
		}
	}
}
