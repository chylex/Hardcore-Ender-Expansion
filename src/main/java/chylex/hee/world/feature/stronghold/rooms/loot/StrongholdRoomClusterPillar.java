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

public class StrongholdRoomClusterPillar extends StrongholdRoom{
	public StrongholdRoomClusterPillar(){
		super(new Size(7,6,7));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// floor and ceiling patterns
		placeBlock(world,rand,placeStoneBrickChiseled,centerX,y,centerZ);
		placeBlock(world,rand,placeStoneBrickChiseled,centerX,y+maxY,centerZ);
		
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+maxY,centerZ,2,false,true);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+maxY,centerZ,1,true,true);
		
		PosMutable mpos = new PosMutable();
		
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,2).move(facing.rotateRight());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateLeft(),false)),mpos.x,y,mpos.z);
			mpos.move(facing.opposite());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateLeft(),false)),mpos.x,y,mpos.z);
			mpos.move(facing.rotateRight());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x,y,mpos.z);
		}
		
		// pillar and cluster
		placeLine(world,rand,placeStoneBrickWall,centerX,y+1,centerZ,centerX,y+maxY-1,centerZ);
		placeBlock(world,rand,IBlockPicker.basic(BlockList.energy_cluster),centerX,y+2,centerZ);
		
		world.setTileEntity(centerX,y+2,centerZ,(tile, random) -> {
			((TileEntityEnergyCluster)tile).generate(EnergyClusterGenerator.stronghold,random);
		});
	}
}
