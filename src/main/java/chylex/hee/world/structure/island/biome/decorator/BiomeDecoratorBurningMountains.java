package chylex.hee.world.structure.island.biome.decorator;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.IslandBiomeBurningMountains;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureCinderPatch;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureDungeonPuzzle;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureIgneousRockOre;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureLavaPool;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureMiningSpot;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureResourcePit;

public class BiomeDecoratorBurningMountains extends IslandBiomeDecorator{
	@Override
	protected final IslandBiomeBase getBiome(){
		return IslandBiomeBase.burningMountains;
	}
	
	private final StructureIgneousRockOre genIgneousRockOre = new StructureIgneousRockOre();
	private final StructureCinderPatch genCinderPatch = new StructureCinderPatch();
	private final StructureDungeonPuzzle genMountainPuzzle = new StructureDungeonPuzzle();
	private final StructureLavaPool genLavaPool = new StructureLavaPool();
	private final StructureResourcePit genResourcePit = new StructureResourcePit();
	private final StructureMiningSpot genMiningSpot = new StructureMiningSpot();
	
	/*
	 * SCORCHING
	 */
	
	public void genScorching(){
		// CINDER
		for(int a = 0; a < 72; a++)generateStructure(genCinderPatch);
		
		// DUNGEON PUZZLE
		generateStructure(genMountainPuzzle);
		
		// IGNEOUS ROCK ORE
		generateStructure(genIgneousRockOre.setAttemptAmount(110));
		
		// SINGLE LAVA BLOCKS
		for(int a = data.hasDeviation(IslandBiomeBurningMountains.SINGLE_LAVA_ONLY) ? 8000 : 6500, xx, yy, zz; a > 0; a--){
			xx = getRandomXZ(rand,32);
			zz = getRandomXZ(rand,32);
			yy = 10+rand.nextInt(65);
			
			if (world.getBlock(xx,yy,zz) == Blocks.end_stone && (world.isAir(xx+1,yy,zz) || world.isAir(xx-1,yy,zz) || world.isAir(xx,yy,zz+1) || world.isAir(xx,yy,zz-1))){
				world.setBlock(xx,yy,zz,Blocks.flowing_lava,0,true);
			}
		}
		
		// LAVA POOLS
		if (!data.hasDeviation(IslandBiomeBurningMountains.SINGLE_LAVA_ONLY)){
			for(int attempt = 0, placed = 0, placedMax = 3+rand.nextInt(10); attempt < 450 && placed < placedMax; attempt++){
				if (generateStructure(genLavaPool))++placed;
			}
		}
		
		// LILYFIRES
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){
				if (rand.nextInt(4) <= 1){
					for(int a = 0; a < 1+rand.nextInt(6)+rand.nextInt(5); a++){
						int xx = cx*16+rand.nextInt(16), zz = cz*16+rand.nextInt(16), yy = world.getHighestY(xx,zz);
						
						if (world.getBlock(xx,yy,zz) == BlockList.end_terrain){
							world.setBlock(xx,yy+1,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire);
						}
					}
				}
			}
		}
		
		// FLAMEWEED
		for(int attempt = 0, xx, yy, zz, lava; attempt < 420; attempt++){
			xx = getRandomXZ(rand,48);
			zz = getRandomXZ(rand,48);
			yy = 10+rand.nextInt(65);
			if (world.isAir(xx,yy,zz))continue;
			
			lava = 0;
			
			for(int lavaCheck = 0; lavaCheck < 25 && lava < 5; lavaCheck++){
				if (world.getBlock(xx+rand.nextInt(11)-5,yy+rand.nextInt(4),zz+rand.nextInt(11)-5).getMaterial() == Material.lava)++lava;
			}
			
			for(int placeAttempt = 40+(7-lava)*5, px, py, pz; placeAttempt > 0; placeAttempt--){
				px = xx+MathUtil.floor((rand.nextDouble()-0.5D)*rand.nextDouble()*65D);
				pz = zz+MathUtil.floor((rand.nextDouble()-0.5D)*rand.nextDouble()*65D);
				
				for(int yAttempt = 0; yAttempt < 8; yAttempt++){
					py = yy+rand.nextInt(11)-5;
					
					if (world.isAir(px,py,pz) && world.getBlock(px,py-1,pz) == BlockList.end_terrain){
						world.setBlock(px,py,pz,BlockList.crossed_decoration,rand.nextInt(3) == 0 ? BlockCrossedDecoration.dataFlameweed1 : rand.nextBoolean() ? BlockCrossedDecoration.dataFlameweed2 : BlockCrossedDecoration.dataFlameweed3);
						break;
					}
				}
			}
		}
	}
	
	/*
	 * MINE
	 */
	
	public void genMine(){
		// RESOURCE PIT
		for(int attempt = 0, placed = 0, placedMax = 3+rand.nextInt(3+rand.nextInt(4)); attempt < 12 && placed < placedMax; attempt++){
			if (generateStructure(genResourcePit))++placed;
		}
		
		// FLAMEWEED
		int[] flameweed = new int[]{ BlockCrossedDecoration.dataFlameweed1, BlockCrossedDecoration.dataFlameweed2, BlockCrossedDecoration.dataFlameweed3 };
		
		for(int type = 0, xx, yy, zz; type < 3; type++){
			for(int attempt = rand.nextInt(300)+750; attempt > 0; attempt--){
				xx = getRandomXZ(rand,8);
				zz = getRandomXZ(rand,8);
				yy = 60-rand.nextInt(20+rand.nextInt(32));
				
				for(int yAttempt = 0; yAttempt < 10; yAttempt++){
					++yy;
					
					if (world.isAir(xx,yy,zz) && world.getBlock(xx,yy-1,zz) == BlockList.end_terrain){
						world.setBlock(xx,yy,zz,BlockList.crossed_decoration,flameweed[type]);
						break;
					}
				}
			}
		}
		
		// MINING SPOT
		genMiningSpot.regenerateOreWeightList(rand);
		
		for(int attempt = 0, attemptAmount = 90+rand.nextInt(20), placed = 0; attempt < attemptAmount; attempt++){
			if (generateStructure(genMiningSpot)){
				if (++placed < 50 && rand.nextInt(5) != 0)--attempt;
				else if (rand.nextBoolean())--attemptAmount;
			}
		}
		
		// IGNEOUS ROCK ORE
		generateStructure(genIgneousRockOre.setAttemptAmount(165));
	}
}
