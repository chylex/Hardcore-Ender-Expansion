package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TByteByteHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryPlan{
	private final List<LaboratoryElement> elements = new ArrayList<>();
	private int score;
	
	public boolean generate(LargeStructureWorld world, Random rand){
		elements.clear();
		
		int x, z;
		
		for(int locAttempt = 0; locAttempt < 42; locAttempt++){
			x = (rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize)*3/4;
			z = (rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize)*3/4;
			LaboratoryElement element = trySpawnElement(world,LaboratoryElementType.SMALL_ROOM,x,z);
			if (element == null)continue;
			
			elements.add(element);
			break;
		}
		
		if (elements.isEmpty())return false;
		
		while(true){
			LaboratoryElement element = elements.get(rand.nextInt(elements.size()));
			
		}
		
		return true;
	}
	
	public int getScore(){
		return score;
	}
	
	private static LaboratoryElement trySpawnElement(LargeStructureWorld world, LaboratoryElementType type, int x, int z){
		if (type.halfSize < 1)return null;
		
		TByteByteHashMap map = new TByteByteHashMap();
		int minY = -1, maxY = -1;
		
		for(int xx = x-type.halfSize, zz, yy; xx <= x+type.halfSize; xx++){
			for(zz = z-type.halfSize; zz <= z+type.halfSize; zz++){
				if ((yy = world.getHighestY(xx,zz)) == 0)return null;
				
				if (minY == -1)minY = maxY = yy;
				else map.adjustOrPutValue((byte)(yy-128),(byte)1,(byte)1);
			}
		}
		
		if (maxY-minY > 6)return null;
		
		int mostFrequentY = 0;
		byte mostFrequentYAmount = 0;
		
		for(byte y:map.keys()){
			byte amt = map.get(y);
			
			if (amt > mostFrequentYAmount){
				mostFrequentYAmount = amt;
				mostFrequentY = y+128;
			}
		}
		
		if (maxY-mostFrequentY > 3)return null;
		if (mostFrequentYAmount/MathUtil.square(type.halfSize*2+1) < 0.25F)return null;
		
		return new LaboratoryElement(type,x,mostFrequentY,z);
	}
}
