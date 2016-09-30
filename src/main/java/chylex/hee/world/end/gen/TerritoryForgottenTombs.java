package chylex.hee.world.end.gen;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.feature.noise.GenerateIslandNoise;
import chylex.hee.world.feature.noise.GenerateIslandNoiseTame;
import chylex.hee.world.feature.ores.GenerateOres;
import chylex.hee.world.structure.StructureWorld;

public class TerritoryForgottenTombs extends TerritoryGenerator{
	public TerritoryForgottenTombs(EndTerritory territory, EnumSet variations, StructureWorld world, Random rand){
		super(territory, variations, world, rand);
	}

	@Override
	public void generate(){
		for(int island = 0; island < 3; island++){
			final double angle = MathUtil.toRad(120D*island+(rand.nextDouble()-0.5D)*20D);
			final double dist = 50D+rand.nextDouble()*10D;
			
			int offX = MathUtil.floor(Math.cos(angle)*dist);
			int offZ = MathUtil.floor(Math.sin(angle)*dist);
			
			for(int attempt = 0; attempt < 25; attempt++){
				GenerateIslandNoise noise = new GenerateIslandNoise(Blocks.end_stone, rand);
				noise.terrainSize = 36;
				noise.noiseHeight = 21;
				noise.sideSmoothness = 180D;
				noise.densityPeakMultiplier = 0.5D;
				noise.surfaceHillScale = 48F;
				
				GenerateIslandNoiseTame generator = new GenerateIslandNoiseTame(noise);
				generator = new GenerateIslandNoiseTame(noise);
				generator.setWorldArea(100, 60, 100);
				generator.setCenterXZ();
				
				if (attempt < 18){
					generator.setMinBlocks(65_000);
					generator.setMinSize(72, 72);
					generator.setMaxSize(100, 100);
				}
				else if (attempt < 24){
					generator.setMinBlocks(30_000);
					generator.setMinSize(50, 50);
					generator.setMaxSize(120, 120);
				}
				
				if (generator.generate(world, offX, rand.nextInt(10), offZ))break;
			}
		}
	}
}
