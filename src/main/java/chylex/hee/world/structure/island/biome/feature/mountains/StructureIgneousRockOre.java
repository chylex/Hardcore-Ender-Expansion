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
		int minX = 0, maxX = ComponentScatteredFeatureIsland.size,
			minY = 20, maxY = 60,
			minZ = 0, maxZ = ComponentScatteredFeatureIsland.size,
			amount, px, py, pz;

		for(int a = 0; a < attempts; a++){
			amount = 3+rand.nextInt(4);
			int xx = rand.nextInt(maxX-minX+1)+minX, yy = rand.nextInt(maxY-minY+1)+minY, zz = rand.nextInt(maxZ-minZ+1)+minZ;
			double sqrtAmount = Math.sqrt(amount*2D);
			
			for(int attempt = 0, placed = 0; attempt < amount*4 && placed < amount; attempt++){
				px = xx+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				py = yy+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				pz = zz+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				
				if (world.getBlock(px,py,pz) == Blocks.end_stone){
					world.setBlock(px,py,pz,BlockList.igneous_rock_ore);
					++placed;
				}
			}
		}
		
		return true;
	}
}
