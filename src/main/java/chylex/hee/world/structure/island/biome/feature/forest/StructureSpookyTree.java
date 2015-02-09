package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSpookyLog;
import chylex.hee.system.util.Direction;
import chylex.hee.world.structure.island.biome.IslandBiomeInfestedForest;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureSpookyTree extends AbstractIslandStructure{
	public static enum TreeType{
		SIMPLE_PYRAMID(1), SIMPLE_BULGING(1), SIMPLE_SPHERICAL(2);
		
		public static final TreeType[] values = values();
		
		final byte rad;
		
		TreeType(int rad){
			this.rad = (byte)rad;
		}
	}
	
	private TreeType treeType;
	private boolean canGenerateFace;
	private boolean looseSpaceCheck;
	
	public StructureSpookyTree setTreeType(TreeType treeType){
		this.treeType = treeType;
		return this;
	}
	
	public StructureSpookyTree setCanGenerateFace(boolean canGenerateFace){
		this.canGenerateFace = canGenerateFace;
		return this;
	}
	
	public StructureSpookyTree setLooseSpaceCheck(boolean looseSpaceCheck){
		this.looseSpaceCheck = looseSpaceCheck;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,treeType.rad), z = getRandomXZ(rand,treeType.rad), y = world.getHighestY(x,z);
			
		if (world.getBlock(x,y++,z) == surface()){
			boolean tallTrees = biomeData.hasDeviation(IslandBiomeInfestedForest.TALL_TREES);
			int treeHeight = rand.nextInt(4+rand.nextInt(4))+(tallTrees ? 5+rand.nextInt(1+rand.nextInt(7)) : 4);
			
			// CHECK SPACE
			
			for(int checkY = 0; checkY <= (looseSpaceCheck ? treeHeight+1 : treeHeight-3); checkY++){
				if (!world.isAir(x,y+checkY,z))return false;
			}
			
			if (!looseSpaceCheck){
				for(int checkY = treeHeight-2; checkY <= treeHeight+1; checkY++){
					for(int checkX = -treeType.rad; checkX <= treeType.rad; checkX++){
						for(int checkZ = -treeType.rad; checkZ <= treeType.rad; checkZ++){
							if (!world.isAir(x+checkX,y+checkY,z+checkZ))return false;
						}
					}
				}
			}
			
			// GENERATE
			
			boolean hasFace = false;
			
			for(int treeY = 0, meta = 0; treeY < treeHeight; treeY++, meta = canGenerateFace && rand.nextInt(12) == 0 ? rand.nextInt(5) : 0){
				world.setBlock(x,y+treeY,z,BlockList.spooky_log.setProperty(BlockSpookyLog.VARIANT,BlockSpookyLog.Variant.values()[hasFace ? 0 : meta]));
				if (meta > 0)hasFace = true;
			}
			
			if (treeType == TreeType.SIMPLE_PYRAMID){
				int pyramidBottomHeight = treeHeight < 5 ? 0 : rand.nextInt(3);
				int pyramidYOffset = pyramidBottomHeight > 0 ? rand.nextInt(3) : rand.nextInt(2);
				int topY = y+treeHeight+pyramidYOffset;
				
				for(int py = topY; py >= y+treeHeight; py--)world.setBlock(x,py,z,BlockList.spooky_leaves);
				for(int dir = 0; dir < 4; dir++)world.setBlock(x+Direction.offsetX[dir],topY-1,z+Direction.offsetZ[dir],BlockList.spooky_leaves);
				
				for(int pyramidBottomY = topY-2; pyramidBottomY >= topY-3-pyramidBottomHeight; pyramidBottomY--){
					for(int px = x-1; px <= x+1; px++){
						for(int pz = z-1; pz <= z+1; pz++){
							if (px == x && pz == z)continue;
							world.setBlock(px,pyramidBottomY,pz,BlockList.spooky_leaves);
						}
					}
				}
			}
			else if (treeType == TreeType.SIMPLE_BULGING || treeType == TreeType.SIMPLE_SPHERICAL){
				int bulgeHeight = treeHeight < 5 ? 1 : 1+rand.nextInt(3);

				for(int py = y+treeHeight+2; py >= y+treeHeight; py--)world.setBlock(x,py,z,BlockList.spooky_leaves);
				
				for(int middleY = 0; middleY <= 2+bulgeHeight; middleY++){
					for(int dir = 0; dir < 4; dir++)world.setBlock(x+Direction.offsetX[dir],y+treeHeight+1-middleY,z+Direction.offsetZ[dir],BlockList.spooky_leaves);
				}
				
				for(int bulgeY = 0; bulgeY <= bulgeHeight; bulgeY++){
					for(int offX = 0; offX < 2; offX++){
						for(int offZ = 0; offZ < 2; offZ++){
							world.setBlock(x-1+2*offX,y+treeHeight-bulgeY,z-1+2*offZ,BlockList.spooky_leaves);
						}
					}
					
					if (treeType == TreeType.SIMPLE_SPHERICAL){
						for(int dir = 0; dir < 4; dir++)world.setBlock(x+Direction.offsetX[dir]*2,y+treeHeight-bulgeY,z+Direction.offsetZ[dir]*2,BlockList.spooky_leaves);
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
}
