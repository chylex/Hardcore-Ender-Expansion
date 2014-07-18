package chylex.hee.world.structure.island.biome;
import java.util.Random;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorBurningMountains;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public class IslandBiomeBurningMountains extends IslandBiomeBase{
	/*public static enum RareVariationMountains implements IRareVariationEnum{
		EXTRA_LILYFIRES, MORE_CAVES
	}
	*/
	
	private final BiomeDecoratorBurningMountains decorator = new BiomeDecoratorBurningMountains();
	
	protected IslandBiomeBurningMountains(int biomeID, KnowledgeRegistration knowledgeRegistration){
		super(biomeID,knowledgeRegistration);
		spawnEntries.add(new SpawnEntry(EntityMobFireGolem.class,14,10));
		spawnEntries.add(new SpawnEntry(EntityMobScorchingLens.class,10,6));
	}

	@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){}
	
	// TODO
	/*@Override
	public void decorateIsland(World world, Random rand, int centerX, int bottomY, int centerZ){
		int minX = centerX-ComponentScatteredFeatureIsland.halfSize, maxX = centerX+ComponentScatteredFeatureIsland.halfSize,
			minY = bottomY+20,maxY = bottomY+60,
			minZ = centerZ-ComponentScatteredFeatureIsland.halfSize, maxZ = centerZ+ComponentScatteredFeatureIsland.halfSize;

		for(int a = 0; a < 150; a++){
			int amount = 3+rand.nextInt(4);
			int xx = rand.nextInt(maxX-minX+1)+minX,yy = rand.nextInt(maxY-minY+1)+minY,zz = rand.nextInt(maxZ-minZ+1)+minZ;
			double sqrtAmount = Math.sqrt(amount*2D);
			
			for(int attempt = 0,placed = 0; attempt < amount*4 && placed < amount; attempt++){
				int _x = xx+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble()),
					_y = yy+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble()),
					_z = zz+(int)(Math.cos(rand.nextDouble()*2D*Math.PI)*sqrtAmount*rand.nextDouble());
				
				if (world.getBlock(_x,_y,_z) == Blocks.end_stone){
					world.setBlock(_x,_y,_z,BlockList.igneous_rock_ore);
					++placed;
				}
			}
		}

		if (rand.nextInt(5) <= 1)new StructureMountainResourcePit().generateInWorld(world,rand,centerX,bottomY,centerZ,this);
		new StructureMountainPuzzle().generateInWorld(world,rand,centerX,bottomY,centerZ,this);
	}
	
	@Override
	public void decorateChunk(ComponentScatteredFeatureIsland island, World world, StructureBoundingBox bb, Random rand, int x, int bottomY, int z){
		for(int a = 0; a < 80; a++){
			int xx = x+1+rand.nextInt(14),zz = z+1+rand.nextInt(14),
				yy = bottomY+26+rand.nextInt(40);
			
			if (island.getBlock(xx,yy,zz,world,bb) == Blocks.end_stone && (island.isAir(xx+1,yy,zz,world,bb) || island.isAir(xx-1,yy,zz,world,bb) || island.isAir(xx,yy,zz+1,world,bb) || island.isAir(xx,yy,zz-1,world,bb))){
				island.placeBlockAndUpdate(Blocks.flowing_lava,0,xx,yy,zz,world,bb);
			}
		}
		
		boolean rareLilyfires = false;//hasRareVariation(RareVariationMountains.EXTRA_LILYFIRES);
		if (rand.nextInt(rareLilyfires?3:5) <= 1){
			for(int a = 0; a < 1+rand.nextInt(6)+rand.nextInt(3)+(rareLilyfires?2+rand.nextInt(12):0); a++){
				int xx = x+rand.nextInt(16),zz = z+rand.nextInt(16),yy = island.getHighestY(xx,zz,world,bb);
				if (island.getBlock(xx,yy-1,zz,world,bb) == BlockList.end_terrain){
					island.placeBlock(BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire,xx,yy,zz,world,bb);
				}
			}
		}
	}*/
	
	@Override
	public float getIslandSurfaceHeightMultiplier(){
		return 8F;
	}
	
	@Override
	public float getCaveAmountMultiplier(){
		return 1.9F; //hasRareVariation(RareVariationMountains.MORE_CAVES) ? 2.5F : 1.9F;
	}
	
	@Override
	public float getOreAmountMultiplier(){
		return 1.35F;
	}

	@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaBurned;
	}
}
