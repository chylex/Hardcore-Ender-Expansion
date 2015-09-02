/*package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureObsidianRoad extends AbstractIslandStructure{
	private static final Block roadBlock = Blocks.obsidian;
	
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,8), z = getRandomXZ(rand,8);
		
		int nearbyRoad = 0;
		
		for(int a = 0, xx, zz; a < 32; a++){
			xx = x+rand.nextInt(5)-rand.nextInt(5);
			zz = z+rand.nextInt(5)-rand.nextInt(5);
			if (world.getBlock(xx,world.getHighestY(xx,zz),zz) == roadBlock)++nearbyRoad;
		}
		
		if (nearbyRoad > 2 || !tryPlaceRoadAt(x,z))return false;
		
		generateRoad(rand,x,z,rand.nextDouble()*2D*Math.PI,0);
		return true;
	}
	
	private void generateRoad(Random rand, double x, double z, double dir, int level){
		if (level >= 3)return;
		
		int dirChange = level > 0 ? 0 : rand.nextInt(5)-2;
		int dirChangeFrequency = 14+level+rand.nextInt(6);
		int branchAmount = 0;
		
		for(int a = 0, max = 270-30*level+rand.nextInt(50); a < max; a++){
			if (dirChange != 0)dir += MathUtil.toRad(3D+(2D*rand.nextDouble()*dirChange));
			
			x += Math.cos(dir)*1.49D;
			z += Math.sin(dir)*1.49D;
			
			if (!tryPlaceRoadAt((int)x,(int)z))break;
			
			if (rand.nextInt(dirChange != 0 ? dirChangeFrequency : dirChangeFrequency-8) == 0){
				if (rand.nextInt(4) == 0)dirChange = 0;
				else if (dirChange == -2)dirChange = rand.nextInt(6) == 0 ? 2 : -1;
				else if (dirChange == 2)dirChange = rand.nextInt(6) == 0 ? -2 : 1;
				else dirChange = dirChange+(rand.nextBoolean() ? -1 : 1);
			}
			
			if (rand.nextInt(28+4*level) == 0 && ++branchAmount < 4)generateRoad(rand,x,z,dir+MathUtil.toRad((rand.nextBoolean() ? 90D : -90D)+(rand.nextDouble()-0.5D)*32D),level+1);
		}
	}
	
	*//**
	 * Returns whether we should keep on trying to place roads.
	 *//*
	private boolean tryPlaceRoadAt(int topLeftX, int topLeftZ){
		boolean shouldContinue = true;
		
		for(int px = 0; px < 2; px++){
			for(int pz = 0; pz < 2; pz++){
				int yy = world.getHighestY(topLeftX+px,topLeftZ+pz);
				Block block = world.getBlock(topLeftX+px,yy,topLeftZ+pz);
				
				if (block == surface())world.setBlock(topLeftX+px,yy,topLeftZ+pz,roadBlock);
				else if (block != roadBlock)shouldContinue = false;
			}
		}
		
		return shouldContinue;
	}
}
*/