package chylex.hee.world.structure.island.feature;
import java.util.Random;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.IslandBiomeInfestedForest;

public class StructureForestTree extends AbstractIslandStructure{
	public static enum TreeType{
		SIMPLE_PYRAMID(1), SIMPLE_BULGING(1), SIMPLE_SPHERICAL(2);
		
		final byte rad;
		
		TreeType(int rad){
			this.rad = (byte)rad;
		}
	}
	
	private TreeType treeType;
	private boolean canGenerateFace;
	private boolean looseSpaceCheck;
	
	public StructureForestTree setTreeType(TreeType treeType){
		this.treeType = treeType;
		return this;
	}
	
	public StructureForestTree setCanGenerateFace(boolean canGenerateFace){
		this.canGenerateFace = canGenerateFace;
		return this;
	}
	
	public StructureForestTree setLooseSpaceCheck(boolean looseSpaceCheck){
		this.looseSpaceCheck = looseSpaceCheck;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		int xx = getRandomXZ(rand,treeType.rad), zz = getRandomXZ(rand,treeType.rad), yy = world.getHighestY(xx,zz);
			
		if (world.getBlock(xx,yy++,zz) == surface()){
			boolean hasFace = false, tallTrees = biomeData.hasDeviation(IslandBiomeInfestedForest.TALL_TREES);
			
			int treeHeight = rand.nextInt(4+rand.nextInt(4))+(tallTrees ? 5+rand.nextInt(1+rand.nextInt(7)) : 4);
			
			// CHECK SPACE
			
			for(int checkY = 0; checkY <= (looseSpaceCheck ? treeHeight+1 : treeHeight-3); checkY++){
				if (!world.isAir(xx,yy+checkY,zz))return false;
			}
			
			if (!looseSpaceCheck){
				for(int checkY = treeHeight-2; checkY <= treeHeight+1; checkY++){
					for(int checkX = -treeType.rad; checkX <= treeType.rad; checkX++){
						for(int checkZ = -treeType.rad; checkZ <= treeType.rad; checkZ++){
							if (!world.isAir(xx+checkX,yy+checkY,zz+checkZ))return false;
						}
					}
				}
			}
			
			// GENERATE
			
			for(int treeY = 0, meta = 0; treeY < treeHeight; treeY++, meta = canGenerateFace && rand.nextInt(12) == 0 ? rand.nextInt(5) : 0){
				world.setBlock(xx,yy+treeY,zz,BlockList.spooky_log,hasFace ? 0 : meta);
				if (meta > 0)hasFace = true;
			}
			
			if (treeType == TreeType.SIMPLE_PYRAMID){
				int pyramidBottomHeight = treeHeight < 5 ? 0 : rand.nextInt(3);
				int pyramidYOffset = pyramidBottomHeight > 0 ? rand.nextInt(3) : rand.nextInt(2);
				int topY = yy+treeHeight+pyramidYOffset;
				
				for(int py = topY; py >= yy+treeHeight; py--)world.setBlock(xx,py,zz,BlockList.spooky_leaves);
				for(int dir = 0; dir < 4; dir++)world.setBlock(xx+Direction.offsetX[dir],topY-1,zz+Direction.offsetZ[dir],BlockList.spooky_leaves);
				
				for(int pyramidBottomY = topY-2; pyramidBottomY >= topY-3-pyramidBottomHeight; pyramidBottomY--){
					for(int px = xx-1; px <= xx+1; px++){
						for(int pz = zz-1; pz <= zz+1; pz++){
							if (px == xx && pz == zz)continue;
							world.setBlock(px,pyramidBottomY,pz,BlockList.spooky_leaves,0);
						}
					}
				}
			}
			else if (treeType == TreeType.SIMPLE_BULGING || treeType == TreeType.SIMPLE_SPHERICAL){
				int bulgeHeight = treeHeight < 5 ? 1 : 1+rand.nextInt(3);

				for(int py = yy+treeHeight+2; py >= yy+treeHeight; py--)world.setBlock(xx,py,zz,BlockList.spooky_leaves);
				
				for(int middleY = 0; middleY <= 2+bulgeHeight; middleY++){
					for(int dir = 0; dir < 4; dir++)world.setBlock(xx+Direction.offsetX[dir],yy+treeHeight+1-middleY,zz+Direction.offsetZ[dir],BlockList.spooky_leaves);
				}
				
				for(int bulgeY = 0; bulgeY <= bulgeHeight; bulgeY++){
					for(int offX = 0; offX < 2; offX++){
						for(int offZ = 0; offZ < 2; offZ++){
							world.setBlock(xx-1+2*offX,yy+treeHeight-bulgeY,zz-1+2*offZ,BlockList.spooky_leaves);
						}
					}
					
					if (treeType == TreeType.SIMPLE_SPHERICAL){
						for(int dir = 0; dir < 4; dir++)world.setBlock(xx+Direction.offsetX[dir]*2,yy+treeHeight-bulgeY,zz+Direction.offsetZ[dir]*2,BlockList.spooky_leaves);
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
}
