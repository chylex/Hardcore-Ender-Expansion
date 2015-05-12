package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.init.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureShadowOrchid extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,16),
			z = getRandomXZ(rand,16),
			y = 7+rand.nextInt(30);
		
		for(int yAttempt = 0; yAttempt < 8; yAttempt++){
			if (world.isAir(x,++y,z) && canPlaceOn(world.getBlock(x,y-1,z)))break;
			else if (yAttempt == 7)return false;
		}
		
		world.setBlock(x,y,z,BlockList.crossed_decoration,BlockCrossedDecoration.dataShadowOrchid);
		
		for(int a = rand.nextInt(3+rand.nextInt(4))*rand.nextInt(3), xx, yy, zz; a > 0; a--){
			xx = x+rand.nextInt(13)-6;
			yy = y+rand.nextInt(9)-4;
			zz = z+rand.nextInt(13)-6;
			
			if (world.isAir(xx,yy,zz) && canPlaceOn(world.getBlock(xx,yy-1,zz))){
				world.setBlock(x,y,z,BlockList.crossed_decoration,BlockCrossedDecoration.dataShadowOrchid);
				--a;
			}
		}
		
		return true;
	}
	
	private boolean canPlaceOn(Block block){
		return ((BlockCrossedDecoration)BlockList.crossed_decoration).canPlaceBlockOn(block);
	}
}
