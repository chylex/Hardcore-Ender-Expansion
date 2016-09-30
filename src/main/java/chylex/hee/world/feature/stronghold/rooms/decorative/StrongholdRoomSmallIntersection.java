package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomSmallIntersection extends StrongholdRoom{
	public StrongholdRoomSmallIntersection(){
		super(new Size(9, 6, 9));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		
		PosMutable mpos = new PosMutable();
		
		for(Facing4 facing:Facing4.list){
			Facing4 left = facing.rotateLeft(), right = facing.rotateRight();
			
			IBlockPicker placeStairRight = placeStoneBrickStairs(right, true);
			IBlockPicker placeStairFront = placeStoneBrickStairs(facing, true);
			
			// top stairs and brick blocks
			mpos.set(x+maxX/2, 0, z+maxZ/2).move(facing, 3);
			placeLine(world, rand, placeStoneBrickStairs(facing, true), mpos.x+left.getX(), y+4, mpos.z+left.getZ(), mpos.x+right.getX(), y+4, mpos.z+right.getZ());
			
			mpos.move(right, 2);
			placeBlock(world, rand, placeStoneBrickPlain, mpos.x, y+4, mpos.z);
			mpos.move(right);
			placeBlock(world, rand, placeStoneBrickPlain, mpos.x, y+4, mpos.z);
			mpos.move(right.rotateRight());
			placeBlock(world, rand, placeStoneBrickPlain, mpos.x, y+4, mpos.z);
			
			// corner column
			mpos.set(x+maxX/2, 0, z+maxZ/2).move(facing, 3).move(right, 3);
			placeBlock(world, rand, placeStoneBrickPlain, mpos.x, y+3, mpos.z);
			placeBlock(world, rand, placeStairRight, mpos.x, y+2, mpos.z);
			placeBlock(world, rand, placeStairRight, mpos.x, y+1, mpos.z);
			
			// next-to-corner
			mpos.set(x+maxX/2, 0, z+maxZ/2).move(facing, 3).move(right, 2);
			placeBlock(world, rand, placeStairFront, mpos.x, y+1, mpos.z);
			placeBlock(world, rand, placeStairFront, mpos.x, y+2, mpos.z);
			
			mpos.move(right).move(right.rotateRight());
			placeBlock(world, rand, placeStairRight, mpos.x, y+1, mpos.z);
			placeBlock(world, rand, placeStairRight, mpos.x, y+2, mpos.z);
			
			placeBlock(world, rand, placeStairFront, mpos.x, y+3, mpos.z);
			mpos.move(left);
			placeBlock(world, rand, placeStairFront, mpos.x, y+3, mpos.z);
			world.setAttentionWhore(mpos.x, y+4, mpos.z, new BlockInfo(Blocks.torch, Meta.torchGround));
			mpos.move(facing);
			placeBlock(world, rand, placeStairRight, mpos.x, y+3, mpos.z);
		}
	}
}
