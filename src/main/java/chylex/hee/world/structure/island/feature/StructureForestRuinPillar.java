package chylex.hee.world.structure.island.feature;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class StructureForestRuinPillar extends AbstractIslandStructure{
	private boolean isDeep;
	private int forcedX, forcedY, forcedZ;
	
	public StructureForestRuinPillar setIsDeep(boolean isDeep){
		this.isDeep = isDeep;
		return this;
	}
	
	public StructureForestRuinPillar setForcedCoords(int forcedX, int forcedY, int forcedZ){
		this.forcedX = forcedX;
		this.forcedY = forcedY;
		this.forcedZ = forcedZ;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		int xx,yy,zz;
		
		if (forcedY != 0){
			xx = forcedX;
			zz = forcedZ;
			yy = forcedY;
		}
		else{
			xx = getRandomXZ(rand,0);
			zz = getRandomXZ(rand,0);
			yy = world.getHighestY(xx,zz)+1;
		}
		
		if (world.getBlock(xx,yy-1,zz) == surface()){
			int py = yy, height = 1+rand.nextInt(3+rand.nextInt(10));
			
			for(; py < yy+height; py++){
				placeRandomPillarBlock(world,xx,py,zz,rand);
				if (rand.nextInt(5) == 0)break;
			}
			
			if (rand.nextInt(4) == 0)placeRandomTopBlock(world,xx,py,zz,rand);
			
			if (isDeep){
				int undergroundSpike = 4+rand.nextInt(30);
				int bottomBlockY = 0;
				
				for(; bottomBlockY < 30; bottomBlockY++){
					if (!world.isAir(xx,bottomBlockY,zz))break;
				}
				
				for(py = yy-1; py > yy-undergroundSpike && py > bottomBlockY; py--){
					placeRandomPillarBlock(world,xx,py,zz,rand);
				}
			}
			
			return true;
		}
		else return false;
	}
	
	public static void placeRandomPillarBlock(LargeStructureWorld world, int x, int y, int z, Random rand){
		int n = rand.nextInt(20);
		
		if (n < 15)world.setBlock(x,y,z,Blocks.stonebrick,rand.nextInt(7) <= 4 ? 0 : (rand.nextBoolean() ? 1 : 2));
		else if (n < 18)world.setBlock(x,y,z,Blocks.cobblestone);
		else if (n < 20)world.setBlock(x,y,z,Blocks.stone);
	}
	
	public static void placeRandomTopBlock(LargeStructureWorld world, int x, int y, int z, Random rand){
		if (rand.nextInt(6) == 0)world.setBlock(x,y,z,Blocks.stone_slab,rand.nextInt(4) == 0 ? 3 : 5);
		else world.setBlock(x,y,z,rand.nextInt(5) == 0 ? Blocks.stone_stairs : Blocks.stone_brick_stairs,rand.nextInt(4));
	}
}
