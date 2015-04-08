package chylex.hee.world.structure.island.gen;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.system.util.BlockPosM;

public class CaveGenerator{
	private static double[] distanceValues = new double[64];
	
	static{
		for(int x = 0; x < 4; x++){
			for(int y = 0; y < 4; y++){
				for(int z = 0; z < 4; z++){
					distanceValues[16*x+4*y+z] = Math.sqrt(x*x+y*y+z*z);
				}
			}
		}
	}
	
	private final int centerX,centerY,centerZ,radX,radY,radZ;
	private final Set<BlockPosM> airList = new HashSet<>();
	private final Set<BlockPosM> clusterList = new HashSet<>();
	
	public CaveGenerator(int centerX, int centerY, int centerZ, int radX, int radY, int radZ){
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.radX = radX;
		this.radY = radY;
		this.radZ = radZ;
	}
	
	public void setup(Random rand, IslandBiomeBase biome){
		for(int a = 0, x, y, z, caveAmount = (int)(biome.getCaveAmountMultiplier()*(8F+rand.nextFloat()*5.5F)); a < caveAmount; a++){
			x = centerX+(int)(Math.sin(rand.nextDouble()*2D*Math.PI)*radX*(rand.nextDouble()*0.1D+0.9D));
			z = centerZ+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*radY*(rand.nextDouble()*0.1D+0.9D));
			y = centerY+rand.nextInt(radY*2)-radY;
			
			generateNode(rand,x,y,z,rand.nextFloat()*0.45F+2.65F,biome.getCaveBranchingChance(),Vec3.createVectorHelper(rand.nextBoolean()?(centerX-x):(x-centerX),(centerY-y)*rand.nextDouble()*4D,rand.nextBoolean()?(centerZ-z):(z-centerZ)).normalize(),0);
		}
	}
	
	public void generate(LargeStructureWorld world){
		for(BlockPosM loc:airList)world.setBlock(loc.x,loc.y,loc.z,Blocks.air);
		for(BlockPosM loc:clusterList){
			boolean foundSolid = false;
			
			for(int py = loc.y-5; py <= loc.y+5; py++){
				if (!world.isAir(loc.x,py,loc.z)){
					foundSolid = true;
					break;
				}
			}
			
			if (foundSolid)world.setBlock(loc.x,loc.y,loc.z,BlockList.energy_cluster);
		}
	}
	
	private void generateNode(Random rand, float x, float y, float z, float rad, float branchingChance, Vec3 vec, int iteration){
		if (iteration == 3)return;
		
		int cycle = 0;
		
		while(++cycle < 150){
			vec.xCoord += 0.65F*(rand.nextDouble()-0.5D);
			vec.yCoord += 0.4F*(rand.nextDouble()-0.5D);
			vec.zCoord += 0.65F*(rand.nextDouble()-0.5D);
			
			x += vec.xCoord;
			y += vec.yCoord;
			z += vec.zCoord;
			
			if (!createAirBlob(rand,MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z),rad))break;
			
			if (rand.nextFloat() < branchingChance){
				generateNode(rand,x,y,z,rad-rand.nextFloat()*0.15F,branchingChance*0.75F,Vec3.createVectorHelper(vec.xCoord+0.8F*(rand.nextDouble()-0.5D),vec.yCoord+0.4F*(rand.nextDouble()-0.5D),vec.zCoord+0.8F*(rand.nextDouble()-0.5D)),iteration+1);
			}
		}
	}
	
	private boolean createAirBlob(Random rand, int x, int y, int z, float rad){
		rad += rand.nextFloat()*0.2F;
		if (rand.nextInt(30) == 0)rad *= 1.5F;
		boolean onePlaced = false;
		int intrad = MathUtil.ceil(rad);

		for(int xx = x-intrad; xx <= x+intrad; xx++){
			for(int yy = y-intrad; yy <= y+intrad; yy++){
				for(int zz = z-intrad; zz <= z+intrad; zz++){
					if (xx <= centerX-radX || xx >= centerX+radX || yy <= centerY-radY || yy >= centerY+radY || zz <= centerZ-radZ || zz >= centerZ+radZ)continue;
					
					if (getDistance(xx-x,yy-y,zz-z) < rad+rand.nextFloat()*0.15F){
						onePlaced = true;
						airList.add(new BlockPosM(xx,yy,zz));
					}
				}
			}
		}
		
		if (rand.nextInt(120) == 0){
			int xx = x+rand.nextInt(intrad)-rand.nextInt(intrad),
				yy = y+rand.nextInt(intrad)-rand.nextInt(intrad),
				zz = z+rand.nextInt(intrad)-rand.nextInt(intrad);
			if ((getDistance(xx-x,yy-y,zz-z) < rad*0.5F || rand.nextInt(8) == 0))clusterList.add(new BlockPosM(xx,yy,zz));
		}
		
		return onePlaced;
	}
	
	public static double getDistance(int xdiff, int ydiff, int zdiff){
		if (xdiff < 0)xdiff *= -1;
		if (ydiff < 0)ydiff *= -1;
		if (zdiff < 0)zdiff *= -1;
		
		if (xdiff > 3 || ydiff > 3 || zdiff > 3)return Math.sqrt(xdiff*xdiff+ydiff*ydiff+zdiff*zdiff);
		else return distanceValues[16*xdiff+4*ydiff+zdiff];
	}
}
