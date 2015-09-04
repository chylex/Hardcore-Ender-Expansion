package chylex.hee.world.feature.stronghold.corridors;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Size;

public class StrongholdCorridorChest extends StrongholdCorridorEmbedded{
	private static final byte variationCount = 5;
	
	public static StrongholdCorridorChest[] generateCorridors(){
		StrongholdCorridorChest[] corridors = new StrongholdCorridorChest[variationCount*2];
		
		for(int variation = 0; variation < variationCount; variation++){
			corridors[variation*2] = new StrongholdCorridorChest(true,(byte)variation);
			corridors[variation*2+1] = new StrongholdCorridorChest(false,(byte)variation);
		}
		
		return corridors;
	}
	
	private byte variation;
	
	public StrongholdCorridorChest(boolean dirX, byte variation){
		super(dirX,new Size(dirX ? 5 : 7,5,dirX ? 7 : 5));
		
		if (variation < 0 || variation >= variationCount)throw new IllegalArgumentException("Invalid StrongholdPieceCorridorChest variation: "+variation);
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
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),x,y+2,z);
		world.setTileEntity(x,y+2,z,Meta.generateChest(dir.opposite(),generateLoot));
		
		// top slabs that are shared across all patterns
		placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+left.getX(),y+3,z+left.getZ(),x+right.getX(),y+3,z+right.getZ());
		
		// pattern
		switch(variation){
			case 0:
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x,y+1,z);
				placeBlock(world,rand,placeStoneBrickStairs(left,true),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,placeStoneBrickStairs(right,true),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 1:
				placeBlock(world,rand,placeStoneBrickStairs(dir,true),x,y+1,z);
				placeBlock(world,rand,placeStoneBrickStairs(right,false),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,placeStoneBrickStairs(left,false),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 2:
				placeBlock(world,rand,placeStoneBrickPlain,x,y+1,z);
				placeBlock(world,rand,placeStoneBrickStairs(right,false),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,placeStoneBrickStairs(left,false),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 3:
				placeBlock(world,rand,placeStoneBrickPlain,x,y+1,z);
				placeBlock(world,rand,placeStoneBrickStairs(dir,true),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,placeStoneBrickStairs(dir,true),x+right.getX(),y+1,z+right.getZ());
				break;
				
			case 4:
				placeBlock(world,rand,placeStoneBrickPlain,x,y+1,z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+left.getX(),y+1,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),x+left.getX(),y+2,z+left.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+right.getX(),y+1,z+right.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),x+right.getX(),y+2,z+right.getZ());
				break;
		}
	}
}
