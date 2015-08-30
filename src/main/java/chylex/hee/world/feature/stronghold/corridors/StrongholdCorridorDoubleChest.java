package chylex.hee.world.feature.stronghold.corridors;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdCorridorDoubleChest extends StrongholdCorridorEmbedded{
	private static final byte variationCount = 2;
	
	public static StrongholdCorridorDoubleChest[] generateCorridors(){
		StrongholdCorridorDoubleChest[] corridors = new StrongholdCorridorDoubleChest[variationCount*2];
		
		for(int variation = 0; variation < variationCount; variation++){
			corridors[variation*2] = new StrongholdCorridorDoubleChest(true,(byte)variation);
			corridors[variation*2+1] = new StrongholdCorridorDoubleChest(false,(byte)variation);
		}
		
		return corridors;
	}
	
	private byte variation;
	
	public StrongholdCorridorDoubleChest(boolean dirX, byte variation){
		super(dirX,new Size(dirX ? 5 : 7,5,dirX ? 7 : 5));
		
		if (variation < 0 || variation >= variationCount)throw new IllegalArgumentException("Invalid StrongholdPieceCorridorDoubleChest variation: "+variation);
		this.variation = variation;
		
		if (dirX){
			addConnection(Facing4.EAST_POSX,4,0,3,withAnything);
			addConnection(Facing4.WEST_NEGX,0,0,3,withAnything);
		}
		else{
			addConnection(Facing4.NORTH_NEGZ,3,0,0,withAnything);
			addConnection(Facing4.SOUTH_POSZ,3,0,4,withAnything);
		}
	}
	
	@Override
	protected void generateEmbedded(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// basic logic
		Facing4 dir = getRandomFacing(rand), left = dir.rotateLeft(), right = dir.rotateRight();

		placeOutsideWall(world,rand,x,y,z,dir,3);
		placeOutsideWall(world,rand,x,y,z,dir.opposite(),2);
		
		x = x+maxX/2+dir.getX()*2;
		z = z+maxZ/2+dir.getZ()*2;
		
		// chest
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),x+left.getX(),y+2,z+left.getZ());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),x+right.getX(),y+2,z+right.getZ());
		
		world.setTileEntity(x+left.getX(),y+2,z+left.getZ(),Meta.generateChest(dir.opposite(),(tile, random) -> {
			// TODO loot
		}));
		
		world.setTileEntity(x+right.getX(),y+2,z+right.getZ(),Meta.generateChest(dir.opposite(),(tile, random) -> {
			// TODO loot
		}));
		
		// pattern
		switch(variation){
			case 0:
				placeBlock(world,rand,placeStoneBrickStairs(dir,true),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,placeStoneBrickStairs(dir,true),x+right.getX(),y+1,z+right.getZ());
				placeLine(world,rand,placeStoneBrickPlain,x,y+1,z,x,y+3,z);
				break;
				
			case 1:
				placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+left.getX(),y+3,z+left.getZ(),x+right.getX(),y+3,z+right.getZ());
				placeLine(world,rand,placeStoneBrick,x+left.getX(),y+1,z+left.getZ(),x+right.getX(),y+1,z+right.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),x,y+2,z);
				break;
		}
	}
}
