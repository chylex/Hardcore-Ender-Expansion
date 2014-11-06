package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TByteByteHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class LaboratoryTerrainMap{
	private static final byte blockSize = 8, blockSizeHalf = blockSize>>1;
	private static final byte blocksFromCenter = (byte)Math.floor(0.5F*ComponentIsland.size/blockSize);
	private static final byte blocksAcross = (byte)(blocksFromCenter*2+1);
	
	private final LargeStructureWorld world;
	private final LaboratoryTerrainNode[][] nodes = new LaboratoryTerrainNode[blocksAcross][blocksAcross];
	private final int score;
	private int startNodeX = -1, startNodeZ = -1;
	
	LaboratoryTerrainMap(LargeStructureWorld world, Random rand, int startX, int startZ){
		this.world = world;
		
		startX &= 0b1111;
		startZ &= 0b1111;
		
		// generate nodes
		
		for(int x = -blocksFromCenter; x <= blocksFromCenter; x++){
			for(int z = -blocksFromCenter; z <= blocksFromCenter; z++){
				nodes[x+blocksFromCenter][z+blocksFromCenter] = generateNode(x*blockSize+startX,z*blockSize+startZ);
			}
		}
		
		Set<NodeIndex> validIndexes = new HashSet<>();
		
		// find start node
		
		for(int attempt = 0, indexX, indexZ; attempt < 20; attempt++){
			LaboratoryTerrainNode node = nodes[indexX = blocksFromCenter+rand.nextInt(9)-4][indexZ = blocksFromCenter+rand.nextInt(9)-4];
			
			if (!node.isUnusable()){
				startNodeX = indexX;
				startNodeZ = indexZ;
				break;
			}
		}
		
		if (startNodeX == -1 || startNodeZ == -1){
			score = -1;
			return;
		}
		
		// find connectable nodes
		
		for(int a = 0; a < blocksAcross; a++){
			for(int b = 0; b < blocksAcross; b++){
				LaboratoryTerrainNode node = nodes[a][b];
				
				if (a > 0)node.tryConnect(nodes[a-1][b],0);
				if (b > 0)node.tryConnect(nodes[a][b-1],1);
				if (a < blocksAcross-1)node.tryConnect(nodes[a+1][b],2);
				if (b < blocksAcross-1)node.tryConnect(nodes[a][b+1],3);
				
				if (node.hasNoAvailableConnections())node.setUnusable();
			}
		}
		
		// find all pathfindable indexes
		
		Set<NodeIndex> checkedIndexes = new HashSet<>();
		List<NodeIndex> toAdd = new ArrayList<>(32);
		
		checkedIndexes.add(new NodeIndex(startNodeX,startNodeZ));
		
		do{
			toAdd.clear();
			
			for(NodeIndex index:checkedIndexes){
				if (!nodes[index.x][index.z].isUnusable())validIndexes.add(index);
				
				if (index.x > 0 && !nodes[index.x-1][index.z].isUnusable())toAdd.add(new NodeIndex(index.x-1,index.z));
				if (index.z > 0 && !nodes[index.x][index.z-1].isUnusable())toAdd.add(new NodeIndex(index.x,index.z-1));
				if (index.x < blocksAcross-1 && !nodes[index.x+1][index.z].isUnusable())toAdd.add(new NodeIndex(index.x+1,index.z));
				if (index.z < blocksAcross-1 && !nodes[index.x][index.z+1].isUnusable())toAdd.add(new NodeIndex(index.x,index.z+1));
			}
			
			toAdd.removeAll(validIndexes);
			checkedIndexes.addAll(toAdd);
		}while(!toAdd.isEmpty());
		
		for(int a = 0; a < blocksAcross; a++){
			for(int b = 0; b < blocksAcross; b++){
				LaboratoryTerrainNode node = nodes[a][b];
				
				if (!validIndexes.contains(new NodeIndex(a,b)))node.setUnusable();
				if (a > 0 && node.checkConnection(0) && nodes[a-1][b].isUnusable())node.setConnectionAvailable(0,false);
				if (b > 0 && node.checkConnection(1) && nodes[a][b-1].isUnusable())node.setConnectionAvailable(1,false);
				if (a < blocksAcross-1 && node.checkConnection(2) && nodes[a+1][b].isUnusable())node.setConnectionAvailable(2,false);
				if (b < blocksAcross-1 && node.checkConnection(3) && nodes[a][b+1].isUnusable())node.setConnectionAvailable(3,false);
			}
		}
		
		// calculate score
		
		int tmpScore = 0;
		
		for(int a = 0; a < blocksAcross; a++){
			for(int b = 0; b < blocksAcross; b++){
				tmpScore += nodes[a][b].getConnectionAmount();
			}
		}
		
		score = tmpScore;
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
		
		if ((float)mostFrequentYAmount/(blockSize*blockSize) < 0.4D)return node.setUnusable();
		if (maxY-mostFrequentY > 2)return node.setUnusable();
		
		return node.setMostFrequentY(mostFrequentY);
	}
	
	public int getScore(){
		return score;
	}
	
	private final class NodeIndex{
		final byte x, z;
		
		NodeIndex(int x, int z){
			this.x = (byte)x;
			this.z = (byte)z;
		}
		
		@Override
		public int hashCode(){
			return x*131+z;
		}
		
		@Override
		public boolean equals(Object o){
			if (o != null && o.getClass() == NodeIndex.class){
				NodeIndex index = (NodeIndex)o;
				return x == index.x && z == index.z;
			}
			else return false;
		}
	}
}
