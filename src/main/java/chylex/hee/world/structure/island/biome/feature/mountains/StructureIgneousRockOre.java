package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.ComponentScatteredFeatureIsland;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureIgneousRockOre extends AbstractIslandStructure{
	private int attempts = 150;
	
	public StructureIgneousRockOre setAttemptAmount(int attempts){
		this.attempts = attempts;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		int minX = 20, maxX = ComponentScatteredFeatureIsland.size-20,
			minY = 20, maxY = 60,
			minZ = 20, maxZ = ComponentScatteredFeatureIsland.size-20,
			amount, px, py, pz;

		for(int a = 0; a < attempts; a++){
			amount = 3+rand.nextInt(4);
			int x = rand.nextInt(maxX-minX+1)+minX, y = rand.nextInt(maxY-minY+1)+minY, z = rand.nextInt(maxZ-minZ+1)+minZ;
			double sqrtAmount = Math.sqrt(amount*2D);
			
			for(int attempt = 0, placed = 0; attempt < amount*4 && placed < amount; attempt++){
				px = x+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				py = y+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				pz = z+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				
				if (world.getBlock(px,py,pz) == Blocks.end_stone){
					world.setBlock(px,py,pz,BlockList.igneous_rock_ore);
					++placed;
				}
			}
		}
		
		return true;
	}
}
