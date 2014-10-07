package chylex.hee.world.structure.island.biome.feature.island;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureEndermanStash extends AbstractIslandStructure{
	private static Block[] blockList;
	
	static{
		List<Block> blocks = new ArrayList<>();
		
		for(Object o:Block.blockRegistry.getKeys()){
			Block block = Block.getBlockFromName((String)o);
			if (EntityEnderman.getCarriable(block))blocks.add(block);
		}
		
		blockList = blocks.toArray(new Block[blocks.size()]);
	}
	
	public static Block getRandomBlock(Random rand){
		return blockList[rand.nextInt(blockList.length)];
	}
	
	@Override
	protected boolean generate(Random rand){
		for(int xx = getRandomXZ(rand,12), zz = getRandomXZ(rand,12), yy, attempt = 0; attempt < 10; attempt++){
			yy = 10+rand.nextInt(30);
			
			float rad = rand.nextFloat()*3F+3F,radSquared = MathUtil.square(rad+0.5F);
			int irad = (int)Math.ceil(rad),height = rand.nextInt(3)+4;
			
			// Test for space
			
			boolean canGenerate = true;
			
			for(int spaceCheck = 0, px, py, pz; spaceCheck < 12; spaceCheck++){
				px = xx+rand.nextInt(irad*2)-irad;
				py = yy+rand.nextInt(10)-2;
				pz = zz+rand.nextInt(irad*2)-irad;
				
				if (world.isAir(px,py,pz)){
					canGenerate = false;
					break;
				}
			}
			
			if (!canGenerate)continue;
			
			// Generate
			
			for(int px = xx-irad; px <= xx+irad; px++){
				for(int pz = zz-irad; pz <= zz+irad; pz++){
					if (MathUtil.distance(px-xx,pz-zz) <= radSquared){
						for(int py = yy; py <= yy+height; py++)world.setBlock(px,py,pz,Blocks.air);
					}
				}
			}
			
			float brad = rad*(0.68F+rand.nextFloat()*0.16F);
			int ibrad = (int)Math.ceil(brad),bheight = (int)Math.ceil(height*(0.8F+rand.nextFloat()*0.15F));
			Block[][][] blocks = new Block[ibrad*2+1][bheight][ibrad*2+1];
			
			int blockAmount = (int)Math.ceil((Math.PI*brad*brad*bheight*bheight*(1.2F+rand.nextFloat()*0.3F))/2.6D);
			for(int a = 0, indexX, indexZ; a < blockAmount; a++){
				indexX = Math.min(ibrad*2,Math.max((int)Math.floor(ibrad+((rand.nextGaussian()-0.5D)*(rand.nextGaussian()-0.5D))*brad*0.4D),0));
				indexZ = Math.min(ibrad*2,Math.max((int)Math.floor(ibrad+((rand.nextGaussian()-0.5D)*(rand.nextGaussian()-0.5D))*brad*0.4D),0));
				blocks[indexX][rand.nextInt(bheight)][indexZ] = blockList[rand.nextInt(blockList.length)];
			}
			
			Block toPlace;
			for(int px = 0,bx,by,bz; px < ibrad*2+1; px++){
				for(int pz = 0; pz < ibrad*2+1; pz++){
					for(int py = 0, pyReal = 0; py < bheight; py++){
						bx = xx+1-ibrad+px;
						by = yy+pyReal;
						bz = zz+1-ibrad+pz;
						
						if ((toPlace = blocks[px][py][pz]) == null || world.isAir(bx,by-1,bz))continue;
						
						if (canPlaceBlockAt(toPlace,bx,by,bz)){
							world.setBlock(bx,by,bz,toPlace,0,true);
							++pyReal;
						}
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean canPlaceBlockAt(Block block, int x, int y, int z){
		if (block == Blocks.cactus || block instanceof BlockFlower)return false;
		return true;
	}
}
