package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class StructureRuinPillar extends AbstractIslandStructure{
	private boolean isDeep;
	private int forcedX, forcedY, forcedZ;
	
	public StructureRuinPillar setIsDeep(boolean isDeep){
		this.isDeep = isDeep;
		return this;
	}
	
	public StructureRuinPillar setForcedCoords(int forcedX, int forcedY, int forcedZ){
		this.forcedX = forcedX;
		this.forcedY = forcedY;
		this.forcedZ = forcedZ;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		int x,y,z;
		
		if (forcedY != 0){
			x = forcedX;
			z = forcedZ;
			y = forcedY;
		}
		else{
			x = getRandomXZ(rand,0);
			z = getRandomXZ(rand,0);
			y = world.getHighestY(x,z)+1;
		}
		
		if (world.getBlock(x,y-1,z) == surface()){
			int py = y, height = 1+rand.nextInt(3+rand.nextInt(10));
			
			for(; py < y+height; py++){
				placeRandomPillarBlock(world,x,py,z,rand);
				if (rand.nextInt(5) == 0)break;
			}
			
			if (rand.nextInt(4) == 0)placeRandomTopBlock(world,x,py,z,rand);
			
			if (isDeep){
				int undergroundSpike = 4+rand.nextInt(30);
				int bottomBlockY = 0;
				
				for(; bottomBlockY < 30; bottomBlockY++){
					if (!world.isAir(x,bottomBlockY,z))break;
				}
				
				for(py = y-1; py > y-undergroundSpike && py > bottomBlockY; py--){
					placeRandomPillarBlock(world,x,py,z,rand);
				}
			}
			
			return true;
		}
		else return false;
	}
	
	public static void placeRandomPillarBlock(LargeStructureWorld world, int x, int y, int z, Random rand){
		int n = rand.nextInt(20);
		
		if (n < 15)world.setBlock(x,y,z,Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT,rand.nextInt(7) <= 4 ? EnumType.DEFAULT : rand.nextBoolean() ? EnumType.MOSSY : EnumType.CRACKED));
		else if (n < 18)world.setBlock(x,y,z,Blocks.cobblestone);
		else if (n < 20)world.setBlock(x,y,z,Blocks.stone);
	}
	
	public static void placeRandomTopBlock(LargeStructureWorld world, int x, int y, int z, Random rand){
		if (rand.nextInt(6) == 0)world.setBlock(x,y,z,Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT,rand.nextInt(4) == 0 ? BlockStoneSlab.EnumType.COBBLESTONE : BlockStoneSlab.EnumType.SMOOTHBRICK));
		else world.setBlock(x,y,z,(rand.nextInt(5) == 0 ? Blocks.stone_stairs : Blocks.stone_brick_stairs).getDefaultState().withProperty(BlockStairs.FACING,EnumFacing.getHorizontal(rand.nextInt(4))));
	}
}
