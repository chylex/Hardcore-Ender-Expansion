package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.util.BlockLocation;

public class WorldGenBlobOld extends WorldGenerator{
	private static final float twoPI = (float)(Math.PI*2D);
	
	private void createBlob(WorldGeneratorBlockList blocks, Random random, int x, int y, int z, float rad, int iteration){
		BlockLocation blockLoc;
		
		double xx, yy, zz;
		for(xx = x-rad; xx <= x+rad; xx++){
			for(yy = y-rad; yy <= y+rad; yy++){
				for(zz = z-rad; zz <= z+rad; zz++){
					if (Math.sqrt(MathUtil.square(xx-x)+MathUtil.square(yy-y)+MathUtil.square(zz-z)) < rad){
						blockLoc = new BlockLocation((int)Math.floor(xx),(int)Math.floor(yy),(int)Math.floor(zz));
						
						if (Math.abs(genCenterX-blockLoc.x) < 15 && Math.abs(genCenterZ-blockLoc.z) < 15)blocks.add(blockLoc);
						else{
							canGenerate = false;
							return;
						}
					}
				}
			}
		}
		
		if (iteration < 4 && random.nextInt(9-iteration*2) > 1){
			for(int a = 0; a < random.nextInt(3-(iteration>>1))+1; a++){
				float a1 = random.nextFloat()*twoPI, a2 = random.nextFloat()*twoPI;
				float len = (rad*0.4F)+(random.nextFloat()*0.65F*rad);
				createBlob(blocks,random,
						   (int)Math.floor(x+MathHelper.cos(a1)*len),
						   (int)Math.floor(y+MathHelper.cos(a2)*len),
						   (int)Math.floor(z+MathHelper.sin(a1)*len),rad*(1F+random.nextFloat()*0.4F-0.2F),iteration+1);
			}
		}
	}
}
