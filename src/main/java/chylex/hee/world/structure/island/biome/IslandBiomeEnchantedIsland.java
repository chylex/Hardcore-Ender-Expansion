package chylex.hee.world.structure.island.biome;
import java.util.Random;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorEnchantedIsland;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public class IslandBiomeEnchantedIsland extends IslandBiomeBase{
	/*public static enum RareVariationIsland implements IRareVariationEnum{
		TALL_PILLARS, LARGE_LAKES
	}*/
	
	private final BiomeDecoratorEnchantedIsland decorator = new BiomeDecoratorEnchantedIsland();
	
	protected IslandBiomeEnchantedIsland(int biomeID, KnowledgeRegistration knowledgeRegistration){
		super(biomeID,knowledgeRegistration);
		spawnEntries.add(new SpawnEntry(EntityMobEnderGuardian.class,9,30));
		spawnEntries.add(new SpawnEntry(EntityMobBabyEnderman.class,16,20));
	}

	// TODO
	@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){}

	/*@Override
	public void decorateIsland(World world, Random rand, int centerX, int bottomY, int centerZ){
		/*if (rand.nextInt(7) <= 3){
			StructureIslandRelic relic = new StructureIslandRelic();
			
			for(int attempt = 0; attempt < 5; attempt++){
				if (relic.generateInWorld(world,rand,centerX,bottomY,centerZ,this))break;
			}
		}*/
		/*
		StructureIslandLake lake = new StructureIslandLake();
		
		for(int attempt = 0,placed = 0,amount = rand.nextInt(3)+5; attempt < 170 && placed < amount; attempt++){
			if (lake.generateInWorld(world,rand,centerX,bottomY,centerZ,this))++placed;
		}
	}
	
	@Override
	public void decorateChunk(ComponentScatteredFeatureIsland island, World world, StructureBoundingBox bb, Random rand, int x, int bottomY, int z){
		if (rand.nextInt(5) <= 2){
			int height = rand.nextInt(14)+(/*hasRareVariation(RareVariationIsland.TALL_PILLARS)?6+rand.nextInt(8):*//*4);
			int radius = rand.nextInt(2)+1;
			
			int ox = x+rand.nextInt(16-radius*2)+radius,
				oz = z+rand.nextInt(16-radius*2)+radius,
				oy = island.getHighestY(ox,oz,world,bb);
			
			if (oy <= 0)return;
			
			for(int xx = ox-radius; xx <= ox+radius; ++xx){
				for(int zz = oz-radius; zz <= oz+radius; ++zz){
					if (MathUtil.square(xx-ox)+MathUtil.square(zz-oz) <= radius*radius+1){
						if (Math.abs(island.getHighestY(xx,zz,world,bb)-oy) > 2)return;
					}
				}
			}
			
			BlockFalling.fallInstantly = true;
			
			for(int xx = ox-radius; xx <= ox+radius; ++xx){
				for(int zz = oz-radius; zz <= oz+radius; ++zz){
					for(int yy = island.getHighestY(xx,zz,world,bb); yy < oy+height && yy < 128; ++yy){
						if (MathUtil.square(xx-ox)+MathUtil.square(zz-oz) <= radius*radius+0.5D+rand.nextGaussian()*0.7D){
							island.placeBlockAndUpdate(BlockList.obsidian_end,0,xx,yy,zz,world,bb);
						}
					}
				}
			}
			
			BlockFalling.fallInstantly = false;
		}
		
		if (rand.nextInt(14) <= 3){
			new StructureIslandStash().generateInChunk(island,world,bb,rand,x,bottomY,z,this);
		}
	}*/
	
	@Override
	public float getIslandMassHeightMultiplier(){
		return 0.8F;
	}
	
	@Override
	public float getIslandFillFactor(){
		return 0.92F;
	}
	
	@Override
	public float getCaveAmountMultiplier(){
		return 0.45F;
	}
	
	@Override
	public float getCaveBranchingChance(){
		return 0.005F;
	}
	
	@Override
	public float getOreAmountMultiplier(){
		return 1.25F;
	}

	@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaEnchanted;
	}
}
