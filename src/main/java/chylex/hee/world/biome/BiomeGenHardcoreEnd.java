package chylex.hee.world.biome;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenEnd;

public final class BiomeGenHardcoreEnd extends BiomeGenEnd{
	public static boolean overrideMobLists;
	
	public BiomeGenHardcoreEnd(int id){
		super(id);
		
		topBlock = Blocks.dirt;
		fillerBlock = Blocks.dirt;
		theBiomeDecorator = createBiomeDecorator();
	}
	
	public void overrideMobLists(){
		if (overrideMobLists){
			spawnableMonsterList.clear();
			spawnableCreatureList.clear();
			spawnableWaterCreatureList.clear();
			spawnableCaveCreatureList.clear();
			
			spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class,10,4,4));
		}
	}
	
	@Override
	public BiomeDecorator createBiomeDecorator(){
		return new BiomeDecoratorHardcoreEnd();
	}
}
