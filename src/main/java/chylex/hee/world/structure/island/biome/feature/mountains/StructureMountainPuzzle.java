package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.gen.CaveGenerator;

public class StructureMountainPuzzle extends AbstractIslandStructure{
	private static final byte[] dungCheckX = new byte[]{ -4, -4, 4, 4 },
								dungCheckZ = new byte[]{ -4, 4, -4, 4 };

	@Override
	protected boolean generate(Random rand){
		for(int attempt = 0, xx, yy, zz; attempt < 500; attempt++){
			boolean canGenerate = true, foundAir = false;
			
			xx = getRandomXZ(rand,32);
			zz = getRandomXZ(rand,32);
			if ((yy = world.getHighestY(xx,zz)) == 0)continue;
			
			for(int a = 0; a < 4; a++){
				int topY = world.getHighestY(xx+dungCheckX[a],zz+dungCheckZ[a]);
				if (Math.abs(topY-yy) > 5){
					canGenerate = false;
					break;
				}
				
				if (topY < yy)yy = topY;
			}
			
			if (!canGenerate)continue;
			
			yy -= 8-rand.nextInt(20)-(attempt > 140 ? rand.nextInt(20) : 0);
			if (yy < 0)continue;
			
			for(int a = 0; a < 4; a++){
				if (world.isAir(xx+dungCheckX[a],yy-1,zz+dungCheckZ[a]) || world.isAir(xx+dungCheckX[a],yy+6,zz+dungCheckZ[a])){
					canGenerate = false;
					break;
				}
			}
			
			if (!canGenerate)continue;
			
			float airX = 0,airY = 0,airZ = 0;
			for(int airAttempt = 0; airAttempt < 60; airAttempt++){
				airX = xx+rand.nextInt(31)-15;
				airY = yy+rand.nextInt(8)-2;
				airZ = zz+rand.nextInt(31)-15;
				
				if (MathUtil.distance(airX-xx,airZ-zz) < 10D)continue;
				if (world.isAir((int)Math.floor(airX),(int)Math.floor(airY),(int)Math.floor(airZ))){
					foundAir = true;
					break;
				}
			}
			
			if (!foundAir)continue;
			
			for(int posY = yy,data = -1; posY < yy+5; posY++){
				for(int posX = xx-4; posX <= xx+4; posX++){
					for(int posZ = zz-4; posZ <= zz+4; posZ++){
						if ((posX == xx-4 || posX == xx+4) || (posZ == zz-4 || posZ == zz+4))data = rand.nextInt(28) == 0 ? BlockDungeonPuzzle.metaWallRock : BlockDungeonPuzzle.metaWall;
						else if (posY == yy)data = rand.nextInt(15) < 9 ? BlockDungeonPuzzle.metaUnlit : rand.nextInt(13) <= (((posX == xx-3 || posX == xx+3) || (posZ == zz-3 || posZ == zz+3)) ? 9 : 5) ? BlockDungeonPuzzle.metaWall : BlockDungeonPuzzle.metaLit;
						else if (posY == yy+4)data = BlockDungeonPuzzle.metaWallLit;
						else data = -1;
						
						if (data == -1)world.setBlock(posX,posY,posZ,Blocks.air);
						else world.setBlock(posX,posY,posZ,BlockList.dungeon_puzzle,data);
					}
				}
			}
			
			Vec3 caveVec = Vec3.createVectorHelper(xx-airX,yy-airY,zz-airZ).normalize();
			int iterLimiter = 0,intrad,add = 1+rand.nextInt(4),brokenDungBlocks = 0;
			yy += add;
			
			while(true){
				float rad = rand.nextFloat()*0.3F+1.9F;
				intrad = (int)Math.ceil(rad);
				
				for(int px = (int)Math.floor(airX-intrad); px <= airX+intrad; px++){
					for(int py = (int)Math.floor(airY-intrad); py <= airY+intrad; py++){
						for(int pz = (int)Math.floor(airZ-intrad); pz <= airZ+intrad; pz++){
							if (CaveGenerator.getDistance((int)(px-airX),(int)(py-airY),(int)(pz-airZ)) < rad+rand.nextFloat()*0.35F){
								if (world.getBlock(px,py,pz) == BlockList.dungeon_puzzle)++brokenDungBlocks;
								world.setBlock(px,py,pz,Blocks.air);
							}
						}
					}
				}
				
				airX += caveVec.xCoord*0.6D;
				airY += caveVec.yCoord*0.6D;
				airZ += caveVec.zCoord*0.6D;
				if (Math.sqrt(MathUtil.square(xx-airX)+MathUtil.square(yy-airY)+MathUtil.square(zz-airZ)) < 4.8D || brokenDungBlocks > 4 || ++iterLimiter > 30)break;
			}
			
			yy -= add;
			return true;
		}
		
		return false;
	}
}
