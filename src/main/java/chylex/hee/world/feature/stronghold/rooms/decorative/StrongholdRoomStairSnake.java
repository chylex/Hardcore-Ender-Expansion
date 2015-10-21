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

public class StrongholdRoomStairSnake extends StrongholdRoom{
	public StrongholdRoomStairSnake(){
		super(new Size(7,7,7));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// floor pattern
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y,centerZ,1,true,false);
		
		// snake
		Facing4 snakeFacing = Facing4.list[rand.nextInt(Facing4.list.length)];
		
		for(int snake = 0; snake < 3; snake++){
			placeBlock(world,rand,placeStoneBrickStairs(snakeFacing = snakeFacing.opposite(),false),centerX,y+1+snake,centerZ);
		}
		
		// top of the snake
		placeBlock(world,rand,placeStoneBrickPlain,centerX,y+4,centerZ);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.torch,Meta.torchGround),centerX,y+5,centerZ);
		
		for(Facing4 facing:Facing4.list){
			placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),centerX+facing.getX(),y+4,centerZ+facing.getZ());
			placeBlock(world,rand,placeStoneBrickStairs(facing,false),centerX+2*facing.getX(),y+5,centerZ+2*facing.getZ());
		}
		
		// cobwebs
		PosMutable webPos = new PosMutable();
		
		for(int attempt = 0; attempt < 5; attempt++){
			webPos.set(x+1+rand.nextInt(5),y+maxY-1-rand.nextInt(2),z+1+rand.nextInt(5));
			if (world.isAir(webPos.x,webPos.y,webPos.z))placeBlock(world,rand,placeAncientWeb,webPos.x,webPos.y,webPos.z);
		}
	}
}
