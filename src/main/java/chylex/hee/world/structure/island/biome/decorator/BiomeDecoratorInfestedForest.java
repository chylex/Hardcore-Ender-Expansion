package chylex.hee.world.structure.island.biome.decorator;
import static chylex.hee.world.structure.island.biome.IslandBiomeInfestedForest.MORE_THORNY_BUSHES;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.feature.forest.StructureBush;
import chylex.hee.world.structure.island.biome.feature.forest.StructureRavagedDungeon;
import chylex.hee.world.structure.island.biome.feature.forest.StructureRuinBuild;
import chylex.hee.world.structure.island.biome.feature.forest.StructureRuinBuild.RuinStructureType;
import chylex.hee.world.structure.island.biome.feature.forest.StructureRuinPillar;
import chylex.hee.world.structure.island.biome.feature.forest.StructureSpookyTree;
import chylex.hee.world.structure.island.biome.feature.forest.StructureSpookyTree.TreeType;

public final class BiomeDecoratorInfestedForest extends IslandBiomeDecorator{
	private static IslandBiomeBase getBiome(){
		return IslandBiomeBase.infestedForest;
	}
	
	private final StructureSpookyTree genTree = new StructureSpookyTree();
	private final StructureBush genBush = new StructureBush();
	private final StructureRuinPillar genRuinPillar = new StructureRuinPillar();
	private final StructureRuinBuild genRuinBuild = new StructureRuinBuild();
	
	/*
	 * DEEP
	 */
	
	public void genDeep(){
		// TREES
		genTree.setCanGenerateFace(true).setLooseSpaceCheck(true);
		
		for(int attempt = 0, placed = 0; attempt < 10000 && placed < 2900; attempt++){
			genTree.setTreeType(rand.nextBoolean() ? TreeType.SIMPLE_BULGING : TreeType.SIMPLE_SPHERICAL);
			if (generateStructure(genTree,getBiome()))++placed;
		}
		
		for(int a = 0; a < 169; a++){
			// THORNY BUSHES
			if (rand.nextBoolean() || data.hasDeviation(MORE_THORNY_BUSHES)){
				for(int attempt = 0; attempt < 18; attempt++){
					int xx = getRandomXZ(rand,0), zz = getRandomXZ(rand,0), yy = world.getHighestY(xx,zz);
					
					if (world.getBlock(xx,yy,zz) == topBlock){
						world.setBlock(xx,yy,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataThornBush);
						if (!data.hasDeviation(MORE_THORNY_BUSHES) && rand.nextInt(5) <= 1)break;
					}
				}
			}
			
			// INFESTED GRASS
			for(int attempt = 0; attempt < 100; attempt++){
				int xx = getRandomXZ(rand,0), zz = getRandomXZ(rand,0), yy = attempt > 70 ? 10+rand.nextInt(50) : world.getHighestY(xx,zz);
				if (world.getBlock(xx,yy,zz) == topBlock){
					world.setBlock(xx,yy+1,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedGrass);
				}
			}
			
			// INFESTED FERNS
			for(int attempt = 0; attempt < 85; attempt++){
				int xx = getRandomXZ(rand,0), zz = getRandomXZ(rand,0), yy = attempt > 60 ? 10+rand.nextInt(50) : world.getHighestY(xx,zz);
				if (world.getBlock(xx,yy,zz) == topBlock){
					world.setBlock(xx,yy+1,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedFern);
				}
			}
			
			// INFESTED TALL GRASS
			for(int attempt = 0; attempt < 80; attempt++){
				int xx = getRandomXZ(rand,0), zz = getRandomXZ(rand,0), yy = attempt > 50 ? 10+rand.nextInt(50) : world.getHighestY(xx,zz);
				if (world.getBlock(xx,yy,zz) == topBlock){
					world.setBlock(xx,yy+1,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedTallgrass);
				}
			}
		}
	}
	
	/*
	 * RAVAGED
	 */
	
	public void genRavaged(){
		// RAVAGED DUNGEON
		for(int attempt = 0; attempt < 5; attempt++){
			if (generateStructure(new StructureRavagedDungeon(),getBiome()))break;
		}
		
		// TREES
		genTree.setCanGenerateFace(false).setLooseSpaceCheck(false);
		
		for(int attempt = 0, placed = 0; attempt < 1600 && placed < 420; attempt++){
			genTree.setTreeType(rand.nextInt(4) == 0 ? TreeType.SIMPLE_PYRAMID : rand.nextBoolean() ? TreeType.SIMPLE_BULGING : TreeType.SIMPLE_SPHERICAL);
			if (generateStructure(genTree,getBiome()))++placed;
		}
		
		// PATCHES OF INFESTED GRASS, TALL GRASS AND FERNS
		for(int pick = 0, pickAmount = rand.nextInt(50)+240; pick < pickAmount; pick++){
			int xx = getRandomXZ(rand,48),
				zz = getRandomXZ(rand,48),
				yy = 8+rand.nextInt(30),
				type = rand.nextInt(3);
			
			for(int yTest = 0; yTest < 40; yTest++){
				if (world.getBlock(xx,yy++,zz) == topBlock || yy >= 60)break;
			}
			
			for(int attempt = 0, attemptAmount = 70+rand.nextInt(80), px, py, pz, meta; attempt < attemptAmount; attempt++){
				px = xx+rand.nextInt(4+(attempt>>2))-rand.nextInt(4+(attempt>>2));
				py = yy+rand.nextInt(4+(attempt>>5))-rand.nextInt(4+(attempt>>5));
				pz = zz+rand.nextInt(4+(attempt>>2))-rand.nextInt(4+(attempt>>2));
				
				if (world.getBlock(px,py,pz) == topBlock && world.isAir(px,py+1,pz)){
					meta = (type == 0 ? BlockCrossedDecoration.dataInfestedTallgrass : type == 1 ? BlockCrossedDecoration.dataInfestedGrass : BlockCrossedDecoration.dataInfestedFern);
					
					if (rand.nextInt(8) == 0){
						int newType = rand.nextInt(3);
						meta = (newType == 0 ? BlockCrossedDecoration.dataInfestedTallgrass : newType == 1 ? BlockCrossedDecoration.dataInfestedGrass : BlockCrossedDecoration.dataInfestedFern);
					}
					
					world.setBlock(px,py+1,pz,BlockList.crossed_decoration,meta);
				}
			}
		}
		
		// RANDOM INFESTED GRASS, TALL GRASS AND FERNS
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){
				for(int attempt = 0, type; attempt < 120; attempt++){
					int xx = cx*16+rand.nextInt(16), zz = cz*16+rand.nextInt(16), yy = 10+rand.nextInt(50);
					
					if (world.getBlock(xx,yy-1,zz) == topBlock){
						type = rand.nextInt(3);
						world.setBlock(xx,yy,zz,BlockList.crossed_decoration,type == 0 ? BlockCrossedDecoration.dataInfestedTallgrass : type == 1 ? BlockCrossedDecoration.dataInfestedGrass : BlockCrossedDecoration.dataInfestedFern);
					}
				}
			}
		}
	}
	
	/*
	 * RUINS
	 */
	
	public void genRuins(){
		// BUSHES
		for(int attempt = 0; attempt < 280; attempt++)generateStructure(genBush,getBiome());
		
		// RANDOM RUIN PILLARS
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){
				for(int attempt = 0, amount = 14+rand.nextInt(7); attempt < amount; attempt++)generateStructure(genRuinPillar.setIsDeep(rand.nextInt(20) == 0),getBiome());
			}
		}

		// TREES
		genTree.setCanGenerateFace(false).setLooseSpaceCheck(false);
		
		for(int attempt = 0; attempt < 650; attempt++){
			genTree.setTreeType(TreeType.values()[rand.nextInt(TreeType.values().length)]);
			generateStructure(genTree,getBiome());
		}

		// CLUSTERS OF RUIN PILLARS
		for(int bulkAttempt = 0, bulk = 0; bulkAttempt < 18 && bulk < 4+rand.nextInt(3); bulkAttempt++){
			int xx = getRandomXZ(rand,16);
			int zz = getRandomXZ(rand,16);
			int yy = 10+rand.nextInt(30);
			
			boolean generates = false;
			
			for(int testY = 0; testY < 40; testY++, yy++){
				if (world.getBlock(xx,yy,zz) == topBlock){
					generates = true;
					break;
				}
			}
			
			if (!generates)continue;
			
			double density = 12D+rand.nextDouble()*rand.nextDouble()*6D;
			int totalAttempts = (int)(18*density);
			
			int px, py, pz;
			for(int attempt = 0; attempt < totalAttempts; attempt++){
				px = xx+(int)((rand.nextInt(20)-rand.nextInt(20))*rand.nextGaussian()*((2D*attempt)/totalAttempts));
				pz = zz+(int)((rand.nextInt(20)-rand.nextInt(20))*rand.nextGaussian()*((2D*attempt)/totalAttempts));
				
				if (Math.abs(px-xx) > 25 || Math.abs(pz-zz) > 25)continue;
				
				for(py = yy-1-rand.nextInt(4); py < yy+4; py++){
					if (world.getBlock(px,py,pz) == topBlock){
						genRuinPillar.setIsDeep(rand.nextInt(6) == 0);
						genRuinPillar.setForcedCoords(px,py+1,pz);
						generateStructure(genRuinPillar,getBiome());
						break;
					}
				}
			}
			
			++bulk;
		}
		
		// COBWEBS
		for(int attempt = 0; attempt < 1000; attempt++){
			int xx = getRandomXZ(rand,0);
			int zz = getRandomXZ(rand,0);
			int yy = 0, yAttempt, checkX, checkZ;
			
			boolean canGenerate = false;
			
			for(yAttempt = 0; yAttempt < 15; yAttempt++){
				yy = 15+rand.nextInt(45);
				
				if (world.isAir(xx,yy,zz)){
					canGenerate = true;
					break;
				}
			}
			
			if (!canGenerate)continue;
			
			for(checkX = xx-2; checkX <= xx+2; checkX++){
				for(checkZ = zz-2; checkZ <= zz+2; checkZ++){
					if (!world.isAir(checkX,yy,checkZ)){
						world.setBlock(xx,yy,zz,Blocks.web);
						checkX += 9;
						break;
					}
				}
			}
		}
		
		// RUIN BUILDS
		for(int attempt = 0; attempt < 42; attempt++){
			genRuinBuild.setStructureType(RuinStructureType.WALL); // TODO
			generateStructure(genRuinBuild,getBiome());
		}
	}
}
