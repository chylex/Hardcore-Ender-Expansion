package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Vec3;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.gen.CaveGenerator;

public class StructureDungeonPuzzle extends AbstractIslandStructure{
	private static final byte[] checkX = new byte[]{ -1, -1, 1, 1 },
								checkZ = new byte[]{ -1, 1, -1, 1 };

	@Override
	protected boolean generate(Random rand){
		int xSize = BlockDungeonPuzzle.minDungeonSize+2*rand.nextInt(1+((BlockDungeonPuzzle.maxDungeonSize-BlockDungeonPuzzle.minDungeonSize)>>1)),
			zSize = BlockDungeonPuzzle.minDungeonSize+2*rand.nextInt(1+((BlockDungeonPuzzle.maxDungeonSize-BlockDungeonPuzzle.minDungeonSize)>>1));
		
		if (xSize == BlockDungeonPuzzle.minDungeonSize)xSize = BlockDungeonPuzzle.minDungeonSize+2*rand.nextInt(1+((BlockDungeonPuzzle.maxDungeonSize-BlockDungeonPuzzle.minDungeonSize)>>1));
		if (zSize == BlockDungeonPuzzle.minDungeonSize)zSize = BlockDungeonPuzzle.minDungeonSize+2*rand.nextInt(1+((BlockDungeonPuzzle.maxDungeonSize-BlockDungeonPuzzle.minDungeonSize)>>1));
		
		int	checkMpX = 1+(xSize>>1), checkMpZ = 1+(zSize>>1),
			distX = xSize>>1, distZ = zSize>>1;
		
		for(int attempt = 0, xx, yy, zz; attempt < 500; attempt++){
			boolean canGenerate = true, foundAir = false;
			
			// find spawn place
			
			xx = getRandomXZ(rand,32);
			zz = getRandomXZ(rand,32);
			if ((yy = world.getHighestY(xx,zz)) == 0)continue;
			
			for(int a = 0, topY; a < 4; a++){
				topY = world.getHighestY(xx+checkX[a]*checkMpX,zz+checkZ[a]*checkMpZ);
				
				if (Math.abs(topY-yy) > 5){
					canGenerate = false;
					break;
				}
				
				if (topY < yy)yy = topY;
			}
			
			if (!canGenerate)continue;
			if ((yy -= 8-rand.nextInt(20)-(attempt > 140 ? rand.nextInt(20) : 0)) < 0)continue;
			
			for(int a = 0; a < 4; a++){
				if (world.isAir(xx+checkX[a]*checkMpX,yy-1,zz+checkZ[a]*checkMpZ) || world.isAir(xx+checkX[a]*checkMpX,yy+6,zz+checkZ[a]*checkMpZ)){
					canGenerate = false;
					break;
				}
			}
			
			if (!canGenerate)continue;
			
			// check air nearby
			
			float airX = 0, airY = 0, airZ = 0;
			
			for(int airAttempt = 0; airAttempt < 60; airAttempt++){
				airX = xx+rand.nextInt(31)-15;
				airY = yy+rand.nextInt(8)-2;
				airZ = zz+rand.nextInt(31)-15;
				
				if (MathUtil.distance(airX-xx,airZ-zz) < 10D)continue;
				if (world.isAir(MathUtil.floor(airX),MathUtil.floor(airY),MathUtil.floor(airZ))){
					foundAir = true;
					break;
				}
			}
			
			if (!foundAir)continue;
			
			// generate walls and air inside
			
			for(int posY = yy; posY < yy+5; posY++){
				boolean genRock = posY > yy && posY < yy+4;
				
				for(int posX = xx-distX; posX <= xx+distX; posX++){
					world.setBlock(posX,posY,zz-distZ,BlockList.dungeon_puzzle,genRock && rand.nextInt(24) == 0 ? BlockDungeonPuzzle.metaRock : BlockDungeonPuzzle.metaWall);
					world.setBlock(posX,posY,zz+distZ,BlockList.dungeon_puzzle,genRock && rand.nextInt(24) == 0 ? BlockDungeonPuzzle.metaRock : BlockDungeonPuzzle.metaWall);
				}
				
				for(int posZ = zz-distZ; posZ <= zz+distZ; posZ++){
					world.setBlock(xx-distX,posY,posZ,BlockList.dungeon_puzzle,genRock && rand.nextInt(24) == 0 ? BlockDungeonPuzzle.metaRock : BlockDungeonPuzzle.metaWall);
					world.setBlock(xx+distX,posY,posZ,BlockList.dungeon_puzzle,genRock && rand.nextInt(24) == 0 ? BlockDungeonPuzzle.metaRock : BlockDungeonPuzzle.metaWall);
				}
				
				if (posY > yy && posY < yy+4){
					for(int posX = xx-distX+1, meta; posX <= xx+distX-1; posX++){
						for(int posZ = zz-distZ+1; posZ <= zz+distZ-1; posZ++){
							world.setBlock(posX,posY,posZ,Blocks.air);
						}
					}
				}
			}
			
			// generate basic floor and ceiling
			
			for(int posX = xx-distX+1, meta; posX <= xx+distX-1; posX++){
				for(int posZ = zz-distZ+1; posZ <= zz+distZ-1; posZ++){
					world.setBlock(posX,yy+4,posZ,BlockList.dungeon_puzzle,BlockDungeonPuzzle.metaCeiling);
					
					if (posX == xx-distX+1 || posX == xx+distX-1 || posZ == zz-distZ+1 || posZ == zz+distZ-1)meta = pickBlock(BlockDungeonPuzzle.metaChainedUnlit,rand);
					else meta = pickBlock(BlockDungeonPuzzle.metaTriggerUnlit,rand);
					
					world.setBlock(posX,yy,posZ,BlockList.dungeon_puzzle,meta);
				}
			}
			
			// generate triggerable blocks around edges so it is possible to solve
			
			for(int a = 0, amt = 3+rand.nextInt(5), posX, posZ; a < amt+rand.nextInt(4+rand.nextInt(10)); a++){
				if (rand.nextBoolean()){
					posX = rand.nextBoolean() ? xx+distX-1 : xx-distX+1;
					posZ = zz+rand.nextInt(distZ*2+1)-distZ;
				}
				else{
					posX = xx+rand.nextInt(distX*2+1)-distX;
					posZ = rand.nextBoolean() ? zz+distZ-1 : zz-distZ+1;
				}
				
				world.setBlock(posX,yy,posZ,BlockList.dungeon_puzzle,pickBlock(BlockDungeonPuzzle.metaTriggerUnlit,rand));
			}
			
			boolean[] foundTriggerable = new boolean[4];
			
			for(int posX = xx-distX+1, meta; posX <= xx+distX-1; posX++){
				if (BlockDungeonPuzzle.getUnlit(world.getMetadata(posX,yy,zz-distZ+1)) == BlockDungeonPuzzle.metaTriggerUnlit)foundTriggerable[0] = true;
				if (BlockDungeonPuzzle.getUnlit(world.getMetadata(posX,yy,zz+distZ-1)) == BlockDungeonPuzzle.metaTriggerUnlit)foundTriggerable[1] = true;
			}
			
			for(int posZ = zz-distZ+1, meta; posZ <= zz+distZ-1; posZ++){
				if (BlockDungeonPuzzle.getUnlit(world.getMetadata(xx-distX+1,yy,posZ)) == BlockDungeonPuzzle.metaTriggerUnlit)foundTriggerable[2] = true;
				if (BlockDungeonPuzzle.getUnlit(world.getMetadata(xx+distX-1,yy,posZ)) == BlockDungeonPuzzle.metaTriggerUnlit)foundTriggerable[3] = true;
			}
			
			if (!foundTriggerable[0])world.setBlock(xx+rand.nextInt(distX*2+1)-distX,yy,zz-distZ+1,BlockList.dungeon_puzzle,pickBlock(BlockDungeonPuzzle.metaTriggerUnlit,rand));
			if (!foundTriggerable[1])world.setBlock(xx+rand.nextInt(distX*2+1)-distX,yy,zz+distZ-1,BlockList.dungeon_puzzle,pickBlock(BlockDungeonPuzzle.metaTriggerUnlit,rand));
			if (!foundTriggerable[2])world.setBlock(xx-distX+1,yy,zz+rand.nextInt(distZ*2+1)-distZ,BlockList.dungeon_puzzle,pickBlock(BlockDungeonPuzzle.metaTriggerUnlit,rand));
			if (!foundTriggerable[3])world.setBlock(xx+distX-1,yy,zz+rand.nextInt(distZ*2+1)-distZ,BlockList.dungeon_puzzle,pickBlock(BlockDungeonPuzzle.metaTriggerUnlit,rand));
			
			// generate distributors
			
			for(int amt = 3+rand.nextInt(4+rand.nextInt(xSize*zSize > 90 ? 5 : 3)), distrAttempt = amt*3, posX, posZ, meta, type; distrAttempt > 0 && amt > 0; distrAttempt--){
				posX = xx+rand.nextInt(distX*2+1)-distX;
				posZ = zz+rand.nextInt(distZ*2+1)-distZ;
				meta = BlockDungeonPuzzle.getUnlit(world.getMetadata(posX,yy,posZ));
				type = rand.nextInt(2); // 0 = spread, 1 = square
				
				if (meta == BlockDungeonPuzzle.metaTriggerUnlit || (meta == BlockDungeonPuzzle.metaChainedUnlit && rand.nextInt(5) > 2)){
					boolean stop = false;
					
					for(int dir = 0; dir < 4; dir++){
						int adjMeta = BlockDungeonPuzzle.getUnlit(world.getMetadata(posX+Direction.offsetX[dir],yy,posZ+Direction.offsetZ[dir]));
						
						if (adjMeta == BlockDungeonPuzzle.metaDistributorSpreadUnlit || adjMeta == BlockDungeonPuzzle.metaDistributorSquareUnlit){
							stop = true;
							break;
						}
					}
					
					if (stop)continue;
					
					if (type == 0){
						int nextX = Integer.MIN_VALUE, nextZ = Integer.MIN_VALUE;
						
						for(int px = xx-distX+1; px <= xx+distX-1; px++){
							if (px != posX && BlockDungeonPuzzle.getUnlit(world.getMetadata(px,yy,posZ)) == BlockDungeonPuzzle.metaDistributorSpreadUnlit){
								nextX = px;
								break;
							}
						}
						
						for(int pz = zz-distZ+1; pz <= zz+distZ-1; pz++){
							if (pz != posZ && BlockDungeonPuzzle.getUnlit(world.getMetadata(posX,yy,zz)) == BlockDungeonPuzzle.metaDistributorSpreadUnlit){
								nextZ = pz;
								break;
							}
						}
						
						if (nextX != Integer.MIN_VALUE && nextZ != Integer.MIN_VALUE && BlockDungeonPuzzle.getUnlit(world.getMetadata(nextX,yy,nextZ)) == BlockDungeonPuzzle.metaDistributorSpreadUnlit){
							continue;
						}
					}
					
					world.setBlock(posX,yy,posZ,BlockList.dungeon_puzzle,pickBlock(type == 0 ? BlockDungeonPuzzle.metaDistributorSpreadUnlit : BlockDungeonPuzzle.metaDistributorSquareUnlit,rand));
				}
			}
			
			// generate additional non-triggerable blocks
			
			for(int amt = 1+rand.nextInt(6+(rand.nextInt(xSize*2+zSize*2)>>3))+(MathUtil.square(xSize)>>4)+(MathUtil.square(zSize)>>4), chainAttempt = amt*3, posX, posZ, meta; chainAttempt > 0 && amt > 0; chainAttempt--){
				posX = xx+rand.nextInt(distX*2+1)-distX;
				posZ = zz+rand.nextInt(distZ*2+1)-distZ;
				
				if ((meta = BlockDungeonPuzzle.getUnlit(world.getMetadata(posX,yy,posZ))) == BlockDungeonPuzzle.metaTriggerUnlit){
					boolean canSpawn = true;
					
					for(int dir = 0, adjacentNonTrig = 0, adjMeta; dir < 4; dir++){
						adjMeta = BlockDungeonPuzzle.getUnlit(world.getMetadata(posX+Direction.offsetX[dir],yy,posZ+Direction.offsetZ[dir]));
						
						if ((adjMeta == BlockDungeonPuzzle.metaChainedUnlit && ++adjacentNonTrig > 1) || adjMeta != BlockDungeonPuzzle.metaTriggerUnlit){
							canSpawn = false;
							break;
						}
					}
					
					if (!canSpawn)continue;
					
					--amt;
					if (rand.nextInt(3) == 0)--amt;
				}
			}
			
			// generate entrance cave
			
			Vec3 caveVec = new Vec3(xx-airX,yy-airY,zz-airZ).normalize();
			int iterLimiter = 0, intrad, add = 1+rand.nextInt(4), brokenDungBlocks = 0;
			yy += add;
			
			while(true){
				float rad = rand.nextFloat()*0.3F+1.9F;
				intrad = MathUtil.ceil(rad);
				
				for(int px = MathUtil.floor(airX-intrad); px <= airX+intrad; px++){
					for(int py = MathUtil.floor(airY-intrad); py <= airY+intrad; py++){
						for(int pz = MathUtil.floor(airZ-intrad); pz <= airZ+intrad; pz++){
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
				if (MathUtil.distance(xx-airX,yy-airY,zz-airZ) < 4.8D || brokenDungBlocks > 4 || ++iterLimiter > 30)break;
			}
			
			yy -= add;
			return true;
		}
		
		return false;
	}
	
	private int pickBlock(int meta, Random rand){
		return rand.nextInt(9) < 7 ? BlockDungeonPuzzle.getUnlit(meta) : BlockDungeonPuzzle.toggleState(BlockDungeonPuzzle.getUnlit(meta));
	}
}
