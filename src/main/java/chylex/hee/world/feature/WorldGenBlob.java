package chylex.hee.world.feature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.block.BlockList;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.blobs.CavePopulator;
import chylex.hee.world.feature.blobs.FlowerPopulator;
import chylex.hee.world.feature.blobs.LakePopulator;
import chylex.hee.world.feature.blobs.ObsidianSpikePopulator;
import chylex.hee.world.feature.blobs.OrePopulator;
import chylex.hee.world.feature.blobs.Populator;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.WorldGeneratorBlockList;

public class WorldGenBlob extends WorldGenerator{
	public static final Block filler = Blocks.end_stone;
	
	private static final List<Populator> populators = new ArrayList<>(Arrays.asList(new Populator[]{
		new CavePopulator(),
		new ObsidianSpikePopulator(),
		new LakePopulator(BlockList.ender_goo),
		new OrePopulator(BlockList.end_powder_ore,13,3),
		new OrePopulator(BlockList.end_powder_ore,9,2),
		new OrePopulator(BlockList.igneous_rock_ore,8,5),
		new FlowerPopulator(BlockList.death_flower)
	}));
	
	private int genCenterX, genCenterZ;
	private boolean canGenerate;
	
	public void prepare(int genCenterX, int genCenterZ){
		this.genCenterX = genCenterX;
		this.genCenterZ = genCenterZ;
	}
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		if (world.getBlock(x-8,y,z) != Blocks.air ||
			world.getBlock(x+8,y,z) != Blocks.air ||
			world.getBlock(x,y,z-8) != Blocks.air ||
			world.getBlock(x,y,z+8) != Blocks.air ||
			world.getBlock(x,y-8,z) != Blocks.air ||
			world.getBlock(x,y+8,z) != Blocks.air)return false;
		
		Stopwatch.timeAverage("WorldGenBlob",64);

		WorldGeneratorBlockList blocks = new WorldGeneratorBlockList(world);
		float rad = rand.nextFloat()*0.8F+rand.nextFloat()*1.9F+1.95F;
		
		canGenerate = true;
		createBlob(blocks,rand,x,y,z,rad,0);
		if (!canGenerate)return false;
		
		int[][] data = blocks.getData();
		int[] size = data[0], minPos = data[1], maxPos = data[2];
		
		if (size[0] > 16 || size[2] > 16)return false;
		if (!blocks.generate(filler))return false;
		
		List<Populator> availablePopulators = new ArrayList<>(populators);
		float n = 1;
		for(int a = 0; a < 3; a++)n += Math.min(2.2F,Math.abs(0.9F*rand.nextGaussian()));
		
		for(int a = 0; a < Math.floor(n); a++){
			if (availablePopulators.isEmpty())break;
			
			try{
				availablePopulators.remove(rand.nextInt(availablePopulators.size())).populate(size,minPos,maxPos,world,rand,x,y,z);
			}catch(Exception e){
				Log.error("Endstone Blob populator failed");
			}
		}
		
		if (rand.nextInt(availablePopulators.contains(populators.get(0)) ? 18 : 5) == 0){
			for(int attempt = 0, xx, yy, zz; attempt < 4; attempt++){
				xx = x+(int)((rand.nextDouble()-rand.nextDouble())*rand.nextDouble()*4D*attempt);
				yy = y+(int)((rand.nextDouble()-rand.nextDouble())*rand.nextDouble()*4D*attempt);
				zz = z+(int)((rand.nextDouble()-rand.nextDouble())*rand.nextDouble()*4D*attempt);
				
				if (world.isAirBlock(xx,yy,zz)){
					world.setBlock(xx,yy,zz,BlockList.energy_cluster);
					break;
				}
			}
		}
		
		Stopwatch.finish("WorldGenBlob");
		
		return true;
	}
	
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
				float a1 = random.nextFloat()*twoPI,a2 = random.nextFloat()*twoPI;
				float len = (rad*0.4F)+(random.nextFloat()*0.65F*rad);
				createBlob(blocks,random,
						   (int)Math.floor(x+MathHelper.cos(a1)*len),
						   (int)Math.floor(y+MathHelper.cos(a2)*len),
						   (int)Math.floor(z+MathHelper.sin(a1)*len),rad*(1F+random.nextFloat()*0.4F-0.2F),iteration+1);
			}
		}
	}
}
