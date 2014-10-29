package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TByteByteHashMap;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class LaboratoryTerrainMap{
	private static final byte blockSize = 14, blockSizeHalf = blockSize>>1;
	private static final byte blocksFromCenter = (byte)Math.floor(0.5F*ComponentIsland.size/blockSize);
	
	private final LargeStructureWorld world;
	private LaboratoryTerrainNode[][] nodes = new LaboratoryTerrainNode[blocksFromCenter*2+1][blocksFromCenter*2+1];
	
	LaboratoryTerrainMap(LargeStructureWorld world, int startX, int startZ){
		this.world = world;
		
		startX &= 0b1111;
		startZ &= 0b1111;
		
		for(int x = -blocksFromCenter; x <= blocksFromCenter; x++){
			for(int z = -blocksFromCenter; z <= blocksFromCenter; z++){
				nodes[x+blocksFromCenter][z+blocksFromCenter] = generateNode(x*blockSize+startX,z*blockSize+startZ);
			}
		}
	}
	
	private LaboratoryTerrainNode generateNode(int blockX, int blockZ){
		LaboratoryTerrainNode node = new LaboratoryTerrainNode();

		int centerY = world.getHighestY(blockX+(blockSize>>1),blockZ+(blockSize>>1));
		if (centerY == 0)return node.setUnusable();
		
		int minY = centerY, maxY = centerY, existingBlocks = 0, fluctuationLevel = 0;
		TByteByteHashMap map = new TByteByteHashMap();
		
		for(int x = blockX, z, y; x < blockX+blockSize; x++){
			for(z = blockZ; z < blockZ+blockSize; z++){
				if ((y = world.getHighestY(x,z)) == 0)continue;
				
				++existingBlocks;
				fluctuationLevel += Math.abs(y-centerY);
				map.adjustOrPutValue((byte)(y-128),(byte)1,(byte)1);
				
				if (y < minY)minY = y;
				else if (y > maxY)maxY = y;
			}
		}
		
		if ((float)existingBlocks/(blockSize*blockSize) < 0.6D)return node.setUnusable();
		
		int mostFrequentY = 0;
		byte mostFrequentYAmount = 0;
		
		for(byte y:map.keys()){
			byte amt = map.get(y);
			
			if (amt > mostFrequentYAmount){
				mostFrequentYAmount = amt;
				mostFrequentY = y+128;
			}
		}
		
		
		
		return node;
	}
	
	public int getScore(){
		return 0; // TODO
	}
}
