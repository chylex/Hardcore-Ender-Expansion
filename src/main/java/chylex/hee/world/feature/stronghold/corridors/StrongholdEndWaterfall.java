package chylex.hee.world.feature.stronghold.corridors;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.StrongholdPieceGeneric;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdEndWaterfall extends StrongholdPieceGeneric{
	public static StrongholdEndWaterfall[] generateDeadEnds(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdEndWaterfall(facing)).toArray(StrongholdEndWaterfall[]::new);
	}
	
	private final Facing4 entranceFrom;
	
	public StrongholdEndWaterfall(Facing4 entranceFrom){
		super(Type.DEADEND,new Size(7,8,7),null,null);
		this.entranceFrom = entranceFrom;
		
		switch(entranceFrom.opposite()){
			case NORTH_NEGZ: addConnection(Facing4.NORTH_NEGZ,maxX/2,1,0,fromDeadEnd); break;
			case SOUTH_POSZ: addConnection(Facing4.SOUTH_POSZ,maxX/2,1,maxZ,fromDeadEnd); break;
			case EAST_POSX: addConnection(Facing4.EAST_POSX,maxX,1,maxZ/2,fromDeadEnd); break;
			case WEST_NEGX: addConnection(Facing4.WEST_NEGX,0,1,maxZ/2,fromDeadEnd); break;
			default:
		}
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final Connection connection = connections.get(0);
		PosMutable mpos = new PosMutable();
		
		// extended floor and ceiling
		placeCube(world,rand,placeStoneBrick,x+1,y+1,z+1,x+maxX-1,y+1,z+maxZ-1);
		placeCube(world,rand,placeStoneBrick,x+1,y+maxY-1,z+1,x+maxX-1,y+maxY-1,z+maxZ-1);
		
		// layout
		for(int side = 0; side < 2; side++){
			final Facing4 facing = side == 0 ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
			
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(facing,2);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),mpos.x,y+maxY-1,mpos.z);
			placeLine(world,rand,IBlockPicker.basic(Blocks.flowing_water),mpos.x,y+1,mpos.z,mpos.x+4*entranceFrom.getX(),y+1,mpos.z+4*entranceFrom.getZ());
			mpos.move(entranceFrom,4).move(facing.opposite());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),mpos.x,y+1,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x,y+2,mpos.z);
		}
		
		// chest(s)
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,5);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom.opposite(),true)),mpos.x,y+2,mpos.z);
		
		for(int chest = 0; chest < 2; chest++){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),mpos.x,chest == 0 ? y+1 : y+3,mpos.z);
			world.setTileEntity(mpos.x,chest == 0 ? y+1 : y+3,mpos.z,Meta.generateChest(entranceFrom.opposite(),generateLoot));
		}
	}
}
