package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureResourcePit extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		double rad = rand.nextDouble()*2.5D+3.5D;
		byte height = (byte)(rand.nextInt(10)+12),irad = (byte)Math.ceil(rad);
		
		byte[] xoff = new byte[]{ (byte)-irad, irad, 0, 0 }, zoff = new byte[]{ 0, 0, irad, (byte)-irad };
		boolean[][] holes = new boolean[irad*2+1][irad*2+1];
		
		for(int xx = -irad; xx <= irad; xx++){
			for(int zz = -irad; zz <= irad; zz++)holes[xx+irad][zz+irad] = xx*xx+zz*zz <= rad*rad;
		}
		
		for(int attempt = 0,xx,zz; attempt < 60; attempt++){
			xx = getRandomXZ(rand,8);
			zz = getRandomXZ(rand,8);
			
			// CHECK AREA
			
			boolean canGenerate = true;
			int maxy = 0;
			
			for(int a = 0,toph,btmh = 0; a < 4; a++){
				if ((toph = world.getHighestY(xx+xoff[a],zz+zoff[a])) == 0){
					canGenerate = false;
					break;
				}
				
				for(int yy = 0; yy < toph; yy++){
					if (!world.isAir(xx,yy,zz)){
						btmh = yy;
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
			
			for(int yy = maxy; yy > maxy-height-rand.nextInt(4); yy--){
				for(int px = -irad; px <= irad; px++){
					for(int pz = -irad; pz <= irad; pz++){
						if (holes[px+irad][pz+irad])world.setBlock(xx+px,yy,zz+pz,yy < maxy-height+3 ? Blocks.flowing_lava : Blocks.air,0,true);
					}
				}
			}
			
			for(int featureAttempt = 0,placed = 0,max = 25+rand.nextInt(18),px,py,pz; featureAttempt < 580+height*5+irad*irad*4 && placed < max+rand.nextInt(8); featureAttempt++){
				px = xx+rand.nextInt(irad+1)-rand.nextInt(irad+1);
				py = Math.min(maxy-1,(int)(maxy-height+Math.abs(rand.nextGaussian()*height*0.65D)));
				pz = zz+rand.nextInt(irad+1)-rand.nextInt(irad+1);
				
				if (world.getBlock(px,py,pz) == Blocks.end_stone && (world.isAir(px-1,py,pz) || world.isAir(px+1,py,pz) || world.isAir(px,py,pz-1) || world.isAir(px,py,pz+1))){
					world.setBlock(px,py,pz,rand.nextInt(7) <= 3 ? BlockList.igneous_rock_ore : rand.nextInt(3) == 0 ? BlockList.instability_orb_ore : Blocks.flowing_lava);
					++placed;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
