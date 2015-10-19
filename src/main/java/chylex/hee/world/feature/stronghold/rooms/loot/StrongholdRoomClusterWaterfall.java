package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomClusterWaterfall extends StrongholdRoom{
	public StrongholdRoomClusterWaterfall(){
		super(new Size(17,9,17),null);
		
		addConnection(Facing4.NORTH_NEGZ,maxX/2,2,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,2,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,2,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,2,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// layout extensions
		placeCube(world,rand,placeStoneBrick,x+1,y+1,z+1,x+maxX-1,y+1,z+maxZ-1);
		placeWalls(world,rand,placeStoneBrick,x+1,y+2,z+1,x+maxX-1,y+maxY-1,z+maxZ-1);
		
		// floor and cluster
		placeOutline(world,rand,placeStoneBrick,x+2,y+2,z+2,x+maxX-2,y+2,z+maxZ-2,1);
		placeOutline(world,rand,placeWater,x+3,y+2,z+3,x+maxX-3,y+2,z+maxZ-3,2);
		placeCube(world,rand,placeStoneBrick,centerX-3,y+2,centerZ-3,centerX+3,y+2,centerZ+3);
		
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothBottom),centerX-1,y+3,centerZ-1,centerX+1,y+3,centerZ+1,1);
		placeBlock(world,rand,placeStoneBrickPlain,centerX,y+3,centerZ);
		
		placeBlock(world,rand,IBlockPicker.basic(BlockList.energy_cluster),centerX,y+4,centerZ);
		
		world.setTileEntity(centerX,y+4,centerZ,(tile, random) -> {
			((TileEntityEnergyCluster)tile).generate(EnergyClusterGenerator.stronghold,random);
		});
		
		// waterfalls and paths
		for(Facing4 facing:Facing4.list){
			Facing4 perpendicular = facing.perpendicular();
			
			for(int off = 0; off < 2; off++){
				final Facing4 offFacing = off == 0 ? facing.rotateLeft() : facing.rotateRight();
				placeBlock(world,rand,placeWater,centerX+7*facing.getX()+3*offFacing.getX(),y+maxY-2,centerZ+7*facing.getZ()+3*offFacing.getZ());
			}
			
			if (connections.stream().anyMatch(connection -> connection.facing == facing)){
				placeCube(world,rand,placeStoneBrick,centerX+4*facing.getX()-perpendicular.getX(),y+2,centerZ+4*facing.getZ()-perpendicular.getZ(),centerX+5*facing.getX()+perpendicular.getX(),y+2,centerZ+5*facing.getZ()+perpendicular.getZ());
				placeCube(world,rand,placeAir,centerX+4*facing.getX()-2*perpendicular.getX(),y+1,centerZ+4*facing.getZ()-2*perpendicular.getZ(),centerX+5*facing.getX()+2*perpendicular.getX(),y+1,centerZ+5*facing.getZ()+2*perpendicular.getZ());
				placeCube(world,rand,placeAir,centerX+7*facing.getX()-perpendicular.getX(),y+3,centerZ+7*facing.getZ()-perpendicular.getZ(),centerX+7*facing.getX()+perpendicular.getX(),y+5,centerZ+7*facing.getZ()+perpendicular.getZ());
			}
			else{
				placeBlock(world,rand,placeWater,centerX+7*facing.getX(),y+maxY-2,centerZ+7*facing.getZ());
			}
		}
	}
}
