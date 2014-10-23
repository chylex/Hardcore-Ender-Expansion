package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureMiningSpot extends AbstractIslandStructure{
	private WeightedList<ObjectWeightPair<Block>> oreWeights;
	private byte iterationsLeft;
	
	public void regenerateOreWeightList(Random rand){
		oreWeights = new WeightedList<>(
			ObjectWeightPair.of(Blocks.emerald_ore,8),
			ObjectWeightPair.of(Blocks.lapis_ore,8),
			ObjectWeightPair.of(Blocks.redstone_ore,11),
			ObjectWeightPair.of(Blocks.diamond_ore,14),
			ObjectWeightPair.of(Blocks.coal_ore,20),
			ObjectWeightPair.of(Blocks.gold_ore,25),
			ObjectWeightPair.of(Blocks.iron_ore,26)
		);
		
		for(int a = 0; a < 1+rand.nextInt(2); a++)oreWeights.remove(rand.nextInt(oreWeights.size()));
	}
	
	@Override
	protected boolean generate(Random rand){
		if (oreWeights == null)regenerateOreWeightList(rand);
		
		int x = getRandomXZ(rand,32), z = getRandomXZ(rand,32), y = 5+rand.nextInt(15+rand.nextInt(30));
		if (world.getBlock(x,y,z) != Blocks.end_stone)return false;
		
		double rad = 1.5D+rand.nextDouble()*0.5D;
		iterationsLeft = (byte)(60+rand.nextInt(50));
		
		generateBlob(rand,x,y,z,rad,0);
		return true;
	}
	
	private void generateBlob(Random rand, int x, int y, int z, double rad, int recursionLevel){
		if (x <= rad || z <= rad || x >= ComponentIsland.size-rad || z >= ComponentIsland.size-rad)return;
		if (--iterationsLeft == 0 || (recursionLevel > 0 && rand.nextInt(30-recursionLevel*2) == 0) || recursionLevel > 12)return;
		
		int xx, yy, zz;
		double dist;
		
		for(xx = (int)Math.floor(x-rad)-1; xx <= x+rad+1; xx++){
			for(yy = (int)Math.floor(y-rad)-1; yy <= y+rad+1; yy++){
				if (yy <= 0)continue;
				
				for(zz = (int)Math.floor(z-rad)-1; zz <= z+rad+1; zz++){
					dist = MathUtil.distance(xx-x,yy-y,zz-z);
					
					if (world.getBlock(xx,yy,zz) == Blocks.end_stone && rand.nextInt(4) == 0 && dist <= rad-rand.nextDouble()*0.5D){
						placeBlock(rand,xx,yy,zz,dist/rad);
					}
				}
			}
		}
		
		if (rand.nextInt(20) == 0)generateBlob(rand,x+rand.nextInt(3)-rand.nextInt(3),y+rand.nextInt(3)-rand.nextInt(3),z+rand.nextInt(3)-rand.nextInt(3),rad,recursionLevel+1);
		generateBlob(rand,x+rand.nextInt(3)-rand.nextInt(3),y+rand.nextInt(3)-rand.nextInt(3),z+rand.nextInt(3)-rand.nextInt(3),rad,recursionLevel);
	}
	
	private void placeBlock(Random rand, int x, int y, int z, double distPercent){
		if (rand.nextBoolean() && rand.nextBoolean() && rand.nextDouble() > distPercent-0.2D-rand.nextDouble()*0.25D)world.setBlock(x,y,z,oreWeights.getRandomItem(rand).getObject());
		else if (rand.nextBoolean())world.setBlock(x,y,z,Blocks.stone);
	}
}
