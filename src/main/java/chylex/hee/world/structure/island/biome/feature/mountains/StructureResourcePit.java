package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureResourcePit extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		double rad = rand.nextDouble()*2.5D+3.5D;
		byte height = (byte)(rand.nextInt(10)+12),irad = (byte)Math.ceil(rad);
		
		byte[] xOff = new byte[]{ (byte)-irad, irad, 0, 0 }, zOff = new byte[]{ 0, 0, irad, (byte)-irad };
		boolean[][] holes = new boolean[irad*2+1][irad*2+1];
		
		for(int x = -irad; x <= irad; x++){
			for(int z = -irad; z <= irad; z++)holes[x+irad][z+irad] = x*x+z*z <= rad*rad;
		}
		
		for(int attempt = 0, x, z; attempt < 60; attempt++){
			x = getRandomXZ(rand,8);
			z = getRandomXZ(rand,8);
			
			// CHECK AREA
			
			boolean canGenerate = true;
			int maxy = 0;
			
			for(int a = 0,toph,btmh = 0; a < 4; a++){
				if ((toph = world.getHighestY(x+xOff[a],z+zOff[a])) == 0){
					canGenerate = false;
					break;
				}
				
				for(int y = 0; y < toph; y++){
					if (!world.isAir(x,y,z)){
						btmh = y;
						break;
					}
				}
				
				if (btmh == 0 || toph-btmh < height+7 || (maxy != 0 && Math.abs(toph-maxy) > 8)){
					canGenerate = false;
					break;
				}
				else if (toph > maxy)maxy = toph;
			}
			
			if (!canGenerate)continue;
			
			// GENERATE HOLE
			
			for(int py = maxy; py > maxy-height-rand.nextInt(4); py--){
				for(int px = -irad; px <= irad; px++){
					for(int pz = -irad; pz <= irad; pz++){
						if (holes[px+irad][pz+irad])world.setBlock(x+px,py,z+pz,py < maxy-height+3 ? Blocks.flowing_lava : Blocks.air,0,py < maxy-height+3);
					}
				}
			}
			
			for(int featureAttempt = 0,placed = 0,max = 25+rand.nextInt(18),px,py,pz; featureAttempt < 580+height*5+irad*irad*4 && placed < max+rand.nextInt(8); featureAttempt++){
				px = x+rand.nextInt(irad+1)-rand.nextInt(irad+1);
				py = Math.min(maxy-1,(int)(maxy-height+Math.abs(rand.nextGaussian()*height*0.65D)));
				pz = z+rand.nextInt(irad+1)-rand.nextInt(irad+1);
				
				if (world.getBlock(px,py,pz) == Blocks.end_stone && (world.isAir(px-1,py,pz) || world.isAir(px+1,py,pz) || world.isAir(px,py,pz-1) || world.isAir(px,py,pz+1))){
					if (rand.nextInt(9) <= 7)world.setBlock(px,py,pz,getOre(rand));
					else world.setBlock(px,py,pz,Blocks.flowing_lava,0,true);
					++placed;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private static Block getOre(Random rand){
		int i = rand.nextInt(19);
		
		if (i < 5)return BlockList.igneous_rock_ore;
		else if (i < 11)return BlockList.end_powder_ore;
		else if (i < 15)return BlockList.instability_orb_ore;
		else return BlockList.stardust_ore;
	}
}
