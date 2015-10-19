package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomClusterIntersection extends StrongholdRoom{
	public StrongholdRoomClusterIntersection(){
		super(new Size(11,12,11));
		
		addConnection(Facing4.NORTH_NEGZ,maxX/2,4,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,4,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,4,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,4,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// floors
		IBlockPicker placeDoubleStoneSlab = IBlockPicker.basic(Blocks.double_stone_slab,Meta.slabStoneSmoothDouble);
		
		for(int level = 0; level < 2; level++){
			int py = y+4*level;
			
			placeBlock(world,rand,placeStoneBrickPlain,centerX,py,centerZ);
			
			for(Facing4 facing:Facing4.list){
				placeLine(world,rand,placeStoneBrickPlain,centerX+facing.getX(),py,centerZ+facing.getZ(),centerX+4*facing.getX(),py,centerZ+4*facing.getZ());
				
				mpos.set(centerX,0,centerZ).move(facing,4).move(facing.rotateRight());
				placeLine(world,rand,placeDoubleStoneSlab,mpos.x,py,mpos.z,mpos.x-3*facing.getX(),py,mpos.z-3*facing.getZ());
				mpos.move(facing.opposite(),3).move(facing.rotateRight());
				placeLine(world,rand,placeDoubleStoneSlab,mpos.x,py,mpos.z,mpos.x+2*facing.rotateRight().getX(),py,mpos.z+2*facing.rotateRight().getZ());
			}
		}
		
		// ceiling pattern
		placeBlock(world,rand,placeStoneBrickChiseled,centerX,y+maxY-1,centerZ);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+maxY-1,centerZ,4,false,true);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+maxY-1,centerZ,1,true,true);
		
		for(Facing4 facing:Facing4.list){
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),centerX+2*facing.getX(),y+maxY-1,centerZ+2*facing.getZ(),centerX+3*facing.getX(),y+maxY-1,centerZ+3*facing.getZ());
		}
		
		// alternate stairs and pillars
		Facing4 contentFacing = Facing4.list[rand.nextInt(Facing4.list.length)];
		generateStairs(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight());
		generatePillar(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight(),true);
		generateStairs(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight());
		generatePillar(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight(),false);
	}
	
	private void generateStairs(StructureWorld world, Random rand, final int centerX, final int y, final int centerZ, Facing4 facing){
		PosMutable mpos = new PosMutable(centerX,0,centerZ).move(facing,4).move(facing = facing.rotateRight(),2);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x,y+1,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,y+1,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x,y+2,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,placeStoneBrickPlain,mpos.x,y+2,mpos.z);
		mpos.move(facing = facing.rotateRight());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,y+2,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x,y+3,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,y+3,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x,y+4,mpos.z);
	}
	
	private void generatePillar(StructureWorld world, Random rand, final int centerX, final int y, final int centerZ, Facing4 facing, boolean hasCluster){
		int pillarX = centerX+2*facing.getX()+2*facing.rotateRight().getX(), pillarZ = centerZ+2*facing.getZ()+2*facing.rotateRight().getZ();
		placeLine(world,rand,placeStoneBrickWall,pillarX,y+1,pillarZ,pillarX,y+maxY-1,pillarZ);
		placeBlock(world,rand,placeStoneBrickChiseled,pillarX,y+4,pillarZ);
		
		if (hasCluster){
			int clusterY = rand.nextBoolean() ? y+2 : y+6;
			placeBlock(world,rand,IBlockPicker.basic(BlockList.energy_cluster),pillarX,clusterY,pillarZ);
			
			world.setTileEntity(pillarX,clusterY,pillarZ,(tile, random) -> {
				((TileEntityEnergyCluster)tile).generate(EnergyClusterGenerator.stronghold,random);
			});
		}
	}
}
