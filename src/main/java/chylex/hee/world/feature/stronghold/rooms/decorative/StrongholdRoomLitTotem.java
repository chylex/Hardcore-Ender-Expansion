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
import chylex.hee.world.util.Size;

public class StrongholdRoomLitTotem extends StrongholdRoom{
	public StrongholdRoomLitTotem(){
		super(new Size(9, 7, 9));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable(centerX, 0, centerZ);
		
		// floor patterns
		placeBlock(world, rand, placeStoneBrickChiseled, centerX, y, centerZ);
		
		placeStairOutline(world, rand, Blocks.stone_brick_stairs, centerX, y, centerZ, 1, true, false);
		
		if (rand.nextInt(3) == 0){
			for(Facing4 facing:Facing4.list){
				Facing4 left = facing.rotateLeft(), right = facing.rotateRight();
				
				mpos.set(centerX, 0, centerZ).move(facing, 2);
				placeBlock(world, rand, placeStoneBrickStairs(facing.opposite(), false), mpos.x, y, mpos.z);
				placeBlock(world, rand, placeStoneBrickStairs(left, false), mpos.x+left.getX(), y, mpos.z+left.getZ());
				placeBlock(world, rand, placeStoneBrickStairs(right, false), mpos.x+right.getX(), y, mpos.z+right.getZ());
				
				mpos.move(facing);
				placeBlock(world, rand, placeStoneBrickStairs(left, false), mpos.x+left.getX(), y, mpos.z+left.getZ());
				placeBlock(world, rand, placeStoneBrickStairs(right, false), mpos.x+right.getX(), y, mpos.z+right.getZ());
			}
		}
		
		// totem
		placeLine(world, rand, placeStoneBrickWall, centerX, y+1, centerZ, centerX, y+5, centerZ);
		
		for(Facing4 facing:Facing4.list){
			placeBlock(world, rand, placeStoneBrickWall, centerX+facing.getX(), y+3, centerZ+facing.getZ());
			world.setAttentionWhore(centerX+facing.getX(), y+4, centerZ+facing.getZ(), new BlockInfo(Blocks.torch, Meta.torchGround));
		}
	}
}
