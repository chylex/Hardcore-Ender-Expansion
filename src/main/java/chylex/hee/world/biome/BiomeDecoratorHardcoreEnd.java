package chylex.hee.world.biome;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.WorldGenBlob;
import chylex.hee.world.feature.WorldGenEndPowderOre;
import chylex.hee.world.feature.WorldGenEndiumOre;
import chylex.hee.world.feature.WorldGenEnergyCluster;
import chylex.hee.world.feature.WorldGenMeteoroid;
import chylex.hee.world.feature.WorldGenObsidianSpike;
import chylex.hee.world.util.WorldGenChance;

/*
 * WorldGen layer specifications
 * =============================
 *  - single = happens once in a range
 *  - constant = same chance for all ranges
 *  - linear+/- = chance increases/decreases linearly in a range
 *  - cubic+/- = chance increases/decreases using cubic easing function in a range
 *  
 * Chance ranges
 * =============
 *  a- => constant - generation starts happening at a point
 *  a-b => single - generation happens once in that range
 *  	=> linear & cubic - generation chance moves inside the range
 *  					  - the chance always equals 0 on the left side, and then moves from 0-1 (increasing) or 1-0 (decreasing)
 *  a/b-c => linear & cubic - generation chance moves inside the b-c range, but the element starts spawning sooner
 *  a-b-c => linear & cubic - generation chance moves from 0-1 and then from 1-0
 * 
 *  - chance can be also modified to stop worldgen elements from disappearing completely
 * 
 * Element list
 * ============
 * Dragon Lair        | single   | 0-120           |
 * End Powder Ore     | constant | 0-              |
 * Endstone Blob      | constant | 100-            | some types spawn a bit further
 * Energy Cluster     | cubic+   | 320-6400        | x != 0 => x*0.45+0.22
 * Energy Cluster (2) | linear+  | 320-6400        | x != 0 => x*0.9+0.1 ... chance to continue spawning
 * Dungeon Tower      | cubic-   | 350/900-3800    | x != 0 => x*0.75+0.25
 * Endium Ore         | cubic+   | 500-12000       | x != 0 => x*0.64+0.28
 * Endium Ore (2)     | linear+  | 1500-21000      | max amount of blocks spawned
 * Meteoroid          | linear+- | 1280-2700-15000 |
 * Biome Island       | linear+  | 1600-4000       | x != 0 => x*0.35+0.65
 */

public class BiomeDecoratorHardcoreEnd extends BiomeEndDecorator{
	private final WorldGenBlob blobGen;
	private final WorldGenMeteoroid meteoroidGen;
	private final WorldGenEndPowderOre endPowderOreGen;
	private final WorldGenEndiumOre endiumOreGen;
	private final WorldGenEnergyCluster clusterGen;
	
	public BiomeDecoratorHardcoreEnd(){
		spikeGen = new WorldGenObsidianSpike();
		blobGen = new WorldGenBlob();
		meteoroidGen = new WorldGenMeteoroid();
		endPowderOreGen = new WorldGenEndPowderOre();
		endiumOreGen = new WorldGenEndiumOre();
		clusterGen = new WorldGenEnergyCluster();
	}
	
	@Override
	protected void genDecorations(BiomeGenBase biome){
		if (currentWorld.provider.dimensionId != 1){
			super.genDecorations(biome);
			return;
		}
		
		generateOres();

		double distFromCenter = MathUtil.distance(chunk_X>>4,chunk_Z>>4)*16D;
		
		if (distFromCenter < 120D && randomGenerator.nextInt(5) == 0){
			Stopwatch.timeAverage("WorldGenObsidianSpike",4);
			int xx = randX(), zz = randZ();
			spikeGen.generate(currentWorld,randomGenerator,xx,currentWorld.getTopSolidOrLiquidBlock(xx,zz),zz);
			Stopwatch.finish("WorldGenObsidianSpike");
		}
		
		if (distFromCenter > 102D && Math.abs(randomGenerator.nextGaussian()) < 0.285D){
			Stopwatch.timeAverage("WorldGenBlob",64);
			blobGen.generate(currentWorld,randomGenerator,chunk_X+16,32+randomGenerator.nextInt(60),chunk_Z+16);
			Stopwatch.finish("WorldGenBlob");
		}
		
		if (distFromCenter > 320D && checkChance(0.22D+0.45D*WorldGenChance.cubic2Incr.calculate(distFromCenter,320D,6400D)) && randomGenerator.nextDouble()*randomGenerator.nextDouble() > 0.6D){
			Stopwatch.timeAverage("WorldGenEnergyCluster",64);
			
			for(int a = 0; a < randomGenerator.nextInt(4); a++){
				clusterGen.generate(currentWorld,randomGenerator,chunk_X+8,0,chunk_Z+8);
				if (!checkChance(0.1D+0.9D*WorldGenChance.linear2Incr.calculate(distFromCenter,320D,6400D)))break;
			}
			
			Stopwatch.finish("WorldGenEnergyCluster");
		}
		
		if (distFromCenter > 500D && checkChance(0.28D+0.64D*WorldGenChance.cubic2Incr.calculate(distFromCenter,500D,12000D))){
			Stopwatch.timeAverage("WorldGenEndiumOre",64);
			
			for(int attempt = 0, max = 1+randomGenerator.nextInt(1+randomGenerator.nextInt(2+MathUtil.ceil(9D*WorldGenChance.linear2Incr.calculate(distFromCenter,1500D,21000D)))); attempt < 440; attempt++){
				if (endiumOreGen.generate(currentWorld,randomGenerator,randX(),10+randomGenerator.nextInt(100),randZ()) && --max <= 0)break;
			}
			
			Stopwatch.finish("WorldGenEndiumOre");
		}
		
		if (distFromCenter > 1280D && checkChance(WorldGenChance.linear3IncrDecr.calculate(distFromCenter,1280D,2700D,15000D)) && randomGenerator.nextFloat()*randomGenerator.nextFloat() > 0.666F){
			Stopwatch.timeAverage("WorldGenMeteoroid",64);
			
			for(int attempt = 0; attempt < randomGenerator.nextInt(3); attempt++){
				meteoroidGen.generate(currentWorld,randomGenerator,randX(),8+randomGenerator.nextInt(112),randZ());
			}

			Stopwatch.finish("WorldGenMeteoroid");
		}
		
		Stopwatch.timeAverage("WorldGenEndPowderOre",64);
		
		for(int attempt = 0, placed = 0, xx, yy, zz; attempt < 22 && placed < 4+randomGenerator.nextInt(5); attempt++){
			xx = randX();
			yy = 35+randomGenerator.nextInt(92);
			zz = randZ();
			
			if (currentWorld.getBlock(xx,yy,zz) == Blocks.end_stone && endPowderOreGen.generate(currentWorld,randomGenerator,xx,yy,zz)){
				++placed;
			}
		}
		
		Stopwatch.finish("WorldGenEndPowderOre");

		if (chunk_X == 0 && chunk_Z == 0){
			EntityBossDragon dragon = new EntityBossDragon(currentWorld);
			dragon.setLocationAndAngles(0D,128D,0D,randomGenerator.nextFloat()*360F,0F);
			currentWorld.spawnEntityInWorld(dragon);
			WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setDragonExists();
		}
	}

	@Override
	protected void generateOres(){
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(currentWorld,randomGenerator,chunk_X,chunk_Z));
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(currentWorld,randomGenerator,chunk_X,chunk_Z));
	}
	
	private int randX(){
		return chunk_X+randomGenerator.nextInt(16)+8;
	}
	
	private int randZ(){
		return chunk_Z+randomGenerator.nextInt(16)+8;
	}
	
	private boolean checkChance(double chance){
		return WorldGenChance.checkChance(chance,randomGenerator);
	}
}
