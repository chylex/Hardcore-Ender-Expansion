package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.BlockColor;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomLitCorners extends StrongholdRoom{
	public static StrongholdRoomLitCorners[] generateColors(){
		return Arrays.stream(new BlockColor[]{
			BlockColor.WHITE, BlockColor.LIGHT_GRAY, BlockColor.BLACK, BlockColor.PURPLE, BlockColor.MAGENTA
		}).map(color -> new StrongholdRoomLitCorners(color)).toArray(StrongholdRoomLitCorners[]::new);
	}
	
	private final BlockColor glassColor;
	
	public StrongholdRoomLitCorners(BlockColor glassColor){
		super(new Size(9,7,9));
		this.glassColor = glassColor;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// floor pattern
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),centerX,y,centerZ);
		for(Facing4 facing:Facing4.list)placeBlock(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),centerX+facing.getX(),y,centerZ+facing.getZ());
		
		// corner lights
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				mpos.set(x+1+6*cornerX,0,z+1+6*cornerZ);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.glowstone),mpos.x,y+1,mpos.z); // light TODO change block
				placeBlock(world,rand,IBlockPicker.basic(Blocks.glowstone),mpos.x,y+maxY-1,mpos.z); // light TODO change block
				placeLine(world,rand,IBlockPicker.basic(Blocks.stained_glass,Meta.getColor(glassColor)),mpos.x,y+2,mpos.z,mpos.x,y+maxY-2,mpos.z); // glass
			}
		}
		
		for(Facing4 facingCycle:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facingCycle,3).move(facingCycle.rotateRight(),2);
			placeBlock(world,rand,placeStoneBrickStairs(facingCycle.rotateRight(),false),mpos.x,y+1,mpos.z); // bottom
			placeBlock(world,rand,placeStoneBrickStairs(facingCycle.rotateRight(),true),mpos.x,y+maxY-1,mpos.z); // top
			
			mpos.move(facingCycle.opposite());
			placeBlock(world,rand,placeStoneBrickStairs(facingCycle.rotateRight(),false),mpos.x,y+1,mpos.z); // bottom
			placeBlock(world,rand,placeStoneBrickStairs(facingCycle.rotateRight(),true),mpos.x,y+maxY-1,mpos.z); // top
			
			mpos.move(facingCycle.rotateRight());
			placeBlock(world,rand,placeStoneBrickStairs(facingCycle,false),mpos.x,y+1,mpos.z); // bottom
			placeBlock(world,rand,placeStoneBrickStairs(facingCycle,true),mpos.x,y+maxY-1,mpos.z); // top
		}
	}
}
