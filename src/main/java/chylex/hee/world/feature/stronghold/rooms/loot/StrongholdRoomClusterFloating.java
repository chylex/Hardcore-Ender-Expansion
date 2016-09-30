package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomClusterFloating extends StrongholdRoom{
	public StrongholdRoomClusterFloating(){
		super(new Size(9, 6, 9));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// basic layout
		placeOutline(world, rand, IBlockPicker.basic(Blocks.double_stone_slab, Meta.slabStoneSmoothDouble), centerX-2, y, centerZ-2, centerX+2, y, centerZ+2, 1);
		placeStairOutline(world, rand, Blocks.stone_brick_stairs, centerX, y+1, centerZ, 1, true, false);
		
		placeStairOutline(world, rand, Blocks.stone_brick_stairs, centerX, y+maxY, centerZ, 3, false, true);
		placeOutline(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneSmoothTop), centerX-2, y+maxY, centerZ-2, centerX+2, y+maxY, centerZ+2, 1);
		placeStairOutline(world, rand, Blocks.stone_brick_stairs, centerX, y+maxY, centerZ, 1, true, true);
		
		// floating cluster
		placeBlock(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneBrickBottom), centerX, y+2, centerZ);
		placeBlock(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneBrickTop), centerX, y+maxY-1, centerZ);
		
		placeBlock(world, rand, IBlockPicker.basic(BlockList.energy_cluster), centerX, y+3, centerZ);
		
		world.setTileEntity(centerX, y+3, centerZ, (tile, random) -> {
			((TileEntityEnergyCluster)tile).generate(EnergyClusterGenerator.stronghold, random);
		});
	}
}
