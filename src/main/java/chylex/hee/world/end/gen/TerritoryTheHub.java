package chylex.hee.world.end.gen;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.feature.noise.GenerateIslandNoise;
import chylex.hee.world.feature.ores.GenerateOres;
import chylex.hee.world.feature.ores.IOreGenerator;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.RandomAmount;

public class TerritoryTheHub extends TerritoryGenerator{
	private final GenerateIslandNoise island;
	private final GenerateOres endPowderOreMain;
	private final GenerateOres endPowderOreSurface;
	
	public TerritoryTheHub(StructureWorld world, Random rand){
		super(world,rand);
		
		this.island = new GenerateIslandNoise(Blocks.end_stone,rand);
		this.island.terrainSize = 56;
		this.island.noiseHeight = 20;
		this.island.peakSmoothness = 50D;
		this.island.surfaceHillScale = 92F;
		
		this.endPowderOreMain = new GenerateOres(Blocks.end_stone,BlockList.end_powder_ore);
		this.endPowderOreMain.setY(10,50);
		this.endPowderOreMain.setChunkSize(24);
		this.endPowderOreMain.setAttemptsPerChunk(36);
		this.endPowderOreMain.setClustersPerChunk(8,14);
		this.endPowderOreMain.setOresPerCluster(2,6,RandomAmount.preferSmaller);
		this.endPowderOreMain.setOreGenerator(new IOreGenerator.AdjacentSpread(true));
		
		this.endPowderOreSurface = new GenerateOres(Blocks.end_stone,BlockList.end_powder_ore);
		this.endPowderOreSurface.setY(36,50);
		this.endPowderOreSurface.setChunkSize(24);
		this.endPowderOreSurface.setAttemptsPerChunk(18);
		this.endPowderOreSurface.setClustersPerChunk(6,9);
		this.endPowderOreSurface.setOresPerCluster(3,9,RandomAmount.preferSmaller);
		this.endPowderOreSurface.setOreGenerator(new IOreGenerator.AdjacentSpread(true));
	}
	
	@Override
	public void generate(){
		island.generate(world);
		
		int lowest = height;
		
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z <= 1; z++){
				lowest = Math.min(lowest,world.getTopY(x,z,Blocks.end_stone));
			}
		}
		
		for(int x = -2; x <= 2; x++){
			for(int z = -2; z <= 2; z++){
				if (Math.abs(x) <= 1 && Math.abs(z) <= 1)world.setAttentionWhore(x,lowest,z,new BlockInfo(Blocks.end_portal,Meta.endPortalActive));
				
				for(int y = lowest+1; y < height; y++){
					if (!world.isAir(x,y,z))world.setAir(x,y,z);
				}
			}
		}
		
		endPowderOreMain.generateSplit(world,rand);
		endPowderOreSurface.generateSplit(world,rand);
	}
}
